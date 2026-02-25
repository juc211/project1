package community.communityBoard.service;

import community.communityBoard.dto.MemberDto;
import community.communityBoard.entity.Member;
import community.communityBoard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(MemberDto.JoinRequest dto) {
        validateDuplicateMember(dto);
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
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
     * 로그인
     */
    public Member login(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(m -> m.getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
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

        member.updateInfo(dto.getNickname(), dto.getPassword());
    }

}
