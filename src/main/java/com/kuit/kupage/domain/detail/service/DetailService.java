package com.kuit.kupage.domain.detail.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.common.response.ResponseCode;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.detail.dto.SignupRequest;
import com.kuit.kupage.domain.detail.repository.DetailRepository;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kuit.kupage.common.response.ResponseCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailService {

    private final MemberRepository memberRepository;
    private final DetailRepository detailRepository;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public AuthTokenResponse signup(SignupRequest signupRequest, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NONE_MEMBER));

        if (member.getDetail() != null) {
            throw new MemberException(ALREADY_MEMBER);
        }

        Detail savedDetail = detailRepository.save(Detail.of(
                signupRequest.name(),
                signupRequest.studentNumber(),
                signupRequest.departName(),
                signupRequest.grade(),
                signupRequest.githubId(),
                signupRequest.email(),
                signupRequest.phoneNumber(),
                signupRequest.birthday()));

        member.updateDetail(savedDetail);

        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(member);

        return authTokenResponse;
    }
}
