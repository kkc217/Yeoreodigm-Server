package com.yeoreodigm.server.dto.search;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SearchRequestDto {

    @NotEmpty
    private String content;

}
