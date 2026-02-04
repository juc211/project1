package community.communityBoard.controller;

import community.communityBoard.domain.Board;
import community.communityBoard.dto.BoardPostDto;
import community.communityBoard.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //게시글 목록 조회 (초기 화면(http://localhost:8080/boards))
    @GetMapping
    public String list(Model model) {
        List<Board> boards = boardService.findBoards();
        model.addAttribute("boards", boards);
        return "boards/list";
    }

    //게시글 등록 폼 호출
    @GetMapping("/new")
    public String createBoardForm(Model model) {
        model.addAttribute("boardPostDto", new BoardPostDto());
        return "boards/createBoardForm";
    }

    //게시글 상세 조회
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable("id") Long id, Model model) {
        Board board = boardService.findOne(id);
        model.addAttribute("board", board);
        return "boards/boardDetail";
    }

    //사용자가 글쓰기 화면에서 내용을 입력한 후 '등록하기' 버튼을 눌렀을 떄 실행
    @PostMapping("/new")
    public String postBoard(@Valid @ModelAttribute BoardPostDto boardPostDto, BindingResult result) {
        if (result.hasErrors()) {
            return "boards/createBoardForm";
        }
        boardService.createBoard(boardPostDto);
        return "redirect:/boards";
    }

    //게시글 수정 폼 호출
    @GetMapping("/{id}/edit")
    public String updateBoardForm(@PathVariable("id") Long id, Model model){
        Board board = boardService.findOne(id);
        model.addAttribute("boardPostDto", new BoardPostDto(board));
        model.addAttribute("id", id);
        return "boards/updateBoardForm";
    }

    //게시글 수정
    @PostMapping("/{id}/edit")
    public String updateBoard(@PathVariable("id") Long id, @Valid @ModelAttribute BoardPostDto boardPostDto, BindingResult result) {
        if (result.hasErrors()) {
            return "boards/updateBoardForm";
        }
        boardService.updateBoard(id, boardPostDto);
        return "redirect:/boards/{id}";
    }

}
