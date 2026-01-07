package tech.aiflowy.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.entity.SysAccountPosition;
import tech.aiflowy.system.entity.SysAccountRole;
import tech.aiflowy.system.mapper.SysAccountMapper;
import tech.aiflowy.system.mapper.SysAccountPositionMapper;
import tech.aiflowy.system.mapper.SysAccountRoleMapper;
import tech.aiflowy.system.mapper.SysRoleMapper;
import tech.aiflowy.system.service.SysAccountService;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户表 服务层实现。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@Service
public class SysAccountServiceImpl extends ServiceImpl<SysAccountMapper, SysAccount> implements SysAccountService {

    @Resource
    private SysAccountRoleMapper sysAccountRoleMapper;
    @Resource
    private SysAccountPositionMapper sysAccountPositionMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public void syncRelations(SysAccount entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }
        //sync roleIds
        List<BigInteger> roleIds = entity.getRoleIds();
        if (roleIds != null) {
            QueryWrapper delW = QueryWrapper.create();
            delW.eq(SysAccountRole::getAccountId, entity.getId());
            sysAccountRoleMapper.deleteByQuery(delW);
            if (!roleIds.isEmpty()) {
                List<SysAccountRole> rows = new ArrayList<>(roleIds.size());
                roleIds.forEach(roleId -> {
                    SysAccountRole row = new SysAccountRole();
                    row.setAccountId(entity.getId());
                    row.setRoleId(roleId);
                    rows.add(row);
                });
                sysAccountRoleMapper.insertBatch(rows);
            }
        }

        //sync positionIds
        List<BigInteger> positionIds = entity.getPositionIds();
        if (positionIds != null) {
            QueryWrapper delW = QueryWrapper.create();
            delW.eq(SysAccountPosition::getAccountId, entity.getId());
            sysAccountPositionMapper.deleteByQuery(delW);
            if (!positionIds.isEmpty()) {
                List<SysAccountPosition> rows = new ArrayList<>(positionIds.size());
                positionIds.forEach(positionId -> {
                    SysAccountPosition row = new SysAccountPosition();
                    row.setAccountId(entity.getId());
                    row.setPositionId(positionId);
                    rows.add(row);
                });
                sysAccountPositionMapper.insertBatch(rows);
            }
        }
    }

    @Override
    public SysAccount getByUsername(String userKey) {
        QueryWrapper w = QueryWrapper.create();
        w.eq(SysAccount::getLoginName, userKey);
        return getOne(w);
    }
}
