package io.factorialsystems.mssccommunication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedDto<T> {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalSize;
    private Integer pages;
    private List<T> list;
}
