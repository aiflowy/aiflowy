package tech.aiflowy.ai.config;

import com.agentsflex.mcp.client.McpClientManager;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class McpClientAutoConfig {

    @PostConstruct
    public void initMcpClient() {
        McpClientManager.getInstance();
    }

    @PreDestroy
    public void closeMcpClient() {
        McpClientManager.getInstance().close();
    }
}