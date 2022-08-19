package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.RingoDataPlan;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.dto.SpectranetRingoDataPlan;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DataPlanMapstructMapperDecorator implements DataPlanMapstructMapper {
    private DataPlanMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(DataPlanMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Override
    public DataPlanDto ringoPlanToDto(RingoDataPlan plan) {
        return mapstructMapper.ringoPlanToDto(plan);
    }

    @Override
    public List<DataPlanDto> listRingoPlanToDto(List<RingoDataPlan> plans) {
        return mapstructMapper.listRingoPlanToDto(plans);
    }

    @Override
    public DataPlanDto spectranetPlanToDto(SpectranetRingoDataPlan.IndividualPlan plan) {
        return DataPlanDto.builder()
                .price(String.valueOf(plan.getPrice()))
                .product_id(String.format("spectranet_%.0f", plan.getPrice()))
                .network("SPECTRANET-DATA")
                .allowance(String.format("%.0f Naira Pin", plan.getPrice()))
                .category("None")
                .validity(("None"))
                .build();
    }

    @Override
    public List<DataPlanDto> listSpectranetPlanToDto(List<SpectranetRingoDataPlan.IndividualPlan> plans) {
        if (plans == null) {
            return null;
        } else {
            List<DataPlanDto> list = new ArrayList<>(plans.size());
            plans.forEach(plan -> list.add(this.spectranetPlanToDto(plan)));

            return list;
        }
    }
}
