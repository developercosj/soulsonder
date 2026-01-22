package com.soul.soulsonder.auth.repository

import com.soul.soulsonder.auth.model.Users
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<Users, Long> {

    // 로그인시 role 이 필요하면, EntityGraph 로 즉시 로딩 가능
    @EntityGraph(attributePaths = ["roles"])
    fun findByEmail(email: String): Users?
}