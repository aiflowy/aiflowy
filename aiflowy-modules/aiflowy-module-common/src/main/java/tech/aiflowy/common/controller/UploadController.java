package tech.aiflowy.common.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import tech.aiflowy.common.domain.Result;

import tech.aiflowy.common.filestorage.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/commons/")
public class UploadController {

    @Resource(name = "default")
    FileStorageService storageService;

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result upload(MultipartFile file) {
        String path = storageService.save(file);
        return Result.success("path", path);
    }

    @PostMapping(value = "/uploadAntd", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result uploadAntd(MultipartFile file) {
        String path = storageService.save(file);
        Map<String,String> data = new HashMap<>();
        data.put("url", path);
        return Result.success()
                .set("status", "success")
                .set("response", data);
    }

    @PostMapping(value = "/uploadPrePath",produces = MediaType.APPLICATION_JSON_VALUE)
    @SaIgnore
    public Result uploadPrePath(MultipartFile file, String prePath) {
        System.out.println(file.getOriginalFilename());
        System.out.println(prePath);
        String path = storageService.save(file,prePath);
        return Result.success("data",path);
    }
}
