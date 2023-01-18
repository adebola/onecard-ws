package io.factorialsystems.msscprovider.mapper.report;

import io.factorialsystems.msscprovider.domain.report.RechargeReportRequest;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
@DecoratedWith(RechargeReportMapstructMapperDecorator.class)
public interface RechargeReportMapstructMapper {
    @Mappings({
            @Mapping(target = "startDate", ignore = true),
            @Mapping(target = "endDate", ignore = true),
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "type", ignore = true)
    })
    RechargeReportRequest toRequest(RechargeReportRequestDto dto);
}
