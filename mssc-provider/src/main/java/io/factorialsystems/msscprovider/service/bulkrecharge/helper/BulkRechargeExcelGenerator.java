package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.dao.ScheduledRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.service.RechargeType;
import io.factorialsystems.msscprovider.service.file.ExcelWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkRechargeExcelGenerator {
    private final ExcelWriter excelWriter;
    private final BulkRechargeMapper newBulkRechargeMapper;
    private final ScheduledRechargeMapper scheduledRechargeMapper;

    public ByteArrayInputStream generateBulkExcelFile(String id) {
        NewBulkRechargeRequest request = newBulkRechargeMapper.findBulkRechargeById(id);
        List<IndividualRequest> individualRequests = newBulkRechargeMapper.findBulkIndividualRequests(id);

        return generateExcelFile(individualRequests, id, request.getCreatedAt(), RechargeType.BULK_RECHARGE);
    }

    public ByteArrayInputStream generateScheduledBulkExcelFile(String id) {
        NewScheduledRechargeRequest request = scheduledRechargeMapper.findById(id);

        if (request == null) {
            throw new ResourceNotFoundException("NewScheduledRechargeRequest", "id", id);
        }

        List<IndividualRequest> individualRequests = newBulkRechargeMapper.findBulkIndividualRequestsByScheduleId(id);
        return generateExcelFile(individualRequests, id, request.getCreatedOn(), RechargeType.SCHEDULED_RECHARGE);

    }

    private ByteArrayInputStream generateExcelFile(List<IndividualRequest> individualRequests, String id, Date date, RechargeType type) {

        if (individualRequests == null || individualRequests.isEmpty()) {
            throw new ResourceNotFoundException("IndividualRequests", "id", id);
        }

        String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(date);
        String title = null;

        if (type == RechargeType.BULK_RECHARGE) {
           title = String.format("BulkRecharge Request (%s) Created On (%s)", id, s);
        } else {
            title = String.format("ScheduledRecharge Request (%s) Created On (%s)", id, s);
        }

        return excelWriter.bulkIndividualRequestToExcel(individualRequests, title);
    }
}
