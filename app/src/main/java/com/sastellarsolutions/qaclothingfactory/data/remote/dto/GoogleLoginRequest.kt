package com.sastellarsolutions.qaclothingfactory.data.remote.dto

/**
 * Sends the Google ID token to the ASP.NET Core backend.
 *
 * The backend must validate this token with Google's
 * public signing keys and check the employee's status
 * before issuing the application's JWT.
 */
data class GoogleLoginRequest(
    val idToken: String
)