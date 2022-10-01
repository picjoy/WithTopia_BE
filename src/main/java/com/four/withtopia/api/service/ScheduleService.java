package com.four.withtopia.api.service;

import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@RequiredArgsConstructor
public class ScheduleService {
    private final VoteRepository voteRepository;

    // 자정이 지나면 투표 내역이 리셋
    @Scheduled(cron = "0 0 0 * * *")// 초(0~59) 분(0~59) 시(0~23) 일(1-31) 월(1-12) 요일(0 = 일 ~ 7 = 토)
    public void deleteVote(){
        voteRepository.deleteAll();
    }
}
