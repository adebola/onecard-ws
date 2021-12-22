package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dto.ProviderCategoryDto;
import io.factorialsystems.msscprovider.dto.PagedDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.*;

@CommonsLog
@SpringBootTest
class ProviderCategoryServiceTest {

    @Autowired
    private ProviderCategoryService service;

    private String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    @Test
    void findProviderCategories() {
        PagedDto<ProviderCategoryDto> providerCategories = service.findProviderCategories(1, 20);
        assert (providerCategories.getTotalSize() > 0);
        log.info(providerCategories);
    }

    @Test
    void findProviderCategoryById() {
        ProviderCategoryDto providerCategory = service.findProviderCategoryById(1);
        assert(providerCategory != null);
        assertEquals(providerCategory.getId(), 1);
        log.info(providerCategory);
    }

    @Test
    void saveProviderCategory() {
        String s = getAlphaNumericString(4);
        ProviderCategoryDto dto = ProviderCategoryDto.builder()
                .categoryName(s)
                .build();

        Integer result = service.saveProviderCategory("foluke", dto);
        assert(result > 0);

        log.info(result);
    }

//    @Test
//    void saveDuplicateProviderCategory() {
//
//        Exception exception = assertThrows(DuplicateKeyException.class, () -> {
//            ProviderCategoryDto dto = ProviderCategoryDto.builder()
//                    .categoryName("Mobile")
//                    .build();
//
//            Integer result = service.saveProviderCategory("foluke" , dto);
//        });
//    }

    @Test
    void updateProviderCategory() {

        ProviderCategoryDto dto = ProviderCategoryDto.builder()
                .categoryName(getAlphaNumericString(4))
                .id(1)
                .build();

        service.updateProviderCategory(1, dto);
        log.info(dto);
    }

    @Test
    void updateDuplicateProviderCategory() {

        Exception exception = assertThrows(DuplicateKeyException.class, () -> {
            ProviderCategoryDto dto = ProviderCategoryDto.builder()
                    .categoryName("Toll")
                    .id(1)
                    .build();

            service.updateProviderCategory(1, dto);
            log.info(dto);
        });

    }
}
