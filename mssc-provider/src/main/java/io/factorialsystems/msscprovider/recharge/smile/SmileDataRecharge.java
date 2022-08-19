package io.factorialsystems.msscprovider.recharge.smile;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmileDataRecharge implements Recharge, ParameterCheck, Balance, DataEnquiry, ReQuery {
    private final SmileProperties properties;
    private final ObjectFactory objectFactory;
    private final WebServiceTemplate webServiceTemplate;

    @Override
    public BigDecimal getBalance() {
        final String sessionId = startSession()
                .orElseThrow(() -> new RuntimeException("Error Loading Smile Session Id"));

        BalanceQuery balanceQuery = new BalanceQuery();
        TPGWContext context = new TPGWContext();
        context.setSessionId(sessionId);
        balanceQuery.setTPGWContext(context);
        balanceQuery.setAccountId(Long.parseLong(properties.getSourceAccount()));

        JAXBElement<BalanceQuery> jaxbElement = objectFactory.createBalanceQuery(balanceQuery);

        JAXBElement<BalanceResult> result =
                (JAXBElement<BalanceResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

        if (result != null && result.getValue() != null) {
            return BigDecimal.valueOf(result.getValue().getAvailableBalanceInCents() / 100);
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
            final String sessionId = startSession()
                    .orElseThrow(() -> new RuntimeException("Error Loading Smile Session Id"));

            Optional<SmileCustomer> customer = validateAccount(request.getRecipient(), sessionId);

            if (customer.isEmpty()) {
                return RechargeStatus.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(String.format("Invalid Customer Id %s", request.getRecipient()))
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

            JAXBElement<BuyBundleResult> result =
                    (JAXBElement<BuyBundleResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

            if (result != null && result.getValue() != null) {
                log.info(String.format("Smile data Recharge for (%s) Successful Plan (%s)", request.getRecipient(), request.getProductId()));
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Smile Data Recharge Successful")
                        .build();
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
        } catch (SoapFaultClientException so) {
            return  Optional.empty();
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

        JAXBElement<AuthenticateResult> result =
                (JAXBElement<AuthenticateResult>) webServiceTemplate.marshalSendAndReceive(properties.getUrl(), jaxbElement);

        if (result != null && result.getValue() != null && result.getValue().getSessionId() != null) {
            return Optional.of(result.getValue().getSessionId());
        }

        return Optional.empty();
    }

    @Override
    @Cacheable("smiledataplans")
    public List<DataPlanDto> getDataPlans(String planCode) {
        log.info("Getting Smile Data Plans");

        final String sessionId = startSession()
                .orElseThrow(() -> new RuntimeException("Error Loading Smile Session Id in Data Plan Request"));

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

        return null;
    }

    @Override
    public DataPlanDto getPlan(String id, String planCode) {
        log.info("Retrieving single smile data plan for id {}", id);

        return getDataPlans(planCode).stream()
                .filter(d -> d.getProduct_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Unable to load Smile Data Plan %s", id)));
    }

    @Override
    public String reQueryRequest(ReQueryRequest request) {
        return null;
    }
}
