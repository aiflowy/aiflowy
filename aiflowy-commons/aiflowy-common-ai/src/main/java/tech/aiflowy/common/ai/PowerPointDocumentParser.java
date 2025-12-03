//package tech.aiflowy.common.ai;
//
//import com.agentsflex.core.document.Document;
//import com.agentsflex.core.document.DocumentParser;
//import org.apache.poi.hslf.usermodel.HSLFSlideShow;
//import org.apache.poi.hslf.usermodel.HSLFTextShape;
//import org.apache.poi.xslf.usermodel.XMLSlideShow;
//import org.apache.poi.xslf.usermodel.XSLFShape;
//import org.apache.poi.xslf.usermodel.XSLFTextShape;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class PowerPointDocumentParser implements DocumentParser {
//
//    @Override
//    public Document parse(InputStream inputStream) {
//        try {
//            // 尝试先按PPTX格式解析
//            try {
//                return parsePPTX(inputStream);
//            } catch (Exception e) {
//                // 如果不是PPTX格式，尝试按PPT格式解析
//                return parsePPT(inputStream);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to parse PowerPoint file", e);
//        }
//    }
//
//    private Document parsePPTX(InputStream inputStream) throws IOException {
//        StringBuilder content = new StringBuilder();
//        Map<String, Object> metadata = new HashMap<>();
//        List<Map<String, String>> slides = new ArrayList<>();
//
//        try (XMLSlideShow slideShow = new XMLSlideShow(inputStream)) {
//            metadata.put("slide_count", slideShow.getSlides().size());
//
//            for (org.apache.poi.xslf.usermodel.XSLFSlide slide : slideShow.getSlides()) {
//                Map<String, String> slideContent = new HashMap<>();
//                StringBuilder slideText = new StringBuilder();
//
//                // 提取幻灯片标题
//                String title = slide.getTitle();
//                if (title != null && !title.isEmpty()) {
//                    slideContent.put("title", title);
//                    slideText.append("Slide Title: ").append(title).append("\n");
//                }
//
//                // 提取幻灯片文本内容
//                for (XSLFShape shape : slide.getShapes()) {
//                    if (shape instanceof XSLFTextShape) {
//                        String text = ((XSLFTextShape) shape).getText();
//                        if (text != null && !text.isEmpty()) {
//                            slideText.append(text).append("\n");
//                        }
//                    }
//                }
//
//                slideContent.put("content", slideText.toString());
//                slideContent.put("slide_number", String.valueOf(slide.getSlideNumber()));
//                slides.add(slideContent);
//                content.append(slideText).append("\n\n");
//            }
//        }
//
//        Document document = new Document(content.toString());
//        document.addMetadata("slides", slides);
//        document.addMetadata("format", "pptx");
//        return document;
//    }
//
//    private Document parsePPT(InputStream inputStream) throws IOException {
//        StringBuilder content = new StringBuilder();
//        Map<String, Object> metadata = new HashMap<>();
//        List<Map<String, String>> slides = new ArrayList<>();
//
//        try (HSLFSlideShow slideShow = new HSLFSlideShow(inputStream)) {
//            metadata.put("slide_count", slideShow.getSlides().size());
//
//            for (org.apache.poi.hslf.usermodel.HSLFSlide slide : slideShow.getSlides()) {
//                Map<String, String> slideContent = new HashMap<>();
//                StringBuilder slideText = new StringBuilder();
//
//                // ✅ 正确获取标题文本
//                String titleText = slide.getTitle();
//                if (titleText != null && !titleText.isEmpty()) {
//                    slideContent.put("title", titleText);
//                    slideText.append("Slide Title: ").append(titleText).append("\n");
//                }
//
//                // 提取幻灯片其他文本内容
//                for (org.apache.poi.hslf.usermodel.HSLFShape shape : slide.getShapes()) {
//                    if (shape instanceof HSLFTextShape) {
//                        String text = ((HSLFTextShape) shape).getText();
//                        if (text != null && !text.isEmpty()) {
//                            slideText.append(text).append("\n");
//                        }
//                    }
//                }
//
//                slideContent.put("content", slideText.toString());
//                slideContent.put("slide_number", String.valueOf(slide.getSlideNumber() + 1));
//                slides.add(slideContent);
//                content.append(slideText).append("\n\n");
//            }
//        }
//
//        Document document = new Document(content.toString());
//        document.addMetadata("slides", slides);
//        document.addMetadata("format", "ppt");
//        return document;
//    }
//}