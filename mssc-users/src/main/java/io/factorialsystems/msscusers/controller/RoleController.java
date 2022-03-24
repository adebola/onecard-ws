package io.factorialsystems.msscusers.controller;

import io.factorialsystems.msscusers.service.RoleService;
import io.factorialsystems.msscusers.service.UserService;
import io.factorialsystems.msscusers.dto.KeycloakRoleDto;
import io.factorialsystems.msscusers.dto.RoleListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class RoleController {

    private final RoleService roleService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<List<KeycloakRoleDto>> getRoles() {
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.OK);
    }

    @GetMapping("/companyroles")
    @PreAuthorize("hasAnyRole('ROLE_Onecard_Admin', 'ROLE_Company_Admin')")
    ResponseEntity<List<KeycloakRoleDto>> getCompanyRoles() {
        return new ResponseEntity<>(roleService.getCompanyRoles(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<KeycloakRoleDto> getRoleById(@PathVariable("id") String id) {
        return new ResponseEntity<>(roleService.getRoleById(id), HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<KeycloakRoleDto> getRoleByName(@PathVariable("name") String name) {
        return new ResponseEntity<>(roleService.getRoleByName(name), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<List<KeycloakRoleDto>> getUserRoles(@PathVariable("id") String id) {
        List<KeycloakRoleDto> roles = userService.getUserRoles(id);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PutMapping("/add/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public void addRoles(@PathVariable("id") String id, @RequestBody @Valid RoleListDto roles) {
        userService.addRoles(id, roles.getRoleList());
    }

    @PutMapping("/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public void removeRoles(@PathVariable("id") String id, @RequestBody @Valid RoleListDto roles) {
        userService.removeRoles(id, roles.getRoleList());
    }
}
