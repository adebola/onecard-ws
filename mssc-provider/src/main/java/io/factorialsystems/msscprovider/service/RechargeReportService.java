package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.RechargeReportMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.CombinedRechargeList;
import io.factorialsystems.msscprovider.domain.CombinedRechargeRequest;
import io.factorialsystems.msscprovider.domain.report.ProviderExpenditure;
import io.factorialsystems.msscprovider.domain.report.RechargeReportRequest;
import io.factorialsystems.msscprovider.domain.report.ReportIndividualRequest;
import io.factorialsystems.msscprovider.dto.UserEntryDto;
import io.factorialsystems.msscprovider.dto.UserEntryListDto;
import io.factorialsystems.msscprovider.dto.UserIdListDto;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
import io.factorialsystems.msscprovider.external.client.UserClient;
import io.factorialsystems.msscprovider.mapper.recharge.CombinedRequestMapstructMapper;
import io.factorialsystems.msscprovider.mapper.report.RechargeReportMapstructMapper;
import io.factorialsystems.msscprovider.utils.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeReportService {
    private final UserClient userClient;
    private final BulkRechargeMapper bulkRechargeMapper;
    private final RechargeReportMapper rechargeReportMapper;
    private final SingleRechargeMapper singleRechargeMapper;
    private final RechargeReportMapstructMapper mapstructMapper;
    private final CombinedRequestMapstructMapper combinedRequestMapstructMapper;

    public ProviderExpenditure getShortRechargeExpenditure(RechargeProviderRequestDto dto) {

        if (dto.getStartDate() != null) {
            dto.setStartDate(Utility.zeroDateTime(dto.getStartDate()));
        }

        if (dto.getEndDate() != null) {
            dto.setEndDate(Utility.maxDateTime(dto.getEndDate()));
        }

        return rechargeReportMapper.findRechargeProviderExpenditure(dto);
    }

    public CombinedRechargeList runRechargeReport(RechargeReportRequestDto dto) {
        RechargeReportRequest request;

        Comparator<CombinedRechargeRequest> comparator = Comparator.comparingLong(c -> c.getCreatedAt().getTime());

        if (dto.getType() == null || dto.getType().equals("all")) {
            // Run Singe and Bulk
            request = mapstructMapper.toRequest(dto);

            List<CombinedRechargeRequest> singleList = singleRechargeMapper.findSingleRechargeByCriteria(request)
                    .stream()
                    .map(combinedRequestMapstructMapper::singleToCombined)
                    .collect(Collectors.toList());

            List<CombinedRechargeRequest> bulkList = bulkRechargeMapper.findBulkRechargeByCriteria(request)
                    .stream()
                    .map(b -> mapBulkToIndividual(b.getId(), request))
                    .flatMap(Collection::stream)
                    .map(combinedRequestMapstructMapper::reportIndividualToCombined)
                    .collect(Collectors.toList());

            log.info("All Report Request: {}, Single Recharge Size {}, Bulk Recharge Size {}", request, singleList.size(), bulkList.size());

            List<CombinedRechargeRequest> allRequests = Stream.concat(singleList.stream(), bulkList.stream())
                    .sorted(comparator)
                    .collect(Collectors.toList());

            Optional<List<CombinedRechargeRequest>> combinedRechargeRequests = mergeUsersToList(allRequests);
            return combinedRechargeRequests.map(CombinedRechargeList::new).orElseGet(() -> new CombinedRechargeList(allRequests));

        } else if (dto.getType().equals("single")) {
            // Run Single ONLY
            request = mapstructMapper.toRequest(dto);

            List<CombinedRechargeRequest> single = singleRechargeMapper.findSingleRechargeByCriteria(request)
                    .stream()
                    .map(combinedRequestMapstructMapper::singleToCombined)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            log.info("Single Report Request {}, Single Recharge Size {}", request, single.size());

            Optional<List<CombinedRechargeRequest>> combinedRechargeRequests = mergeUsersToList(single);
            return combinedRechargeRequests.map(CombinedRechargeList::new).orElseGet(() -> new CombinedRechargeList(single));

        } else if (dto.getType().equals("bulk")) {
            // Run Bulk Only
            request = mapstructMapper.toRequest(dto);

            List<CombinedRechargeRequest> bulk = bulkRechargeMapper.findBulkRechargeByCriteria(request)
                    .stream()
                    .map(b -> mapBulkToIndividual(b.getId(), request))
                    .flatMap(Collection::stream)
                    .map(combinedRequestMapstructMapper::reportIndividualToCombined)
                    .sorted(comparator)
                    .collect(Collectors.toList());

            log.info("Bulk Only Report Request: {}, Bulk Recharge Size {}", request, bulk.size());

            Optional<List<CombinedRechargeRequest>> combinedRechargeRequests = mergeUsersToList(bulk);
            return combinedRechargeRequests.map(CombinedRechargeList::new).orElseGet(() -> new CombinedRechargeList(bulk));

        } else {
            throw new RuntimeException(String.format("Invalid Report type %s requested, valid options are 'all', 'single' and 'bulk'", dto.getType()));
        }
    }

    private Optional<List<CombinedRechargeRequest>> mergeUsersToList(List<CombinedRechargeRequest> requests) {
        final List<String> ids = requests.stream()
                .map(CombinedRechargeRequest::getUserId)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!ids.isEmpty()) {
            UserIdListDto userIdListDto = new UserIdListDto(ids);
            UserEntryListDto userEntries = userClient.getUserEntries(userIdListDto);

            if (userEntries != null && userEntries.getEntries() != null && userEntries.getEntries().size() > 0) {
                List<CombinedRechargeRequest> collect = requests.stream().peek(u -> {
                    if (u.getUserId() != null) {
                        Optional<UserEntryDto> first = userEntries.getEntries()
                                .stream()
                                .filter(x -> x.getId().equals(u.getUserId()))
                                .findFirst();

                        first.ifPresent(userEntryDto -> u.setUserName(userEntryDto.getName()));
                    }
                }).collect(Collectors.toList());

                return Optional.of(collect);
            }
        }

        return Optional.empty();
    }

    private List<ReportIndividualRequest> mapBulkToIndividual(String id, RechargeReportRequest requestCriteria) {
        Map<String, String> criteriaMap = new HashMap<>();
        criteriaMap.put("id", id);

        if (requestCriteria.getServiceId() != null) {
            criteriaMap.put("serviceId", String.valueOf(requestCriteria.getServiceId()));
        }

        if (requestCriteria.getStatus() != null) {
            if (requestCriteria.getStatus()) {
                criteriaMap.put("status", String.valueOf(1));
            } else {
                criteriaMap.put("status", String.valueOf(0));
            }
        }

        return bulkRechargeMapper.findBulkIndividualRequestsByCriteria(criteriaMap);
    }
}
