package tech.aiflowy.common.ai;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;

public class MarkdownParser {

    /**
     * 将 Markdown 文件解析为纯文本
     *
     * @param inputStream Markdown 文件的输入流
     * @return 解析后的纯文本内容
     * @throws IOException 如果读取流时发生错误
     */
    public String parseToPlainText(InputStream inputStream) throws IOException {
        // 将 InputStream 转换为字符串
        String markdownContent = convertStreamToString(inputStream);

        // 使用 CommonMark 解析 Markdown 内容
        Parser parser = Parser.builder().build();
        Node documentNode = parser.parse(markdownContent);

        // 将 Markdown 转换为 HTML
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlContent = renderer.render(documentNode);

        // 去掉 HTML 标签，提取纯文本
        return extractPlainTextFromHtml(htmlContent);
    }

    /**
     * 将 InputStream 转换为字符串
     *
     * @param stream 输入流
     * @return 转换后的字符串
     * @throws IOException 如果读取流时发生错误
     */
    private String convertStreamToString(InputStream stream) throws IOException {
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    /**
     * 从 HTML 中提取纯文本
     *
     * @param htmlContent 包含 HTML 的字符串
     * @return 提取的纯文本内容
     */
    private String extractPlainTextFromHtml(String htmlContent) {
        return Jsoup.parse(htmlContent).text().trim();
    }
}