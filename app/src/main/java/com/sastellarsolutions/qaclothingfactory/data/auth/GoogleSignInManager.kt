package com.sastellarsolutions.qaclothingfactory.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sastellarsolutions.qaclothingfactory.R

class GoogleSignInManager(
    private val context: Context
) {

    private val credentialManager =
        CredentialManager.create(context)

    suspend fun signIn(): String {

        val googleIdOption =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(
                    context.getString(
                        R.string.google_web_client_id
                    )
                )
                .build()

        val request =
            GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

        val result =
            credentialManager.getCredential(
                context = context,
                request = request
            )

        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            val googleCredential =
                GoogleIdTokenCredential.createFrom(
                    credential.data
                )

            return googleCredential.idToken
        }

        throw IllegalStateException(
            "Unsupported Google credential returned."
        )
    }
}
