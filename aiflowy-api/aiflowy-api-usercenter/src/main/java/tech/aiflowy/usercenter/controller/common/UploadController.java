package tech.aiflowy.usercenter.controller.common;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.vo.UploadResVo;
import tech.aiflowy.common.filestorage.FileStorageService;

import javax.annotation.Resource;

/**
 * 文件上传
 */
@RestController
@RequestMapping("/api/v1/commons/")
public class UploadController {

    @Resource(name = "default")
    private FileStorageService storageService;

    /**
     * 上传
     */
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UploadResVo> upload(MultipartFile file) {
        String path = storageService.save(file);
        UploadResVo resVo = new UploadResVo();
        resVo.setPath(path);
        return Result.ok(resVo);
    }

    /**
     * @ignore
     */
    @PostMapping(value = "/uploadAntd", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UploadResVo> uploadAntd(MultipartFile file) {
        String path = storageService.save(file);
        UploadResVo resVo = new UploadResVo();
        resVo.setPath(path);
        return Result.ok(resVo);
    }

    /**
     * @ignore
     */
    @PostMapping(value = "/uploadPrePath",produces = MediaType.APPLICATION_JSON_VALUE)
    @SaIgnore
    public Result<UploadResVo> uploadPrePath(MultipartFile file, String prePath) {
        String path = storageService.save(file,prePath);
        UploadResVo resVo = new UploadResVo();
        resVo.setPath(path);
        return Result.ok(resVo);
    }
}
