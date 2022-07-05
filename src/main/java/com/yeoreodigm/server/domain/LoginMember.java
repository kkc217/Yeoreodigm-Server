package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private String nickname;
    private Authority authority;

}
