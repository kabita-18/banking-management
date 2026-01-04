package com.example.bankingapp.DAO;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bankingapp.model.RefreshToken;
import com.example.bankingapp.model.RegisterUser;
import com.example.bankingapp.repository.RefreshTokenRepository;
import com.example.bankingapp.repository.RegisterUsersRepository;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenServiceImple{
	
	private static final long REFRESH_TOKEN_DURATION_MS = 7 * 24 * 60 * 60 * 1000; //7 days
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private RegisterUsersRepository registerUsersRepository;
	
//	@Transactional
	public RefreshToken createRefreshToken(RegisterUser user) {
		
		refreshTokenRepository.deleteByUser(user);
		
		RefreshToken refreshToken = new RefreshToken();
		
		refreshToken.setUser(user);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpirayDate(
				Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS)
				);
		return refreshTokenRepository.save(refreshToken);
		
	}
	
	public RefreshToken verifyExpiration(RefreshToken token) {
		if(token.getExpirayDate().isBefore(Instant.now())) {
			refreshTokenRepository.delete(token);
			throw new RuntimeException("Refresh token expired");
		}
		return token;
	}
	
	
}
