package com.yeoreodigm.server.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckEmailRequestDto {
    @NotEmpty
    private String email;
}
