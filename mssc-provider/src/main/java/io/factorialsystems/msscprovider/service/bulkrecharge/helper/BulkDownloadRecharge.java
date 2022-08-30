package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.UserEntryDto;
import io.factorialsystems.msscprovider.dto.UserEntryListDto;
import io.factorialsystems.msscprovider.dto.UserIdListDto;
import io.factorialsystems.msscprovider.dto.user.SimpleUserDto;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.service.file.ExcelWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkDownloadRecharge {
    private final ExcelWriter excelWriter;

    @Value("${api.local.host.baseurl}")
    private String baseUrl;
    private final NewBulkRechargeMapper newBulkRechargeMapper;

    public InputStreamResource userBulk(String id) {
        List<NewBulkRechargeRequest> requests = newBulkRechargeMapper.findListBulkRequestByUserId(id);
        SimpleUserDto simpleDto = getUser(id);

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
                SimpleUserDto simpleUserDto = getUser(rechargeRequest.getUserId());

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

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        UserEntryListDto userEntries =
                restTemplate.postForObject(baseUrl + "/api/v1/user/usernames", dto, UserEntryListDto.class);

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

            SimpleUserDto simpleDto = getUser(bulkRequest.getUserId());

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

    private SimpleUserDto getUser(String id) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        return restTemplate.getForObject(baseUrl + "/api/v1/user/simple/" + id, SimpleUserDto.class);
    }
}
