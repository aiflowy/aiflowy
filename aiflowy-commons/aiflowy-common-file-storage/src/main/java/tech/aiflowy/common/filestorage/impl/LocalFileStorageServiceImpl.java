package tech.aiflowy.common.filestorage.impl;

import tech.aiflowy.common.util.DateUtil;
import tech.aiflowy.common.filestorage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;


@Component("local")
public class LocalFileStorageServiceImpl implements FileStorageService {
    private static final Logger LOG = LoggerFactory.getLogger(LocalFileStorageServiceImpl.class);


    @Value("${aiflowy.storage.local.root:}")
    private String root;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
    }


    @Override
    public String save(MultipartFile file) {
        try {
            String path = newFile(getExtName(file.getOriginalFilename()));
            File target = getLocalFile(path);
            if (!target.getParentFile().exists() && !target.getParentFile().mkdirs()) {
                LOG.error("Can not mkdirs: {} ", target.getParentFile());
            }
            file.transferTo(target);
            return path;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public InputStream readStream(String path) throws IOException {
        File target = getLocalFile(path);
        return Files.newInputStream(target.toPath());
    }


    private File getLocalFile(String path) throws IOException {
        String root = StringUtils.hasText(this.root) ? this.root : getDefaultRoot();
        return new File(root, path);
    }


    public static String getExtName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf('.');
        if (index != -1 && (index + 1) < fileName.length()) {
            return fileName.substring(index + 1);
        } else {
            return null;
        }
    }

    public static String newFile(String extName) {
        return "/attachment/"
                + DateUtil.toString(new Date(), "yyyy/MM-dd") + "/"
                + UUID.randomUUID() + "." + extName;
    }

    private String getDefaultRoot() throws IOException {
        ClassPathResource fileResource = new ClassPathResource("/");
        return new File(fileResource.getFile(), "/public").getAbsolutePath();
    }
}
