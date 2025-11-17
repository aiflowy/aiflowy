package tech.aiflowy.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.ai.util.UUIDGenerator;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.common.vo.PkVo;
import tech.aiflowy.system.service.SysApiKeyService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2025-04-18
 */
@RestController
@RequestMapping("/api/v1/sysApiKey")
public class SysApiKeyController extends BaseCurdController<SysApiKeyService, SysApiKey> {
    public SysApiKeyController(SysApiKeyService service) {
        super(service);
    }

    /**
     * 添加（保存）数据
     *
     * @return {@code Result.errorCode == 0} 添加成功，否则添加失败
     */
    @PostMapping("/key/save")
    @SaCheckPermission("/api/v1/sysApiKey/save")
    public Result<PkVo> save() {
        String apiKey = UUIDGenerator.generateUUID();
        SysApiKey entity = new SysApiKey();
        entity.setApiKey(apiKey);
        entity.setCreated(new Date());
        entity.setStatus(1);
        // 将Date转换为LocalDate
        LocalDate localDate = new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 添加30天
        LocalDate newLocalDate = localDate.plusDays(30);
        // 转换回Date
        Date expireDate =  Date.from(newLocalDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        entity.setExpiredAt(expireDate);
        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        commonFiled(entity,loginAccount.getId(),loginAccount.getTenantId(),loginAccount.getDeptId());
        service.save(entity);
        onSaveOrUpdateAfter(entity, true);
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entity.getClass());
        Object[] pkArgs = tableInfo.buildPkSqlArgs(entity);
        return Result.ok(new PkVo(pkArgs));
    }
}