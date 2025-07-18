package tech.aiflowy.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.entity.SysToken;
import tech.aiflowy.system.mapper.SysTokenMapper;
import tech.aiflowy.system.service.SysTokenService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * iframe 嵌入用 Token 表 控制层。
 *
 * @author Administrator
 * @since 2025-05-26
 */
@RestController
@RequestMapping("/api/v1/sysToken")
public class SysTokenController extends BaseCurdController<SysTokenService, SysToken> {
    public SysTokenController(SysTokenService service) {
        super(service);
    }

    @Autowired
    private SysTokenService sysTokenService;

    @Autowired
    private SysTokenMapper sysTokenMapper;

    // 手动生成 Token 并绑定账号
    @GetMapping("/generateToken")
    public Result generateToken() {
        return sysTokenService.saveGenerateToken();
    }

    @PostMapping("/updateToken")
    public Result updateToken(@JsonBody SysToken sysToken) {
        return sysTokenService.updateToken(sysToken);
    }

    @Override
    protected Result onRemoveBefore(Collection<Serializable> ids) {
        for (Serializable id : ids){
            SysToken sysToken = sysTokenMapper.selectOneById(id);
            StpUtil.renewTimeout(sysToken.getToken(), 0);
        }
        return super.onRemoveBefore(ids);
    }

    @Override
    public Result page(HttpServletRequest request, String sortKey, String sortType, Long pageNumber, Long pageSize) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }
        QueryWrapper queryWrapper = QueryWrapper.create().select("*").from("tb_sys_token")
                .where("user_id = ? ", StpUtil.getLoginIdAsString());
        Page<SysToken> sysTokenPage = sysTokenMapper.paginateAs(pageNumber, pageSize, queryWrapper, SysToken.class);
        return Result.success(sysTokenPage);
    }
}