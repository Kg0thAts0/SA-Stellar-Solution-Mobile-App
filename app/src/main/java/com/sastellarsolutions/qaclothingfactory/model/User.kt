package com.sastellarsolutions.qaclothingfactory.model

data class User(

    // Unique user ID.
    // Later this value will come from the backend.
    val id: String,

    // Employee's first name.
    val firstName: String,

    // Employee's surname.
    val lastName: String,

    // Employee's work email.
    val email: String,

    // Employee's authorised system role.
    val role: UserRole
) {

    // Convenient full-name property.
    val fullName: String
        get() = "$firstName $lastName"
}