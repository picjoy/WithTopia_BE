package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.CommentService;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.dto.request.CommentRequestDto;
import com.four.withtopia.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "게시물 댓글 작성")
    @PostMapping("/comment")
    public ResponseEntity<PrivateResponseBody> createComment(@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(commentService.createComment(commentRequestDto, request));
    }

    @ApiOperation(value = "게시물 댓글 조회")
    @GetMapping("/comments/{page}")
    public ResponseEntity<PrivateResponseBody> getAllComment(@RequestParam("postId") Long postId, @PathVariable int page){
        return new ResponseUtil<>().forSuccess(commentService.getAllComment(postId, page));
    }

    @ApiOperation(value = "게시물 댓글 수정")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<PrivateResponseBody> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(commentService.updateComment(commentId, commentRequestDto, request));
    }

    @ApiOperation(value = "게시물 댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<PrivateResponseBody> deleteComment(@PathVariable Long commentId, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(commentService.deleteComment(commentId, request));
    }



}
