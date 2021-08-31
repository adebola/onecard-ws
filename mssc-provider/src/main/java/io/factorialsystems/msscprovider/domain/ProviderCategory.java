package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCategory {
    private Integer id;
    private String categoryName;
    private String createdBy;
    private Date createdDate;

}
