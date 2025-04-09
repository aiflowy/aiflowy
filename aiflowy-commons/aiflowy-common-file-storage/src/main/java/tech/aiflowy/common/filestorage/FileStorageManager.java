package tech.aiflowy.common.filestorage;

import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.common.filestorage.impl.LocalFileStorageServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component("default")
public class FileStorageManager implements FileStorageService {

    @Override
    public String save(MultipartFile file) {
        return getService().save(file);
    }

    @Override
    public InputStream readStream(String path) throws IOException {
        return getService().readStream(path);
    }

    private FileStorageService getService() {
        String type = StorageConfig.getInstance().getType();
        if (!StringUtils.hasText(type)) {
            return SpringContextUtil.getBean(LocalFileStorageServiceImpl.class);
        } else {
            return SpringContextUtil.getBean(type);
        }
    }
}
