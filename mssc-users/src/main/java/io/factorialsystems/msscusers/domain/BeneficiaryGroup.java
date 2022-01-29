package io.factorialsystems.msscusers.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryGroup {
    private Integer id;
    private String groupName;
    private String userId;
    private String userName;
}