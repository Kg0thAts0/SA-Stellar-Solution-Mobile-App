package com.sastellarsolutions.qaclothingfactory.ui.login

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.AuthRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(

    onLoginSuccess: (LoginResponse) -> Unit,

    onRegisterClick: () -> Unit = {},

    onForgotPasswordClick: () -> Unit = {},

    onGoogleSignInClick: () -> Unit = {}

) {

    // ========================================================
    // AUTHENTICATION
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

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var rememberMe by remember {
        mutableStateOf(false)
    }


    // ========================================================
    // VALIDATION STATE
    // ========================================================

    var emailError by remember {
        mutableStateOf<String?>(null)
    }

    var passwordError by remember {
        mutableStateOf<String?>(null)
    }


    // ========================================================
    // LOGIN STATE
    // ========================================================

    var loginError by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }


    // ========================================================
    // ENTRANCE ANIMATION
    // ========================================================

    var contentVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(150)

        contentVisible = true
    }


    // ========================================================
    // MAIN SCREEN
    // ========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        AnimatedVisibility(
            visible = contentVisible,

            enter =
                fadeIn() +
                        slideInVertically(
                            initialOffsetY = { fullHeight ->
                                fullHeight / 10
                            }
                        )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                // =================================================
                // TOP SPACE
                // =================================================

                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )


                // =================================================
                // LOGO
                // =================================================

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
                        Modifier.size(105.dp)
                )


                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )


                // =================================================
                // COMPANY NAME
                // =================================================

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
                        Modifier.height(26.dp)
                )


                // =================================================
                // WELCOME
                // =================================================

                Text(
                    text =
                        "Welcome Back",

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
                        Modifier.height(8.dp)
                )


                Text(
                    text =
                        "Sign in to access your factory management workspace.",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.Normal,

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
                        Modifier.height(30.dp)
                )


                // =================================================
                // EMAIL
                // =================================================

                OutlinedTextField(
                    value =
                        email,

                    onValueChange = {

                        email = it

                        emailError = null

                        loginError = null
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
                        OutlinedTextFieldDefaults
                            .colors(

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


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                // =================================================
                // PASSWORD
                // =================================================

                OutlinedTextField(
                    value =
                        password,

                    onValueChange = {

                        password = it

                        passwordError = null

                        loginError = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isLoading,

                    label = {

                        Text(
                            text =
                                "Password",

                            fontFamily =
                                MontserratFontFamily
                        )
                    },

                    trailingIcon = {

                        Text(
                            text =
                                if (passwordVisible) {
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

                            letterSpacing =
                                0.5.sp,

                            color =
                                Color(0xFF111111),

                            modifier =
                                Modifier
                                    .clickable(
                                        enabled =
                                            !isLoading
                                    ) {

                                        passwordVisible =
                                            !passwordVisible
                                    }
                                    .padding(12.dp)
                        )
                    },

                    visualTransformation =
                        if (passwordVisible) {

                            VisualTransformation.None

                        } else {

                            PasswordVisualTransformation()
                        },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Password
                        ),

                    singleLine =
                        true,

                    isError =
                        passwordError != null,

                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),

                    colors =
                        OutlinedTextFieldDefaults
                            .colors(

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


                if (passwordError != null) {

                    Text(
                        text =
                            passwordError ?: "",

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


                // =================================================
                // BACKEND LOGIN ERROR
                // =================================================

                if (loginError != null) {

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Text(
                        text =
                            loginError ?: "",

                        modifier =
                            Modifier.fillMaxWidth(),

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.Medium,

                        fontSize =
                            12.sp,

                        lineHeight =
                            17.sp,

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
                        Modifier.height(10.dp)
                )


                // =================================================
                // REMEMBER ME + FORGOT PASSWORD
                // =================================================

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Checkbox(
                        checked =
                            rememberMe,

                        onCheckedChange = {

                            if (!isLoading) {
                                rememberMe = it
                            }
                        },

                        enabled =
                            !isLoading,

                        colors =
                            CheckboxDefaults.colors(

                                checkedColor =
                                    Color(0xFF111111),

                                uncheckedColor =
                                    Color(0xFF777777),

                                checkmarkColor =
                                    Color.White
                            )
                    )


                    Text(
                        text =
                            "Remember me",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.Normal,

                        fontSize =
                            12.sp,

                        color =
                            Color(0xFF555555)
                    )


                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )


                    Text(
                        text =
                            "Forgot password?",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize =
                            12.sp,

                        color =
                            Color(0xFF111111),

                        modifier =
                            Modifier.clickable(
                                enabled =
                                    !isLoading
                            ) {

                                onForgotPasswordClick()
                            }
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                // =================================================
                // SIGN IN BUTTON
                // =================================================

                Button(
                    onClick = {

                        // =========================================
                        // PREVENT DOUBLE LOGIN
                        // =========================================

                        if (isLoading) {
                            return@Button
                        }


                        // =========================================
                        // CLEAR ERRORS
                        // =========================================

                        emailError = null

                        passwordError = null

                        loginError = null


                        // =========================================
                        // EMAIL VALIDATION
                        // =========================================

                        if (email.isBlank()) {

                            emailError =
                                "Please enter your work email."

                        } else if (
                            !Patterns.EMAIL_ADDRESS
                                .matcher(
                                    email.trim()
                                )
                                .matches()
                        ) {

                            emailError =
                                "Please enter a valid email address."
                        }


                        // =========================================
                        // PASSWORD VALIDATION
                        // =========================================

                        if (password.isBlank()) {

                            passwordError =
                                "Please enter your password."

                        } else if (
                            password.length < 6
                        ) {

                            passwordError =
                                "Password must contain at least 6 characters."
                        }


                        // =========================================
                        // STOP IF INVALID
                        // =========================================

                        if (
                            emailError != null ||
                            passwordError != null
                        ) {

                            return@Button
                        }


                        // =========================================
                        // REAL AUTHENTICATION
                        // =========================================

                        coroutineScope.launch {

                            isLoading = true


                            when (
                                val result =
                                    authRepository.login(
                                        email =
                                            email.trim(),

                                        password =
                                            password
                                    )
                            ) {

                                // =================================
                                // SUCCESS
                                // =================================

                                is AuthRepository
                                .LoginResult
                                .Success -> {

                                    isLoading =
                                        false

                                    password =
                                        ""

                                    onLoginSuccess(
                                        result.response
                                    )
                                }


                                // =================================
                                // ERROR
                                // =================================

                                is AuthRepository
                                .LoginResult
                                .Error -> {

                                    isLoading =
                                        false

                                    loginError =
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

                    // =============================================
                    // LOADING / SIGN IN
                    // =============================================

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
                                Modifier.width(12.dp)
                        )


                        Text(
                            text =
                                "SIGNING IN...",

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
                                "SIGN IN",

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


                // =================================================
                // DIVIDER
                // =================================================

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.Center
                ) {

                    HorizontalDivider(
                        modifier =
                            Modifier.weight(1f),

                        color =
                            Color(0xFFE0E0E0)
                    )


                    Text(
                        text =
                            "  OR CONTINUE WITH  ",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.Medium,

                        fontSize =
                            9.sp,

                        letterSpacing =
                            0.7.sp,

                        color =
                            Color(0xFF888888)
                    )


                    HorizontalDivider(
                        modifier =
                            Modifier.weight(1f),

                        color =
                            Color(0xFFE0E0E0)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )


                // =================================================
                // GOOGLE
                // =================================================

                OutlinedButton(
                    onClick = {

                        if (!isLoading) {
                            onGoogleSignInClick()
                        }
                    },

                    enabled =
                        !isLoading,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    Color(0xFF111111)
                            )
                ) {

                    Text(
                        text =
                            "G",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize =
                            17.sp,

                        color =
                            Color(0xFF111111)
                    )


                    Spacer(
                        modifier =
                            Modifier.width(12.dp)
                    )


                    Text(
                        text =
                            "Continue with Google",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.Medium,

                        fontSize =
                            13.sp
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(27.dp)
                )


                // =================================================
                // REGISTER
                // =================================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "Don't have an account? ",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.Normal,

                        fontSize =
                            12.sp,

                        color =
                            Color(0xFF777777)
                    )


                    Text(
                        text =
                            "Register",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize =
                            12.sp,

                        color =
                            Color(0xFF111111),

                        modifier =
                            Modifier.clickable(
                                enabled =
                                    !isLoading
                            ) {

                                onRegisterClick()
                            }
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                // =================================================
                // SECURITY
                // =================================================

                Text(
                    text =
                        "Secure access to QA Clothing Factory",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.Normal,

                    fontSize =
                        9.sp,

                    letterSpacing =
                        0.3.sp,

                    color =
                        Color(0xFFAAAAAA),

                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )
            }
        }
    }
}