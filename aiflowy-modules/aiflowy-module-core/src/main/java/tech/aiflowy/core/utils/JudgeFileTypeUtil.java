package tech.aiflowy.core.utils;

public class JudgeFileTypeUtil {

    /**
     * 判断文件的文档类型
     * @param fileName 文件名
     * @return
     */
    public static String getFileTypeByExtension(String fileName) {
        if (fileName.endsWith(".txt")) {
            return "txt";
        } else if (fileName.endsWith(".pdf")) {
            return "pdf";
        } else if (fileName.endsWith(".md")) {
            return "md";
        } else if (fileName.endsWith(".docx")) {
            return "docx";
        } else {
            return null;
        }
    }
}
