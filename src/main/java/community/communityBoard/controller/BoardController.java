package community.communityBoard.controller;

import community.communityBoard.domain.Board;
import community.communityBoard.dto.BoardPostDto;
import community.communityBoard.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 1. @Controller -> @RestController로 변경 (JSON 반환)
@RequestMapping("/api/boards") // 관례상 API 경로는 /api를 붙이기도 합니다.
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 목록 조회 (GET http://localhost:8080/api/boards)
    @GetMapping
    public List<Board> list() {
        return boardService.findBoards();
    }

    // 게시글 상세 조회 (GET http://localhost:8080/api/boards/1)
    @GetMapping("/{id}")
    public ResponseEntity<Board> boardDetail(@PathVariable("id") Long id) {
        Board board = boardService.findOne(id);
        return ResponseEntity.ok(board);
    }

    // 게시글 등록 (POST http://localhost:8080/api/boards)
    // Postman에서 Body -> raw -> JSON으로 데이터를 보내야 하므로 @RequestBody 사용
    @PostMapping
    public ResponseEntity<?> postBoard(@Valid @RequestBody BoardPostDto boardPostDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        boardService.createBoard(boardPostDto);
        return ResponseEntity.ok("게시글이 성공적으로 등록되었습니다.");
    }

    // 게시글 수정 (POST 또는 PUT http://localhost:8080/api/boards/1)
    @PostMapping("/{id}/edit")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id, @Valid @RequestBody BoardPostDto boardPostDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        boardService.updateBoard(id, boardPostDto);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }
}
