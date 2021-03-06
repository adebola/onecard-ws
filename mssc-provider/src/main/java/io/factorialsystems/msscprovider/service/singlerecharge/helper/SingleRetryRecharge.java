package io.factorialsystems.msscprovider.service.singlerecharge.helper;

import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequestRetry;
import io.factorialsystems.msscprovider.dto.AsyncRechargeDto;
import io.factorialsystems.msscprovider.dto.SimpleUserDto;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.recharge.ReQuery;
import io.factorialsystems.msscprovider.recharge.ReQueryRequest;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.service.singlerecharge.SingleRechargeService;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleRetryRecharge {
    private final FactoryProducer producer;
    private final ParameterCache parameterCache;
    private final SingleRechargeMapper singleRechargeMapper;

    @Value("${api.local.host.baseurl}")
    private String baseUrl;

    public RechargeStatus retryRecharge(String id, String recipient) {
        String email = null;

        SingleRechargeRequest request = Optional.ofNullable(singleRechargeMapper.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("SingleRechargeRequest", "id", id));

        if (recipient == null) recipient = request.getRecipient();

        if (request.getRetryId() != null) {
            SingleRechargeRequestRetry rechargeRequestRetry = Optional.ofNullable(singleRechargeMapper.findRequestRetryById(request.getRetryId()))
                    .orElseThrow(() -> new ResourceNotFoundException("SingleRechargeRequestRetry", id, request.getRetryId()));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
            String dateString = simpleDateFormat.format(rechargeRequestRetry.getRetriedOn());

            final String message =
                    String.format("The Recharge Request was retried on %s by %s and the message is %s", dateString, rechargeRequestRetry.getRetriedBy(), rechargeRequestRetry.getStatusMessage());
            log.info(message);

            return RechargeStatus.builder()
                    .message(message)
                    .status(HttpStatus.OK)
                    .build();
        }

        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(request.getServiceId());

        if (parameters != null && !parameters.isEmpty()) {

            if (request.getUserId() != null) {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getInterceptors().add(new RestTemplateInterceptor());

                SimpleUserDto simpleDto =
                        Optional.ofNullable(restTemplate.getForObject(baseUrl + "/api/v1/user/simple/" + request.getUserId(), SimpleUserDto.class))
                                .orElseThrow(() -> new ResourceNotFoundException("SimpleUserDto", "id", request.getUserId()));
                email = simpleDto.getEmail();;
            }

            RechargeFactoryParameters parameter = parameters.get(0);
            String rechargeProviderCode = parameter.getRechargeProviderCode();
            AbstractFactory factory = producer.getFactory(rechargeProviderCode);
            Recharge recharge = factory.getRecharge(parameter.getServiceAction());
            ReQuery reQuery = factory.getReQuery();

            ReQueryRequest reQueryRequest = new ReQueryRequest();
            reQueryRequest.setId(request.getId());
            String result = reQuery.reQueryRequest(reQueryRequest);

            if (result.equalsIgnoreCase("failed")) {
                final String retryId = UUID.randomUUID().toString();

                // temporarily reset the id for the request, it will be used as unique identifier in the recharge
                // if we leave the current id, it will error with  duplicate id error
                request.setId(retryId);

                // Also set recipient
                request.setRecipient(recipient);
                RechargeStatus retryStatus = recharge.recharge(request);

                if (retryStatus.getStatus() == HttpStatus.OK) {
                    singleRechargeMapper.saveRetryRequest (
                            SingleRechargeRequestRetry.builder()
                                    .retriedBy(K.getUserName() == null ? "auto" : K.getUserName())
                                    .id(retryId)
                                    .requestId(id)
                                    .successful(true)
                                    .statusMessage("Success")
                                    .recipient(recipient)
                                    .build()
                    );

                    Map<String, String> param = new HashMap<>();
                    param.put("id", id);
                    param.put("retryId", retryId);

                    singleRechargeMapper.saveSuccessfulRetry(param);
                } else {
                    singleRechargeMapper.saveRetryRequest (
                            SingleRechargeRequestRetry.builder()
                                    .retriedBy(K.getUserName())
                                    .id(retryId)
                                    .requestId(id)
                                    .successful(false)
                                    .statusMessage(retryStatus.getMessage())
                                    .recipient(recipient)
                                    .build()
                    );
                }

                if (email != null) {
                    AsyncRechargeDto dto = AsyncRechargeDto.builder()
                            .id(id)
                            .email(email)
                            .build();

                    SingleRechargeService.sendMail(request, dto, retryStatus);
                }

                return RechargeStatus.builder()
                        .message("Recharge Retry Success")
                        .status(HttpStatus.OK)
                        .build();
            } else {
                final String message = String.format("Cannot Retry Recharge status is %s", result);
                log.info(message);

                return RechargeStatus.builder()
                        .message(message)
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
        }

        return RechargeStatus.builder()
                .message("Recharge Retry Failure Please contact Onecard Support")
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }
}
