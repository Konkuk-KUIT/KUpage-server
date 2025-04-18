package com.kuit.kupage.domain.detail.service;

import com.kuit.kupage.common.auth.AuthTokenResponse;
import com.kuit.kupage.common.auth.JwtTokenService;
import com.kuit.kupage.domain.detail.Detail;
import com.kuit.kupage.domain.detail.dto.SignupRequest;
import com.kuit.kupage.domain.detail.repository.DetailRepository;
import com.kuit.kupage.domain.member.Member;
import com.kuit.kupage.domain.member.repository.MemberRepository;
import com.kuit.kupage.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailService {

    private final MemberRepository memberRepository;
    private final DetailRepository detailRepository;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public AuthTokenResponse signup(SignupRequest signupRequest, Long memberId) {

        //todo 예외 처리 어캐할지?
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));

        if (member.getDetail() != null) {
            throw new MemberException("이미 회원가입 된 멤버입니다.");
        }

        Detail savedDetail = detailRepository.save(Detail.of(signupRequest.name(),
                signupRequest.studentNumber(),
                signupRequest.departName(),
                signupRequest.grade(),
                signupRequest.githubId(),
                signupRequest.email(),
                signupRequest.phoneNumber(),
                signupRequest.birthday()));

        member.updateDetail(savedDetail);

        AuthTokenResponse authTokenResponse = jwtTokenService.generateTokens(memberId);

        return authTokenResponse;
    }
}
