package io.factorialsystems.msscaudit.repository;

import io.factorialsystems.msscaudit.document.AuditMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface AuditMessageRepository extends MongoRepository<AuditMessage, String> {
    static final String FIND_BY_DATE_BETWEEN = "{createdDate: {$gte: ?0, $lte: ?1}}";

    @Query(FIND_BY_DATE_BETWEEN)
    Page<AuditMessage> findPageableByCreatedDateBetween(Date start, Date end, Pageable pageable);
    @Query(FIND_BY_DATE_BETWEEN)
    List<AuditMessage> findByCreatedDateBetween(Date start, Date end, Sort sort);
    Page<AuditMessage> findPageableByServiceActionLike(String serviceName, Pageable pageable);
    Page<AuditMessage> findPageableByCreatedDateBetweenAndServiceActionLike(Date start, Date end, String serviceAction, Pageable pageable);
}
