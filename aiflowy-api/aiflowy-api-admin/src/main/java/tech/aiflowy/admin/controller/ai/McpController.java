package tech.aiflowy.admin.controller.ai;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.BotMcp;
import tech.aiflowy.ai.entity.Mcp;
import tech.aiflowy.ai.service.BotMcpService;
import tech.aiflowy.ai.service.McpService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 *  控制层。
 *
 * @author wangGangQiang
 * @since 2026-01-04
 */
@RestController
@RequestMapping("/api/v1/mcp")
public class McpController extends BaseCurdController<McpService, Mcp> {
    public McpController(McpService service) {
        super(service);
    }

    @Resource
    private BotMcpService botMcpService;
    @Override
    public Result<?> save(Mcp entity) {
        service.saveMcp(entity);
        return Result.ok();
    }

    @Override
    public Result<?> update(Mcp entity) {
        service.updateMcp(entity);
        return Result.ok();
    }

    @Override
    @Transactional
    public Result<?> remove(Serializable id) {
        service.removeMcp(id);
        botMcpService.remove(QueryWrapper.create().eq(BotMcp::getMcpId, id));
        return Result.ok();
    }

    @Override
    public Result<Page<Mcp>> page(HttpServletRequest request, String sortKey, String sortType, Long pageNumber, Long pageSize) {
        Result<Page<Mcp>> page = super.page(request, sortKey, sortType, pageNumber, pageSize);
        return service.pageMcpTools(page);
    }
}