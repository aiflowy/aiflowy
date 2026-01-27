package tech.aiflowy.ai.service.impl;

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
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.agentsflex.tool.McpTool;
import tech.aiflowy.ai.entity.BotMcp;
import tech.aiflowy.ai.entity.Mcp;
import tech.aiflowy.ai.mapper.McpMapper;
import tech.aiflowy.ai.service.McpService;
import tech.aiflowy.ai.utils.CommonFiledUtil;
import tech.aiflowy.common.constant.enums.EnumRes;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.util.StringUtil;
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
public class McpServiceImpl extends ServiceImpl<McpMapper, Mcp>  implements McpService {
    private final McpClientManager mcpClientManager = McpClientManager.getInstance();
    protected Logger Log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Override
    public Result<?> saveMcp(Mcp entity) {
        Result<?> validateResult = validateMcpConfig(entity);
        if (!(EnumRes.SUCCESS.getCode() == validateResult.getErrorCode())) {
            return validateResult;
        }
        String serverName = getFirstMcpServerName(entity.getConfigJson());
        if (!StringUtil.hasText(serverName)) {
            return Result.fail("未找到mcp服务名称", serverName);
        }
        try {
            mcpClientManager.registerFromJson(entity.getConfigJson());
        } catch (Exception e) {
            Log.error("MCP服务名称：{} 注册失败", serverName, e);
        }
        if (entity.getStatus()) {
        try {
            getMcpClient(entity, mcpClientManager);
        } catch (Exception e) {
            Log.error("MCP服务名称：{} 启动失败", serverName, e);
            return Result.fail("MCP服务名称：" + serverName + " 启动失败", serverName);
        }
        }

        LoginAccount loginAccount = SaTokenUtil.getLoginAccount();
        CommonFiledUtil.commonFiled(entity, loginAccount.getId(), loginAccount.getTenantId(), loginAccount.getDeptId());
        this.save(entity);
        return Result.ok();
    }

    @Override
    public Result<?> updateMcp(Mcp entity) {
        Result<?> validateResult = validateMcpConfig(entity);
        if (!(EnumRes.SUCCESS.getCode() == validateResult.getErrorCode())) {
            return validateResult;
        }
        String serverName = getFirstMcpServerName(entity.getConfigJson());
        if (!StringUtil.hasText(serverName)) {
            return Result.fail("未找到mcp服务名称", serverName);
        }
        if (entity.getStatus()) {
            try {
                mcpClientManager.registerFromJson(entity.getConfigJson());
            } catch (Exception e) {
                Log.error("MCP服务名称：{} 注册失败", serverName, e);
            }
            try {
                getMcpClient(entity, mcpClientManager);
            } catch (Exception e) {
                Log.error("MCP服务名称：{} 启动失败", serverName, e);
                return Result.fail("MCP服务名称：" + serverName + " 启动失败," + "请尝试重新启动！");
            }

        } else {
            entity.setClientOnline(false);
            if (StringUtil.hasText(serverName)) {
                if (mcpClientManager.isClientOnline(serverName)) {
                    mcpClientManager.getMcpClient(serverName).close();
                }
            }
        }
        entity.setModified(new Date());
        this.updateById(entity);
        return Result.ok();
    }

    @Override
    public void removeMcp(Serializable id) {
        Mcp mcp = this.getById(id);
        if (mcp != null && mcp.getStatus()) {
            McpSyncClient mcpClient = getMcpClient(mcp, mcpClientManager);
            mcpClient.close();
        }
        this.removeById(id);
    }

    @Override
    public Result<Page<Mcp>> pageMcp(Result<Page<Mcp>> page) {
        List<Mcp> records = page.getData().getRecords();
        records.forEach(mcp -> {
            boolean clientOnline = mcpClientManager.isClientOnline(getFirstMcpServerName(mcp.getConfigJson()));
            mcp.setClientOnline(clientOnline);
                }
        );
        page.getData().setRecords(records);
        return page;
    }

    @Override
    public Mcp getMcpTools(String id) {
        Mcp mcp = this.getById(id);
        if (mcp != null && mcp.getStatus()) {
            McpSyncClient mcpClient = getMcpClient(mcp, mcpClientManager);
            List<McpSchema.Tool> tools = null;
            if (mcpClient != null) {
                tools = mcpClient.listTools().tools();
            }
            mcp.setTools(tools);
        }
        return mcp;
    }

    public static McpSyncClient getMcpClient(Mcp mcp, McpClientManager mcpClientManager) {
        String configJson = mcp.getConfigJson();
        String mcpServerName = getFirstMcpServerName(configJson);
        if (StringUtil.hasText(mcpServerName)) {
            return mcpClientManager.getMcpClient(mcpServerName);
        }
        return null;
    }

    @Override
    public Tool toFunction(BotMcp botMcp) {
        Mcp mcpInfo = this.getById(botMcp.getMcpId());
        String configJson = mcpInfo.getConfigJson();
        String mcpServerName = getFirstMcpServerName(configJson);
        if (StringUtil.hasText(mcpServerName)) {
            McpSyncClient mcpClient = mcpClientManager.getMcpClient(mcpServerName);
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

    public static Set<String> getMcpServerNames(String mcpJson) {
        JSONObject rootJson = JSON.parseObject(mcpJson);

        JSONObject mcpServersJson = rootJson.getJSONObject("mcpServers");
        if (mcpServersJson == null) {
            return Set.of();
        }

        // 提取 mcpServers 的所有键 → 即为 MCP 服务名称（如 everything）
        return mcpServersJson.keySet();
    }

    public static String getFirstMcpServerName(String mcpJson) {
        Set<String> serverNames = getMcpServerNames(mcpJson);
        Optional<String> firstServerName = serverNames.stream().findFirst();
        return firstServerName.orElse(null);
    }

    @Override
    public Page<Mcp> pageTools(Page<Mcp> page) {
        page.getRecords().forEach(mcp -> {
            // mcp 未启用，不查询工具
            if (!mcp.getStatus()) {
                return;
            }
            String configJson = mcp.getConfigJson();
            String serverName = getFirstMcpServerName(configJson);
            if (StringUtil.hasText(serverName)) {
                mcpClientManager.registerFromJson(configJson);
                try {
                    McpSyncClient mcpClient = mcpClientManager.getMcpClient(serverName);
                    List<McpSchema.Tool> tools = mcpClient.listTools().tools();
                    mcp.setTools(tools);
                } catch (Exception e) {
                    Log.error("MCP服务名称：{} 启动失败", serverName, e);
                }

            } else {
                throw new BusinessException("MCP 配置 JSON 中没有找到任何 MCP 服务名称");
            }
        });
        return page;
    }

    private Result<?> validateMcpConfig(Mcp entity) {
        if (entity == null || !StringUtil.hasText(entity.getConfigJson())) {
            Log.error("MCP 配置不能为空");
            return Result.fail("MCP 配置 JSON 不能为空");
        }
        return Result.ok();
    }
}
