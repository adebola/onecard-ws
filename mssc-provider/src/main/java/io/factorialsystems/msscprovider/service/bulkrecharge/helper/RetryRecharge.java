package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.query.IndividualRequestQuery;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequestRetry;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.StatusMessageDto;
import io.factorialsystems.msscprovider.recharge.ReQuery;
import io.factorialsystems.msscprovider.recharge.ReQueryRequest;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.service.model.ServiceHelper;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryRecharge {
    private final ServiceHelper helper;
    private final FactoryProducer producer;
    private final ParameterCache parameterCache;
    private final SingleRechargeMapper singleRechargeMapper;
    private final NewBulkRechargeMapper newBulkRechargeMapper;

    public void retryRequestsWithoutPayment(List<IndividualRequest> requests) {
        requests.forEach(this::retryIndividualRequestWithoutPayment);
    }

    public StatusMessageDto retryIndividualRequestWithoutPayment(IndividualRequest individualRequest) {
        int status = 300;
        String statusMessage = "Recharge Retry Failed";

        Optional<ReQuery> reQuery = getRequery(individualRequest.getServiceId());

        if (reQuery.isEmpty()) {
            final String message = String.format("ReQuery Interface Not Found for %d", individualRequest.getServiceId());
            log.error(message);

            return StatusMessageDto.builder()
                    .message(message)
                    .status(status)
                    .build();
        }

        Optional <Recharge> recharge = getRecharge(individualRequest.getServiceId());

        if (recharge.isEmpty()) {
            final String message = String.format("Invalid Service Id %d in Individual Request %d", individualRequest.getServiceId(), individualRequest.getId());
            log.error(message);

            return StatusMessageDto.builder()
                    .message(message)
                    .status(status)
                    .build();
        }

        ReQueryRequest reQueryRequest = new ReQueryRequest();
        reQueryRequest.setId(individualRequest.getExternalRequestId());
        String result = reQuery.get().reQueryRequest(reQueryRequest);

        if (result.equalsIgnoreCase("failure")) {
            SingleRechargeRequest singleRechargeRequest = SingleRechargeRequest.builder()
                    .serviceId(individualRequest.getServiceId())
                    .serviceCode(individualRequest.getServiceCode())
                    .serviceCost(individualRequest.getServiceCost())
                    .id(individualRequest.getExternalRequestId())
                    .recipient(individualRequest.getRecipient())
                    .productId(individualRequest.getProductId())
                    .bulkRequestId(individualRequest.getBulkRequestId())
                    .paymentMode(K.WALLET_PAY_MODE)
                    .build();

            RechargeStatus rechargeStatus = recharge.get().recharge(singleRechargeRequest);

            if (rechargeStatus.getStatus() == HttpStatus.OK) {
                status = 200;
                statusMessage = "Recharge Retry Success";

                saveSuccessfulIndividualRetry(individualRequest);
            } else {
                statusMessage = statusMessage + " Reason: " + rechargeStatus.getMessage();
            }
        } else {
            statusMessage = statusMessage + "Reason: " + result;
        }

        return StatusMessageDto.builder()
                .message(statusMessage)
                .status(status)
                .build();
    }

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

    public StatusMessageDto retrySingleRechargeWithoutPayment(String id) {
        SingleRechargeRequest request = singleRechargeMapper.findById(id);

        if (request != null) return retrySingleRechargeWithoutPayment(request);

        final String message = String.format("Retry Failed for Single Recharge %s, it s not existent", id);
        return StatusMessageDto.builder()
                .message(message)
                .status(300)
                .build();
    }

    public StatusMessageDto retrySingleRechargeWithoutPayment(SingleRechargeRequest request) {
        return null;
    }

    private void saveSuccessfulIndividualRetry(IndividualRequest request) {
        final String id = UUID.randomUUID().toString();

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


    private Optional<Recharge> getRecharge(Integer serviceId) {
        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(serviceId);

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);

            return Optional.ofNullable(factory.getRecharge(parameter.getServiceAction()));
        }

        return Optional.empty();
    }

    private Optional<ReQuery> getRequery(Integer serviceId) {

        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(serviceId);

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);

            return Optional.ofNullable(factory.getReQuery());
        }

        return Optional.empty();
    }
}
