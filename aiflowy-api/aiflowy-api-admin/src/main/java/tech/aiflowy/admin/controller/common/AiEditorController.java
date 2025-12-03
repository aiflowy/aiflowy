//package tech.aiflowy.admin.controller.common;
//
//import tech.aiflowy.common.ai.ChatManager;
//import tech.aiflowy.common.ai.FunctionsManager;
//import tech.aiflowy.common.domain.Result;
//import tech.aiflowy.common.web.jsonbody.JsonBody;
//import tech.aiflowy.common.filestorage.FileStorageService;
//import com.agentsflex.core.llm.functions.Function;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletResponse;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/aieditor/")
//public class AiEditorController {
//
//    @Resource(name = "default")
//    FileStorageService storageService;
//
//    @PostMapping("chat")
//    public SseEmitter chat(@JsonBody("prompt") String prompt, HttpServletResponse response) {
//        response.setContentType("text/event-stream");
//        List<Function> functions = FunctionsManager.getInstance().getFunctions(prompt);
//        if (functions != null && !functions.isEmpty()) {
//            return FunctionsManager.getInstance().call(prompt, functions);
//        }
//        return ChatManager.getInstance().sseEmitter(prompt);
//    }
//
//
//    @PostMapping(value = "/upload/image", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Result<?> uploadImage(@RequestParam("image") MultipartFile file) {
//        String path = storageService.save(file);
//        Map<String, Object> data = new HashMap<>();
//        data.put("src", path);
//        data.put("alt", file.getName());
//
//        return Result.ok(data);
//    }
//
//
//    @PostMapping(value = "/upload/video", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Result<?> uploadVideo(@RequestParam("video") MultipartFile file) {
//        String path = storageService.save(file);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("src", path);
//        data.put("poster", null);
//
//        return Result.ok(data);
//    }
//
//
//    @PostMapping(value = "/upload/attachment", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Result<?> uploadAttachment(@RequestParam("attachment") MultipartFile file) {
//        String path = storageService.save(file);
//        Map<String, Object> data = new HashMap<>();
//        data.put("href", path);
//        data.put("fileName", file.getName());
//
//        return Result.ok(data);
//    }
//
//
//}
