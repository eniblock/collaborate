package collaborate.catalog.repository;

import collaborate.catalog.domain.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;

public class SearchRepositoryImpl implements SearchRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public SearchRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Document> searchByScope(String organizationId, Long datasourceId, UUID scopeId, Pageable pageable, String q) {
        Query query = new Query();

        query.with(pageable);

        query.addCriteria(Criteria.where("organizationId").is(organizationId));
        query.addCriteria(Criteria.where("datasourceId").is(datasourceId));
        query.addCriteria(Criteria.where("scopeId").is(scopeId));

        if (null != q && !q.isEmpty()) {
            TextCriteria textCriteria = TextCriteria
                    .forDefaultLanguage()
                    .matchingPhrase(q);

            query.addCriteria(textCriteria);
        }

        List<Document> documents = mongoTemplate.find(query, Document.class);

        Page<Document> page = PageableExecutionUtils.getPage(
                documents,
                pageable,
                () -> mongoTemplate.count(query, Document.class));

        return page;
    }
}
