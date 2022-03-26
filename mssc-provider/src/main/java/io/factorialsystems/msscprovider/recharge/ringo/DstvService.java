package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.RingoValidateCableRequestDto;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DstvService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;
    private final RechargeMapstructMapper rechargeMapstructMapper;

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

    public RechargeResponseStatus validateCable(RingoValidateCableRequestDto request){
        RingoValidateCableRequest ringoValidateCableRequest = rechargeMapstructMapper.ringoValidateCableRequest(request);

        switch (request.getServiceCode()){
            case "dstv":
                ringoValidateCableRequest.setCableType(ringoProperties.getDstv());
                break;
            case "gotv":
                ringoValidateCableRequest.setCableType(ringoProperties.getGotv());
                break;
            case "startimes":
                ringoValidateCableRequest.setCableType(ringoProperties.getStartimes());
                break;
        }

        ringoValidateCableRequest.setCableServiceCode(ringoProperties.getCableVerification());


        log.info("INFO "+ringoValidateCableRequest);

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(ringoValidateCableRequest), getHeader());

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

        } catch (Exception e) {
            e.printStackTrace();

            return RechargeResponseStatus.builder()
                    .status(true)
                    .message("Ringo Validate Cable Failed, But Below result is just for testing.")
                    .data(getSampleResponse(ringoValidateCableRequest.getCableType()))
                    .build();
        }

        return RechargeResponseStatus.builder()
                .status(false)
                .message("Ringo Validate Cable Failed")
                .build();
    }

    private RingoValidateCableResponse getSampleResponse(String cableType) {
        RingoValidateCableResponse sample = new RingoValidateCableResponse();
        sample.setCustomerName("GEORGE IGWE-LAGII-INTER");
        sample.setMessage("Successful");
        sample.setStatus("200");
        sample.setType(cableType);

        List<RingoValidateCableResponse.ProductItem> productItemList = new ArrayList<>();
        RingoValidateCableResponse.ProductItem productItem0 = new RingoValidateCableResponse.ProductItem();
        productItem0.setCode("ACSSE36");
        productItem0.setMonth(1);
        productItem0.setName("DStv Access");
        productItem0.setPrice(2000);
        productItem0.setPeriod(1);

        RingoValidateCableResponse.ProductItem productItem1 = new RingoValidateCableResponse.ProductItem();
        productItem1.setCode("COFAME36");
        productItem1.setMonth(1);
        productItem1.setName("DStv Family");
        productItem1.setPrice(4000);
        productItem1.setPeriod(1);

        RingoValidateCableResponse.ProductItem productItem2 = new RingoValidateCableResponse.ProductItem();
        productItem2.setCode("COMPE36");
        productItem2.setMonth(1);
        productItem2.setName("DStv Compact");
        productItem2.setPrice(6800);
        productItem2.setPeriod(1);

        productItemList.add(productItem0);
        productItemList.add(productItem1);
        productItemList.add(productItem2);
        sample.setProduct(productItemList);

        return sample;
    }

    public RechargeResponseStatus fetchAddonList(RingoFetchDstvAddonRequest request){

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            log.info("INFO "+request.toString());

            RingoFetchAddonDstvResponse response = restTemplate.postForObject(ringoProperties.getDstvAddon(), entity, RingoFetchAddonDstvResponse.class);

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
                .code(singleRechargeRequest.getProductId())
                .type(singleRechargeRequest.getServiceCode())
                .period("1")
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