// trong package repository
package com.minhduyen.quanlydatphong.repository;

import com.minhduyen.quanlydatphong.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}