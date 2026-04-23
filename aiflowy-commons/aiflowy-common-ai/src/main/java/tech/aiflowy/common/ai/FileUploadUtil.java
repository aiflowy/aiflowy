package tech.aiflowy.common.ai;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.objects.SourceType;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.io.IOException;


public class FileUploadUtil {

    public static MultipartBody buildFileMultipartBody(String filePathOrUrl, String model) throws IOException {
        try {
            File file;
            String fileName;

            // 判断是否是HTTP/HTTPS URL
            if (filePathOrUrl.startsWith("http://") || filePathOrUrl.startsWith("https://")) {
                // 使用 Hutool 下载远程文件到临时目录
                String tempDir = System.getProperty("java.io.tmpdir");
                fileName = extractFileNameFromUrl(filePathOrUrl);

                // 创建临时文件路径
                File tempFile = new File(tempDir, fileName);

                // 下载文件（如果已存在则覆盖）
                long size = HttpUtil.downloadFile(filePathOrUrl, FileUtil.file(tempFile.getAbsolutePath()));

                if (size <= 0) {
                    throw new IOException("文件下载失败或文件大小为0: " + filePathOrUrl);
                }

                file = tempFile;
            } else {
                // 本地文件
                file = new File(filePathOrUrl);
                fileName = file.getName();
            }

            // 验证文件
            if (!file.exists()) {
                throw new IllegalArgumentException("文件不存在：" + filePathOrUrl);
            }

            if (!file.isFile()) {
                throw new IllegalArgumentException("不是文件：" + filePathOrUrl);
            }

            if (file.length() == 0) {
                throw new IllegalArgumentException("文件为空：" + filePathOrUrl);
            }

            // 确定MIME类型
            MediaType mediaType = detectMediaType(fileName);

            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("model", model)
                    .addFormDataPart(
                            "file",
                            fileName,
                            RequestBody.create(file, mediaType)
                    )
                    .build();
        } catch (IOException e) {
            throw new IOException("文件上传失败：" + e.getMessage());
        } finally {
            FileUtil.del(String.valueOf(SourceType.file));
        }

    }

    /**
     * 从URL提取文件名
     */
    private static String extractFileNameFromUrl(String url) {
        // 从URL中提取文件名
        if (url.contains("/")) {
            String name = url.substring(url.lastIndexOf("/") + 1);

            // 移除URL参数
            if (name.contains("?")) {
                name = name.substring(0, name.indexOf("?"));
            }

            if (!name.isEmpty() && name.contains(".")) {
                return name;
            }
        }

        // 如果无法提取，生成一个带时间戳的文件名
        return "downloaded_" + System.currentTimeMillis() + ".tmp";
    }

    /**
     * 检测文件的MediaType
     */
    public static MediaType detectMediaType(String fileName) {
        if (fileName == null) {
            return MediaType.parse("application/octet-stream");
        }

        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".pdf")) {
            return MediaType.parse("application/pdf");
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return MediaType.parse("image/jpeg");
        } else if (lowerName.endsWith(".png")) {
            return MediaType.parse("image/png");
        } else if (lowerName.endsWith(".iso")) {  // 添加ISO类型
            return MediaType.parse("application/x-iso9660-image");
        } else if (lowerName.endsWith(".txt")) {
            return MediaType.parse("text/plain");
        } else if (lowerName.endsWith(".doc")) {
            return MediaType.parse("application/msword");
        } else if (lowerName.endsWith(".docx")) {
            return MediaType.parse("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else {
            return MediaType.parse("application/octet-stream");
        }
    }

    /**
     * 指定下载目录的方法（如你示例中的E盘）
     */
    public static MultipartBody buildFileMultiBody(String filePathOrUrl, String downloadDir) throws IOException {
        File file;
        String fileName;

        // 判断是否是HTTP/HTTPS URL
        if (filePathOrUrl.startsWith("http://") || filePathOrUrl.startsWith("https://")) {
            // 提取文件名
            fileName = extractFileNameFromUrl(filePathOrUrl);

            // 创建指定目录的文件路径
            File targetFile = new File(downloadDir, fileName);

            // 确保目录存在
            FileUtil.mkdir(downloadDir);

            // 下载文件到指定目录
            long size = HttpUtil.downloadFile(filePathOrUrl, FileUtil.file(targetFile.getAbsolutePath()));

            if (size <= 0) {
                throw new IOException("文件下载失败或文件大小为0: " + filePathOrUrl);
            }

            file = targetFile;
        } else {
            // 本地文件
            file = new File(filePathOrUrl);
            fileName = file.getName();
        }

        // 验证文件...
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在：" + filePathOrUrl);
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("不是文件：" + filePathOrUrl);
        }

        if (file.length() == 0) {
            throw new IllegalArgumentException("文件为空：" + filePathOrUrl);
        }

        // 确定MIME类型
        MediaType mediaType = detectMediaType(fileName);

        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("model", "PDF-Extract-Kit-1.0")
                .addFormDataPart(
                        "file",
                        fileName,
                        RequestBody.create(file, mediaType)
                )
                .build();
    }

}