package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.RechargeResponseStatus;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoFetchDstvAddonRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoPayCableRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoValidateCableRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoFetchAddonDstvResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoPayCableResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoValidateCableResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DstvService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;

    private static HttpHeaders httpHeaders=null;

    private HttpHeaders getHeader(){
        if(httpHeaders==null){
            httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("email", ringoProperties.getMail());
            httpHeaders.add("password", ringoProperties.getPassword());
        }

        return httpHeaders;
    }

    public RechargeResponseStatus validateCable(RingoValidateCableRequest request){
        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            RingoValidateCableResponse response = restTemplate.postForObject(ringoProperties.getBaseUrl(), entity, RingoValidateCableResponse.class);

            if(response!=null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeResponseStatus.builder()
                        .status(true)
                        .message("Ringo Validate Cable Successful")
                        .data(response)
                        .build();
            }

            if(response!=null && !Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeResponseStatus.builder()
                        .status(false)
                        .message("Ringo Validate Cable Failed")
                        .data(response)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return RechargeResponseStatus.builder()
                .status(false)
                .message("Ringo Validate Cable Failed")
                .build();
    }

    public RechargeResponseStatus fetchAddonList(RingoFetchDstvAddonRequest request){

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            RingoFetchAddonDstvResponse response = restTemplate.postForObject(ringoProperties.getBaseUrl(), entity, RingoFetchAddonDstvResponse.class);

            if(response!=null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeResponseStatus.builder()
                        .status(true)
                        .message("Ringo Fetch Dstv Addon Successful")
                        .data(response)
                        .build();
            }

            if(response!=null && !Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeResponseStatus.builder()
                        .status(false)
                        .message("Ringo Fetch Dstv Addon Failed")
                        .data(response)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return RechargeResponseStatus.builder()
                .status(false)
                .message("Ringo Fetch Dstv Addon Failed")
                .build();
    }

    public RechargeStatus recharge(SingleRechargeRequest singleRechargeRequest){
        RingoPayCableRequest request = RingoPayCableRequest
                .builder()
                .serviceCode(singleRechargeRequest.getServiceCode())
                .requestId(singleRechargeRequest.getId())
                .code(singleRechargeRequest.getCode())
                .type(singleRechargeRequest.getType().getValue())
                .addondetails(singleRechargeRequest.getAddondetails())
                .period(singleRechargeRequest.getPeriod())
                .smartCardNo(singleRechargeRequest.getRecipient())
                .name(singleRechargeRequest.getName())
                .build();
        request.setCode(singleRechargeRequest.getServiceCode());

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            RingoPayCableResponse response = restTemplate.postForObject(ringoProperties.getBaseUrl(), entity, RingoPayCableResponse.class);

            if(response!=null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Ringo Pay Cable Successful")
                        .data(response)
                        .build();
            }

            if(response!=null && !Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeStatus.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Ringo Pay Cable Failed")
                        .data(response)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return RechargeStatus.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Ringo Pay Cable Addon Failed")
                .build();
    }

}