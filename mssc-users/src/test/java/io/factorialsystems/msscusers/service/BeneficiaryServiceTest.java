package io.factorialsystems.msscusers.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@CommonsLog
@SpringBootTest
class BeneficiaryServiceTest {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Test
    void addBeneficiary() {
//        Exception exception = assertThrows(DuplicateKeyException.class, () -> {
//            BeneficiaryDto dto = new BeneficiaryDto();
//            dto.setEmail("damolaomoboya@gmail.com");
//            dto.setFirstName("Adedamola");
//            dto.setLastName("Omoboya");
//            dto.setTelephone("09055572307");
//            //dto.setUserName("adeomoboya@googlemail.com");
//            //dto.setUserId("95b9571c-a3ac-4241-9b5a-4bc2c387f244");
//
//            var newBen = beneficiaryService.addBeneficiary(dto);
//            log.info(newBen);
//        });
    }

    @Test
    void addBeneficiaries() {
//        BeneficiaryDto dto1 = new BeneficiaryDto();
//        dto1.setEmail("adeomoboya@yahoo.co.uk");
//        dto1.setFirstName("Adeoye");
//        dto1.setLastName("Omoboya");
//        dto1.setTelephone("08028422700");
//        dto1.setUserId("95b9571c-a3ac-4241-9b5a-4bc2c387f244");
//
//        BeneficiaryDto dto2 = new BeneficiaryDto();
//        dto2.setEmail("bodeomoboya@gmail.com");
//        dto2.setFirstName("Bode");
//        dto2.setLastName("Omoboya");
//        dto2.setTelephone("08033356709");
//        dto2.setUserId("95b9571c-a3ac-4241-9b5a-4bc2c387f244");
//
//        List<BeneficiaryDto> dtoList = new ArrayList<>();
//        dtoList.add(dto1);
//        dtoList.add(dto2);
//
//        beneficiaryService.addBeneficiaries(dtoList);

    }

    @Test
    void removeBeneficiary() {
        // beneficiaryService.removeBeneficiary(1);
    }

    @Test
    void updateBeneficiary() {
//        BeneficiaryDto beneficiaryDto = beneficiaryService.getBeneficiary(2);
//        assertNotNull(beneficiaryDto);
//        beneficiaryDto.setFirstName("TEST");
//        beneficiaryService.updateBeneficiary(2, beneficiaryDto);
//
//        BeneficiaryDto newDto = beneficiaryService.getBeneficiary(2);
//        assertNotNull(newDto);
//        assertEquals(newDto.getFirstName(), "TEST");
//
//        log.info(newDto);
    }

    @Test
    void getBeneficiary() {
//        BeneficiaryDto beneficiaryDto = beneficiaryService.getBeneficiary(2);
//        assertNotNull(beneficiaryDto);
//        log.info(beneficiaryDto);
    }

    @Test
    void getUserBeneficiaries() {
//        List<BeneficiaryDto> beneficiaries = beneficiaryService.getUserBeneficiaries("95b9571c-a3ac-4241-9b5a-4bc2c387f244");
//        assertNotNull(beneficiaries);
//        assert(beneficiaries.size() > 0);
//        log.info(beneficiaries);
    }

    @Test
    void getUserBeneficiariesByName() {
//        final List<BeneficiaryDto> foluke = beneficiaryService.getUserBeneficiariesByName("adeomoboya@googlemail.com");
//        assertNotNull(foluke);
//        log.info(foluke);
    }
}
