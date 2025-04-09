package tech.aiflowy.common.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileStorageService {


    String save(MultipartFile file);

    InputStream readStream(String path) throws IOException;
}
