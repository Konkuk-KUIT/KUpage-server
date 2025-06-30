package com.kuit.kupage.domain.role.repository;

import com.kuit.kupage.domain.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.discordRoleId IN :ids")
    List<Role> findAllByDiscordRoleId(List<Long> ids);

}
