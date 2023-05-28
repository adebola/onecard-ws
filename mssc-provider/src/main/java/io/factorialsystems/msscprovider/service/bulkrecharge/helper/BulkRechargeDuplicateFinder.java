package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkRechargeDuplicateFinder {
    private final BulkRefundRecharge refundRecharge;
    private final BulkRechargeMapper newBulkRechargeMapper;

    public boolean checkForDuplicates(NewBulkRechargeRequest request) {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("id", request.getId());
        parameterMap.put("userId", request.getUserId());

        List<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findByUserIdToday(parameterMap);

        if (requests.stream().anyMatch(r -> filterDateAndCost(request, r))) {
            log.info("Bulk Recharge Request {} looks like a duplicate request it will not be run", request.getId());
            newBulkRechargeMapper.duplicateRequest(request.getId());
            refundRecharge.refundRecharges(request.getId());

            return true;
        }

        return false;
    }

    private boolean filterDateAndCost(NewBulkRechargeRequest originalRequest, NewBulkRechargeRequest existingRequest) {
        if (originalRequest.getTotalServiceCost().equals(existingRequest.getTotalServiceCost())) {
            LocalDateTime l1 = LocalDateTime.ofInstant(originalRequest.getCreatedAt().toInstant(), ZoneId.systemDefault());
            LocalDateTime l2 = LocalDateTime.ofInstant(existingRequest.getCreatedAt().toInstant(), ZoneId.systemDefault());

            long diff = ChronoUnit.SECONDS.between(l2, l1);

            if (diff < Constants.FIVE_MINUTES) {
                Integer i = newBulkRechargeMapper.individualCount(originalRequest.getId());
                Integer j = newBulkRechargeMapper.individualCount(existingRequest.getId());

                return Objects.equals(i, j);
            }
        }

        return false;
    }
}
