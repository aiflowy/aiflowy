package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.model.chat.tool.Parameter;
import com.agentsflex.core.model.chat.tool.Tool;
import com.agentsflex.mcp.client.McpClientManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.ai.agentsflex.tool.McpTool;
import tech.aiflowy.ai.entity.BotMcp;
import tech.aiflowy.ai.entity.Mcp;
import tech.aiflowy.ai.mapper.McpMapper;
import tech.aiflowy.ai.service.McpService;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.utils.CommonFiledUtil;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.io.Serializable;
import java.util.*;

/**
 *  服务层实现。
 *
 * @author wangGangQiang
 * @since 2026-01-04
 */
@Service
public class McpServiceImpl extends ServiceImpl<McpMapper, Mcp>  implements McpService{
    private final McpClientManager mcpClientManager = McpClientManager.getInstance();
    protected Logger Log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Override
    public void saveMcp(Mcp entity) {

        if (entity == null || entity.getConfigJson() == null || entity.getConfigJson().trim().isEmpty()) {
            Log.error("MCP 配置不能为空");
            throw new BusinessException("MCP 配置 JSON 不能为空");
        }
        mcpClientManager.registerFromJson(entity.getConfigJson());
        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        CommonFiledUtil.commonFiled(entity, loginAccount.getId(), loginAccount.getTenantId(), loginAccount.getDeptId());
        this.save(entity);
    }

    @Override
    public void updateMcp(Mcp entity) {
        if (entity == null || entity.getConfigJson() == null || entity.getConfigJson().trim().isEmpty()) {
            Log.error("MCP 配置不能为空");
            throw new BusinessException("MCP 配置 JSON 不能为空");
        }
        if (entity.getStatus()) {
            mcpClientManager.registerFromJson(entity.getConfigJson());
        } else {
            Optional<String> serverName = getFirstMcpServerName(entity.getConfigJson());
            if (serverName.isPresent()) {
                try {
                    McpSyncClient mcpClient = mcpClientManager.getMcpClient(serverName.get());
                    mcpClient.close();
                } catch (Exception e) {
                    Log.error("MCP 服务器关闭失败", e);
                }

            }
        }
        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        CommonFiledUtil.commonFiled(entity, loginAccount.getId(), loginAccount.getTenantId(), loginAccount.getDeptId());
        this.updateById(entity);
    }

    @Override
    public void removeMcp(Serializable id) {
        Mcp mcp = this.getById(id);
        if (mcp != null && mcp.getStatus()) {
            Optional<String> serverName = getFirstMcpServerName(mcp.getConfigJson());
            if (serverName.isPresent()) {
                McpSyncClient mcpClient = mcpClientManager.getMcpClient(serverName.get());
                mcpClient.close();
            }
        }
        this.removeById(id);
    }

    @Override
    public Tool toFunction(BotMcp botMcp) {
        Mcp mcpInfo = this.getById(botMcp.getMcpId());
        String configJson = mcpInfo.getConfigJson();
        Optional<String> mcpServerName = getFirstMcpServerName(configJson);
        if (mcpServerName.isPresent()) {
            // 获取mcp 服务名称
            String serverName = mcpServerName.get();
            McpSyncClient mcpClient = mcpClientManager.getMcpClient(serverName);
            List<McpSchema.Tool> tools = mcpClient.listTools().tools();
            for (McpSchema.Tool tool : tools) {
                if (tool.name().equals(botMcp.getMcpToolName())) {
                    Map<String, Object> properties = tool.inputSchema().properties();
                    List<String> required = tool.inputSchema().required();
                    McpTool mcpTool = new McpTool();
                    mcpTool.setName(tool.name());
                    mcpTool.setDescription(tool.description());
                    List<Parameter> paramList = new ArrayList<>();
                    Set<String> keySet = properties.keySet();
                    keySet.forEach(key -> {
                        Parameter parameter = new Parameter();
                        parameter.setName(key);
                        LinkedHashMap params = (LinkedHashMap) properties.get(key);
                        Set<Object> paramsKeySet = params.keySet();
                        paramsKeySet.forEach(paramsKey -> {
                            if (paramsKey.equals("type")) {
                                parameter.setType((String) params.get(paramsKey));
                            } else if (paramsKey.equals("description")) {
                                parameter.setDescription((String) params.get(paramsKey));
                            }
                        });
                        paramList.add(parameter);
                        Parameter[] parametersArr = paramList.toArray(new Parameter[properties.size()]);
                        mcpTool.setParameters(parametersArr);
                    });
                    mcpTool.setMcpId(mcpInfo.getId());
                    return mcpTool;
                }
            }
        }
        return null;
    }

    @Override
    public Result<Page<Mcp>> pageMcpTools(Result<Page<Mcp>> page) {
        page.getData().getRecords().forEach(mcp -> {
            // mcp 未启用，不查询工具
                if (!mcp.getStatus()) {
                return;
            }
            String configJson = mcp.getConfigJson();
            Optional<String> firstServerName = getFirstMcpServerName(configJson);
            if (firstServerName.isPresent()) {
                String serverName = firstServerName.get();
                mcpClientManager.registerFromJson(configJson);
                McpSyncClient mcpClient = mcpClientManager.getMcpClient(serverName);
                List<McpSchema.Tool> tools = mcpClient.listTools().tools();
                mcp.setTools(tools);
            } else {
                throw new BusinessException("MCP 配置 JSON 中没有找到任何 MCP 服务名称");
            }
        });
        return page;
    }

    public static Set<String> getMcpServerNames(String mcpJson) {
        JSONObject rootJson = JSON.parseObject(mcpJson);

        JSONObject mcpServersJson = rootJson.getJSONObject("mcpServers");
        if (mcpServersJson == null) {
            return Set.of();
        }

        // 提取 mcpServers 的所有键 → 即为 MCP 服务名称（如 everything）
        return mcpServersJson.keySet();
    }

    public static Optional<String> getFirstMcpServerName(String mcpJson) {
        Set<String> serverNames = getMcpServerNames(mcpJson);
        return serverNames.stream().findFirst();
    }

}
