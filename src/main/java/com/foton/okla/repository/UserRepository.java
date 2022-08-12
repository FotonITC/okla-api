package com.foton.okla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foton.okla.model.OklaUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<OklaUser, Long> {
	
	Optional<OklaUser> findById(Long id);
    OklaUser findByEmail(String email);
	boolean existsById(Long id);
	boolean existsByEmail(String email);
}
