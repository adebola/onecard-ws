package io.factorialsystems.msscprovider.recharge.smile;

import io.factorialsystems.msscprovider.config.CacheProxy;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.wsdl.smile.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmileDataRecharge implements Recharge, ParameterCheck, Balance, DataEnquiry, ReQuery {
    private final CacheProxy cacheProxy;
    private final SmileProperties properties;
    private final ObjectFactory objectFactory;
    private final WebServiceTemplate webServiceTemplate;

    @Override
    public BigDecimal getBalance() {
        Optional<String> optionalSession = this.startSession();

        if (optionalSession.isPresent()) {
            String sessionId = optionalSession.get();
            BalanceQuery balanceQuery = new BalanceQuery();
            TPGWContext context = new TPGWContext();
            context.setSessionId(sessionId);
            balanceQuery.setTPGWContext(context);
            balanceQuery.setAccountId(Long.parseLong(properties.getSourceAccount()));

            JAXBElement<BalanceQuery> jaxbElement = objectFactory.createBalanceQuery(balanceQuery);

            try {
                JAXBElement<BalanceResult> result =
                        (JAXBElement<BalanceResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

                if (result != null && result.getValue() != null) {
                    return BigDecimal.valueOf(result.getValue().getAvailableBalanceInCents() / 100);
                }
            } catch (SoapFaultClientException faultClientException) {
                log.error("Error Retrieving Smile Balance");
                log.error(faultClientException.getMessage());
                log.error(faultClientException.getFaultStringOrReason());
            }
        }

        return BigDecimal.ZERO;
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {

        log.info("Validating Parameters sent for Smile Request");

        if (request == null || request.getProductId() == null || request.getRecipient() == null) {
            log.error("Error Validating Smile Recharge Request NULL Values");
            return false;
        }

        int productCode = 0;
        long accountNumber = 0;

        try {
            accountNumber = Long.parseLong(request.getRecipient());
            productCode = Integer.parseInt(request.getProductId());
        } catch (NumberFormatException nfe) {
            log.error(nfe.getMessage());
            log.error("Invalid Type for Smile Account {} of Product {} for Request {}", request.getRecipient(), request.getProductId(), request.getId());
            return false;
        }

        return (accountNumber != 0 && productCode != 0);
    }

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        log.info("Recharge Request fulfilled by Smile Recharge Provider for {}", request.getId());

        String errorMessage = null;

        try {
            Optional<String> optionalSession = this.startSession();

            if (optionalSession.isPresent()) {
                String sessionId = optionalSession.get();

                Optional<SmileCustomer> customer = validateAccount(request.getRecipient(), sessionId);

                if (customer.isEmpty()) {
                    return RechargeStatus.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .message(String.format("Error Validating Customer %s", request.getRecipient()))
                            .build();
                }

                BuyBundle buyBundle = new BuyBundle();
                buyBundle.setUniqueTransactionId(request.getId());
                buyBundle.setBundleTypeCode(Integer.parseInt(request.getProductId()));
                buyBundle.setQuantityBought(1);
                buyBundle.setCustomerAccountId(Long.parseLong(request.getRecipient()));
                buyBundle.setCustomerTenderedAmountInCents(request.getServiceCost().multiply(BigDecimal.valueOf(100)).doubleValue());

                TPGWContext tpgwContext = new TPGWContext();
                tpgwContext.setSessionId(sessionId);

                buyBundle.setTPGWContext(tpgwContext);

                JAXBElement<BuyBundle> jaxbElement = objectFactory.createBuyBundle(buyBundle);

                try {
                    JAXBElement<BuyBundleResult> result =
                            (JAXBElement<BuyBundleResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

                    if (result != null && result.getValue() != null) {
                        log.info(String.format("Smile data Recharge for (%s) Successful Plan (%s)", request.getRecipient(), request.getProductId()));

                        return RechargeStatus.builder()
                                .status(HttpStatus.OK)
                                .message("Smile Data Recharge Successful")
                                .build();
                    }
                } catch (SoapFaultClientException faultClientException) {
                    log.error("Error Recharging {}", request.getId());
                    log.error(faultClientException.getMessage());
                    log.error(faultClientException.getFaultStringOrReason());

                    errorMessage = String.format("Reason: %s, Message: %s", faultClientException.getFaultStringOrReason(), faultClientException.getMessage());
                }
            }
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            log.error(errorMessage);
        }

        if (errorMessage == null) {
            errorMessage = String.format("Error performing Smile Recharge %s", request.getId());
        }

        return RechargeStatus.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(errorMessage)
                .build();
    }

    @Override
    @Cacheable("smiledataplans")
    public List<DataPlanDto> getDataPlans(String planCode) {
        log.info("Getting Smile Data Plans code {}", planCode);

        Optional<String> optionalSession = startSession();

        if (optionalSession.isPresent()) {
            final String sessionId = optionalSession.get();

            BundleCatalogueQuery bundleCatalogueQuery = new BundleCatalogueQuery();
            TPGWContext context = new TPGWContext();
            context.setSessionId(sessionId);
            bundleCatalogueQuery.setTPGWContext(context);

            JAXBElement<BundleCatalogueQuery> jaxbElement = objectFactory.createBundleCatalogueQuery(bundleCatalogueQuery);

            JAXBElement<BundleCatalogueResult> result =
                    (JAXBElement<BundleCatalogueResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

            if (result != null && result.getValue() != null) {
                BundleCatalogueResult bundleCatalogueResult = result.getValue();
                return bundleCatalogueResult.getBundleList().getBundle().stream()
                        .map(bundle -> {
                            return DataPlanDto.builder()
                                    .price(String.valueOf(bundle.getBundlePrice() / 100))
                                    .network("SMILE")
                                    .product_id(String.valueOf(bundle.getBundleTypeCode()))
                                    .validity(String.valueOf(bundle.getValidityDays()))
                                    .allowance(bundle.getBundleDescription())
                                    .build();
                        }).collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    @Override
    public DataPlanDto getPlan(String id, String planCode) {
        log.info("Retrieving single smile data plan for id {}, code {}", id, planCode);

        return cacheProxy.getSmileDataPlans(planCode).stream()
                .filter(d -> d.getProduct_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Unable to load Smile Data Plan %s", id)));
    }

    @Override
    public ReQueryRequestStatus reQueryRequest(ReQueryRequest request) {
        Optional<String> optionalSession = startSession();

        if (optionalSession.isPresent()) {
            TransactionStatusQuery transactionStatusQuery = new TransactionStatusQuery();
            transactionStatusQuery.setUniqueTransactionId(request.getId());

            TPGWContext context = new TPGWContext();
            context.setSessionId(optionalSession.get());
            transactionStatusQuery.setTPGWContext(context);

            JAXBElement<TransactionStatusQuery> jaxbElement = objectFactory.createTransactionStatusQuery(transactionStatusQuery);

            try {
                JAXBElement<TransactionStatusResult> result =
                        (JAXBElement<TransactionStatusResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

                if (result != null && result.getValue() != null) {
                    TransactionStatusResult transactionStatusResult = result.getValue();

                   if (transactionStatusResult.getTransactionStatus() != null) {
                       switch (transactionStatusResult.getTransactionStatus()) {
                           case "SUCCESSFUL":
                               return ReQueryRequestStatus.SUCCESSFUL;

                           case "NOT FOUND":
                               return ReQueryRequestStatus.NOTFOUND;

                           case "FAILED":
                               return ReQueryRequestStatus.FAILED;
                       }
                   }
                }
            } catch (SoapFaultClientException faultClientException) {
                log.error("Error ReQuerying Recharging {}", request.getId());
                log.error(faultClientException.getMessage());
                log.error(faultClientException.getFaultStringOrReason());
            }
        }

        return ReQueryRequestStatus.UNKNOWN;
    }

    private Optional<SmileCustomer> validateAccount(String recipient, String sessionId) {
        ValidateAccountQuery validateAccountQuery = new ValidateAccountQuery();

        validateAccountQuery.setAccountId(Long.parseLong(recipient));
        TPGWContext context = new TPGWContext();
        context.setSessionId(sessionId);

        validateAccountQuery.setTPGWContext(context);

        JAXBElement<ValidateAccountQuery> jaxbElement = objectFactory.createValidateAccountQuery(validateAccountQuery);
        JAXBElement<ValidateAccountResult> result;

        try {
            result =
                    (JAXBElement<ValidateAccountResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);
        } catch (SoapFaultClientException faultClientException) {
            log.error("Error Validating Soap Customer {}", recipient);
            log.error(faultClientException.getMessage());
            log.error(faultClientException.getFaultStringOrReason());

            return Optional.empty();
        }

        if (result != null && result.getValue() != null) {
            return Optional.of(SmileCustomer.builder()
                    .firstName(result.getValue().getFirstName())
                    .lastName(result.getValue().getLastName())
                    .build());
        }

        return Optional.empty();
    }

    private Optional<String> startSession() {
        Authenticate authenticate = new Authenticate();
        authenticate.setPassword(properties.getPasword());
        authenticate.setUsername(properties.getUserName());

        JAXBElement<Authenticate> jaxbElement = objectFactory.createAuthenticate(authenticate);

        try {
            JAXBElement<AuthenticateResult> result =
                    (JAXBElement<AuthenticateResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

            if (result != null && result.getValue() != null && result.getValue().getSessionId() != null) {
                return Optional.of(result.getValue().getSessionId());
            }
        } catch (SoapFaultClientException faultClientException) {
            log.error(faultClientException.getMessage());
            log.error(faultClientException.getFaultStringOrReason());
        }

        return Optional.empty();
    }
}
