package io.factorialsystems.msscprovider.service.singlerecharge.helper;

import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.UserEntryDto;
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

    public InputStreamResource downloadByUserId(String id) {
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

    public InputStreamResource downloadFailed() {
        List<SingleRechargeRequest> requests = singleRechargeMapper.findListFailedRequests();

        List<UserEntryDto> userEntries = requests.stream()
                .map(SingleRechargeRequest::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .map(r -> new UserEntryDto(r, null))
                .collect(Collectors.toList());

        UserIdListDto dto = new UserIdListDto(userEntries);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        UserIdListDto userIdListDto =
                restTemplate.postForObject(baseUrl + "/api/v1/user/usernames", dto, UserIdListDto.class);

        if (userIdListDto != null && userIdListDto.getEntries() != null && userIdListDto.getEntries().size() > 0) {
            Map<String, String> userIdMap = userIdListDto.getEntries().stream()
                    .collect(Collectors.toMap(UserEntryDto::getId, UserEntryDto::getName));

            return new InputStreamResource(excelWriter.singleRequestToExcel(requests, userIdMap, "Failed Single Recharges"));
        }

        return new InputStreamResource(excelWriter.singleRequestToExcel(requests, null, "Failed Single Recharges"));
    }
}
