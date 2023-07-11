package io.factorialsystems.msscprovider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCategory {
    private Integer id;

    @EqualsAndHashCode.Exclude
    private String categoryName;

    @EqualsAndHashCode.Exclude
    private String createdBy;

    @EqualsAndHashCode.Exclude
    private Date createdDate;
}
