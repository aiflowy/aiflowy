package tech.aiflowy.system.service.impl;

import tech.aiflowy.common.util.Maps;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.mapper.SysAccountMapper;
import tech.aiflowy.system.service.SysAccountService;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

    @Override
    public void syncRelations(SysAccount entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }

        SnowFlakeIDKeyGenerator generator = new SnowFlakeIDKeyGenerator();

        //sync roleIds
        List<BigInteger> roleIds = entity.getRoleIds();
        if (roleIds != null) {
            Db.deleteByMap("tb_sys_account_role", Maps.of("account_id", entity.getId()));
            if (!roleIds.isEmpty()) {
                List<Row> rows = new ArrayList<>(roleIds.size());
                roleIds.forEach(roleId -> {
                    Row row = new Row();
                    row.set("id", generator.nextId());
                    row.set("account_id", entity.getId());
                    row.set("role_id", roleId);
                    rows.add(row);
                });
                Db.insertBatch("tb_sys_account_role", rows);
            }
        }

        //sync positionIds
        List<BigInteger> positionIds = entity.getPositionIds();
        if (positionIds != null) {
            Db.deleteByMap("tb_sys_account_position", Maps.of("account_id", entity.getId()));
            if (!positionIds.isEmpty()) {
                List<Row> rows = new ArrayList<>(positionIds.size());
                positionIds.forEach(positionId -> {
                    Row row = new Row();
                    row.set("id", generator.nextId());
                    row.set("account_id", entity.getId());
                    row.set("position_id", positionId);
                    rows.add(row);
                });
                Db.insertBatch("tb_sys_account_position", rows);
            }
        }
    }
}
