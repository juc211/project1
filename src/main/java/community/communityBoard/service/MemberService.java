package community.communityBoard.service;

import community.communityBoard.dto.MemberDto;
import community.communityBoard.entity.Member;
import community.communityBoard.entity.constant.Role;
import community.communityBoard.jwt.JwtTokenProvider;
import community.communityBoard.repository.MemberRepository;
import community.communityBoard.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(MemberDto.JoinRequest dto) {
        //중복 회원 검증
        validateDuplicateMember(dto);

        Member member = Member.builder()
                .email(dto.getEmail())
                //비밀번호 암호화 : PasswordEncoder 도입
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .role(Role.USER)
                .build();
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 중복 회원 검증
     */
    private void validateDuplicateMember(MemberDto.JoinRequest dto) {
        // 실무에서는 멀티쓰레드 상황을 고려해 DB의 email 컬럼에 유니크 제약조건을 추가하는 것이 안전함
        Optional<Member> findMember = memberRepository.findByEmail(dto.getEmail());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 로그인 (security & JWT 적용)
     */
    @Transactional
    public MemberDto.LoginResponse login (MemberDto.LoginRequest dto){
        try{
            // 1. Spring Security 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

             // 2. JWT 토큰 생성
            String token = jwtTokenProvider.createToken(authentication.getName());

            // 3. 인증된 사용자 정보 추출
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 4.토큰, 사용자 정보 리턴
            return new MemberDto.LoginResponse(
                    token,
                    userDetails.getUsername(),
                    userDetails.getNickname()
            );
        }catch (BadCredentialsException e) {
            // 비밀번호 불일치 시 Spring Security가 던지는 예외
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }catch (InternalAuthenticationServiceException e) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }catch (AuthenticationException e){
            throw new RuntimeException("인증 과정에서 오류가 발생하였습니다.");
        }

    }


    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 개별 조회
     */
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + memberId));
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public void update(Long id, MemberDto.UpdateRequest dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + id));

        member.updateInfo(dto.getNickname(), passwordEncoder.encode(dto.getPassword()));
    }

}
