package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.AutoIndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.AutoRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.ShortAutoRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.service.AutoRechargeService;
import io.factorialsystems.msscprovider.utils.K;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;

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
        List<AutoIndividualRequestDto> recipients = dto.getRecipients();

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

        individualRequestHelper.checkAutoRequests(request.getRecipients());
        return request;
    }

    @Override
    public AutoRechargeRequestDto requestToDto(AutoRechargeRequest request) {
        return mapstructMapper.requestToDto(request);
    }

    @Override
    public AutoIndividualRequest individualDtoToIndividual(AutoIndividualRequestDto dto) {
        return mapstructMapper.individualDtoToIndividual(dto);
    }

    @Override
    public List<AutoIndividualRequest> listIndividualDtoToIndividual(List<AutoIndividualRequestDto> dtos) {
        return mapstructMapper.listIndividualDtoToIndividual(dtos);
    }

    @Override
    public AutoIndividualRequestDto individualToIndividualDto(AutoIndividualRequest request) {
        return mapstructMapper.individualToIndividualDto(request);
    }

    @Override
    public List<AutoIndividualRequestDto> listIndividualToIndividualDto(List<AutoIndividualRequest> requests) {
        return mapstructMapper.listIndividualToIndividualDto(requests);
    }

    @Override
    public AutoIndividualRequestDto autoToNonAuto(IndividualRequest request) {
        return mapstructMapper.autoToNonAuto(request);
    }

    @Override
    public List<AutoIndividualRequestDto> listAutoToNonAuto(List<IndividualRequestDto> requests) {
        return mapstructMapper.listAutoToNonAuto(requests);
    }

    @Override
    public IndividualRequestDto nonAutoToAuto(AutoIndividualRequest request) {
        return mapstructMapper.nonAutoToAuto(request);
    }

    @Override
    public List<IndividualRequestDto> listNonAutoToAuto(List<AutoIndividualRequest> requests) {
        return mapstructMapper.listNonAutoToAuto(requests);
    }

    @Override
    public AutoRechargeRequestDto uploadToRechargeRequestDto(AutoUploadFileRechargeRequestDto dto) {
        return mapstructMapper.uploadToRechargeRequestDto(dto);
    }

    @Override
    public ShortAutoRechargeRequestDto shortDtoToShort(ShortAutoRechargeRequest request) {
        ShortAutoRechargeRequestDto dto = mapstructMapper.shortDtoToShort(request);

        switch (request.getRecurringType()) {
            case AutoRechargeService.AUTO_RECURRING_WEEKLY_TYPE:
                dto.setRecurringType("Weekly");
                break;

            case AutoRechargeService.AUTO_RECURRING_MONTHLY_TYPE:
                dto.setRecurringType("Monthly");
                break;
        }

        return dto;
    }

    @Override
    public List<ShortAutoRechargeRequestDto> listShortDtoToShort(List<ShortAutoRechargeRequest> requests) {

        if (requests == null) {
            return null;
        } else {
            List<ShortAutoRechargeRequestDto> list = new ArrayList(requests.size());

            for (ShortAutoRechargeRequest shortAutoRechargeRequest : requests) {
                list.add(this.shortDtoToShort(shortAutoRechargeRequest));
            }

            return list;
        }
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
