package com.example.webflux.service;

import com.example.webflux.dao.UserDao;
import com.example.webflux.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author MrBird
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private ReactiveMongoTemplate template;

    public Flux<User> getUsers() {
        return userDao.findAll();
    }

    public Mono<User> getUser(String id) {
        return this.userDao.findById(id);
    }

    /**
     * 新增和修改都是 save方法，
     * id 存在为修改，id 不存在为新增
     */
    public Mono<User> createUser(User user) {
        return userDao.save(user);
    }

    public Mono<Void> deleteUser(String id) {
        return this.userDao.findById(id)
                .flatMap(user -> this.userDao.delete(user));
    }

    public Mono<User> updateUser(String id, User user) {
        return this.userDao.findById(id)
                .flatMap(u -> {
                    u.setName(user.getName());
                    u.setAge(user.getAge());
                    u.setDescription(user.getDescription());
                    return this.userDao.save(u);
                });
    }

    public Flux<User> getUserByAge(Integer from, Integer to) {
        return this.userDao.findByAgeBetween(from, to);
    }

    public Flux<User> getUserByName(String name) {
        return this.userDao.findByNameEquals(name);
    }

    public Flux<User> getUserByDescription(String description) {
        return this.userDao.findByDescriptionIsLike(description);
    }

    /**
     * 分页查询，只返回分页后的数据，count值需要通过 getUserByConditionCount
     * 方法获取
     */
    public Flux<User> getUserByCondition(int size, int page, User user) {
        Query query = getQuery(user);
        Sort sort = new Sort(Sort.Direction.DESC, "age");
        Pageable pageable = PageRequest.of(page, size, sort);

        return template.find(query.with(pageable), User.class);
    }

    /**
     * 返回 count，配合 getUserByCondition使用
     */
    public Mono<Long> getUserByConditionCount(User user) {
        Query query = getQuery(user);
        return template.count(query, User.class);
    }

    private Query getQuery(User user) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (!StringUtils.isEmpty(user.getName())) {
            criteria.and("name").is(user.getName());
        }
        if (!StringUtils.isEmpty(user.getDescription())) {
            criteria.and("description").regex(user.getDescription());
        }
        query.addCriteria(criteria);
        return query;
    }
}
