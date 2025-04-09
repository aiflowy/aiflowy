package tech.aiflowy.system.controller;

import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.log.annotation.LogRecord;
import tech.aiflowy.system.entity.SysLog;
import tech.aiflowy.system.service.SysLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.relation.RelationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 操作日志表 控制层。
 *
 * @author michael
 * @since 2024-03-06
 */
@RestController
@RequestMapping("/api/v1/sysLog")
public class SysLogController extends BaseCurdController<SysLogService, SysLog> {
    public SysLogController(SysLogService service) {
        super(service);
    }

    @Override
    @LogRecord("分页查询")
    protected Page<SysLog> queryPage(Page<SysLog> page, QueryWrapper queryWrapper) {
        RelationManager.setQueryRelations(Collections.singleton("account"));
        return service.getMapper().paginateWithRelations(page, queryWrapper);
    }
}