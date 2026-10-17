package com.sastellarsolutions.qaclothingfactory.ui.forgotpassword

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.R
import com.sastellarsolutions.qaclothingfactory.data.repository.AuthRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    onResetPassword: (
        email: String,
        token: String
    ) -> Unit,

    onBackToLogin: () -> Unit
) {

    // ========================================================
    // REPOSITORY
    // ========================================================

    val authRepository =
        remember {
            AuthRepository()
        }

    val coroutineScope =
        rememberCoroutineScope()


    // ========================================================
    // FORM STATE
    // ========================================================

    var email by remember {
        mutableStateOf("")
    }

    var emailError by remember {
        mutableStateOf<String?>(null)
    }

    var requestError by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }


    // ========================================================
    // SCREEN
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(
                rememberScrollState()
            )
            .imePadding()
            .padding(
                horizontal = 28.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier =
                Modifier.height(40.dp)
        )


        // ====================================================
        // LOGO
        // ====================================================

        Image(
            painter =
                painterResource(
                    id =
                        R.drawable
                            .qa_clothing_factory_logo
                ),

            contentDescription =
                "QA Clothing Factory Logo",

            modifier =
                Modifier.size(100.dp)
        )


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // ====================================================
        // COMPANY
        // ====================================================

        Text(
            text =
                "QA CLOTHING FACTORY",

            fontFamily =
                CinzelFontFamily,

            fontWeight =
                FontWeight.SemiBold,

            fontSize =
                17.sp,

            letterSpacing =
                1.5.sp,

            color =
                Color(0xFF111111),

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(38.dp)
        )


        // ====================================================
        // TITLE
        // ====================================================

        Text(
            text =
                "Forgot Password",

            fontFamily =
                CinzelFontFamily,

            fontWeight =
                FontWeight.SemiBold,

            fontSize =
                27.sp,

            color =
                Color(0xFF111111),

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Text(
            text =
                "Enter the email address linked to your employee account to continue.",

            fontFamily =
                MontserratFontFamily,

            fontSize =
                13.sp,

            lineHeight =
                19.sp,

            color =
                Color(0xFF777777),

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(32.dp)
        )


        // ====================================================
        // EMAIL
        // ====================================================

        OutlinedTextField(
            value =
                email,

            onValueChange = {

                email = it

                emailError = null

                requestError = null
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                !isLoading,

            label = {

                Text(
                    text =
                        "Work Email",

                    fontFamily =
                        MontserratFontFamily
                )
            },

            placeholder = {

                Text(
                    text =
                        "name@qafactory.com",

                    fontFamily =
                        MontserratFontFamily,

                    fontSize =
                        12.sp
                )
            },

            singleLine =
                true,

            isError =
                emailError != null,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Email
                ),

            shape =
                RoundedCornerShape(
                    14.dp
                ),

            colors =
                OutlinedTextFieldDefaults.colors(

                    focusedBorderColor =
                        Color(0xFF111111),

                    unfocusedBorderColor =
                        Color(0xFFD0D0D0),

                    focusedLabelColor =
                        Color(0xFF111111),

                    cursorColor =
                        Color(0xFF111111)
                )
        )


        if (emailError != null) {

            Text(
                text =
                    emailError ?: "",

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 10.dp,
                            top = 5.dp
                        ),

                fontFamily =
                    MontserratFontFamily,

                fontSize =
                    11.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .error
            )
        }


        // ====================================================
        // REQUEST ERROR
        // ====================================================

        if (requestError != null) {

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    requestError ?: "",

                modifier =
                    Modifier.fillMaxWidth(),

                fontFamily =
                    MontserratFontFamily,

                fontWeight =
                    FontWeight.Medium,

                fontSize =
                    12.sp,

                lineHeight =
                    18.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .error,

                textAlign =
                    TextAlign.Center
            )
        }


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ====================================================
        // CONTINUE BUTTON
        // ====================================================

        Button(
            onClick = {

                if (isLoading) {
                    return@Button
                }


                // =============================================
                // CLEAR ERRORS
                // =============================================

                emailError = null

                requestError = null


                // =============================================
                // VALIDATE EMAIL
                // =============================================

                val trimmedEmail =
                    email.trim()


                if (trimmedEmail.isBlank()) {

                    emailError =
                        "Please enter your work email."

                    return@Button
                }


                if (
                    !Patterns.EMAIL_ADDRESS
                        .matcher(trimmedEmail)
                        .matches()
                ) {

                    emailError =
                        "Please enter a valid email address."

                    return@Button
                }


                // =============================================
                // REQUEST RESET
                // =============================================

                coroutineScope.launch {

                    isLoading = true


                    when (
                        val result =
                            authRepository
                                .forgotPassword(
                                    email =
                                        trimmedEmail
                                )
                    ) {

                        // =====================================
                        // SUCCESS
                        // =====================================

                        is AuthRepository
                        .ForgotPasswordResult
                        .Success -> {

                            isLoading =
                                false


                            val resetToken =
                                result
                                    .response
                                    .resetToken


                            // =================================
                            // DEVELOPMENT RESET TOKEN
                            // =================================
                            //
                            // During development the API
                            // returns the token directly.
                            //
                            // Production will deliver this
                            // through email instead.
                            // =================================

                            if (
                                !resetToken
                                    .isNullOrBlank()
                            ) {

                                onResetPassword(
                                    trimmedEmail,
                                    resetToken
                                )

                            } else {

                                requestError =
                                    "Password reset instructions could not be generated for this account."
                            }
                        }


                        // =====================================
                        // ERROR
                        // =====================================

                        is AuthRepository
                        .ForgotPasswordResult
                        .Error -> {

                            isLoading =
                                false

                            requestError =
                                result.message
                        }
                    }
                }
            },

            enabled =
                !isLoading,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),

            shape =
                RoundedCornerShape(
                    14.dp
                ),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        Color(0xFF111111),

                    contentColor =
                        Color.White,

                    disabledContainerColor =
                        Color(0xFF555555),

                    disabledContentColor =
                        Color.White
                )
        ) {

            if (isLoading) {

                CircularProgressIndicator(
                    modifier =
                        Modifier.size(21.dp),

                    strokeWidth =
                        2.dp,

                    color =
                        Color.White
                )


                Spacer(
                    modifier =
                        Modifier.size(10.dp)
                )


                Text(
                    text =
                        "PLEASE WAIT...",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize =
                        12.sp,

                    letterSpacing =
                        1.sp
                )

            } else {

                Text(
                    text =
                        "CONTINUE",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize =
                        13.sp,

                    letterSpacing =
                        1.2.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        // ====================================================
        // BACK TO LOGIN
        // ====================================================

        Text(
            text =
                "BACK TO SIGN IN",

            fontFamily =
                MontserratFontFamily,

            fontWeight =
                FontWeight.SemiBold,

            fontSize =
                11.sp,

            letterSpacing =
                1.sp,

            color =
                Color(0xFF111111),

            modifier =
                Modifier
                    .clickable(
                        enabled =
                            !isLoading
                    ) {

                        onBackToLogin()
                    }
                    .padding(
                        vertical = 12.dp
                    )
        )


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}