
package com.proyecto.descuentosya.auth

object AuthManager {
    private val adminUser = User("admin", "admin@descuentosya.com", hashPassword("1234"))
    var currentUser: User? = null

    data class User(val username: String, val email: String, val hashedPassword: String)

    fun login(email: String, password: String): Boolean {
        return if (email == adminUser.email && hashPassword(password) == adminUser.hashedPassword) {
            currentUser = adminUser
            true
        } else {
            false
        }
    }

    private fun hashPassword(password: String): String {
        // Simulamos hash con base64 (solo como ejemplo simple)
        return password.reversed() // ¡Reemplazalo por una lib segura si lo hacés real!
    }

    fun logout() {
        currentUser = null
    }
}
