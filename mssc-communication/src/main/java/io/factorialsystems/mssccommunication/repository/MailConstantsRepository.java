package io.factorialsystems.mssccommunication.repository;

import io.factorialsystems.mssccommunication.domain.MailConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MailConstantsRepository  {
    private final MongoTemplate mongoTemplate;

    public MailConstant getMailFooter() {
        return mongoTemplate.findOne(
                Query.query(Criteria.where("name").is("footer")), MailConstant.class
        );
    }

}
