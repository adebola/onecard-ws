package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.AccountDetailsRequest;
import io.factorialsystems.msscprovider.dto.ServerResponse;
import io.factorialsystems.msscprovider.recharge.ekedp.ServicesImpl;
import io.factorialsystems.msscprovider.wsdl.CustomerInfo;
import io.factorialsystems.msscprovider.wsdl.OrderDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "EKEDC Management", description = "Endpoint")
@RequestMapping("api/test/ekedc")
public class EKEDPController {

    @Autowired
    private ServicesImpl services;

    @PostMapping("/validate")
    @ApiOperation(value = "GET Meter Details by parsing meter number.", response = ServerResponse.class)
    public ResponseEntity<ServerResponse> validateCustomer(@RequestBody AccountDetailsRequest accountDetailsRequest){
        CustomerInfo customerInfo = services.validateCustomer(accountDetailsRequest.getMeter(), "EKEDP");
        ServerResponse response = new ServerResponse(false, "Failed", null);

        if(customerInfo!=null){
            response.setStatus(true);
            response.setMessage("Success");
            response.setData(customerInfo);
        }

        return new ResponseEntity<>(response, customerInfo==null? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/recharge")
    @ApiOperation(value = "Perform a recharge, note that recharge type depends on the accountType parsed, either PREPAID or POSTPAID.", response = ServerResponse.class)
    public ResponseEntity<ServerResponse> performRecharge(@RequestBody SingleRechargeRequest singleRechargeRequest){
        OrderDetails orderDetails = services.performRecharge(singleRechargeRequest);
        ServerResponse response = new ServerResponse(false, "Failed", null);

        if(orderDetails!=null){
            response.setStatus(true);
            response.setMessage("Success");
            response.setData(orderDetails);
        }

        return new ResponseEntity<>(response, orderDetails==null? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}