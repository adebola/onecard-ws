package io.factorialsystems.msscusers.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscusers.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    Page<User> findAll();
    Page<User> search(String searchString);
    User findUserById(String id);
    User findByName(String name);
    void save(User user);
    void update(User user);
}
