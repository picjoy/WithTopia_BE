package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Vote;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.VoteRepository;
import com.four.withtopia.dto.request.VoteRequestDto;
import com.four.withtopia.dto.response.VoteResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final MemberCheckUtils memberCheckUtils;
    private final MemberRepository memberRepository;

    @Transactional
    public ResponseEntity<?> vote(VoteRequestDto requestDto, HttpServletRequest request){
        //토큰 검증 및 투표하는 멤버 객체 가져오기
        Member voteByMember = memberCheckUtils.checkMember(request);

        // 자신에게 투표하지 못하게 막기
        if(voteByMember.getNickName().equals(requestDto.getNickname())){
            return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.NOT_VOTE_TO_SELF_ERROR), HttpStatus.BAD_REQUEST);
        }

        // 이 멤버가 투표를 했는가? 만일 했다면 에러코드 발생
        Vote checkVote = voteRepository.findByVoteByAndVoteTo(voteByMember.getNickName(), requestDto.getNickname());
        if(checkVote != null){
            return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.VOTE_DUPLICATION_ERROR), HttpStatus.BAD_REQUEST);
        }

        // 이 멤버가 투표를 안 했다면?
        // 투표를 생성하고, 좋아요를 올린다.
        Vote vote = Vote.builder()
                .voteBy(voteByMember.getNickName())
                .voteTo(requestDto.getNickname())
                .build();

        voteRepository.save(vote);

        // 투표 받는 멤버 찾기
        Optional<Member> voteToMember = memberRepository.findByNickName(requestDto.getNickname());

        // 투표 받는 멤버가 없다면 에러코드 발생
        if(voteToMember == null || voteToMember.isEmpty()){
            return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.POST_MEMEBER_NOT_FOUND_ERROR), HttpStatus.BAD_REQUEST);
        }

        // 싫어요를 받으면 lick count 내리기
        if(!requestDto.isVote()){
            if(voteToMember.get().getLikeCnt() == 0){
                // 인기도가 이미 0이라면
                return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.HAVE_NOT_POPULARITY_ERROR), HttpStatus.BAD_REQUEST);
            }
            Long likeCnt = voteToMember.get().getLikeCnt() - 1;
            VoteResponseDto responseDto = getVoteResponseDto(voteToMember, likeCnt);
            return new ResponseEntity(new PrivateResponseBody(ErrorCode.OK, responseDto), HttpStatus.OK);
        }

        // 좋아요를 받는다면 like count 올라가기
        Long likeCnt = voteToMember.get().getLikeCnt() + 1;
        VoteResponseDto responseDto = getVoteResponseDto(voteToMember, likeCnt);
        return new ResponseEntity(new PrivateResponseBody(ErrorCode.OK, responseDto), HttpStatus.OK);
    }

    private VoteResponseDto getVoteResponseDto(Optional<Member> voteToMember, Long likeCnt) {
        voteToMember.get().updatePopularity(likeCnt);
        memberRepository.save(voteToMember.get());
        VoteResponseDto responseDto = VoteResponseDto.createVoteResponse(voteToMember.get());
        return responseDto;
    }

}
