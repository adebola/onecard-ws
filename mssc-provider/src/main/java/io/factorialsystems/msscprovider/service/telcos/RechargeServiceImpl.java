package io.factorialsystems.msscprovider.service.telcos;

import io.factorialsystems.msscprovider.dto.ServerResponse;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RechargeServiceImpl implements RechargeService {

    @Override
    public Boolean isParametersChecked(SingleRechargeRequestDto singleRechargeRequest) {
        log.info(String.valueOf(singleRechargeRequest));

        //ServiceCode is assumed to help differentiate between telcos, we can either change or create a new properties that takes the TelcoType
        if ("MTN".equals(singleRechargeRequest.getServiceCode())) {
            //Below is to simulate a basic check needed to proceed a specific Telco
            return singleRechargeRequest.getTelephone().startsWith("0803") ||
                    singleRechargeRequest.getTelephone().startsWith("0703") ||
                    singleRechargeRequest.getTelephone().startsWith("0903");
        }

        return false;
    }

    @Override
    public ServerResponse startRecharge(SingleRechargeRequestDto singleRechargeRequest) {
        //0. Do a parameter check
        //1. check if it's an exiting request
        //2. if existing, check if closed and return error, else return existing object
        //3. if not existing, create new record and continue to step 4
        //4. call payment gateway, update record with initialised payment gateway details and return record.

        ServerResponse serverResponse = new ServerResponse();

        if(!isParametersChecked(singleRechargeRequest))
            throw new RuntimeException("Invalid or Missing Parameters");

        if(singleRechargeRequest.getServiceCost().intValue() < 100){
            serverResponse.setStatus(false);
            serverResponse.setMessage("Service cost is invalid");
            return serverResponse;
        }

        serverResponse.setData(new SingleRechargeRequestResponseDto());
        serverResponse.setStatus(true);
        serverResponse.setMessage("Success");
        return serverResponse;
    }

    @Override
    public ServerResponse completeRecharge(String rechargeRequestId) {
        //1. check if it's an exiting request
        //2. if existing, check if closed and return error, else proceed to step ?
        //3. if not existing, return error
        //4. confirm payment is done by calling payment gateway and update record accordingly
        //5. if payment is successful, call respective telco, update record with telco response and return record.
        //6. if payment is unsuccessful or pending, update record and return record.
        return null;
    }
}
