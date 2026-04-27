package com.productionPractice.level2.util;

import com.productionPractice.level2.wrapper.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public class PaginationUtil {
    public static <E,T> PagedResponse<T>build(Page<E> page, List<T>content)
    {
        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

}
