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

            final String id = UUID.randomUUID().toString();

            SingleResolve singleResolve = SingleResolve.builder()
                    .id(id)
                    .rechargeId(dto.getRechargeId())
                    .resolvedBy(dto.getResolvedBy())
                    .resolutionMessage(dto.getMessage())
                    .build();

            singleRechargeMapper.saveResolution(singleResolve);

            Map<String, String> rechargeMap = new HashMap<>();
            rechargeMap.put("id", dto.getRechargeId());
            rechargeMap.put("resolveId", id);

            if (singleRechargeMapper.resolveRequest(rechargeMap)) {
                log.info(String.format("Recharge %s Admin Resolve by %s", singleRechargeRequest.getId(), singleResolve.getResolvedBy()));
            } else {
                log.error(String.format("Recharge %s Admin Resolve by %s No Record updated", singleRechargeRequest.getId(), singleResolve.getResolvedBy()));
            }

            dto.setId(id);

            return Optional.of(dto);
        }

        return Optional.empty();
    }
}
