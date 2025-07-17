package tech.aiflowy.ai.node;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.chain.Chain;
import com.agentsflex.core.chain.node.BaseNode;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import tech.aiflowy.common.filestorage.FileStorageManager;
import tech.aiflowy.common.filestorage.FileStorageService;
import tech.aiflowy.common.util.SpringContextUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeFileNode extends BaseNode {

    private String suffix;

    public MakeFileNode() {
    }

    public MakeFileNode(String suffix) {
        this.suffix = suffix;
    }

    @Override
    protected Map<String, Object> execute(Chain chain) {
        Map<String, Object> map = chain.getParameterValues(this);

        Map<String, Object> res = new HashMap<>();

        String content = map.get("content").toString();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        createFile(suffix, content, os);

        String fileName = IdUtil.fastSimpleUUID() + "." + suffix;
        CustomFile file = new CustomFile(fileName, os.toByteArray());

        FileStorageManager manager = SpringContextUtil.getBean(FileStorageManager.class);

        String url = manager.save(file);
        res.put("url", url);
        return res;
    }

    private void createFile(String suffix, String content, ByteArrayOutputStream os) {
        if ("docx".equals(suffix)) {
            docx(content, os);
        }
    }

    private void docx(String content, ByteArrayOutputStream os) {
        String separator = "\n";
        List<String> split = StrUtil.split(content, separator);
        // 创建一个新的Word文档
        XWPFDocument doc = new XWPFDocument();
        // 创建样式
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId("IndentStyle");
        CTPPrGeneral pPr = ctStyle.addNewPPr();
        CTInd ind = pPr.addNewInd();
        ind.setFirstLine(400);
        doc.createStyles().addStyle(new XWPFStyle(ctStyle));

        for (String str : split) {
            // 创建段落
            XWPFParagraph paragraph = doc.createParagraph();
            paragraph.setStyle("IndentStyle");
            XWPFRun run = paragraph.createRun();
            run.setText(str);
        }
        try {
            doc.write(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                os.close();
                doc.close();
            } catch (IOException e) {
                System.out.println("关闭流异常" + e.getMessage());
            }
        }
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
