package io.factorialsystems.msscprovider.service.singlerecharge.helper;

import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.dto.UserEntryDto;
import io.factorialsystems.msscprovider.dto.UserEntryListDto;
import io.factorialsystems.msscprovider.dto.UserIdListDto;
import io.factorialsystems.msscprovider.dto.user.SimpleUserDto;
import io.factorialsystems.msscprovider.security.RestTemplateInterceptor;
import io.factorialsystems.msscprovider.service.UserService;
import io.factorialsystems.msscprovider.service.file.ExcelWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SingleDownloadRecharge {
    private final UserService userService;
    private final ExcelWriter excelWriter;
    private final SingleRechargeMapper singleRechargeMapper;

    @Value("${api.local.host.baseurl}")
    private String baseUrl;

    public InputStreamResource downloadFailedByUserId(String id) {
        Optional<SimpleUserDto> userById = userService.getUserById(id);

        String title = null;

        if (userById.isPresent()) {
            title = String.format("Recharge Requests for User (%s)", userById.get().getUserName());
        } else {
            title = String.format("Recharge Requests for User (%s)", id);
        }

        List<SingleRechargeRequest> requests = singleRechargeMapper.findByUserId(id);
        return new InputStreamResource(excelWriter.singleRequestToExcel(requests, null, title));
    }

    public InputStreamResource downloadRechargeByDateRange(DateRangeDto dto) {

        String title = null;

        final String pattern = "EEEEE dd MMMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            title = String.format("Single Recharge Download for User %s Date Range %s to %s", dto.getId(),
                    simpleDateFormat.format(dto.getStartDate()), simpleDateFormat.format(dto.getEndDate()));
        } else if (dto.getStartDate() != null) {
            title = String.format("Single Recharge Download for User %s Date %s", dto.getId(), simpleDateFormat.format(dto.getStartDate()));
        } else {
            title = String.format("Single Recharge Download for User %s", dto.getId());
        }

        List<SingleRechargeRequest> requests = singleRechargeMapper.findByUserIdAndDateRange(dto);
        return new InputStreamResource(excelWriter.singleRequestToExcel(requests, null, title));
    }

    public InputStreamResource downloadFailed(String type) {
        List<SingleRechargeRequest> requests;

        if (type.equals("all")) {
            requests = singleRechargeMapper.findListFailedRequests();
        } else {
           requests = singleRechargeMapper.findListUnresolvedFailedRequests();
        }

        List<String> ids = requests.stream()
                .map(SingleRechargeRequest::getUserId)
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

            return new InputStreamResource(excelWriter.singleRequestToExcel(requests, userIdMap, "Failed Single Recharges"));
        }

        return new InputStreamResource(excelWriter.singleRequestToExcel(requests, null, "Failed Single Recharges"));
    }
}
