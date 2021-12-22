package io.factorialsystems.msscvoucher.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Batch;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface BatchMapper {
    Page<Batch> findAll();
    Page<Batch> search(String search);
    Page<Batch> findByClusterId(String id);
    void suspend(String id);
    void unsuspend(String id);
    void activate(Map<String, Object> map);
    void update(Batch batch);
    Batch findById(String id);
    void generateBatch(Batch batch);
    void adjustBalances(Map<String, Object> map);
    void changeVoucherExpiry(Map<String, Object> map);

//    Integer checkBatchUsed(String id);
//    Integer checkBatchExists(String id);
//    void changeDenomination(Map<String, Object> args);
//    void changeExpiry(Map<String, Object> args);
//    void activateBatch(String id);
}
