package io.factorialsystems.mssccommunication.service.mail;

import io.factorialsystems.mssccommunication.document.MailMessage;
import io.factorialsystems.mssccommunication.domain.MailConstant;
import io.factorialsystems.mssccommunication.dto.MailMessageDto;
import io.factorialsystems.mssccommunication.mapper.MailMessageMapper;
import io.factorialsystems.mssccommunication.repository.MailConstantsRepository;
import io.factorialsystems.mssccommunication.repository.MailMessageRepository;
import io.factorialsystems.mssccommunication.service.file.FileService;
import io.factorialsystems.mssccommunication.service.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
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
    private final MailConstantsRepository mailConstantsRepository;

    @Value("${mail.secret}")
    private String mailSecret;

    private String mailFooter;

    @Value("${user.mail}")
    private String fromAddress;

    public String sendMail(MailMessageDto dto, MultipartFile multipartFile)  {

        // Due to the Async nature of other services that send mails, they typically don't have a valid SecurityContext
        // when invoking the MailController, the fulfillment and mail sending is done on a JMS Thread, not the main thread,
        // the routes are Authentication free, hence the need for an additional layer, a Mail BCrypted Password
        if (!mailSecret.equals(dto.getSecret())) {
            log.error("Invalid Secret {}, sending Mail", dto.getSecret());
            throw new AccessDeniedException("Invalid Credentials Sending Mail");
        }

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

            final String emailBody = String.format("%s \n\n\n\n\n\n\n\n\n\n\n %s", mail.getBody(), getMailFooter());

            helper.setTo(mail.getTo());
            helper.setText(emailBody, false);
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

    private String getMailFooter() {
        if (this.mailFooter == null) {
            MailConstant mailConstant = mailConstantsRepository.getMailFooter();

            if (mailConstant != null && mailConstant.getValue() != null) {
                this.mailFooter = mailConstant.getValue();
            }
        }

        return this.mailFooter;
    }
}
