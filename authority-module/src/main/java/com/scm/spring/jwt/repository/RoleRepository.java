package com.scm.spring.jwt.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.scm.spring.jwt.model.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Optional<Role> findByName(String name);
}