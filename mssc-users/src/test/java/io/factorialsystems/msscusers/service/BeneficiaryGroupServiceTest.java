package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.domain.BeneficiaryGroup;
import io.factorialsystems.msscusers.dto.BeneficiaryDto;
import io.factorialsystems.msscusers.dto.BeneficiaryGroupDto;
import io.factorialsystems.msscusers.dto.BeneficiaryRequestDto;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@CommonsLog
class BeneficiaryGroupServiceTest {

    @Autowired
    private BeneficiaryGroupService service;

    @Test
    void findByUserId() {
        String id = "3ad67afe-77e7-11ec-825f-5c5181925b12";
        List<BeneficiaryGroupDto> dtos = service.findByUserId(id);
        assertNotNull(dtos);
        log.info(dtos);
    }

    @Test
    void findBeneficiaries() {
        var x = service.findBeneficiaries(1);
        assertNotNull(x);
        log.info(x);
    }

    @Test
    void findById() {
        BeneficiaryGroupDto dto = service.findById(1);
        assertNotNull(dto);
    }

    @Test
    void removeBeneficiary() {
        BeneficiaryRequestDto dto = new BeneficiaryRequestDto();
        dto.setBeneficiaryId(1);
        dto.setGroupId(1);

        service.removeBeneficiary(dto);
        List<BeneficiaryDto> groupDtos = service.findBeneficiaries(1);
        assertNotNull(groupDtos);
        log.info(groupDtos);
    }

    @Test
    void addBeneficiary() {
        BeneficiaryRequestDto dto = new BeneficiaryRequestDto();
        dto.setBeneficiaryId(1);
        dto.setGroupId(1);

        service.addBeneficiary(dto);
        List<BeneficiaryDto> groupDtos = service.findBeneficiaries(1);
        assertNotNull(groupDtos);
        assert(groupDtos.size() > 0);
        log.info(groupDtos);

    }

    @Test
    void save() {
        String id = "3ad67afe-77e7-11ec-825f-5c5181925b12";
        BeneficiaryGroup beneficiaryGroup = new BeneficiaryGroup();
        beneficiaryGroup.setGroupName(generate());
        beneficiaryGroup.setUserId(id);

        BeneficiaryGroupDto group = service.save(beneficiaryGroup);
        assertNotNull(group);
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private String generate() {
        return RandomStringUtils.randomAlphabetic(8);
    }
}