package io.factorialsystems.msscvoucher.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Cluster;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClusterMapper {
    Page<Cluster> findAll();
    Page<Cluster> Search(String search);
    List<Cluster> findValid();
    Cluster findById(String id);
    void adjustBalance(Map<String, Object> map);
    void suspend(String id);
    void unsuspend(String id);
    void save(Cluster cluster);
    void update(Cluster cluster);
}
