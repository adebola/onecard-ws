package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.service.bulkrecharge.helper.BulkRechargeExcelGenerator;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@CommonsLog
class NewScheduledRechargeServiceTest {

    @Autowired
    private NewScheduledRechargeService service;

    @Autowired
    private BulkRechargeExcelGenerator excelGenerator;


    @Test
    void getUserRecharges() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            var y = service.getUserRecharges(1, 20);

            log.info(y);
            log.info(y.getTotalSize());
        }
    }

    @Test
    void getBulkIndividualRequests() {
        final String id = "bc17bff1-3913-483b-9ee5-ffa9f4fe237d";
        var x = service.getBulkIndividualRequests(id, 1, 20);
        log.info(x);
        log.info(x.getTotalSize());
    }

    @Test
    void getRechargeByDateRange() throws IOException, ParseException {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            DateRangeDto dto = new DateRangeDto();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
            final String dateString = "27-04-2022 09:15:55";
            dto.setStartDate(formatter.parse(dateString));
            dto.setEndDate(formatter.parse("12-12-2022 01:00:00"));

            InputStreamResource resource = service.getRechargeByDateRange(dto);
            File targetFile = new File("/Users/adebola/Downloads/scheduled-date-range.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }

    @Test
    void searchByDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "08-03-2022 10:15:55 AM";

        Date d = formatter.parse(dateString);


        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            var x = service.searchByDate(d, 1, 20);
            log.info(x);
            log.info(x.getTotalSize());
        }
    }

    @Test
    void generateExcelFile() throws IOException {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String bulkId = "158f4d0b-19be-4d8d-8c83-398383890188";
        final String scheduledId = "41cd5d1c-ce7e-4023-84c5-d67657880c8b";


        try (MockedStatic<ProviderSecurity> k  = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getUserId).thenReturn(id);
            assertThat(ProviderSecurity.getUserId()).isEqualTo(id);
            log.info(ProviderSecurity.getUserId());

            InputStreamResource resource = new InputStreamResource(excelGenerator.generateScheduledBulkExcelFile(scheduledId));
            File targetFile = new File("/Users/adebola/Downloads/test4.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);


            log.info(targetFile.getAbsolutePath());
        }
    }

}
