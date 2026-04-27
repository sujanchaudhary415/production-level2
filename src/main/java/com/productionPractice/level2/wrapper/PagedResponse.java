package com.productionPractice.level2.wrapper;


import lombok.*;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse <T>{
    private List<T>content;
    private int pageNumber;
    private int pageSize;
    private Long totalElements;
    private int totalPages;
    private boolean last;
}
