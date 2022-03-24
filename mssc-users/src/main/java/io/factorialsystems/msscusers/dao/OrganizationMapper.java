package io.factorialsystems.msscusers.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscusers.domain.Organization;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrganizationMapper {
    Page<Organization> findAll();
    Organization findById(String id);
    void save(Organization organization);
    void update(Organization organization);
    Page<Organization> search(String search);
    void delete(String id);
    Integer findUserCount(String id);
}
