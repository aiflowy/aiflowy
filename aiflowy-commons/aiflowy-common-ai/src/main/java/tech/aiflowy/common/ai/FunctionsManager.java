package tech.aiflowy.common.ai;

import tech.aiflowy.common.ai.annotation.FunctionModuleDef;
import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.common.util.StringUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.llm.functions.JavaNativeFunctions;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.prompt.FunctionPrompt;
import com.mybatisflex.core.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class FunctionsManager {
    private static final Logger logger = LoggerFactory.getLogger(FunctionsManager.class);
    private ExecutorService sseExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Map<String, FunctionModule> functionModules = new HashMap<>();


    @EventListener(ApplicationReadyEvent.class)
    public void initFunctionModules() {
        Map<String, Object> functionModuleBeanMap = SpringContextUtil.getBeansWithAnnotation(FunctionModuleDef.class);
        for (Object functionModuleBean : functionModuleBeanMap.values()) {
            Class<?> usefulClass = ClassUtil.getUsefulClass(functionModuleBean.getClass());
            FunctionModuleDef annotation = usefulClass.getAnnotation(FunctionModuleDef.class);

            FunctionModule functionModule = new FunctionModule();
            functionModule.setName(annotation.name());
            functionModule.setTitle(annotation.title());
            functionModule.setDescription(annotation.description());
            functionModule.setFunctionsObject(functionModuleBean);

            functionModules.put(functionModule.getName(), functionModule);
        }
    }


    public List<Function> getFunctions(String userPrompt, String... names) {
        StringBuilder prompt = new StringBuilder("我的系统包含了如下的模块：\n");
        functionModules.forEach((s, functionModule) ->
                prompt.append("- **").append(functionModule.getTitle()).append("**: ").append(functionModule.getDescription()).append("\n")
        );
        prompt.append("\n请根据如下的用户内容，帮我分析出该用户想了解关于哪个模块的信息，并直接告诉我模块的名称，不用做任何的解释，也不需要除了模块名称以外其他的文字内容或信息。如果没有匹配任何模块，请直接告知我：无模块匹配。");
        prompt.append("\n\n用户的提问内容是：\n").append(userPrompt);


        String result = ChatManager.getInstance().chat(prompt.toString());

        if (!StringUtil.hasText(result)) {
            return null;
        }

        FunctionModule functionModule = null;
        for (FunctionModule module : functionModules.values()) {
            if (StringUtil.hasText(module.getTitle()) && result.contains(module.getTitle())) {
                functionModule = module;
                break;
            }
        }

        if (functionModule == null) {
            return null;
        }

        return JavaNativeFunctions.from(functionModule.getFunctionsObject());
    }

    public SseEmitter call(String prompt, List<Function> functions) {
        return call(prompt, functions, null);
    }

    public SseEmitter call(String prompt, List<Function> functions, ChatMemory memory) {
        MySseEmitter emitter = new MySseEmitter(20000L);
        sseExecutor.execute(() -> {
            Llm llm = ChatManager.getInstance().getChatLlm();
            if (llm == null) {
                emitter.sendAndComplete("AI 大模型未配置正确");
                return;
            }
            FunctionPrompt functionPrompt = new FunctionPrompt(prompt, functions);
            AiMessageResponse response = llm.chat(functionPrompt);


            if (response.getMessage() == null) {
                llm.chatStream(prompt, new StreamResponseListener() {
                    @Override
                    public void onMessage(ChatContext chatContext, AiMessageResponse aiMessageResponse) {
                        String content = aiMessageResponse.getMessage().getContent();
                        System.out.println(">>>>response: " + content);
                        emitter.send(content);
                    }

                    @Override
                    public void onStop(ChatContext context) {
                        emitter.complete();
                    }
                });
            } else {
                Object result = response.callFunctions();
                memory.addMessage(response.getMessage());
                emitter.sendAndComplete(result + " \n");
            }
        });
        return emitter;
    }


    public static FunctionsManager getInstance() {
        return SpringContextUtil.getBean(FunctionsManager.class);
    }

}
