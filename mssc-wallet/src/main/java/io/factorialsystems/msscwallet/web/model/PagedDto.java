package io.factorialsystems.msscwallet.web.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedDto<T> {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalSize;
    private Integer pages;
    private List<T> list;
}
