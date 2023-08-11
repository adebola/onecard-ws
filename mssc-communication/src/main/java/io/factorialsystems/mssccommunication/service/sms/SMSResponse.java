package io.factorialsystems.mssccommunication.service.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SMSResponse {
    private String code;
    private String message_id;
    private String message_id_str;
    private String message;
    private BigDecimal balance;
    private String user;
}
