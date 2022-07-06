package com.yeoreodigm.server.api.session;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmMember implements Serializable {

    private static final long serialVersionUID = -6348456621487522974L;

    private String email;
    private String confirmCode;

}
