package tech.aiflowy.system.controller;

import tech.aiflowy.common.domain.Result;

import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.system.entity.SysPosition;
import tech.aiflowy.system.service.SysPositionService;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 职位表 控制层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@RestController("sysPositionController")
@RequestMapping("/api/v1/sysPosition")
public class SysPositionController extends BaseCurdController<SysPositionService, SysPosition> {
    public SysPositionController(SysPositionService service) {
        super(service);
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysPosition entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), entity.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return null;
    }
}