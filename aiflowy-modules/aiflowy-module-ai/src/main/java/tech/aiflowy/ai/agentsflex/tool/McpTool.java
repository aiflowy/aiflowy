package tech.aiflowy.ai.agentsflex.tool;

import com.agentsflex.core.model.chat.tool.BaseTool;
import com.agentsflex.core.model.chat.tool.Tool;
import com.agentsflex.mcp.client.McpClientManager;
import tech.aiflowy.ai.entity.Mcp;
import tech.aiflowy.ai.service.McpService;
import tech.aiflowy.ai.service.impl.McpServiceImpl;
import tech.aiflowy.common.util.SpringContextUtil;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

public class McpTool extends BaseTool {
    private BigInteger mcpId;

    @Override
    public Object invoke(Map<String, Object> argsMap) {
        return runMcp(this.mcpId, argsMap);
    }

    public Object runMcp(BigInteger mcpId, Map<String, Object> argsMap) {

        McpService mcpService = SpringContextUtil.getBean(McpService.class);
        Mcp mcp = mcpService.getMapper().selectOneById(mcpId);
        Optional<String> serverName = McpServiceImpl.getFirstMcpServerName(mcp.getConfigJson());
        if (serverName.isPresent()) {
            McpClientManager mcpClientManager = McpClientManager.getInstance();
            Tool mcpTool = mcpClientManager.getMcpTool(serverName.get(), this.name);
            Object result = mcpTool.invoke(argsMap);
            return result;
        }
        return null;
    }


    public void setMcpId(BigInteger mcpId) {
        this.mcpId = mcpId;
    }

    public BigInteger getMcpId() {
        return mcpId;
    }
}
