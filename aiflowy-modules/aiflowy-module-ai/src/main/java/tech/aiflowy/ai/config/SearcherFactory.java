package tech.aiflowy.ai.config;

import com.agentsflex.engine.es.ElasticSearcher;
import com.agentsflex.search.engine.lucene.LuceneSearcher;
import com.agentsflex.search.engine.service.DocumentSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearcherFactory {

    @Autowired
    private AiLuceneConfig luceneConfig;

    @Autowired
    private AiEsConfig aiEsConfig;

    @Value("${rag.searcher.type}")
    private String defaultSearcherType;

    @Bean
    public LuceneSearcher luceneSearcher() {
        return new LuceneSearcher(luceneConfig);
    }

    @Bean
    public ElasticSearcher elasticSearcher() {
        return new ElasticSearcher(aiEsConfig);
    }


    public DocumentSearcher getSearcher(String type) {
        switch (defaultSearcherType) {
            case "elasticSearch":
                return new ElasticSearcher(aiEsConfig);
            case "lucene":
            default:
                return new LuceneSearcher(luceneConfig);
        }
    }
}