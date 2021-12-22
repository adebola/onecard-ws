package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.PagedDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CommonsLog
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void getUsers() {
        PagedDto<KeycloakUserDto> dtos = userService.findUsers(1, 20);
        log.info(dtos.getTotalSize());
        log.info(dtos.getList().get(0).getId());
        log.info(dtos.getList().get(0).getEmail());
        log.info(dtos.getList().get(0).getFirstName());
        log.info(dtos.getList().get(0).getAccount());

        assertEquals(null, dtos.getList().get(0).getAccount());
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

}
