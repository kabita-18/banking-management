package com.example.bankingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankingapp.model.Login;

public interface LoginRepository extends JpaRepository<Login, Long>{

}
