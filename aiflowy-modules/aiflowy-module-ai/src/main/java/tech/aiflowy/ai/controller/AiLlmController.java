package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.service.AiLlmService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import tech.aiflowy.common.util.StringUtil;
import org.springframework.util.StringUtils;
import tech.aiflowy.common.web.exceptions.BusinessException;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiLlm")
public class AiLlmController extends BaseCurdController<AiLlmService, AiLlm> {

    public AiLlmController(AiLlmService service) {
        super(service);
    }

    @Autowired
    AiLlmService aiLlmService;

    @GetMapping("list")
    @SaCheckPermission("/api/v1/aiLlm/query")
    public Result list(AiLlm entity, Boolean asTree, String sortKey, String sortType) {
        return super.list(entity, asTree, sortKey, sortType);
    }

    @PostMapping("/addAiLlm")
    @SaCheckPermission("/api/v1/aiLlm/save")
    public Result addAiLlm(AiLlm entity) {
        LoginAccount account = SaTokenUtil.getLoginAccount();
        commonFiled(entity, account.getId(), account.getTenantId(), account.getDeptId());
        return aiLlmService.addAiLlm(entity);
    }


    @PostMapping("verifyLlmConfig")
    @SaCheckPermission("/api/v1/aiLlm/save")
    public Result verifyLlmConfig(@RequestBody AiLlm llm) {

        service.verifyLlmConfig(llm);

        return Result.success();
    }
}