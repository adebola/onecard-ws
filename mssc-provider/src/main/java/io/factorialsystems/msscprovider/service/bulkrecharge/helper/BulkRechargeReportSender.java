package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import io.factorialsystems.msscprovider.dao.BulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.MailMessageDto;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRechargeDto;
import io.factorialsystems.msscprovider.service.MailService;
import io.factorialsystems.msscprovider.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkRechargeReportSender {
    private final MailService mailService;
    private final BulkRechargeMapper newBulkRechargeMapper;
    private final BulkRechargeExcelGenerator excelGenerator;

    public void sendReport(AsyncRechargeDto dto, NewBulkRechargeRequest request) {

        if (dto.getEmail() == null || dto.getName() == null) {
            log.info("Unable to Send E-mail for Bulk Transaction, No E-mail Address or Customer Name found");
            return;
        }

        String dateString = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());

        final String messageBody = String.format("Dear %s\n\nPlease find attached report for your Bulk Recharge\nCarried out on %s\nCurrent Balance %.2f",
                dto.getName(), dateString, dto.getBalance());

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .body(messageBody)
                .to(dto.getEmail())
                .subject("Bulk Recharge Report")
                .build();

        InputStreamResource resource = new InputStreamResource(excelGenerator.generateBulkExcelFile(dto.getId()));
        File targetFile = new File(dto.getId() + ".xlsx");

        try {
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            //FileSystemResource fileSystemResource = new FileSystemResource(targetFile);
            final String emailId = mailService.sendMailWithAttachment(targetFile, mailMessageDto, Constants.MULTIPART_REQUESTPART_NAME, Constants.EXCEL_CONTENT_TYPE);

            Map<String, String> emailMap = new HashMap<>();
            emailMap.put("id", dto.getId());
            emailMap.put("emailId", emailId);
            newBulkRechargeMapper.setEmailId(emailMap);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    public void sendDuplicateReport(AsyncRechargeDto dto, NewBulkRechargeRequest request) {
        if (dto.getEmail() == null || dto.getName() == null) {
            log.info("Unable to Send E-mail for Bulk Transaction, No E-mail Address or Customer Name found");
            return;
        }

        String dateString = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(request.getCreatedAt());

        final String messageBody =
                String.format("Dear %s\n\nThe Bulk Recharge Request submitted on %s\nwas identified as a duplicate submission and was not run, all charges have been reversed.\nIf you still want to run the Bulk Recharge please re-submit it",
                        dto.getName(), dateString);

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .body(messageBody)
                .to(dto.getEmail())
                .subject("Duplicate Submission Detected")
                .build();

        mailService.pushMailWithOutAttachment(mailMessageDto);
    }
}
