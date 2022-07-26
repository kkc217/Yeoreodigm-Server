package com.yeoreodigm.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageResult<T> {

    private T data;

    private boolean hasNext;

}
