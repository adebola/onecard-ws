package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provider {
    private Integer id;
    private Integer categoryId;
    private String category;
    private String name;
    private String code;
    private Boolean activated;
    private ProviderStatusEnum status;
    private String createdBy;
    private Date createdDate;
    private Date activationDate;
}
