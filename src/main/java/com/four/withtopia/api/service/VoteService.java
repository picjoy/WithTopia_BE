package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateResponseBody;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Rank;
import com.four.withtopia.db.domain.Vote;
import com.four.withtopia.db.repository.VoteRepository;
import com.four.withtopia.dto.request.VoteRequestDto;
import com.four.withtopia.dto.response.VoteResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final MemberCheckUtils memberCheckUtils;
    private final RankService rankService;

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
        // 투표를 생성하고, 랭크에 좋아요를 받은 사람을 등록
        Vote vote = Vote.builder()
                .voteBy(voteByMember.getNickName())
                .voteTo(requestDto.getNickname())
                .build();

        voteRepository.save(vote);

        Rank rankSave = rankService.rankSave(requestDto);
        System.out.println("rankSave = " + rankSave);
        if(rankSave == null){
            // 좋아요를 받은 내역이 없는 사람이 싫어요를 받는다면 에러 "더 이상 내려갈 인기도가 없습니다." 메세지 보내기
            return new ResponseEntity<>(new PrivateResponseBody(ErrorCode.HAVE_NOT_POPULARITY_ERROR), HttpStatus.BAD_REQUEST);
        }

        VoteResponseDto responseDto = VoteResponseDto.createVoteResponse(rankSave);

        return new ResponseEntity(new PrivateResponseBody(ErrorCode.OK, responseDto), HttpStatus.OK);
    }

    // 자정이 지나면 투표 내역이 리셋
    @Scheduled(cron = "0 0 0 * * *")// 초(0~59) 분(0~59) 시(0~23) 일(1-31) 월(1-12) 요일(0 = 일 ~ 7 = 토)
    public void scheduleRun(){
        voteRepository.deleteAll();
    }
}
