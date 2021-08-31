package io.factorialsystems.msscvoucher.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Batch;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface BatchMapper {
    Page<Batch> findAllBatches();
    Page<Batch> Search(String search);
    Batch findBatch(String id);
    void generateBatch(Map<String, Object> args);
    Integer checkBatchUsed(String id);
    Integer checkBatchExists(String id);
    void deleteBatch(String id);
    void changeDenomination(Map<String, Object> args);
    void changeExpiry(Map<String, Object> args);
    void activateBatch(String id);
    void deActivateBatch(String id);
}
