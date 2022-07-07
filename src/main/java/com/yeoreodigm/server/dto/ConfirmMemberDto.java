package com.yeoreodigm.server.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ConfirmMemberDto implements Serializable {

    private static final long serialVersionUID = -6348456621487522974L;

    private String email;
    private String confirmCode;

}
