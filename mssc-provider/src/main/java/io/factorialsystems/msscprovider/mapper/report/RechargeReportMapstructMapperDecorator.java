package io.factorialsystems.msscprovider.mapper.report;

import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.report.RechargeReportRequest;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.service.UserService;
import io.factorialsystems.msscprovider.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class RechargeReportMapstructMapperDecorator implements RechargeReportMapstructMapper {
    private  UserService userService;
    private  ServiceActionMapper serviceActionMapper;
    private  RechargeReportMapstructMapper mapstructMapper;

    public static final String SUCCESS_STATUS = "success";
    public static final String FAIL_STATUS = "failed";
    public static final String BULK_TYPE = "bulk";
    public static final String SINGLE_TYPE = "single";

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMapstructMapper(RechargeReportMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setServiceActionMapper(ServiceActionMapper serviceActionMapper) {
        this.serviceActionMapper = serviceActionMapper;
    }

    @Override
    public RechargeReportRequest toRequest(RechargeReportRequestDto dto) {
        RechargeReportRequest request = mapstructMapper.toRequest(dto);

        // Check UserId and make sure it is valid
        if (dto.getUserId() != null) {
            if (!userService.isUserValid(dto.getUserId())) {
                throw new ResourceNotFoundException("User", "id", dto.getUserId());
            }

            request.setUserId(dto.getUserId());
        }

        // Check Services make sure they are valid services
        if (dto.getServiceCode() != null) {
            ServiceAction serviceAction = serviceActionMapper.findByCode(dto.getServiceCode());

            if (serviceAction == null) {
                throw new ResourceNotFoundException("Service", "code", dto.getServiceCode());
            }

            request.setServiceId(serviceAction.getId());
        }

        // Check for Run status
        final String status = dto.getStatus();

        if (status != null) {
            if (status.equals(SUCCESS_STATUS)) {
                request.setStatus(false);
            } else if (status.equals(FAIL_STATUS)) {
                request.setStatus(true);
            } else {
                throw new RuntimeException(String.format("Invalid Recharge Status %s specified should either be 'success', 'fail', 'all' or left blank for all", dto.getStatus()));
            }
        }

        final String type = dto.getType();

        if (type != null) {
            if (type.equals(BULK_TYPE) || type.equals(SINGLE_TYPE)) {
                request.setType(type);
            } else  {
                throw new RuntimeException(String.format("Invalid Recharge Type should %s either 'single', 'bulk' oe left blank for both", type));
            }
        }

        final Date startDate = dto.getStartDate();

        if (startDate != null) {
            request.setStartDate(Utility.zeroDateTime(startDate));
        }

        final Date endDate = dto.getEndDate();

        if (endDate != null) {
            request.setEndDate(Utility.maxDateTime(endDate));
        }

        return request;
    }
}
