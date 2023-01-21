package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.RechargeReportMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.CombinedRechargeList;
import io.factorialsystems.msscprovider.domain.CombinedRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.report.ProviderExpenditure;
import io.factorialsystems.msscprovider.domain.report.RechargeReportRequest;
import io.factorialsystems.msscprovider.domain.report.ReportIndividualRequest;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
import io.factorialsystems.msscprovider.mapper.recharge.CombinedRequestMapstructMapper;
import io.factorialsystems.msscprovider.mapper.report.RechargeReportMapstructMapper;
import io.factorialsystems.msscprovider.utils.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeReportService {
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

        if (dto.getType() == null || dto.getType().equals("all")) {
            // Run Singe and Bulk
            request = mapstructMapper.toRequest(dto);

            log.info("All Report Request: {}", request);

            Stream<CombinedRechargeRequest> singleStream = singleRechargeMapper.findSingleRechargeByCriteria(request)
                    .stream()
                    .map(combinedRequestMapstructMapper::singleToCombined);

            Stream<CombinedRechargeRequest> bulkStream = bulkRechargeMapper.findBulkRechargeByCriteria(request).stream()
                    .map(b -> mapBulkToIndividual(b, request))
                    .flatMap(Collection::stream)
                    .map(combinedRequestMapstructMapper::reportIndividualToCombined);

            return new CombinedRechargeList(Stream.concat(singleStream, bulkStream).collect(Collectors.toList()));
        } else if (dto.getType().equals("single")) {
            // Run Single ONLY
            request = mapstructMapper.toRequest(dto);

            log.info("Single Only Report Request: {}", request);

            return new CombinedRechargeList(
                    singleRechargeMapper.findSingleRechargeByCriteria(request)
                            .stream()
                            .map(combinedRequestMapstructMapper::singleToCombined)
                            .collect(Collectors.toList())
            );
        } else if (dto.getType().equals("bulk")) {
            // Run Bulk Only
            request = mapstructMapper.toRequest(dto);

            log.info("Bulk Only Report Request: {}", request);

            return new CombinedRechargeList(
                    bulkRechargeMapper.findBulkRechargeByCriteria(request).stream()
                            .map(b -> mapBulkToIndividual(b, request))
                            .flatMap(Collection::stream)
                            .map(combinedRequestMapstructMapper::reportIndividualToCombined)
                            .collect(Collectors.toList())
            );
        } else {
            throw new RuntimeException(String.format("Invalid Report type %s requested, valid options are 'all', 'single' and 'bulk'", dto.getType()));
        }
    }

    private List<ReportIndividualRequest> mapBulkToIndividual(NewBulkRechargeRequest b, RechargeReportRequest r) {
        Map<String, String> map = new HashMap<>();
        map.put("id", b.getId());

        if (r.getServiceId() != null) {
            map.put("serviceId", String.valueOf(r.getServiceId()));
        }

        if (r.getStatus() != null) {
            if (r.getStatus()) {
                map.put("status", String.valueOf(1));
            } else {
                map.put("status", String.valueOf(0));
            }
        }

        return bulkRechargeMapper.findBulkIndividualRequestsByCriteria(map);
    }
}
