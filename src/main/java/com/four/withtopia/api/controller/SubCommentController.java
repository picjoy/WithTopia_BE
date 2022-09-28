package com.four.withtopia.api.controller;

import com.four.withtopia.api.service.SubCommentService;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.dto.request.SubCommentRequestDto;
import com.four.withtopia.util.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class SubCommentController {

    private final SubCommentService subCommentService;

    @ApiOperation(value = "게시물 대댓글 작성")
    @PostMapping("/subcomment")
    public ResponseEntity<PrivateResponseBody> createSubComment(@RequestBody SubCommentRequestDto subCommentRequestDto, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(subCommentService.createSubComment(subCommentRequestDto, request));
    }

    @ApiOperation(value = "게시물 대댓글 조회")
    @GetMapping("/subcomments")
    public ResponseEntity<PrivateResponseBody> getAllSubComment(@RequestParam("commentId") Long commentId){
        return new ResponseUtil<>().forSuccess(subCommentService.getAllSubComment(commentId));
    }

    @ApiOperation(value = "게시물 대댓글 수정")
    @PutMapping("/subcomments/{subcommentId}")
    public ResponseEntity<PrivateResponseBody> updateSubComment(@PathVariable Long subcommentId,@RequestBody SubCommentRequestDto subCommentRequestDto, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(subCommentService.updateSubComment(subcommentId, subCommentRequestDto, request));
    }

    @ApiOperation(value = "게시물 대댓글 삭제")
    @DeleteMapping("/subcomments/{subcommentId}")
    public ResponseEntity<PrivateResponseBody> deleteSubComment(@PathVariable Long subcommentId, HttpServletRequest request){
        return new ResponseUtil<>().forSuccess(subCommentService.deleteSubComment(subcommentId,request));
    }


}
