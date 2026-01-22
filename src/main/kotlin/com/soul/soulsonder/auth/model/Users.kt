package com.soul.soulsonder.auth.model

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "users")
data class Users(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var password: String,


    // 값 타입 컬렉션을 별도 테이블에 저장함
    // 조인 컬럼의 암묵 추론과 네이밍 전략
    // Hibernat 의 암묵 규칙이 또 하나의 조인 컬럼을 논리명 userId 로 추론 -> 암묵 논리명 userId -> 물리명 user_id 로 바뀌어 물리적 충돌 발생
    // -> Hibernate 는 같은 물리 컬럼(user_id) 에 서로 다른 논리명(user_id, userId) 을 바인딩 했다고 판단해서 예외 던짐
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "role"])]
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<Role> = mutableSetOf(Role.USER),

    var enabled: Boolean = true,

    )


enum class Role {USER, ADMIN}
