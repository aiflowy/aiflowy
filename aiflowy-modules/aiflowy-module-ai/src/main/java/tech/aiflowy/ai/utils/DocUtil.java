package tech.aiflowy.ai.utils;

import dev.tinyflow.core.util.OkHttpClientUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocUtil {

    private static final Logger log = LoggerFactory.getLogger(DocUtil.class);

    public static byte[] downloadFile(String url) {
        Request.Builder reqBuilder = new Request.Builder()
                .url(url);
        Request build = reqBuilder.build();
        OkHttpClient client = OkHttpClientUtil.buildDefaultClient();
        Call call = client.newCall(build);
        try (Response response = call.execute()) {
            if (response.body() != null) {
                return response.body().bytes();
            } else {
                throw new RuntimeException("下载内容为空");
            }
        } catch (Exception e) {
            log.error("下载文件失败：", e);
            throw new RuntimeException(e);
        }
    }

    public static String readWordFile(String suffix, InputStream is) {
        String content = "";
        try {
            if ("docx".equals(suffix)) {
                XWPFDocument document = new XWPFDocument(is);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                content = extractor.getText();
                // 关闭资源
                extractor.close();
                document.close();
            }
            if ("doc".equals(suffix)) {
                HWPFDocument document = new HWPFDocument(is);
                WordExtractor extractor = new WordExtractor(document);
                // 获取全部文本
                content = extractor.getText();
                // 关闭资源
                extractor.close();
                document.close();
            }
        } catch (IOException e) {
            log.error("读取word文件失败：", e);
            throw new RuntimeException(e);
        }
        return content;
    }

    public static String readPdfFile(InputStream is) {
        try (PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            log.error("读取pdf文件失败：", e);
            throw new RuntimeException(e);
        }
    }

    public static Map<Integer, byte[]> splitPdf(byte[] bytes, int splitSize) {

        Map<Integer, byte[]> map = new HashMap<>();

        int i = 0;
        try (PDDocument document = PDDocument.load(bytes)) {

            PDPageTree pages = document.getPages();
            // 判断页面数量是否小于等于拆分大小
            if (pages.getCount() <= splitSize) {
                map.put(1, bytes);
                return map;
            }
            // 创建Splitter实例
            Splitter splitter = new Splitter();

            // 设置拆分大小（每份文档的页数）
            splitter.setSplitAtPage(splitSize);

            // 拆分文档
            List<PDDocument> splitDocuments = splitter.split(document);

            // 保存拆分后的文档
            i = 1;
            for (PDDocument splitDoc : splitDocuments) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                splitDoc.save(baos);
                map.put(i, baos.toByteArray());
                splitDoc.close();
                i++;
            }
        } catch (Exception e) {
            log.error("PDF拆分失败：", e);
            throw new RuntimeException(e);
        }
        System.out.println("PDF拆分完成，共生成 " + (i - 1) + " 个文件。");
        return map;
    }

    public static Map<Integer, byte[]> splitWord(byte[] bytes, int splitSize) {

        Map<Integer, byte[]> map = new HashMap<>();

        InputStream is = new ByteArrayInputStream(bytes);
        try {
            XWPFDocument document = new XWPFDocument(is);

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            List<XWPFTable> tables = document.getTables();

            int totalParts = (int) Math.ceil((double) paragraphs.size() / splitSize);

            for (int i = 0; i < totalParts; i++) {
                XWPFDocument newDoc = new XWPFDocument();

                int start = i * splitSize;
                int end = Math.min((i + 1) * splitSize, paragraphs.size());

                // 复制段落
                for (int j = start; j < end; j++) {
                    XWPFParagraph newPara = newDoc.createParagraph();
                    newPara.getCTP().set(paragraphs.get(j).getCTP());
                }

                // 复制表格（可选，可以根据需要调整）
                for (XWPFTable table : tables) {
                    XWPFTable newTable = newDoc.createTable();
                    newTable.getCTTbl().set(table.getCTTbl());
                }

                // 保存拆分后的文档
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newDoc.write(baos);
                map.put(i, baos.toByteArray());
                newDoc.close();
            }
            document.close();
            is.close();
        } catch (IOException e) {
            log.error("Word文档拆分失败：", e);
            throw new RuntimeException(e);
        }
        return map;
    }

    public static String getSuffix(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static byte[] readBytes(InputStream inputStream) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            buffer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer.toByteArray();
    }


    public static String getFileNameByUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
