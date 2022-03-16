package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.RechargeResponseStatus;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoFetchDstvAddonRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoPayDstvWithAddonRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoValidateDstvRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoFetchAddonDstvResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoPayWithAddonDstvResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoPayWithOutAddonDstvResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoValidateDstvResponse;
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

    public RechargeResponseStatus validateDstv(RingoValidateDstvRequest request){
        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            RingoValidateDstvResponse response = restTemplate.postForObject(ringoProperties.getBaseUrl(), entity, RingoValidateDstvResponse.class);

            if(response!=null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeResponseStatus.builder()
                        .status(true)
                        .message("Ringo Validate Dstv Successful")
                        .data(response)
                        .build();
            }

            if(response!=null && !Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeResponseStatus.builder()
                        .status(false)
                        .message("Ringo Validate Dstv Failed")
                        .data(response)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return RechargeResponseStatus.builder()
                .status(false)
                .message("Ringo Validate Dstv Failed")
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
        return singleRechargeRequest.getWithAddon() ? payWithAddon(singleRechargeRequest) : payWithOutAddon(singleRechargeRequest);
    }

    private RechargeStatus payWithAddon(SingleRechargeRequest singleRechargeRequest){
        RingoPayDstvWithAddonRequest request = RingoPayDstvWithAddonRequest
                .builder()
                .serviceCode(singleRechargeRequest.getServiceCode())
                .requestId(singleRechargeRequest.getId())
                .code(singleRechargeRequest.getCode())
                .addondetails(singleRechargeRequest.getAddondetails())
                .period(singleRechargeRequest.getPeriod())
                .smartCardNo(singleRechargeRequest.getRecipient())
                .name(singleRechargeRequest.getName())
                .build();
        request.setCode(singleRechargeRequest.getServiceCode());

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            RingoPayWithAddonDstvResponse response = restTemplate.postForObject(ringoProperties.getBaseUrl(), entity, RingoPayWithAddonDstvResponse.class);

            if(response!=null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Ringo Pay Dstv With Addon Successful")
                        .data(response)
                        .build();
            }

            if(response!=null && !Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeStatus.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Ringo Pay Dstv With Addon Failed")
                        .data(response)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return RechargeStatus.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Ringo Pay Dstv With Addon Failed")
                .build();
    }

    private RechargeStatus payWithOutAddon(SingleRechargeRequest singleRechargeRequest){
        RingoPayDstvWithAddonRequest request = RingoPayDstvWithAddonRequest
                .builder()
                .serviceCode(singleRechargeRequest.getServiceCode())
                .requestId(singleRechargeRequest.getId())
                .code(singleRechargeRequest.getCode())
                .addondetails(singleRechargeRequest.getAddondetails())
                .period(singleRechargeRequest.getPeriod())
                .smartCardNo(singleRechargeRequest.getRecipient())
                .name(singleRechargeRequest.getName())
                .build();
        request.setCode(singleRechargeRequest.getServiceCode());

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            RingoPayWithOutAddonDstvResponse response = restTemplate.postForObject(ringoProperties.getBaseUrl(), entity, RingoPayWithOutAddonDstvResponse.class);

            if(response!=null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Ringo Pay Dstv Without Addon Successful")
                        .data(response)
                        .build();
            }

            if(response!=null && !Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())){
                return RechargeStatus.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Ringo Pay Dstv Without Addon Failed")
                        .data(response)
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return RechargeStatus.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Ringo Pay Dstv Without Addon Failed")
                .build();
    }

}