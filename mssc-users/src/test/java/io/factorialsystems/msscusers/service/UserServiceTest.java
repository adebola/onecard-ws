package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.dao.UserMapper;
import io.factorialsystems.msscusers.domain.search.SearchUserDto;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.PagedDto;
import io.factorialsystems.msscusers.dto.SimpleUserDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@CommonsLog
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Test
    void findAdminUsers() {
        var x = userService.findAdminUsers(1, 20);
        assertNotNull(x);
        log.info(x.getTotalSize());
        log.info(x);
    }

    @Test
    void findOrdinaryUsers() {
        var x = userService.findOrdinaryUsers(1, 20);
        assertNotNull(x);
        log.info(x.getTotalSize());
        log.info(x);
    }

    @Test
    void search() {
        String search = "fol";
        SearchUserDto dto = new SearchUserDto();
        dto.setSearch(search);
        dto.setAdmin(true);
//        dto.setOrdinary(true);
        var x = userMapper.search(dto);
        log.info(x);
        log.info(x.size());
    }

    @Test
    void setPassword() {
//        PasswordEncoder encoder = new BCryptPasswordEncoder();
//        User user = userMapper.findUserById("28e05596-9ad0-4187-ac11-fd93fb7701af");
//        user.setSecret(encoder.encode("password"));
//        userMapper.update(user);
    }

    @Test
    void findUserByIdOrNameOrEmail() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String email = "aomoboya@icloud.com";

        SimpleUserDto s1 = userService.findSimpleUserByIdOrNameOrEmail(id);
        SimpleUserDto s2 = userService.findSimpleUserByIdOrNameOrEmail(email);
        assertNotNull(s1);
        assertNotNull(s2);

        assertEquals(s1.getId(), s2.getId());
        log.info(s1);
        log.info(s2);
    }

    @Test
    void getUsers() {
        PagedDto<KeycloakUserDto> dtos = userService.findUsers(1, 20);
        log.info(dtos.getTotalSize());
        log.info(dtos.getList().get(0).getId());
        log.info(dtos.getList().get(0).getEmail());
        log.info(dtos.getList().get(0).getFirstName());
        log.info(dtos.getList().get(0).getAccount());

        assertNull(dtos.getList().get(0).getAccount());
    }

    @Test
    void getRealmUser() {
        String id = "ab0826d8-d01f-43ea-838d-59aea219fc3f";

//        KeycloakUserDto dto = userService.findRealmUserById(id);
//        assertEquals(id, dto.getId());
//        log.info(dto.getId());
//        log.info(dto.getFirstName());
//        log.info(dto.getEmail());
//        log.info(dto.getUsername());
//        log.info(dto.getCreatedDate());
//
//        assertEquals(null, dto.getAccount());
    }

    @Test
    void getUser() {
//        String id = "ab0826d8-d01f-43ea-838d-59aea219fc3f";
//
//        KeycloakUserDto dto = userService.findUserById(id);
//        assertEquals(id, dto.getId());
//        log.info(dto.getId());
//        log.info(dto.getFirstName());
//        log.info(dto.getEmail());
//        log.info(dto.getUsername());
//        log.info(dto.getCreatedDate());
    }

    @Test
    void updateUser() {
//        String id = "ab0826d8-d01f-43ea-838d-59aea219fc3f";
//
//        KeycloakUserDto dto = new KeycloakUserDto();
//        dto.setFirstName("Adebola");
//        dto.setLastName("Omoboya");
//        dto.setEnabled(true);
//        userService.updateUser(id, dto);
    }

    @Test
    void changePassword() {
//        String id = "98b74149-8cd7-4e77-9260-7ebf30092b14";
//        userService.changePassword(id, "abcd1234");
    }

    @Test
    void getUserRoles() {
//        String id = "98b74149-8cd7-4e77-9260-7ebf30092b14";
//        log.info(userService.getUserRoles(id));
    }

    @Test
    void addRole() {
//        String id = "98b74149-8cd7-4e77-9260-7ebf30092b14";
//        String[] role = {"6da965d4-b6db-426a-9918-179bfcde04e6"};

//        userService.addRoles(id, role);
    }

    @Test
    void removeRole() {
//        String id = "98b74149-8cd7-4e77-9260-7ebf30092b14";
//        String[] role = {"6da965d4-b6db-426a-9918-179bfcde04e6"};
//
//        userService.removeRoles(id, role);
    }

    @Test
    void userNameList() {
       List<String> ids = Arrays.asList("28e05596-9ad0-4187-ac11-fd93fb7701af", "3ad67afe-77e7-11ec-825f-5c5181925b12");

        //String[] ids = {"28e05596-9ad0-4187-ac11-fd93fb7701af", "3ad67afe-77e7-11ec-825f-5c5181925b12"};

        var x = userMapper.getUserNamesFromIds(ids);
        log.info(x);
    }

    @Test
    void toggleUser() {
        final String id = "552eb89c-94f3-4223-8dad-706b1dece34b";
//        var x = userService.toggleUser(id);
//        log.info(x);
    }

    @Test
    void saveImage() throws IOException {
//        FileInputStream fis = new FileInputStream("/Users/adebola/downloads/IMG_0256.jpg");
//        MockMultipartFile multipartFile = new MockMultipartFile("file", fis);
//        String s = userService.saveImageFile(multipartFile);
//        log.info(s);
    }
}
