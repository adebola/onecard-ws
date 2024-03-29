package io.factorialsystems.msscprovider.dto.recharge;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.factorialsystems.msscprovider.dto.recharge.IndividualRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewScheduledRechargeRequestDto {
    @Null(message = "Id cannot be set")
    private String id;

    @NotEmpty(message = "Please specify type either single or bulk recharge")
    private String rechargeType;

    @NotNull(message = "Scheduled Date must be specified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date scheduledDate;

    private String redirectUrl;
    private String paymentMode;

    @NotEmpty
    private List<@Valid IndividualRequestDto> recipients;
}


