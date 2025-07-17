package tech.aiflowy.ai.node;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.Parameter;
import com.agentsflex.core.chain.node.BaseNode;
import com.agentsflex.core.util.StringUtil;
import com.mybatisflex.core.tenant.TenantManager;
import tech.aiflowy.ai.entity.AiResource;
import tech.aiflowy.ai.service.AiResourceService;
import tech.aiflowy.ai.utils.DocUtil;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.constant.enums.EnumResourceOriginType;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.filestorage.FileStorageManager;
import tech.aiflowy.common.filestorage.FileStorageService;
import tech.aiflowy.common.util.SpringContextUtil;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadNode extends BaseNode {

    private Integer resourceType;

    public DownloadNode() {
    }

    public DownloadNode(Integer resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    protected Map<String, Object> execute(Chain chain) {
        Map<String, Object> map = chain.getParameterValues(this);
        Map<String, Object> res = new HashMap<>();

        String originUrl = map.get("originUrl").toString();

        byte[] bytes = DocUtil.downloadFile(originUrl);

        String suffix = FileTypeUtil.getType(new ByteArrayInputStream(bytes));

        if (suffix == null) {
            suffix = "unknown";
        }

        String fileName = IdUtil.simpleUUID() + "." + suffix;

        FileStorageManager manager = SpringContextUtil.getBean(FileStorageManager.class);

        String resourceUrl = manager.save(new CustomFile(fileName, bytes));

        AiResource resource = new AiResource();

        // 默认为未知来源
        LoginAccount account = defaultAccount();
        Object cache = chain.getMemory().get(Constants.LOGIN_USER_KEY);
        if (cache != null) {
            account = (LoginAccount) cache;
        }

        resource.setDeptId(account.getDeptId());
        resource.setTenantId(account.getTenantId());
        resource.setResourceType(this.resourceType);
        resource.setResourceName(DocUtil.getFileNameByUrl(resourceUrl).split("\\.")[0]);
        resource.setSuffix(suffix);
        resource.setResourceUrl(resourceUrl);
        resource.setOrigin(EnumResourceOriginType.GENERATE.getCode());
        resource.setCreated(new Date());
        resource.setCreatedBy(account.getId());
        resource.setModified(new Date());
        resource.setModifiedBy(account.getId());
        try {
            TenantManager.ignoreTenantCondition();
            AiResourceService service = SpringContextUtil.getBean(AiResourceService.class);
            service.save(resource);
        } finally {
            TenantManager.restoreTenantCondition();
        }

        String key = "resourceUrl";
        List<Parameter> outputDefs = getOutputDefs();
        if (outputDefs != null && !outputDefs.isEmpty()) {
            String defName = outputDefs.get(0).getName();
            if (StringUtil.hasText(defName)) key = defName;
        }
        res.put(key, resourceUrl);
        return res;
    }

    private LoginAccount defaultAccount() {
        LoginAccount account = new LoginAccount();
        account.setId(new BigInteger("0"));
        account.setDeptId(new BigInteger("0"));
        account.setTenantId(new BigInteger("0"));
        return account;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }
}
