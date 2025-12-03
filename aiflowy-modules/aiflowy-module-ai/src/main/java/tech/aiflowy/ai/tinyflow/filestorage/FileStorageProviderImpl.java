package tech.aiflowy.ai.tinyflow.filestorage;

import dev.tinyflow.core.filestoreage.FileStorage;
import dev.tinyflow.core.filestoreage.FileStorageProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FileStorageProviderImpl implements FileStorageProvider {

    @Resource(name = "tinyFlowFileStorage")
    private FileStorage tinyFlowFileStorage;

    @Override
    public FileStorage getFileStorage() {
        return tinyFlowFileStorage;
    }
}
