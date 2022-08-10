package io.factorialsystems.msscusers.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.domain.search.SearchUserDto;
import io.factorialsystems.msscusers.mapper.dbtransfer.RoleParameter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    Page<User> findAll();
    Page<User> findAdminUser();
    Page<User> findOrdinaryUser();
    Page<User> search(SearchUserDto dto);
    User findUserById(String id);
    User findUserByIdOrNameOrEmail(String id);
    User findByName(String name);
    void save(User user);
    void update(User user);
    Page<User> findUserByOrganizationId(String id);
    Page<User> findUserForOrganization();
    void removeOrganization(String id);
    void updateProfilePicture(Map<String, String> params);
    void addRoles(List<RoleParameter> roleParameters);
    void removeRole(RoleParameter roleParameter);
}
