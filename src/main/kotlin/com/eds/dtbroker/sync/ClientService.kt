package com.eds.dtbroker.sync

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ClientService(private val userRepository: UserRepository) {

    fun findAllUsers(): List<Client> {
        return userRepository.findAll()
    }

    fun saveUser(user: Client): Client {
        return userRepository.save(user)
    }
}