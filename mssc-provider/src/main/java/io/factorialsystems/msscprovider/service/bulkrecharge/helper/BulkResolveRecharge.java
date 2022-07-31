package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.SingleResolve;
import io.factorialsystems.msscprovider.domain.query.IndividualRequestQuery;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.ResolveRechargeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleResolveRecharge.createResolve;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkResolveRecharge {
    private final NewBulkRechargeMapper bulkRechargeMapper;

    @Transactional
    public Optional<ResolveRechargeDto> resolveBulk(ResolveRechargeDto dto) {
        List<IndividualRequest> failedRequests = bulkRechargeMapper.findBulkIndividualFailedRequests(dto.getRechargeId());

        if (failedRequests == null || failedRequests.isEmpty()) {
            log.error("Resolve Recharge Failed no failed requests id {}", dto.getRechargeId());
            return Optional.empty();
        }

        SingleResolve singleResolve = createResolve(dto.getRechargeId(), dto.getResolvedBy(), dto.getMessage());

        bulkRechargeMapper.saveResolution(singleResolve);

        Map<String, String> rechargeMap = new HashMap<>();
        rechargeMap.put("id", dto.getRechargeId());
        rechargeMap.put("resolveId", singleResolve.getId());

        if (bulkRechargeMapper.resolveBulkRequest(rechargeMap)) {
            log.info(String.format("Bulk Recharge %s Admin Resolve by %s", dto.getRechargeId(), singleResolve.getResolvedBy()));
        } else {
            log.error(String.format("Bulk Recharge %s Admin Resolve by %s No Record updated", dto.getRechargeId(), singleResolve.getResolvedBy()));
        }

        dto.setId(singleResolve.getId());
        return Optional.of(dto);
    }

    @Transactional
    public Optional<ResolveRechargeDto> resolveIndividual(ResolveRechargeDto dto) {
        NewBulkRechargeRequest request = bulkRechargeMapper.findBulkRechargeById(dto.getRechargeId());

        if (request == null) {
            log.error("Unable to Load Parent Bulk Recharge {} for Individual Resolve Request", dto.getRechargeId());
            return Optional.empty();
        }

        IndividualRequestQuery query = IndividualRequestQuery.builder()
                .id(dto.getIndividualId())
                .userId(request.getUserId())
                .build();

        IndividualRequest failedRequest = bulkRechargeMapper.findIndividualRequestById(query);

        if (failedRequest == null || !failedRequest.getFailed() || failedRequest.getResolveId() != null ||
                failedRequest.getRefundId() != null || failedRequest.getRetryId() != null) {
            log.error("Resolve Recharge Failed, the request did not fail or it has been closed {}", dto.getRechargeId());
            return Optional.empty();
        }

        SingleResolve singleResolve = createResolve(dto.getRechargeId(), dto.getResolvedBy(), dto.getMessage());
        bulkRechargeMapper.saveResolution(singleResolve);

        Map<String, String> rechargeMap = new HashMap<>();
        rechargeMap.put("id", String.valueOf(dto.getIndividualId()));
        rechargeMap.put("resolveId", singleResolve.getId());

        if (bulkRechargeMapper.resolveIndividualRequest(rechargeMap)) {
            log.info(String.format("Bulk Individual Recharge %d Admin Resolve by %s", dto.getIndividualId(), singleResolve.getResolvedBy()));
        } else {
            log.error(String.format("Bulk Recharge %d Admin Resolve by %s No Record updated", dto.getIndividualId(), singleResolve.getResolvedBy()));
        }

        dto.setId(singleResolve.getId());
        return Optional.of(dto);
    }
}
