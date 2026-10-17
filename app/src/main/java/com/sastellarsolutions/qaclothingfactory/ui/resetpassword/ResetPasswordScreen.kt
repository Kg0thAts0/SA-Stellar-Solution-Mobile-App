package com.sastellarsolutions.qaclothingfactory.ui.resetpassword

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.R
import com.sastellarsolutions.qaclothingfactory.data.repository.AuthRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(
    email: String,
    token: String,
    onPasswordResetSuccess: () -> Unit,
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

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var showNewPassword by remember {
        mutableStateOf(false)
    }

    var showConfirmPassword by remember {
        mutableStateOf(false)
    }

    var newPasswordError by remember {
        mutableStateOf<String?>(null)
    }

    var confirmPasswordError by remember {
        mutableStateOf<String?>(null)
    }

    var requestError by remember {
        mutableStateOf<String?>(null)
    }

    var successMessage by remember {
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
        // COMPANY NAME
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
                "Reset Password",

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
                "Create a new password for your employee account.",

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
                Modifier.height(12.dp)
        )


        // ====================================================
        // EMAIL
        // ====================================================

        Text(
            text =
                email,

            fontFamily =
                MontserratFontFamily,

            fontWeight =
                FontWeight.SemiBold,

            fontSize =
                12.sp,

            color =
                Color(0xFF444444),

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )


        // ====================================================
        // NEW PASSWORD
        // ====================================================

        OutlinedTextField(
            value =
                newPassword,

            onValueChange = {

                newPassword = it

                newPasswordError = null

                requestError = null
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                !isLoading &&
                        successMessage == null,

            label = {

                Text(
                    text =
                        "New Password",

                    fontFamily =
                        MontserratFontFamily
                )
            },

            singleLine =
                true,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password
                ),

            visualTransformation =
                if (showNewPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            trailingIcon = {

                Text(
                    text =
                        if (showNewPassword) {
                            "HIDE"
                        } else {
                            "SHOW"
                        },

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize =
                        10.sp,

                    color =
                        Color(0xFF111111),

                    modifier =
                        Modifier
                            .clickable(
                                enabled =
                                    !isLoading &&
                                            successMessage == null
                            ) {

                                showNewPassword =
                                    !showNewPassword
                            }
                            .padding(8.dp)
                )
            },

            isError =
                newPasswordError != null,

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


        if (newPasswordError != null) {

            Text(
                text =
                    newPasswordError ?: "",

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


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        // ====================================================
        // CONFIRM PASSWORD
        // ====================================================

        OutlinedTextField(
            value =
                confirmPassword,

            onValueChange = {

                confirmPassword = it

                confirmPasswordError = null

                requestError = null
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                !isLoading &&
                        successMessage == null,

            label = {

                Text(
                    text =
                        "Confirm Password",

                    fontFamily =
                        MontserratFontFamily
                )
            },

            singleLine =
                true,

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password
                ),

            visualTransformation =
                if (showConfirmPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },

            trailingIcon = {

                Text(
                    text =
                        if (showConfirmPassword) {
                            "HIDE"
                        } else {
                            "SHOW"
                        },

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize =
                        10.sp,

                    color =
                        Color(0xFF111111),

                    modifier =
                        Modifier
                            .clickable(
                                enabled =
                                    !isLoading &&
                                            successMessage == null
                            ) {

                                showConfirmPassword =
                                    !showConfirmPassword
                            }
                            .padding(8.dp)
                )
            },

            isError =
                confirmPasswordError != null,

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


        if (confirmPasswordError != null) {

            Text(
                text =
                    confirmPasswordError ?: "",

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
        // API ERROR
        // ====================================================

        if (requestError != null) {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
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


        // ====================================================
        // SUCCESS MESSAGE
        // ====================================================

        if (successMessage != null) {

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            Text(
                text =
                    successMessage ?: "",

                modifier =
                    Modifier.fillMaxWidth(),

                fontFamily =
                    MontserratFontFamily,

                fontWeight =
                    FontWeight.SemiBold,

                fontSize =
                    12.sp,

                lineHeight =
                    18.sp,

                color =
                    Color(0xFF1B5E20),

                textAlign =
                    TextAlign.Center
            )
        }


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ====================================================
        // RESET PASSWORD BUTTON
        // ====================================================

        if (successMessage == null) {

            Button(
                onClick = {

                    if (isLoading) {
                        return@Button
                    }


                    // =========================================
                    // CLEAR ERRORS
                    // =========================================

                    newPasswordError = null

                    confirmPasswordError = null

                    requestError = null


                    // =========================================
                    // NEW PASSWORD VALIDATION
                    // =========================================

                    if (newPassword.isBlank()) {

                        newPasswordError =
                            "Please enter your new password."

                        return@Button
                    }


                    if (newPassword.length < 8) {

                        newPasswordError =
                            "Password must contain at least 8 characters."

                        return@Button
                    }


                    if (
                        newPassword.none {
                            it.isUpperCase()
                        }
                    ) {

                        newPasswordError =
                            "Password must contain an uppercase letter."

                        return@Button
                    }


                    if (
                        newPassword.none {
                            it.isLowerCase()
                        }
                    ) {

                        newPasswordError =
                            "Password must contain a lowercase letter."

                        return@Button
                    }


                    if (
                        newPassword.none {
                            it.isDigit()
                        }
                    ) {

                        newPasswordError =
                            "Password must contain a number."

                        return@Button
                    }


                    if (
                        newPassword.all {
                            it.isLetterOrDigit()
                        }
                    ) {

                        newPasswordError =
                            "Password must contain a special character."

                        return@Button
                    }


                    // =========================================
                    // CONFIRM PASSWORD
                    // =========================================

                    if (confirmPassword.isBlank()) {

                        confirmPasswordError =
                            "Please confirm your new password."

                        return@Button
                    }


                    if (
                        newPassword !=
                        confirmPassword
                    ) {

                        confirmPasswordError =
                            "Passwords do not match."

                        return@Button
                    }


                    // =========================================
                    // TOKEN
                    // =========================================

                    if (token.isBlank()) {

                        requestError =
                            "The password reset request is invalid. Please request a new password reset."

                        return@Button
                    }


                    // =========================================
                    // RESET PASSWORD
                    // =========================================

                    coroutineScope.launch {

                        isLoading = true


                        when (
                            val result =
                                authRepository
                                    .resetPassword(
                                        email =
                                            email,

                                        token =
                                            token,

                                        newPassword =
                                            newPassword,

                                        confirmPassword =
                                            confirmPassword
                                    )
                        ) {

                            is AuthRepository
                            .ResetPasswordResult
                            .Success -> {

                                isLoading =
                                    false

                                successMessage =
                                    result
                                        .response
                                        .message
                            }


                            is AuthRepository
                            .ResetPasswordResult
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
                            "RESETTING...",

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
                            "RESET PASSWORD",

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

        } else {

            // =================================================
            // SUCCESS - RETURN TO LOGIN
            // =================================================

            Button(
                onClick = {

                    onPasswordResetSuccess()
                },

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
                            Color.White
                    )
            ) {

                Text(
                    text =
                        "RETURN TO SIGN IN",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize =
                        13.sp,

                    letterSpacing =
                        1.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(22.dp)
        )


        // ====================================================
        // BACK TO LOGIN
        // ====================================================

        if (successMessage == null) {

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
        }


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}