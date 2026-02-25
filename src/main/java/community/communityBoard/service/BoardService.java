package community.communityBoard.service;

import community.communityBoard.dto.BoardDto;
import community.communityBoard.entity.Board;
import community.communityBoard.entity.Member;
import community.communityBoard.repository.BoardRepository;
import community.communityBoard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) //클래스 레벨에서의 트랜잭션 설정 (읽기 전용)
@RequiredArgsConstructor // final필드, @NonNull이 붙은 필드에 대한 생성자 자동 생성
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시판 등록
     */
    @Transactional // 메서드 레벨에서의 트랜잭션 설정 (더 우선순위로 쓰기 권한을 가짐)
    public Long createBoard(BoardDto.Request boardPostDto, Long memberId) {

        // 작성자(Member) 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id = " + memberId));

        //빌더 패턴 적용
        Board board = Board.builder()
                .title(boardPostDto.getTitle())
                .content(boardPostDto.getContent())
                .member(member)
                .build();

        return boardRepository.save(board).getId();
    }

    /**
     * 게시판 수정
     */
    @Transactional
    public void updateBoard(Long id, BoardDto.Request dto, Long currentMemberId) {
        // 검증 1. DB 존재 여부 확인
        Board board = boardRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));

        // 검증 2. 게시글 작성자 id 와 현재 로그인한 멤버의 id 비교
        if(!board.getMember().getId().equals(currentMemberId)){
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        //Dirty Checking
        board.updateTitle(dto.getTitle());
        board.updateContent(dto.getContent());
    }

    /**
     * 게시판 목록 조회
     */
    public List<BoardDto.Response> findBoards() {

        
        return boardRepository.findAll().stream()
                .map(BoardDto.Response ::new)
                .collect(Collectors.toList());
    }

    /**
     * 게시판 상세 조회
     */
    public Board findOne(Long id){
        //존재하지 않는 ID를 조회 시 에러를 던짐
        return boardRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id = " + id));
    }

    /**
     * 게시판 삭제
     */
    @Transactional
    public void deleteBoard(Long id, Long currentMemberId) {
        //삭제하려는 게시물이 없을 시 에러를 던짐
        Board board = boardRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("삭제하려는 게시물이 없습니다 id = "+id));

        // 권한 검증
        if (!board.getMember().getId().equals(currentMemberId)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }
        boardRepository.delete(board);
    }
}
