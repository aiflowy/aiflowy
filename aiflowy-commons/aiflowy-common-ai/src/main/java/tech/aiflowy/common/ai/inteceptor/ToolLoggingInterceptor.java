package tech.aiflowy.common.ai.inteceptor;

import com.agentsflex.core.model.chat.tool.GlobalToolInterceptors;
import com.agentsflex.core.model.chat.tool.ToolChain;
import com.agentsflex.core.model.chat.tool.ToolContext;
import com.agentsflex.core.model.chat.tool.ToolInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
@Component
public class ToolLoggingInterceptor implements ToolInterceptor {

    @PostConstruct
    public void init() {
        GlobalToolInterceptors.addInterceptor(this);
    }

    @Override
    public Object intercept(ToolContext context, ToolChain chain) throws Exception {
        String toolName = context.getTool().getName();
        Map<String, Object> args = context.getArgsMap();

        System.out.println("▶ 调用工具: " + toolName + ", 参数: " + args);

        long start = System.currentTimeMillis();
        try {
            Object result = chain.proceed(context);
            System.out.println("✅ 工具返回: " + result);
            return result;
        } finally {
            long duration = System.currentTimeMillis() - start;
            System.out.println("⏱️ 耗时: " + duration + "ms");
        }
    }

}
