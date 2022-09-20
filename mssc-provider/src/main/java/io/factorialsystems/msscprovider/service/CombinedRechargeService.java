package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.CombinedRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.CombinedRequestDto;
import io.factorialsystems.msscprovider.dto.user.SimpleUserDto;
import io.factorialsystems.msscprovider.mapper.recharge.CombinedRequestMapstructMapper;
import io.factorialsystems.msscprovider.service.file.ExcelWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CombinedRechargeService {
    private final ExcelWriter excelWriter;
    private final UserService userService;
    private final NewBulkRechargeMapper bulkRechargeMapper;
    private final SingleRechargeMapper singleRechargeMapper;
    private final CombinedRequestMapstructMapper combinedRequestMapstructMapper;

    public ByteArrayInputStream getCombinedResource(CombinedRequestDto dto) {
        List<NewBulkRechargeRequest>bulkRequests = bulkRechargeMapper.findBulkByUserIdAndDateRange(dto);
        List <SingleRechargeRequest> singleRequests = singleRechargeMapper.findSingleByUserIdAndDateRange(dto);

        List<CombinedRechargeRequest> combinedRequests = null;

        if (singleRequests != null && !singleRequests.isEmpty()) {
            combinedRequests = singleRequests.stream()
                    .map(combinedRequestMapstructMapper::singleToCombined)
                    .collect(Collectors.toList());
        }

        if (combinedRequests == null) {
            combinedRequests = new ArrayList<>();
        }

        List<CombinedRechargeRequest> finalCombinedRequests = combinedRequests;

        bulkRequests.forEach(b -> {
            List<CombinedRechargeRequest> bulkCombinedRequests = bulkRechargeMapper.findBulkIndividualRequests(b.getId()).stream()
                    .map(combinedRequestMapstructMapper::individualToCombined)
                    .peek(m -> m.setCreatedAt(b.getCreatedAt()))
                    .collect(Collectors.toList());

            finalCombinedRequests.addAll(bulkCombinedRequests);
        });

//        List<CombinedRechargeRequest> bulkCombinedRequests = bulkRequests.stream()
//                .map(b -> bulkRechargeMapper.findBulkIndividualRequests(b.getId()))
//                .flatMap(Collection::stream)
//                .map(combinedRequestMapstructMapper::individualToCombined)
//                .peek(a -> a.setCreatedAt())
//                .collect(Collectors.toList());

        combinedRequests.addAll(finalCombinedRequests);

        SimpleUserDto userDto = userService.getUserById(dto.getId()).orElse(null);

        String title;

        Locale locale = new Locale("en", "NG");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

        if (userDto == null) {
            title = String.format("All Recharge Report for Date Range %s to %s",
                    dateFormat.format(dto.getStartDate()),
                    dto.getEndDate() == null ? "Date" : dateFormat.format(dto.getEndDate()));
        } else {
            title = String.format("All Recharge Report for Date Range %s to %s for %s %s",
                    dateFormat.format(dto.getStartDate()),
                    dto.getEndDate() == null ? "Date" : dateFormat.format(dto.getEndDate()),
                    userDto.getFirstName(), userDto.getLastName());
        }

        return excelWriter.combinedRequestToExcel(combinedRequests, title);
    }
}
