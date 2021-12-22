package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.RingoDataPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RingoDataPlanMapper {
    List<RingoDataPlan> findAll();
    RingoDataPlan findById(String id);
    void save(RingoDataPlan plan);
    void saveList(List<RingoDataPlan> plans);
}
