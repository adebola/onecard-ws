package io.factorialsystems.msscprovider.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRechargeRequestDto {
    @NotNull(message = "Please specify type either single or bulk recharge")
    private String rechargeType;

    @NotNull(message = "Scheduled Date must be specified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date scheduledDate;

    @NotNull(message = "ServiceCode must be specified")
    private String serviceCode;

    private Integer groupId;
    private String[] recipients;
    private String recipient;
    private String productId;
    private String telephone;
    private BigDecimal serviceCost;
    private String redirectUrl;
    private String paymentMode;
}
