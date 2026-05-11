package com.suplementos.erp_suplementos.modules.security.repository;

import com.suplementos.erp_suplementos.modules.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {

    // O Spring Security precisa que o retorno seja do tipo UserDetails
    UserDetails findByLogin(String login);
}