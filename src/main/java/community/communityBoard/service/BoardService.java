package community.communityBoard.service;

import community.communityBoard.entity.Board;
import community.communityBoard.dto.BoardPostDto;
import community.communityBoard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //클래스 레벨에서의 트랜잭션 설정 (읽기 전용)
@RequiredArgsConstructor // final필드, @NonNull이 붙은 필드에 대한 생성자 자동 생성
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 게시판 등록
     */
    @Transactional // 메서드 레벨에서의 트랜잭션 설정 (더 우선순위로 쓰기 권한을 가짐)
    public Long createBoard(BoardPostDto boardPostDto) {

        //빌더 패턴 적용
        Board board = Board.builder()
                .title(boardPostDto.getTitle())
                .content(boardPostDto.getContent())
                .writer(boardPostDto.getWriter())
                .build();

        return boardRepository.save(board).getId();
    }

    /**
     * 게시판 수정
     */
    @Transactional
    public void updateBoard(Long id, BoardPostDto boardPostDto) {
        Board board = boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));

        //Dirty Checking
        board.updateTitle(boardPostDto.getTitle());
        board.updateContent(boardPostDto.getContent());
    }

    /**
     * 게시판 목록 조회
     */
    public List<Board> findBoards() {
        return boardRepository.findAll();
    }

    /**
     * 게시판 상세 조회
     */
    public Board findOne(Long id){
        return boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id = " + id));
    }

    /**
     * 게시판 삭제
     */
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("삭제하려는 게시물이 없습니다 id = "+id));
        boardRepository.delete(board);
    }
}
