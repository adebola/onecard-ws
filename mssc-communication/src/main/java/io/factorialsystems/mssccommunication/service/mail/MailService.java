package io.factorialsystems.mssccommunication.service.mail;

import io.factorialsystems.mssccommunication.document.MailMessage;
import io.factorialsystems.mssccommunication.dto.MailMessageDto;
import io.factorialsystems.mssccommunication.mapper.MailMessageMapper;
import io.factorialsystems.mssccommunication.repository.MailMessageRepository;
import io.factorialsystems.mssccommunication.service.file.FileService;
import io.factorialsystems.mssccommunication.service.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final FileService fileService;
    private final JavaMailSender mailSender;
    private final MailMessageMapper mailMessageMapper;
    private final MailMessageRepository mailMessageRepository;

    @Value("${user.mail}")
    private String fromAddress;

    public String sendMail(MailMessageDto dto, MultipartFile multipartFile)  {

        MailMessage mailMessage = mailMessageMapper.dtoToMailMessageTo(dto);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            UploadFile file = fileService.uploadFile(multipartFile);
            mailMessage.setFileName(file.getFileName());
        }

        mailMessage.setCreatedDate(new Date());
        mailMessage.setSentBy(dto.getTo());
        mailMessage.setFrom(fromAddress);

        MimeMessage message = getMimeMessage(mailMessage);
        mailSender.send(message);

        mailMessageRepository.save(mailMessage);

        log.info(String.format("Sending Mail to %s", mailMessage.getTo()));

        return mailMessage.getId();
    }

    private MimeMessage getMimeMessage(MailMessage mail) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;

        try {
            helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(mail.getTo());
            helper.setText(mail.getBody(), false);
            helper.setSubject(mail.getSubject());
            helper.setFrom(mail.getFrom());

            if (mail.getFileName() != null) {
                FileSystemResource file = new FileSystemResource(new File(mail.getFileName()));

                if (file.exists() && file.isReadable()) {
                    helper.addAttachment(extractFilename(mail.getFileName()), file);
                }
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return message;
    }

    private String extractFilename(String path) {
        String result = path.substring(path.lastIndexOf('/') + 1);
        return result.substring(result.lastIndexOf('\\') + 1);
    }
}
