package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.service.file.ExcelReader;
import io.factorialsystems.msscprovider.service.file.UploadFile;
import io.factorialsystems.msscprovider.service.model.IndividualRequestFailureNotification;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@CommonsLog
class BulkRechargeServiceTest {

    @Autowired
    private NewBulkRechargeService service;

    @Autowired
    NewBulkRechargeMapper newBulkRechargeMapper;

    @Test
    void saveService() {

    }

    @Test
    void searchByDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "23-02-2022 10:15:55 AM";

        Date d = formatter.parse(dateString);

        var x = service.searchByDate(d, 1, 20);
        log.info(x);
        log.info(x.getPageSize());
    }

    @Test
    void getUserRecharges() {

        //final String id = "3ad67afe-77e7-11ec-825f-5c5181925b12";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            var y = service.getUserRecharges(1, 20);
            log.info(y);
        }
    }

    @Test
    void getBulkIndividualRequests() {
        final String id = "158f4d0b-19be-4d8d-8c83-398383890188";

        var x = service.getBulkIndividualRequests(id, 1, 20);
        log.info(x);
    }

    @Test
    void runRechargeFail() {
        final String id = "158f4d0b-19be-4d8d-8c83-398383890188";

        RechargeStatus rechargeStatus = new RechargeStatus("Recharge Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        Recharge recharge = Mockito.mock(Recharge.class);
        Mockito.when(recharge.recharge(any())).thenReturn(rechargeStatus);

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            service.runBulkRecharge(id);

            var y = service.getBulkIndividualRequests(id, 1, 20);
            assertNotNull(y);
            assert(y.getList().size() > 0);
            assert(y.getList().get(0).getFailedMessage().equals("Recharge Failed"));
            assert(y.getList().get(0).getFailed().equals(true));

            log.info(y);


        }
    }

    @Test
    void runSimpleRechargeFail() {
        final String id = "158f4d0b-19be-4d8d-8c83-398383890188";

        IndividualRequestFailureNotification notification = IndividualRequestFailureNotification
                .builder()
                .errorMsg("Recharge Failed")
                .id(5)
                .build();

        newBulkRechargeMapper.failIndividualRequest(notification);
        notification.setId(6);
        newBulkRechargeMapper.failIndividualRequest(notification);

        var y = service.getBulkIndividualRequests(id, 1, 20);
        assertNotNull(y);
        assert(y.getList().size() > 0);
        assert(y.getList().get(0).getFailedMessage().equals("Recharge Failed"));
        assert(y.getList().get(0).getFailed().equals(true));

        log.info(y);
    }

    @Test
    void uploadFile() {
        final String fileName = "/Users/adebola/Downloads/Larfarge-2-plus.xlsx";
        File file = new File(fileName);

        UploadFile uploadFile = new UploadFile(file, fileName);
        ExcelReader excelReader = new ExcelReader(uploadFile);
    }
}