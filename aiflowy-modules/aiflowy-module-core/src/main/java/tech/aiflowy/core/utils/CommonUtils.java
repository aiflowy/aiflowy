package tech.aiflowy.core.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    // 定义文件类型映射
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp"
    ));

    private static final Set<String> VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList(
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm"
    ));

    private static final Set<String> DOCUMENT_EXTENSIONS = new HashSet<>(Arrays.asList(
            "pdf", "docx", "txt", "xls", "xlsx", "ppt", "pptx", "csv"
    ));

    // 使用正则表达式提取文件名
    public static String extractFileName(String path) {
        // 匹配路径中的最后一段（文件名）
        Pattern pattern = Pattern.compile("[^/]+$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(); // 返回匹配到的文件名
        }
        return null;
    }

    // 使用正则表达式提取文件后缀
    public static String extractFileExtension(String fileName) {
        // 匹配文件名中的后缀（以点开头，后面跟随字母或数字）
        Pattern pattern = Pattern.compile("\\.([a-zA-Z0-9]+)$");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1); // 返回匹配到的后缀（去掉点）
        }
        return null; // 如果没有匹配到后缀，返回 null
    }

    // 判断文件类型
    public static int getFileType(String path) {
        // 提取文件名
        String fileName = extractFileName(path);

        // 提取文件后缀
        String extension = extractFileExtension(fileName);

        // 如果没有后缀，返回未知文件类型
        if (extension == null) {
            return 0;
        }

        // 转换为小写以忽略大小写
        extension = extension.toLowerCase();

        // 判断是否为图片
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return 1;
        }

        // 判断是否为视频
        if (VIDEO_EXTENSIONS.contains(extension)) {
            return 2;
        }

        // 判断是否为文件
        if (DOCUMENT_EXTENSIONS.contains(extension)) {
            return 3;
        }

        // 其他情况
        return 0;
    }

    /**
     * 字节转换GB  or  MB
     *
     * @return
     */
    public static String calcByte(Long sizeInBytes) {
        if (sizeInBytes == null) {
            return "";
        }

        String sizeFormatted;
        if (sizeInBytes >= 1024 * 1024 * 1024) {
            // Convert to GB
            double sizeInGB = sizeInBytes / (1024.0 * 1024.0 * 1024.0);
            sizeFormatted = String.format("%.2f GB", sizeInGB);
        } else if (sizeInBytes >= 1024 * 1024) {
            // Convert to MB
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
            sizeFormatted = String.format("%.2f MB", sizeInMB);
        } else if (sizeInBytes >= 1024) {
            // Convert to KB
            double sizeInKB = sizeInBytes / 1024.0;
            sizeFormatted = String.format("%.2f KB", sizeInKB);
        } else {
            // Keep in bytes
            sizeFormatted = sizeInBytes + " bytes";
        }
        return sizeFormatted;
    }
}
