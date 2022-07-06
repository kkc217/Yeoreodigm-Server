package com.yeoreodigm.server.api.session;

import com.yeoreodigm.server.domain.Authority;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginMember implements Serializable {

    private static final long serialVersionUID = 7430391015319438320L;

    private String email;
    private String nickname;
    private Authority authority;

}
