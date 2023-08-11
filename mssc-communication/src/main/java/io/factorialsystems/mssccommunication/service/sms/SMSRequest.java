package io.factorialsystems.mssccommunication.service.sms;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SMSRequest {
    private String api_key;
    private String to;
    private String from;
    private String sms;
    private String type;
    private String channel;

    public SMSRequest(String sms, String to, String key) {
        this.sms = sms;
        this.to = to;
        this.channel = "dnd";
        this.type = "plain";
        this.from = "Onecard";
        this.api_key = key;
    }
}
