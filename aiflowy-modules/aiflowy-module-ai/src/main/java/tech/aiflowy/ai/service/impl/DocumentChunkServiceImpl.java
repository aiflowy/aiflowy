package tech.aiflowy.ai.service.impl;

import com.agentsflex.search.engine.service.DocumentSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import tech.aiflowy.ai.config.SearcherFactory;
import tech.aiflowy.ai.entity.DocumentChunk;
import tech.aiflowy.ai.entity.DocumentCollection;
import tech.aiflowy.ai.mapper.DocumentChunkMapper;
import tech.aiflowy.ai.service.DocumentChunkService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

import static tech.aiflowy.ai.entity.DocumentCollection.KEY_SEARCH_ENGINE_TYPE;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class DocumentChunkServiceImpl extends ServiceImpl<DocumentChunkMapper, DocumentChunk> implements DocumentChunkService {

    @Autowired
    private SearcherFactory searcherFactory;

    @Override
    public boolean removeChunk(DocumentCollection knowledge, BigInteger chunkId) {
        DocumentSearcher searcher = searcherFactory.getSearcher(knowledge.getId());
        // 删除搜索引擎中的数据
        if (searcher == null) {
            return true;
        }
        return searcher.deleteDocument(chunkId);
    }
}
