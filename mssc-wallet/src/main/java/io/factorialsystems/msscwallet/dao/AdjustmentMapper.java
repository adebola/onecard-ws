package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.Adjustment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdjustmentMapper {
    void save(Adjustment adjustment);
    List<Adjustment> findAll();
}
