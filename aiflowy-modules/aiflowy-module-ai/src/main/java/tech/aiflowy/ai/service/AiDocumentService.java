package tech.aiflowy.ai.service;

import tech.aiflowy.ai.entity.AiDocument;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface AiDocumentService extends IService<AiDocument> {

    Page<AiDocument> getDocumentList(String knowledgeId , int pageSize, int pageNum, String fileName);

    boolean removeDoc(String id);

    ResponseEntity<?> previewFile(String documentId) throws IOException;
}
