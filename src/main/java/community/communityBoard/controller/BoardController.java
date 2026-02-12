package community.communityBoard.controller;

import community.communityBoard.entity.Board;
import community.communityBoard.dto.BoardPostDto;
import community.communityBoard.dto.BoardResponseDto;
import community.communityBoard.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController // JSON 반환을 위한 @RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> list() {
        List<Board> boards = boardService.findBoards();

        // Stream을 사용하여 Board 엔티티 리스트를 DTO 리스트로 변환
        List<BoardResponseDto> responseDto = boards.stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> boardDetail(@PathVariable("id") Long id) {
        Board board = boardService.findOne(id);
        return ResponseEntity.ok(new BoardResponseDto(board));
    }

    /**
     * 게시글 등록
     */
    @PostMapping
    public ResponseEntity<?> postBoard(@Valid @RequestBody BoardPostDto boardPostDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Long boardId = boardService.createBoard(boardPostDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(boardId + "번 게시글이 성공적으로 등록되었습니다.");
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/{id}") //PostMapping -> patchMapping 변경으로 URL에 동작을 작성하지 않아도 의미 전달
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id, @Valid @RequestBody BoardPostDto boardPostDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        boardService.updateBoard(id, boardPostDto);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }

    /**
     * 게시글 삭제
     *
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable("id") Long id) {
        try {
            boardService.deleteBoard(id);
            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
