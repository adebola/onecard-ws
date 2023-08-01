package io.factorialsystems.msscusers.remote;

import io.factorialsystems.msscusers.dto.AsyncRefundResponseDto;
import io.factorialsystems.msscusers.dto.MailMessageDto;
import io.factorialsystems.msscusers.dto.SimpleUserDto;
import io.factorialsystems.msscusers.external.client.CommunicationClient;
import io.factorialsystems.msscusers.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoteMail {
    private final UserService userService;
    private final CommunicationClient communicationClient;

    @Value("${mail.secret}")
    private String mailSecret;

    public void sendRefundMail(AsyncRefundResponseDto dto) {
        SimpleUserDto userDto = userService.findSimpleUserById(dto.getUserId());
        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .body(String.format("You have been refunded %.2f due to Recharge Failure", dto.getAmount()))
                .to(userDto.getEmail())
                .subject("Wallet Refund")
                .secret(mailSecret)
                .build();

        communicationClient.sendMailWithoutAttachment(mailMessageDto);
    }
}
