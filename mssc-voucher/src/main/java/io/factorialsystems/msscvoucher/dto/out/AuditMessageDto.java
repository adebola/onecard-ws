package io.factorialsystems.msscvoucher.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditMessageDto {
    @Null(message = "Message Id cannot be set")
    private String id;

    @NotNull(message = "ServiceName must be specified ")
    private String serviceName;

    @NotNull(message = "ServiceAction must be specified ")
    private String serviceAction;

    @NotNull(message = "Username must be specified ")
    private String userName;

    @NotNull(message = "Description must be specified ")
    private String description;

    @Null(message = "Message date cannot be set")
    private Date createdDate;
}
