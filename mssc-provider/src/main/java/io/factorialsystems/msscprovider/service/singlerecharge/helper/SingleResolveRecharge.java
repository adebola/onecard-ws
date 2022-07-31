package io.factorialsystems.msscprovider.service.singlerecharge.helper;

import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.SingleResolve;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.ResolveRechargeDto;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleResolveRecharge {
    private final SingleRechargeMapper singleRechargeMapper;

    @Transactional
    public Optional<ResolveRechargeDto> resolve(ResolveRechargeDto dto) {

        SingleRechargeRequest singleRechargeRequest = Optional.ofNullable(singleRechargeMapper.findById(dto.getRechargeId()))
                .orElseThrow(() -> new ResourceNotFoundException("SingleRecharge", "id", dto.getRechargeId()));

        // To Resolve a Recharge it must have failed and Not Refunded, Not Retried Successfully and Not Resolved
        if (singleRechargeRequest.getFailed() != null && singleRechargeRequest.getFailed()
                && singleRechargeRequest.getRetryId() == null && singleRechargeRequest.getRefundId() == null
                && singleRechargeRequest.getResolveId() == null) {

            SingleResolve singleResolve = createResolve(dto.getRechargeId(), dto.getResolvedBy(), dto.getMessage());

            singleRechargeMapper.saveResolution(singleResolve);

            Map<String, String> rechargeMap = new HashMap<>();
            rechargeMap.put("id", dto.getRechargeId());
            rechargeMap.put("resolveId", singleResolve.getId());

            if (singleRechargeMapper.resolveRequest(rechargeMap)) {
                log.info(String.format("Recharge %s Admin Resolve by %s", singleRechargeRequest.getId(), singleResolve.getResolvedBy()));
            } else {
                log.error(String.format("Recharge %s Admin Resolve by %s No Record updated", singleRechargeRequest.getId(), singleResolve.getResolvedBy()));
            }

            dto.setId(dto.getId());
            return Optional.of(dto);
        }

        log.error("Unable to resolve Single Recharge {}, it did not fail or has been resolved, retried or refunded", dto.getRechargeId());
        return Optional.empty();
    }
    static public SingleResolve createResolve(String rechargeId, String resolvedBy, String message) {
        return SingleResolve.builder()
                .id(UUID.randomUUID().toString())
                .rechargeId(rechargeId)
                .resolvedBy(resolvedBy)
                .resolutionMessage(message)
                .build();
    }
}
