package tech.aiflowy.ai.controller;

import tech.aiflowy.ai.service.AiOllamaService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 *  控制层
 *
 * @author wangGangQiang
 * @since 2025-03-13
 */
@RequestMapping("/api/v1/ollama")
@RestController
public class OllamaController {

    @Autowired
    AiOllamaService ollamaService;



    /**
     * 获取大模型列表
     * @param modelName
     * @return
     */
    @RequestMapping("/list")
    public Result getLargeModels(@RequestParam (name="apiUrl",required = false) String modelName,
                                 @RequestParam (name="current",required = false) Integer current,
                                 @RequestParam (name="pageSize",required = false) Integer pageSize
                                  )
    {
       return ollamaService.getLargeModels(modelName,current,pageSize);

    }


    /**
     * 查询ollama提供的所有的可安装的大模型
     * @return
     */
    @RequestMapping("/models/list")
    public Result getOllamaModels()
    {
        return ollamaService.getOllamaModels();

    }

    /**
     * 指定服务器地址安装大模型
     * @param modelApiUrl ollama所在的服务器格式为 ip:port
     * @param modelName 大模型的名称
     * @return
     */
    @RequestMapping(value = "/installModel", produces = "text/event-stream")
    public SseEmitter installModel(@RequestParam(name="modelApiUrl", required = false) String modelApiUrl,
                                   @JsonBody("modelName") String modelName
                                   ) {

        return ollamaService.installModel(modelApiUrl, modelName);
    }

    /**
     * 指定服务器地址删除大模型
     * @param modelApiUrl ollama所在的服务器格式为 ip:port
     * @param modelName 大模型的名称
     * @return
     */
    @RequestMapping(value = "/deleteModel")
    public Result deleteModel(@RequestParam(name="modelApiUrl", required = false) String modelApiUrl,
                                   @RequestParam(name="modelName") String modelName
    ) {
        boolean success = ollamaService.deleteModel(modelApiUrl, modelName);
        if (success) {
            return Result.success(modelName);
        } else {
            return Result.fail();
        }
    }
}
