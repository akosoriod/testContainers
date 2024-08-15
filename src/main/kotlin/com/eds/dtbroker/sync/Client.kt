package com.eds.dtbroker.sync

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Entity

data class Client(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @field:NotBlank val name: String,
    @field:Email @field:NotBlank val email: String
){
    // No-arg constructor for JPA
    constructor() : this(null, "", "")
}

@Repository
interface UserRepository : JpaRepository<Client, Long>