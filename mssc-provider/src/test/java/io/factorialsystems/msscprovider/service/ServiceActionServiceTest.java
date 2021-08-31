package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.web.mapper.action.ServiceActionMapstructMapper;
import io.factorialsystems.msscprovider.web.model.PagedDto;
import io.factorialsystems.msscprovider.web.model.ServiceActionDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Valid;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CommonsLog
class ServiceActionServiceTest {

    @Autowired
    ServiceActionService service;

    @Autowired
    ServiceActionMapstructMapper mapper;


    @Test
    void getProviderActions() {
        PagedDto<ServiceActionDto> dtoPagedDto = service.getProviderActions("MTN", 1, 20);
        assert(dtoPagedDto != null);
        assert (dtoPagedDto.getPages() > 0);
        assert (dtoPagedDto.getPageSize() > 0);
        assert (dtoPagedDto.getPageNumber() > 0);
        assert (dtoPagedDto.getTotalSize() > 0);
        log.info(dtoPagedDto.getList());
    }

    @Test
    void getProviderAction() {
        ServiceActionDto dto = service.getProviderAction(2);
        log.info(dto);
    }

    @Test
    void saveActionWrongCode() {

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ServiceActionDto dto = ServiceActionDto.builder()
                    .id(null)
                    .createdBy("Adebola")
                    .serviceCost(new BigDecimal(1000.0))
                    .serviceName("Airtel Data Plan1")
                    .providerCode("XYZ")
                    .build();

            service.saveAction("ADEBOLA", dto);
        });

        String expectedMessage = "Provider Code does not exist Code";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        log.info(actualMessage);
    }

    @Test
    void saveAction() {
        ServiceActionDto dto = ServiceActionDto.builder()
                .id(null)
                .createdBy("Adebola")
                .serviceCost(new BigDecimal(1000.0))
                .serviceName("MTN Data Plan3")
                .providerCode("MTN")
                .build();

        Integer id = service.saveAction("ADEBOLA", dto);
        ServiceActionDto action = service.getProviderAction(id);

       assert (action.getId() > 0);
       assertEquals(action.getCreatedBy(), "ADEBOLA");
       //assertEquals(action.getServiceCost(), new BigDecimal(1000.00));
       log.info(action);
    }

    @Test
    void updateAction() {
        ServiceActionDto dto = service.getProviderAction(1);
        log.info(dto);
        dto.setActivated(true);

        service.updateAction(1, dto);
    }
}
