package tech.aiflowy.ai.tinyflow.filestorage;

import dev.tinyflow.core.chain.Chain;
import dev.tinyflow.core.filestoreage.FileStorage;
import dev.tinyflow.core.node.BaseNode;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.node.InputStreamFile;
import tech.aiflowy.common.filestorage.FileStorageService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Map;

@Component(value = "tinyFlowFileStorage")
public class StorageImpl implements FileStorage {

    @Resource(name = "default")
    private FileStorageService fileStorageService;

    @Override
    public String saveFile(InputStream stream, Map<String, String> headers, BaseNode node, Chain chain) {
        return fileStorageService.save(new InputStreamFile(stream, headers));
    }
}
