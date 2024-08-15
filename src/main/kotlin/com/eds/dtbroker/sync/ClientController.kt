package com.eds.dtbroker.sync

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class ClientController(private val userService: ClientService) {

    @GetMapping
    fun getAllUsers(): List<Client> {
        return userService.findAllUsers()
    }

    @PostMapping
    fun createUser(@RequestBody user: Client): Client {
        return userService.saveUser(user)
    }
}