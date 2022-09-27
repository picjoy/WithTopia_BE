package com.four.withtopia.db.repository;


import com.four.withtopia.db.domain.RefreshToken;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


@EnableRedisRepositories
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


    Optional<RefreshToken> findByNickname(String email);

    RefreshToken findByValue(String value);
}
