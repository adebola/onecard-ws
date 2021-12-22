package io.factorialsystems.msscreports.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscreports.domain.Report;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {
    Page<Report> findAll();
    Page<Report> search(String s);
    Report findById(Integer id);
    void update(Report report);
    void save(Report report);
}
