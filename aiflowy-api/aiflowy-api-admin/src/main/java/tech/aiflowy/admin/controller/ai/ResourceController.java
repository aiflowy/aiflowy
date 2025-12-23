package tech.aiflowy.admin.controller.ai;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.Resource;
import tech.aiflowy.ai.service.ResourceService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Date;

/**
 * 素材库
 *
 * @author ArkLight
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/api/v1/aiResource")
public class ResourceController extends BaseCurdController<ResourceService, Resource> {
    public ResourceController(ResourceService service) {
        super(service);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(Resource entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            String resourceUrl = entity.getResourceUrl();
            byte[] bytes = HttpUtil.downloadBytes(resourceUrl);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            String suffix = FileTypeUtil.getType(stream, resourceUrl);
            entity.setSuffix(suffix);
            entity.setFileSize(BigInteger.valueOf(bytes.length));
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }
}