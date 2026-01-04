package com.example.bankingapp.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String token;
	
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName="id")
	private RegisterUser user;
	
	@Column(nullable = false)
	private Instant expirayDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public RegisterUser getUser() {
		return user;
	}

	public void setUser(RegisterUser user) {
		this.user = user;
	}

	public Instant getExpirayDate() {
		return expirayDate;
	}

	public void setExpirayDate(Instant expirayDate) {
		this.expirayDate = expirayDate;
	}

	public RefreshToken(Long id, String token, RegisterUser user, Instant expirayDate) {
		super();
		this.id = id;
		this.token = token;
		this.user = user;
		this.expirayDate = expirayDate;
	}

	public RefreshToken() {
		super();
	}

	@Override
	public String toString() {
		return "RefreshToken [id=" + id + ", token=" + token + ", user=" + user + ", expirayDate=" + expirayDate + "]";
	}
	
	
	
	

}
