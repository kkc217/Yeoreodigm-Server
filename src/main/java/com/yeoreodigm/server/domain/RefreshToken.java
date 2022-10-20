package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String key;

    @Column(nullable = false)
    private String value;

    public void changeValue(String value) {
        this.value = value;
    }

    public RefreshToken(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
