package tech.aiflowy.admin.controller.system;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.entity.SysPosition;
import tech.aiflowy.system.service.SysPositionService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

import static tech.aiflowy.system.entity.table.SysPositionTableDef.SYS_POSITION;

/**
 * 职位表 控制层。
 * <p>
 * 提供岗位的增删改查及状态管理功能。
 * </p>
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

    /**
     * 分页查询岗位列表
     * <p>
     * 支持按岗位名称模糊查询，按状态、编码精确查询。
     * </p>
     *
     * @param request    请求对象
     * @param sortKey    排序字段
     * @param sortType   排序类型 (asc/desc)
     * @param pageNumber 当前页码
     * @param pageSize   每页条数
     * @return 分页结果
     */
    @Override
    @GetMapping("page")
    public Result<Page<SysPosition>> page(HttpServletRequest request, String sortKey, String sortType, Long pageNumber, Long pageSize) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10L;
        }

        // 构建自定义查询条件
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(SYS_POSITION.ALL_COLUMNS)
                .from(SYS_POSITION);

        // 获取查询参数
        String positionName = request.getParameter("positionName");
        String positionCode = request.getParameter("positionCode");
        String status = request.getParameter("status");

        // 岗位名称 - 模糊查询
        if (StringUtil.hasText(positionName)) {
            queryWrapper.where(SYS_POSITION.POSITION_NAME.like(positionName));
        }
        // 岗位编码 - 精确查询
        if (StringUtil.hasText(positionCode)) {
            queryWrapper.where(SYS_POSITION.POSITION_CODE.eq(positionCode));
        }
        // 状态 - 精确查询
        if (StringUtil.hasText(status)) {
            queryWrapper.where(SYS_POSITION.STATUS.eq(status));
        }

        // 处理排序
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));

        return Result.ok(service.page(new Page<>(pageNumber, pageSize), queryWrapper));
    }

    /**
     * 修改岗位状态（启用/禁用）
     *
     * @param body 包含 id 和 status 的 JSON 对象
     * @return 操作结果
     */
    @PostMapping("changeStatus")
    public Result<?> changeStatus(@RequestBody Map<String, Object> body) {
        String idStr = (String) body.get("id");
        Integer status = (Integer) body.get("status");

        if (StringUtil.noText(idStr) || status == null) {
            return Result.fail("参数错误：id 和 status 不能为空");
        }

        SysPosition position = new SysPosition();
        position.setId(new java.math.BigInteger(idStr));
        position.setStatus(status);
        
        // 设置修改信息
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        position.setModified(new Date());
        position.setModifiedBy(loginUser.getId());

        boolean success = service.updateById(position);
        return success ? Result.ok() : Result.fail("状态修改失败");
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysPosition entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), entity.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return null;
    }
}