package com.example.bankingapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.bankingapp.repository.AccountRepository;

@Component("accountSecurity")
public class AccountSecurity {
	
	@Autowired
	private AccountRepository accountRepository;
	
	public boolean isOwner(Long accountNumber) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean isAdmin = auth.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
				
		if(isAdmin) return true;
		
		String username = auth.getName();
		return accountRepository.existsByAccountNumberAndEmail(accountNumber, username);
	}
	
	
}
