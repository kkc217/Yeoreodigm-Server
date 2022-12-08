package com.yeoreodigm.server.dto.member;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MemberAuthDto implements Serializable {

    private String email;

    private String confirmCode;

}
