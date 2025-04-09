package tech.aiflowy.system.controller;

import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.tree.Tree;

import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.entity.SysDept;
import tech.aiflowy.system.service.SysAccountService;
import tech.aiflowy.system.service.SysDeptService;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 部门表 控制层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@RestController("sysDeptController")
@RequestMapping("/api/v1/sysDept")
public class SysDeptController extends BaseCurdController<SysDeptService, SysDept> {

    @Resource
    private SysAccountService sysAccountService;

    public SysDeptController(SysDeptService service) {
        super(service);
    }

    @Override
    protected String getDefaultOrderBy() {
        return "sort_no asc";
    }

    @Override
    @GetMapping("list")
    public Result list(SysDept entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<SysDept> sysMenus = service.list(queryWrapper);
        return Result.success(Tree.tryToTree(sysMenus, "id", "parentId"));
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysDept entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        BigInteger parentId = entity.getParentId();
        if (parentId.equals(BigInteger.ZERO)) {
            entity.setAncestors(parentId.toString());
        } else {
            SysDept parent = service.getById(parentId);
            entity.setAncestors(parent.getAncestors() + "," + parentId);
        }
        if (isSave) {
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return null;
    }

    @Override
    protected Result onRemoveBefore(Collection<Serializable> ids) {
        List<SysDept> records = service.listByIds(ids);
        for (SysDept dept : records) {
            if (Constants.ROOT_DEPT.equals(dept.getDeptCode())) {
                return Result.fail(1, "无法删除根部门");
            }
        }
        QueryWrapper w = QueryWrapper.create();
        w.in(SysAccount::getDeptId, ids);
        long count = sysAccountService.count(w);
        if (count > 0) {
            return Result.fail(1, "该部门下有员工，不能删除");
        }
        return super.onRemoveBefore(ids);
    }
}