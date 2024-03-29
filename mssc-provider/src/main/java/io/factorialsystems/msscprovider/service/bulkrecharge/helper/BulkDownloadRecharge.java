package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.dto.user.SimpleUserDto;
import io.factorialsystems.msscprovider.external.client.UserClient;
import io.factorialsystems.msscprovider.service.file.ExcelWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkDownloadRecharge {
    private final UserClient userClient;
    private final ExcelWriter excelWriter;
    private final BulkRechargeMapper newBulkRechargeMapper;

    public InputStreamResource userBulk(String id) {
        List<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findListBulkRequestByUserId(id);
        SimpleUserDto simpleDto = userClient.getUserById(id);

        String title;

        if (simpleDto != null && simpleDto.getUserName() != null) {
            title = String.format("Bulk recharges for User %s", simpleDto.getUserName());
        } else {
            title = "Bulk Recharges Report";
        }

        return new InputStreamResource(excelWriter.bulkRequestToExcel(requests, null, title));
    }

    public InputStreamResource userIndividuals(String id) {
        List<IndividualRequest> requests = newBulkRechargeMapper.findBulkIndividualRequests(id);

        if (requests != null && requests.size() > 0) {
            NewBulkRechargeRequest rechargeRequest = newBulkRechargeMapper.findBulkRechargeById(requests.get(0).getBulkRequestId());

            if (rechargeRequest != null && rechargeRequest.getUserId() != null) {
                SimpleUserDto simpleUserDto = userClient.getUserById(rechargeRequest.getUserId());

                String title;

                if (simpleUserDto != null && simpleUserDto.getUserName() != null) {
                    title = String.format("Individual Requests for Bulk Request %s for User %s", rechargeRequest.getId(), simpleUserDto.getUserName());
                } else {
                    title = String.format("Individual Requests for Bulk Request %s", rechargeRequest.getId());
                }

                return new InputStreamResource(excelWriter.bulkIndividualRequestToExcel(requests, title));
            }
        }

        return new InputStreamResource(new ByteArrayInputStream(new byte[0]));
    }

    public InputStreamResource failed(String type) {

        List<NewBulkRechargeRequest> requests;

        if (type.equals("all")) {
            requests = newBulkRechargeMapper.findListFailedRequests();
        } else {
            requests = newBulkRechargeMapper.findListFailedUnResolvedRequests();
        }

        List<String> ids = requests.stream()
                .map(NewBulkRechargeRequest::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        UserIdListDto dto = new UserIdListDto(ids);
        UserEntryListDto userEntries = userClient.getUserEntries(dto);

        if (userEntries != null && userEntries.getEntries() != null && userEntries.getEntries().size() > 0) {
            Map<String, String> userIdMap = userEntries.getEntries().stream()
                    .collect(Collectors.toMap(UserEntryDto::getId, UserEntryDto::getName));

            return new InputStreamResource(excelWriter.bulkRequestToExcel(requests, userIdMap, "Failed Bulk Recharges"));
        }

        return new InputStreamResource(excelWriter.bulkRequestToExcel(requests, null, "Failed Bulk Recharges"));
    }

    public InputStreamResource failedIndividual(String id, String type) {
        List<IndividualRequest> requests;

        if (type.equals("all")) {
            requests = newBulkRechargeMapper.findListFailedIndividuals(id);
        } else {
            requests = newBulkRechargeMapper.findListFailedUnresolvedIndividuals(id);
        }

        if (requests != null && requests.size() > 0) {
            NewBulkRechargeRequest bulkRequest = newBulkRechargeMapper.findBulkRechargeById(requests.get(0).getBulkRequestId());

            SimpleUserDto simpleDto = userClient.getUserById(bulkRequest.getUserId());

            String title;

            if (type.equals("all")) {
                title = String.format("All Failed Individual Recharges for Bulk Request Id %s, by User %s",
                        bulkRequest.getId(),
                        simpleDto == null ? "anonymous" : simpleDto.getUserName());
            } else {
                title = String.format("Unresolved Failed Individual Recharges for Bulk Request Id %s, by User %s",
                        bulkRequest.getId(),
                        simpleDto == null ? "anonymous" : simpleDto.getUserName());
            }

            return new InputStreamResource(excelWriter.bulkIndividualRequestToExcel(requests, title));
        }

        throw new RuntimeException(String.format("Unable to load Requests for %s", id));
    }

    public InputStreamResource downloadRechargeByDateRange(DateRangeDto dto) {
        String title = null;

        final String pattern = "EEEEE dd MMMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            title = String.format("Bulk Recharge Download for User %s Date Range %s to %s", dto.getId(),
                    simpleDateFormat.format(dto.getStartDate()), simpleDateFormat.format(dto.getEndDate()));
        } else if (dto.getStartDate() != null) {
            title = String.format("Bulk Recharge Download for User %s Date %s", dto.getId(), simpleDateFormat.format(dto.getStartDate()));
        } else {
            title = String.format("Bulk Recharge Download for User %s", dto.getId());
        }

        CombinedRequestDto combinedRequestDto = new CombinedRequestDto();
        combinedRequestDto.setId(dto.getId());
        combinedRequestDto.setStartDate(dto.getStartDate());
        combinedRequestDto.setEndDate(dto.getEndDate());

        List<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findBulkByUserIdAndDateRange(combinedRequestDto);
        return new InputStreamResource(excelWriter.bulkRequestToExcel(requests, null, title));
    }
}
