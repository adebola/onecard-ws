package io.factorialsystems.msscprovider.recharge.ringo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoElectricResponse {
    private String token;
    private String unit;
    private BigDecimal amount;
    private BigDecimal amountCharged;
    private String message;
    private Integer status;
    private String customerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date date;

    private String TransRef;
    private String disco;
}
