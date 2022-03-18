package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.dao.RingoDataPlanMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.mapper.recharge.DataPlanMapstructMapper;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.ringo.request.SpectranetRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.SpectranetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@CommonsLog
@Component
@RequiredArgsConstructor
public class RingoSpectranetRecharge implements Recharge, DataEnquiry, ParameterCheck {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RingoProperties properties;
    private final RingoDataPlanMapper ringoDataPlanMapper;
    private final DataPlanMapstructMapper dataPlanMapstructMapper;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        SpectranetRequest spectranetRequest = SpectranetRequest.builder()
                .amount(request.getServiceCost())
                .request_id(request.getId())
                .pinNo(String.valueOf(1))
                .serviceCode(properties.getOtherDataServiceCode())
                .type(properties.getSpectranetType())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("email", properties.getMail());
        headers.add("password", properties.getPassword());

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(spectranetRequest), headers);
            SpectranetResponse response =
                    restTemplate.postForObject(properties.getAirtimeUrl(), entity, SpectranetResponse.class);

            String errorMessage = null;

            if (response != null && response.getMessage() != null) {
                log.info("Spectranet PIN Request Response Not Null");
                if (response.getMessage().equalsIgnoreCase("Successful")) {
                    log.info(response);
                    log.info(String.format("Spectranet data Pin purchase (%.2f) Successful", request.getServiceCost()));
                    return RechargeStatus.builder()
                            .status(HttpStatus.OK)
                            .message("Spectranet Purchase Successful Pin (: " + response.getPin().get(0).getPin() + ")")
                            .build();
                } else {
                    errorMessage = String.format("Error Acquiring Spectranet PIN message (%s)", response.getMessage());
                }
            } else {
                errorMessage = "Unknown Error Acquiring Spectranet Pin";
            }

            log.error(errorMessage);

            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessage)
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<DataPlanDto> getDataPlans(String planCode) {
        String network = RingoRechargeFactory.codeMapper.get(planCode);
        return dataPlanMapstructMapper.listRingoPlanToDto(ringoDataPlanMapper.findByNetworkId(network));
    }

    @Override
    public DataPlanDto getPlan(String id) {
        return dataPlanMapstructMapper.ringoPlanToDto(ringoDataPlanMapper.findById(id));
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null;
    }
}
