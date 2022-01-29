package io.factorialsystems.msscwallet.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@CommonsLog
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Test
    void findAccounts() {
//       var accounts = accountService.findAccounts(1, 20);
//       assert(accounts != null);
//       assert(accounts.getTotalSize() > 0);
//
//       log.info(accounts);
    }

    @Test
    void findAccountById() {
//        String id = "31c2a399-8a45-4cd0-b6da-40c2f295d9d7";
//
//        AccountDto account = accountService.findAccountById(id);
//        assert(account != null);
//        assertEquals(id, account.getId());
//
//        log.info(account);
    }


//    @Test
//    void findAccountByUserId() {
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
//        Jwt jwt =Mockito.mock(Jwt.class);
//        SecurityContextHolder.setContext(securityContext);
//
////        Map<Object, String> claims = new HashMap<>();
////        claims.put("Adebola", "name");
//
//        Principal principal = Mockito.mock(Principal.class);
//        HashMap claims = Mockito.mock(HashMap.class);
//        claims.put("Adebola", "name");
//        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
//        Mockito.when(securityContext.getAuthentication().getPrincipal()).thenReturn(principal);
//        Mockito.when(jwt.getClaims()).thenReturn(claims);
//
//
////        Map<String, Object> claims = jwt.getClaims();
////        String userName = (String) claims.get("name");
//
//        String id = "31c2a399-8a45-4cd0-b6da-40c2f295d9d7";
//        AccountDto dto = accountService.findAccountByUserId(id);
//        assert (dto != null);
//    }

    @Test
    void updateAccount() {

//        String id = "31c2a399-8a45-4cd0-b6da-40c2f295d9d7";
//        AccountDto dto = accountService.findAccountById(id);
//
//        assert(dto != null);
//        assertEquals(id, dto.getId());
//        dto.setName("New Name");
//        dto.setBalance(new BigDecimal(1000.0));
//        accountService.updateAccount(id, dto);
//
//        dto = accountService.findAccountById(id);
//        assert(dto != null);
//        assertEquals(id, dto.getId());
//        assertEquals("New Name", dto.getName());
    }

    @Test
    void updateAccountBalance() {

//        String id = "31c2a399-8a45-4cd0-b6da-40c2f295d9d7";
//        AccountDto dto = accountService.findAccountById(id);
//        assert(dto != null);
//        dto.setBalance(new BigDecimal(1233));
//
//        accountService.updateAccountBalance(id, dto);
    }
}
