package com.example.bankingapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.bankingapp.model.RefreshToken;
import com.example.bankingapp.model.RegisterUser;

import jakarta.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	@Modifying
	@Transactional
	void deleteByUser(RegisterUser user);

	Optional<RefreshToken> findByToken(String refreshToken);
	

}
