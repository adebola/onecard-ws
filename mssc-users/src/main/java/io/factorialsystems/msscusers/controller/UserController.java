package io.factorialsystems.msscusers.controller;

import io.factorialsystems.msscusers.domain.search.SearchUserDto;
import io.factorialsystems.msscusers.dto.KeycloakUserDto;
import io.factorialsystems.msscusers.dto.MessageDto;
import io.factorialsystems.msscusers.dto.PasswordDto;
import io.factorialsystems.msscusers.service.UserService;
import io.factorialsystems.msscusers.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
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


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getAdminUsers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(userService.findAdminUsers(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/ordinary")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getOrdinaryUsers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(userService.findOrdinaryUsers(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/self")
    public ResponseEntity<?> getSelf() {
        return new ResponseEntity<>(userService.findUserById(K.getUserId()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @GetMapping("/simple/{id}")
//    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getSimpleUserById(@PathVariable("id") String id) {
        return new ResponseEntity<>(userService.findSimpleUserByIdOrNameOrEmail(id), HttpStatus.OK);
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<?> verifyUser(@PathVariable("id") String id) {
        return new ResponseEntity<>(userService.verifyUser(id), HttpStatus.OK);
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

    @PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> searchUsers(@Valid @RequestBody SearchUserDto dto,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(userService.searchUser(pageNumber, pageSize, dto), HttpStatus.OK);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  void changePassword(@Valid @RequestBody PasswordDto dto) {
        userService.changePassword(K.getUserId(), dto.getPassword());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public void UpdateUser(@PathVariable("id") String id, @Valid @RequestBody KeycloakUserDto dto) {
        userService.updateUser(id, dto);
    }

    @PutMapping("/self")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSelf(@Valid @RequestBody KeycloakUserDto dto) {
        userService.updateUser(K.getUserId(), dto);
    }

    @GetMapping("/generate")
    @PreAuthorize("hasAnyRole('ROLE_Company_Admin', 'ROLE_Company_Operator', 'ROLE_Company_User')")
    public ResponseEntity<?> generateSecret() {
        return new ResponseEntity<>(userService.generateSecret(), HttpStatus.OK);
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return userService.saveImageFile(file);
    }

//    private Boolean isAdminOrSelf(String id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null) return false;
//
//        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
//        return (authentication.getName().equals(id) || roles.stream().anyMatch(r -> r.getAuthority().equals(ADMIN_ROLE)));
//    }
//
//    private Boolean isSelf() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null) return false;
//
//        return authentication.getName().equals(K.getUserId());
//    }
}
