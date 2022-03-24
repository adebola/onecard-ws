package io.factorialsystems.msscusers.controller;

import io.factorialsystems.msscusers.service.UserService;
import io.factorialsystems.msscusers.utils.K;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private static final String ADMIN_ROLE = "ROLE_Onecard_Admin";

    @GetMapping
     public ResponseEntity<?> getUsers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                       @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(userService.findUsers(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {

        if (isAdminOrSelf(id)) {
            return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
        }

        return new ResponseEntity<>(new MessageDto("Forbidden"), HttpStatus.FORBIDDEN);
    }

    @GetMapping("/roles/{id}")
    ResponseEntity<?> getUserRoles(@PathVariable("id") String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authentication.getName().equals(id)) {
            List<String> roleList = authorities
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(roleList, HttpStatus.OK);
        }

        boolean isAdmin = authorities
                .stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_Onecard_Admin"));

        if (isAdmin) {
            return new ResponseEntity<>(userService.getStringUserRoles(id), HttpStatus.OK);
        }

        return new ResponseEntity<>(new MessageDto("Forbidden"), HttpStatus.FORBIDDEN);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> searchUsers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                         @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(userService.searchUser(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> UpdateUser(@PathVariable("id") String id, @Valid @RequestBody KeycloakUserDto dto) {

        if (isAdminOrSelf(id)) {
            userService.updateUser(id, dto);
            return new ResponseEntity<>(new MessageDto("User Updated Successfully"), HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>(new MessageDto("Forbidden"), HttpStatus.FORBIDDEN);
    }

    @PutMapping("/roles/add/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> addRole(@PathVariable("id") String id,
                                     @RequestParam(value = "roleName") String roleName) {
        return null;
    }

    @PutMapping("/roles/remove/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> removeRole(@PathVariable("id") String id,
                                        @RequestParam(value = "roleName") String roleName) {
        return null;
    }

    private Boolean isAdminOrSelf(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return false;

        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        return (authentication.getName().equals(id) || roles.stream().anyMatch(r -> r.getAuthority().equals(ADMIN_ROLE)));
    }
}
