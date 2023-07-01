package io.factorialsystems.msscaudit.utils;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestBuilder {

    @AllArgsConstructor
    private static class PageParameters {
        private int pageNumber;
        private int pageSize;
    }

    public static PageRequest build(Integer pageNumber, Integer pageSize) {
        PageParameters p = buildPage(pageNumber, pageSize);
        return PageRequest.of(p.pageNumber, p.pageSize);
    }

    public static PageRequest buildWithSort(Integer pageNumber, Integer pageSize, String order, Boolean descending) {
        PageParameters p = buildPage(pageNumber, pageSize);

        if (order == null) {
            return PageRequest.of(p.pageNumber, p.pageSize);
        }

        Sort sort;
        if (descending == null || descending) {
            sort = Sort.by(order).descending();
        } else {
            sort = Sort.by(order).ascending();
        }

        return PageRequest.of(p.pageNumber, p.pageSize, sort);
    }

    private static PageParameters buildPage(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber == null || pageNumber < 1) {
            queryPageNumber = K.DEFAULT_PAGE_NUMBER;
        } else {
            queryPageNumber = pageNumber - 1;
        }

        if (pageSize == null) {
            queryPageSize = K.DEFAULT_PAGE_SIZE;
        } else if (pageSize > 500) {
            queryPageSize = 500;
        } else {
            queryPageSize = pageSize;
        }

        return new PageParameters(queryPageNumber, queryPageSize);
    }
}
