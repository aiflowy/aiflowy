package tech.aiflowy.ai.service;

import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.ai.entity.AiDocument;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import org.springframework.http.ResponseEntity;
import tech.aiflowy.ai.entity.AiDocumentChunk;
import tech.aiflowy.common.domain.Result;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

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

    Result textSplit(BigInteger knowledgeIdm, MultipartFile file, String splitterName, Integer chunkSize, Integer overlapSize, String regex, Integer rowsPerChunk);

    Result saveTextResult(BigInteger knowledgeId, String previewList, String aiDocument);
}
