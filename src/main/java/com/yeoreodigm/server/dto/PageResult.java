package com.yeoreodigm.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private T data;

    private int next;

}
