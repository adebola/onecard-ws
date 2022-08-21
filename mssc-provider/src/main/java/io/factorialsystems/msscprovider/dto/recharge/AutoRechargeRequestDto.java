package io.factorialsystems.msscprovider.dto.recharge;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoRechargeRequestDto {

    private String paymentMode;

    @NotEmpty(message = "Title must be specified")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private Date endDate;

    private List<Integer> daysOfWeek;
    private List<Integer> daysOfMonth;

    @NotEmpty
    private List<@Valid AutoIndividualRequestDto> recipients;
}
