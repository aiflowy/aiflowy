package tech.aiflowy.common.controller;

import tech.aiflowy.common.domain.Result;

import tech.aiflowy.common.filestorage.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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


}
