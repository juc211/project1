package community.communityBoard.service;

import community.communityBoard.domain.Board;
import community.communityBoard.dto.BoardPostDto;
import community.communityBoard.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public Long createBoard(BoardPostDto boardPostDto) {
        Board board = new Board();
        board.updateTitle(boardPostDto.getTitle());
        board.updateContent(boardPostDto.getContent());
        board.updateWriter(boardPostDto.getWriter());
        return boardRepository.save(board).getId();
    }

    @Transactional
    public void updateBoard(Long id, BoardPostDto boardPostDto) {
        Board board = boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));
        board.updateTitle(boardPostDto.getTitle());
        board.updateContent(boardPostDto.getContent());
    }

    public List<Board> findBoards() {
        return boardRepository.findAll();
    }

    public Board findOne(Long id){
        return boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id = " + id));
    }

}
