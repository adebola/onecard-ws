package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.AutoRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.dto.AutoRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.AutoUploadFileRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.IndividualRequestDto;
import io.factorialsystems.msscprovider.utils.K;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AutoRechargeMapstructMapperDecorator implements AutoRechargeMapstructMapper {
    private IndividualRequestHelper individualRequestHelper;
    private AutoRechargeMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(AutoRechargeMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setIndividualRequestHelper(IndividualRequestHelper individualRequestHelper) {
        this.individualRequestHelper = individualRequestHelper;
    }

    @Override
    public AutoRechargeRequest dtoToRequest(AutoRechargeRequestDto dto) {
        AutoRechargeRequest request = mapstructMapper.dtoToRequest(dto);
        request.setUserId(K.getUserId());

        List<Integer> daysOfWeek = dto.getDaysOfWeek();
        List<Integer> daysOfMonth = dto.getDaysOfMonth();
        List<IndividualRequestDto> recipients = dto.getRecipients();

        if ((daysOfMonth == null || daysOfMonth.isEmpty()) && (daysOfWeek == null || daysOfWeek.isEmpty())) {
            throw new RuntimeException("You must specify either Days of the Week or Month for the recharges to run");
        } else if (daysOfMonth != null && !daysOfMonth.isEmpty() && daysOfWeek != null && !daysOfWeek.isEmpty()) {
            throw new RuntimeException("You must specify either Weekly or Monthly Auto Recharge not both, you can create them separately");
        }

        if (recipients == null || recipients.isEmpty()) {
            throw new RuntimeException("No recipients specified");
        }

        if (dto.getPaymentMode() == null || dto.getPaymentMode().equals("wallet")) {
            request.setPaymentMode("wallet");
        } else {
            throw new RuntimeException(String.format("Invalid Payment Mode (%s) specified", dto.getPaymentMode()));
        }

        if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
            if (daysOfWeek.size() > 7 || daysOfWeek.stream().anyMatch(d -> d < 1 || d > 7)) {
                throw new RuntimeException("Days of the Week must be between 1 and 7 and the size of the submitted array cannot exceed 7");
            }
        }

        if (daysOfMonth != null && !daysOfMonth.isEmpty()) {
            if (daysOfMonth.size() > 31 || daysOfMonth.stream().anyMatch(d -> d < 1 || d > 31)) {
                throw new RuntimeException("Days of the Month must be between 1 and 31 and the size of the submitted array cannot exceed 31");
            }
        }

        Timestamp start_ts = new Timestamp(zeroTime(dto.getStartDate()).getTime());
        request.setStartDate(start_ts);

        individualRequestHelper.checkRequests(request.getRecipients());
        return request;
    }

    @Override
    public AutoRechargeRequestDto requestToDto(AutoRechargeRequest request) {
        return mapstructMapper.requestToDto(request);
    }

    @Override
    public IndividualRequest individualDtoToIndividual(IndividualRequestDto dto) {
        return mapstructMapper.individualDtoToIndividual(dto);
    }

    @Override
    public List<IndividualRequest> listIndividualDtoToIndividual(List<IndividualRequestDto> dtos) {
        return mapstructMapper.listIndividualDtoToIndividual(dtos);
    }

    @Override
    public IndividualRequestDto individualToIndividualDto(IndividualRequest request) {
        return mapstructMapper.individualToIndividualDto(request);
    }

    @Override
    public List<IndividualRequestDto> listIndividualToIndividualDto(List<IndividualRequest> requests) {
        return mapstructMapper.listIndividualToIndividualDto(requests);
    }

    @Override
    public AutoRechargeRequestDto uploadToRechargeRequestDto(AutoUploadFileRechargeRequestDto dto) {
        return mapstructMapper.uploadToRechargeRequestDto(dto);
    }

    private Date zeroTime(Date date) {
        Calendar calendar = Calendar.getInstance();

        if (date != null) {
            calendar.setTime(date);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
