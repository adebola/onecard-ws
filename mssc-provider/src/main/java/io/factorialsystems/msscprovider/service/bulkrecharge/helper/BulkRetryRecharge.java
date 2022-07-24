package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.query.IndividualRequestQuery;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequestRetry;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.StatusMessageDto;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkRetryRecharge {
    private final RechargeInterfaceRequester requester;
    private final NewBulkRechargeMapper newBulkRechargeMapper;

    // Retry List of Recharges
    public void retryRequestsWithoutPayment(List<IndividualRequest> requests) {
        requests.forEach(this::retryIndividualRequestWithoutPayment);
    }

    // Retry One Recharge from a list of Failed Bulk Recharges - Overloaded
    public StatusMessageDto retryIndividualRequestWithoutPayment(IndividualRequest individualRequest) {
        int status = 300;
        String statusMessage = "Recharge Retry Failed";

        Optional<ReQuery> reQuery = requester.getReQuery(individualRequest.getServiceId());

        if (reQuery.isEmpty()) {
            final String message = String.format("ReQuery Interface Not Found for %d", individualRequest.getServiceId());
            log.error(message);

            return StatusMessageDto.builder()
                    .message(message)
                    .status(status)
                    .build();
        }

        Optional<RechargeParameters> optionalParameters = requester.getRecharge(individualRequest.getServiceId());

        if (optionalParameters.isEmpty()) {
            final String message = String.format("Invalid Service Id %d in Individual Request %d", individualRequest.getServiceId(), individualRequest.getId());
            log.error(message);

            return StatusMessageDto.builder()
                    .message(message)
                    .status(status)
                    .build();
        }

        Recharge recharge = optionalParameters.get().getRecharge();

        ReQueryRequest reQueryRequest = new ReQueryRequest();
        reQueryRequest.setId(individualRequest.getExternalRequestId());
        String result = reQuery.get().reQueryRequest(reQueryRequest);

        log.info(String.format("Query Bulk Individual Recharge %d result %s", individualRequest.getId(), result));

        if (result.equalsIgnoreCase("failed")) {
            final String requestRetryId = UUID.randomUUID().toString();

            SingleRechargeRequest singleRechargeRequest = SingleRechargeRequest.builder()
                    .serviceId(individualRequest.getServiceId())
                    .serviceCode(individualRequest.getServiceCode())
                    .serviceCost(individualRequest.getServiceCost())
                    .id(requestRetryId)
                    .recipient(individualRequest.getRecipient())
                    .productId(individualRequest.getProductId())
                    .bulkRequestId(individualRequest.getBulkRequestId())
                    .paymentMode(K.WALLET_PAY_MODE)
                    .build();

            RechargeStatus rechargeStatus = recharge.recharge(singleRechargeRequest);

            if (rechargeStatus.getStatus() == HttpStatus.OK) {
                status = 200;
                statusMessage = "Recharge Retry Success";

                log.info(String.format("Successful Retry Recharge %s/%s", individualRequest.getServiceCode(), individualRequest.getRecipient()));
                saveSuccessfulIndividualRetry(individualRequest, requestRetryId);
            } else {
                log.error(String.format("Failed Retry Recharge %s/%s Reason %s", individualRequest.getServiceCode(), individualRequest.getRecipient(), rechargeStatus.getMessage()));
                statusMessage = statusMessage + " Reason: " + rechargeStatus.getMessage();
                saveUnsuccessfulIndividualRetry(individualRequest, requestRetryId);
            }
        } else {
            statusMessage = statusMessage + "Reason: " + result;
        }

        return StatusMessageDto.builder()
                .message(statusMessage)
                .status(status)
                .build();
    }

    // Retry One Recharge from a list of Failed Bulk Recharges - Overloaded
    public StatusMessageDto retryIndividualRequestWithoutPayment(Integer id) {
        Optional<IndividualRequest> request = getIndividualRequest(id);

        if (request.isEmpty()) {
            final String message = String.format("Recharge Retry Failed for %d request did not fail or non-existent", id);
            log.error(message);

            return StatusMessageDto.builder()
                    .message(message)
                    .status(300)
                    .build();
        }

        return retryIndividualRequestWithoutPayment(request.get());
    }

    private void saveUnsuccessfulIndividualRetry(IndividualRequest request, String id) {
        String tokenUser = K.getUserId();

        IndividualRequestRetry requestRetry = IndividualRequestRetry.builder()
                .recipient(request.getRecipient())
                .requestId(request.getId())
                .retriedBy(tokenUser != null ? tokenUser : "auto")
                .id(id)
                .statusMessage("Retry Failed")
                .successful(false)
                .build();

        newBulkRechargeMapper.saveRetryRequest(requestRetry);
    }


    // Once a Retry Recharge Succeeds Update the Database accordingly
    private void saveSuccessfulIndividualRetry(IndividualRequest request, String id) {

        String tokenUser = K.getUserId();

        IndividualRequestRetry requestRetry = IndividualRequestRetry.builder()
                .recipient(request.getRecipient())
                .requestId(request.getId())
                .retriedBy(tokenUser != null ? tokenUser : "auto")
                .id(id)
                .statusMessage("Retry Successful")
                .successful(true)
                .build();

        newBulkRechargeMapper.saveRetryRequest(requestRetry);

        Map<String, String> map = new HashMap<>();

        map.put("id", String.valueOf(request.getId()));
        map.put("retryId", id);

        Boolean b = newBulkRechargeMapper.saveSuccessfulRetry(map);
    }

    // Common Code - Load Individual Request from Database
    private Optional<IndividualRequest> getIndividualRequest(Integer id) {

        IndividualRequestQuery query = IndividualRequestQuery.builder()
                .id(id)
                .userId(K.getUserId())
                .build();

        IndividualRequest individualRequest = newBulkRechargeMapper.findIndividualRequestById(query);

        if (individualRequest == null || !individualRequest.getFailed()) {
            return Optional.empty();
        }

        return Optional.of(individualRequest);
    }
}
