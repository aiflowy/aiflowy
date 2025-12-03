//package tech.aiflowy.common.ai;
//
//import com.agentsflex.core.document.Document;
//import com.agentsflex.core.document.DocumentParser;
//import org.commonmark.node.Node;
//import org.commonmark.parser.Parser;
//import org.commonmark.renderer.html.HtmlRenderer;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Scanner;
//import java.util.regex.Pattern;
//
//public class MarkdownDocumentParser implements DocumentParser {
//
//    public MarkdownDocumentParser() {
//        // 构造函数，可以在这里初始化一些必要的资源
//    }
//
//    @Override
//    public Document parse(InputStream stream) {
//        try {
//            // 将 InputStream 转换为字符串
//            String markdownContent = convertStreamToString(stream);
//
//            // 使用 CommonMark 解析 Markdown 内容
//            Parser parser = Parser.builder().build();
//            Node documentNode = parser.parse(markdownContent);
//
//            // 将 Markdown 转换为 HTML
//            HtmlRenderer renderer = HtmlRenderer.builder().build();
//            String htmlContent = renderer.render(documentNode);
//
//            // 去掉 HTML 标签，提取纯文本
//            String plainText = removeHtmlTags(htmlContent);
//
//            // 创建并返回 Document 对象
//            return new Document(plainText);
//        } catch (IOException e) {
//            throw new RuntimeException("解析 Markdown 文件时发生错误", e);
//        }
//    }
//
//    /**
//     * 将 InputStream 转换为字符串
//     *
//     * @param stream 输入流
//     * @return 转换后的字符串
//     * @throws IOException 如果读取流时发生错误
//     */
//    private String convertStreamToString(InputStream stream) throws IOException {
//        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
//            return scanner.useDelimiter("\\A").next();
//        }
//    }
//
//    /**
//     * 去掉 HTML 标签，提取纯文本
//     *
//     * @param htmlContent 包含 HTML 的字符串
//     * @return 去掉 HTML 标签后的纯文本
//     */
//    private String removeHtmlTags(String htmlContent) {
//        // 使用正则表达式匹配 HTML 标签
//        Pattern pattern = Pattern.compile("<[^>]*>");
//        return pattern.matcher(htmlContent).replaceAll("").trim();
//    }
//}