package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.domain.Provider;
import io.factorialsystems.msscprovider.web.mapper.provider.ProviderMapstructMapper;
import io.factorialsystems.msscprovider.web.model.ProviderDto;
import io.factorialsystems.msscprovider.web.model.PagedDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@CommonsLog
@SpringBootTest
class ProviderServiceTest {

    @Autowired
    ProviderService providerService;

    @Autowired
    ProviderMapstructMapper providerMapstructMapper;

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
    public void getAllProviders() {
        PagedDto<ProviderDto> providers = providerService.findProviders(1, 20);
        assert (providers != null);
        log.info(providers);
    }

    @Test
    public void findProviderById() {
        ProviderDto providerDto = providerService.findProviderById(1);
        assert(providerDto != null);

        log.info(providerDto);

//        ProviderDto providerDto = providerMapstructMapper.providerToProviderDto(provider);
        assert (providerDto != null);

        assertEquals(providerDto.getId(), providerDto.getId());
        assertEquals(providerDto.getActivationDate(), providerDto.getActivationDate());
        assertEquals(providerDto.getCategory(), providerDto.getCategory());
        assertEquals(providerDto.getStatus(), providerDto.getStatus());
        assertEquals(providerDto.getName(), providerDto.getName());
        assertEquals(providerDto.getCreatedDate(), providerDto.getCreatedDate());

        log.info(providerDto);
    }

    @Test
    public void providerDtoToProvider() {
        ProviderDto dto = new ProviderDto();
        dto.setStatus("UNAPPROVED");
        dto.setCategory("Wrong-Category");
        dto.setCode("AIRT");
        dto.setName("Airtel");
        dto.setCreatedBy("Adebola");
        dto.setId(1);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            Provider provider = providerMapstructMapper.providerDtoToProvider(dto);
        });

        String expectedMessage = "Invalid Provider Category:";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        log.info(actualMessage);
    }

    @Test
    public void InvalidStatus() {

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ProviderDto dto = new ProviderDto();
            dto.setStatus("Wrong-Status");
            dto.setCategory("Mobile");
            dto.setCode("AIRT");
            dto.setName("Airtel");
            dto.setCreatedBy("Adebola");
            dto.setId(1);

            Provider provider = providerMapstructMapper.providerDtoToProvider(dto);
        });

        String expectedMessage = "Invalid Provider Status";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        log.info(actualMessage);
    }

    @Test
    public void InvalidCategory() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            ProviderDto dto = new ProviderDto();
            dto.setStatus("UNAPPROVED");
            dto.setCategory("Wrong-Category");
            dto.setCode("AIRT");
            dto.setName("Airtel");
            dto.setCreatedBy("Adebola");
            dto.setId(1);

            Provider provider = providerMapstructMapper.providerDtoToProvider(dto);
        });

        String expectedMessage = "Invalid Provider Category:";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        log.info(actualMessage);

    }

    @Test
    public void saveProvider() {

        String generatedString = getAlphaNumericString(4);

        ProviderDto dto = ProviderDto.builder()
                .category("Mobile")
                .code(generatedString)
                .name("Vodafone TEST")
                .build();

        Integer result = providerService.saveProvider("adebola", dto);
        assert(result > 0);
        log.info(result);
    }

    @Test
    public void updateProvider() {

        String generatedString = getAlphaNumericString(4);

        ProviderDto dto = providerService.findProviderById(1);
//        ProviderDto dto = providerMapstructMapper.providerToProviderDto(provider);
        dto.setCode(generatedString);

        providerService.updateProvider(dto.getId(), dto);
        ProviderDto provider1 = providerService.findProviderById(1);


        assertEquals(dto.getCode(), provider1.getCode());
        assertEquals(dto.getActivated(), provider1.getActivated());
        assertEquals(dto.getCreatedBy(), provider1.getCreatedBy());
        assertEquals(dto.getName(), provider1.getName());
        assertEquals(dto.getCategory(), provider1.getCategory());
    }

    @Test
    public void updateSingleColumn() {

        String generatedString = getAlphaNumericString(4);

        ProviderDto dto = ProviderDto.builder()
                .id(1)
                .code(generatedString)
                .build();

        providerService.updateProvider(dto.getId(), dto);

        ProviderDto provider = providerService.findProviderById(1);

        assertEquals(dto.getCode(), provider.getCode());
    }
}
