package com.example.webflux.dao;

import com.example.webflux.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * @author MrBird
 */
@Repository
public interface UserDao extends ReactiveMongoRepository<User, String> {

    /**
     * 根据年龄段来查找
     *
     * @param from from
     * @param to   to
     * @return Flux<User>
     */
    Flux<User> findByAgeBetween(Integer from, Integer to);

    /**
     * 更具描述来模糊查询用户
     *
     * @param description 描述
     * @return Flux<User>
     */
    Flux<User> findByDescriptionIsLike(String description);

    /**
     * 通过用户名查询
     *
     * @param name 用户名
     * @return Flux<User>
     */
    Flux<User> findByNameEquals(String name);

}
