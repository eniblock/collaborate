package collaborate.catalog.repository;

import collaborate.catalog.domain.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

public class ScopeRepositoryImpl implements ScopeRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ScopeRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Scope> findScopes() {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("type").is("metadata"));
        ProjectionOperation projectOperation = Aggregation.project("organizationId", "organizationName", "datasourceId", "scope", "scopeId");
        GroupOperation groupOperation = Aggregation.group("organizationId", "organizationName", "datasourceId", "scope", "scopeId");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, projectOperation);
        AggregationResults<Scope> groupResults = mongoTemplate.aggregate(aggregation, "document", Scope.class);

        return groupResults.getMappedResults();
    }

    @Override
    public Scope findScopeById(UUID scopeId) throws ResponseStatusException {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("type").is("metadata").and("scopeId").is(scopeId));

        ProjectionOperation projectOperation = Aggregation.project("organizationId", "organizationName", "datasourceId", "scope", "scopeId");
        GroupOperation groupOperation = Aggregation.group("organizationId", "organizationName", "datasourceId", "scope", "scopeId");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, projectOperation);
        AggregationResults<Scope> groupResults = mongoTemplate.aggregate(aggregation, "document", Scope.class);

        if (groupResults.getMappedResults().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return groupResults.getMappedResults().get(0);
    }
}