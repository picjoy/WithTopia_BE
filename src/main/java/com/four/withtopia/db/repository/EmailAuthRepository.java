package com.four.withtopia.db.repository;

import com.four.withtopia.db.domain.EmailAuth;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableRedisRepositories
public interface EmailAuthRepository extends CrudRepository<EmailAuth, String> {

    boolean existsByEmail(String email);

    EmailAuth findByEmail(String email);


}
