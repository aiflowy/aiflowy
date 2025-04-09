package tech.aiflowy.common.ai;

import com.agentsflex.core.document.DocumentParser;
import com.agentsflex.core.document.parser.DefaultTextDocumentParser;
import com.agentsflex.document.parser.PdfBoxDocumentParser;
import com.agentsflex.document.parser.PoiDocumentParser;

public class DocumentParserFactory {

    public static DocumentParser getDocumentParser(String typeOrFileName) {
        if (typeOrFileName == null) {
            throw new NullPointerException("typeOrFileName can not be null");
        } else {
            typeOrFileName = typeOrFileName.trim().toLowerCase();
        }
        if (typeOrFileName.endsWith(".pdf")) {
            return new PdfBoxDocumentParser();
        }
        if (typeOrFileName.endsWith(".docx")) {
            return new PoiDocumentParser();
        }
        if (typeOrFileName.endsWith(".txt")) {
            return new DefaultTextDocumentParser();
        }
        if (typeOrFileName.endsWith(".md")) {
            return new MarkdownDocumentParser();
        }
        return null;
    }
}
