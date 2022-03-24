package io.factorialsystems.msscapiuser.dao;

import io.factorialsystems.msscapiuser.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUserId(String id);
}
