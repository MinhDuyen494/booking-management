package com.minhduyen.quanlydatphong.repository;

import com.minhduyen.quanlydatphong.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}