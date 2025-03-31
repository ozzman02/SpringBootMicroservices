package com.ossant.photoapp.api.users.repository;

import com.ossant.photoapp.api.users.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

}
