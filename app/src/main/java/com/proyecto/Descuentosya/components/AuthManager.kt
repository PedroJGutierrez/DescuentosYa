package com.proyecto.Descuentosya.components

object AuthManager {
    private val users = listOf(
        User("admin", "admin@descuentosya.com", hashPassword("1234")),
        User("admin2", "admin@correo.com", hashPassword("1234"))
    )

    var currentUser: User? = null

    data class User(val username: String, val email: String, val hashedPassword: String)

    fun login(email: String, password: String): Boolean {
        val user = users.find { it.email == email && it.hashedPassword == hashPassword(password) }
        return if (user != null) {
            currentUser = user
            true
        } else {
            false
        }
    }

    private fun hashPassword(password: String): String {
        return password.reversed()
    }

    fun logout() {
        currentUser = null
    }
}
