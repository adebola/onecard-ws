package io.factorialsystems.msscusers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntryListDto {
    List<UserEntryDto> entries;
}
