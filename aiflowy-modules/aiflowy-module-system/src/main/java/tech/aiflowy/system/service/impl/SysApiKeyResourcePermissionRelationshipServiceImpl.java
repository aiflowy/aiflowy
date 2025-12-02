package tech.aiflowy.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.entity.SysApiKeyResourcePermissionRelationship;
import tech.aiflowy.system.mapper.SysApiKeyResourcePermissionRelationshipMapper;
import tech.aiflowy.system.service.SysApiKeyResourcePermissionRelationshipService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * apikey-请求接口表 服务层实现。
 *
 * @author 12076
 * @since 2025-12-01
 */
@Service
public class SysApiKeyResourcePermissionRelationshipServiceImpl extends ServiceImpl<SysApiKeyResourcePermissionRelationshipMapper, SysApiKeyResourcePermissionRelationship>  implements SysApiKeyResourcePermissionRelationshipService {

    /**
     * 批量授权apiKey接口
     * @param entity
     */
    @Override
    public void authInterface(SysApiKey entity) {
        this.remove(QueryWrapper.create().eq(SysApiKeyResourcePermissionRelationship::getApiKeyId, entity.getId()));
        List<SysApiKeyResourcePermissionRelationship> rows = new ArrayList<>(entity.getPermissionIds().size());
        BigInteger apiKeyId = BigInteger.valueOf(entity.getId());
        for (BigInteger resourceId : entity.getPermissionIds()) {
            SysApiKeyResourcePermissionRelationship sysApiKeyResourcePermissionRelationship = new SysApiKeyResourcePermissionRelationship();
            sysApiKeyResourcePermissionRelationship.setApiKeyId(apiKeyId);
            sysApiKeyResourcePermissionRelationship.setApiKeyResourceId(resourceId);
            rows.add(sysApiKeyResourcePermissionRelationship);
        }
        this.saveBatch(rows);
    }
}
