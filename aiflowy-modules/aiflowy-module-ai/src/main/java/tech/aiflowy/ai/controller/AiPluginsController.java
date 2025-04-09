package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.entity.AiBotPlugins;
import tech.aiflowy.ai.entity.AiPlugins;
import tech.aiflowy.ai.service.AiBotPluginsService;
import tech.aiflowy.ai.service.AiPluginsService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * 插件 控制层。
 *
 * @author ArkLight
 * @since 2025-04-01
 */
@RestController
@RequestMapping("/api/v1/aiPlugins")
public class AiPluginsController extends BaseCurdController<AiPluginsService, AiPlugins> {

    public AiPluginsController(AiPluginsService service) {
        super(service);
    }

    @Resource
    private AiBotPluginsService aiBotPluginsService;

    @Override
    protected Result onSaveOrUpdateBefore(AiPlugins entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @Override
    protected void onRemoveAfter(Collection<Serializable> ids) {
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.in(AiBotPlugins::getPluginId, ids);
        aiBotPluginsService.remove(wrapper);
    }
}