package io.factorialsystems.msscaudit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageDto {
    @Null(message = "Message Id cannot be set")
    private String id;

    @NotEmpty(message = "Name must be specified")
    private String name;

    @NotEmpty(message = "E-Mail must be specified")
    private String email;

    @NotEmpty(message = "Telephone Number must be specified")
    private String phone;

    @NotEmpty(message = "Message must be specified")
    private String message;

    @Null(message = "Created Date cannot be set")
    private Date createdDate;
}
