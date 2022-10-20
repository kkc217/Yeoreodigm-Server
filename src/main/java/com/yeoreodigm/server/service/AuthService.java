package com.yeoreodigm.server.service;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.RefreshToken;
import com.yeoreodigm.server.dto.jwt.TokenDto;
import com.yeoreodigm.server.dto.member.LoginRequestDto;
import com.yeoreodigm.server.exception.BadRequestException;
import com.yeoreodigm.server.exception.LoginRequiredException;
import com.yeoreodigm.server.jwt.CustomEmailPasswordAuthToken;
import com.yeoreodigm.server.jwt.TokenProvider;
import com.yeoreodigm.server.repository.MemberRepository;
import com.yeoreodigm.server.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDto login(LoginRequestDto requestDto) {
        CustomEmailPasswordAuthToken customEmailPasswordAuthToken
                = new CustomEmailPasswordAuthToken(requestDto.getEmail(), requestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(customEmailPasswordAuthToken);

        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email);
        if (Objects.isNull(member)) throw new BadRequestException("일치하는 사용자가 없습니다.");

        String accessToken = tokenProvider.createAccessToken(email, member.getAuthority());
        String refreshToken = tokenProvider.createRefreshToken(email, member.getAuthority());

        Optional<RefreshToken> originRefreshTokenOpt = refreshTokenRepository.findByKey(email);
        if (originRefreshTokenOpt.isEmpty()) {
            refreshTokenRepository.save(new RefreshToken(email, refreshToken));
        } else {
            RefreshToken originRefreshToken = originRefreshTokenOpt.get();
            originRefreshToken.changeVale(refreshToken);
            refreshTokenRepository.save(originRefreshToken);
        }

        return tokenProvider.createTokenDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenDto reissue(TokenDto tokenDto) {
        String originAccessToken = tokenDto.getAccessToken();
        String originRefreshToken = tokenDto.getRefreshToken();

        int refreshTokenFlag = tokenProvider.validateToken(originRefreshToken);

        if (Objects.equals(-1, refreshTokenFlag) //잘못된 토큰
                || Objects.equals(2, refreshTokenFlag)) { // 토큰 유효기간 만료
            throw new BadRequestException("다시 로그인해주시기 바랍니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);

        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new LoginRequiredException("다시 로그인해주시기 바랍니다."));

        if (!Objects.equals(originRefreshToken, refreshToken.getValue())) {
            throw new BadRequestException("다시 로그인해주시기 바랍니다.");
        }

        String email = tokenProvider.getMemberEmailByToken(originAccessToken);
        Member member = memberRepository.findByEmail(email);

        if (Objects.isNull(member)) throw new BadRequestException("일치하는 사용자가 없습니다.");

        String newAccessToken = tokenProvider.createAccessToken(email, member.getAuthority());
        String newRefreshToken = tokenProvider.createRefreshToken(email, member.getAuthority());
        TokenDto newTokenDto = tokenProvider.createTokenDto(newAccessToken, newRefreshToken);

        refreshToken.changeVale(newRefreshToken);
        refreshTokenRepository.saveAndFlush(refreshToken);

        return newTokenDto;
    }

}
