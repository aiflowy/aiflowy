package tech.aiflowy.ai.service;

import com.agentsflex.core.document.Document;
import tech.aiflowy.ai.entity.DocumentCollection;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.common.domain.Result;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 *  服务层。
 *
 * @author michael
 * @since 2024-08-23
 */
public interface DocumentCollectionService extends IService<DocumentCollection> {

    List<Document> search(BigInteger id, String keyword);

    DocumentCollection getDetail(String idOrAlias);

    DocumentCollection getByAlias(String idOrAlias);

    Result<?> beforeRemove(Collection<Serializable> ids);
}
