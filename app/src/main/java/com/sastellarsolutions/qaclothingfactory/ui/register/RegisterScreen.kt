package com.sastellarsolutions.qaclothingfactory.ui.register

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.sastellarsolutions.qaclothingfactory.data.repository.AuthRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit = {}
) {

    // ========================================================
    // REPOSITORY + COROUTINE
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

    var firstName by remember {
        mutableStateOf("")
    }

    var lastName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }


    // ========================================================
    // ERROR STATE
    // ========================================================

    var firstNameError by remember {
        mutableStateOf<String?>(null)
    }

    var lastNameError by remember {
        mutableStateOf<String?>(null)
    }

    var emailError by remember {
        mutableStateOf<String?>(null)
    }

    var passwordError by remember {
        mutableStateOf<String?>(null)
    }

    var confirmPasswordError by remember {
        mutableStateOf<String?>(null)
    }

    var registrationError by remember {
        mutableStateOf<String?>(null)
    }


    // ========================================================
    // REGISTRATION STATE
    // ========================================================

    var isLoading by remember {
        mutableStateOf(false)
    }

    var registrationSuccessful by remember {
        mutableStateOf(false)
    }

    var successMessage by remember {
        mutableStateOf("")
    }


    // ========================================================
    // SCREEN ANIMATION
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

                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                // =================================================
                // LOGO
                // =================================================

                Image(
                    painter =
                        painterResource(
                            id =
                                R.drawable.qa_clothing_factory_logo
                        ),

                    contentDescription =
                        "QA Clothing Factory Logo",

                    modifier =
                        Modifier.size(90.dp)
                )


                Spacer(
                    modifier = Modifier.height(5.dp)
                )


                // =================================================
                // COMPANY NAME
                // =================================================

                Text(
                    text = "QA CLOTHING FACTORY",

                    fontFamily =
                        CinzelFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 16.sp,

                    letterSpacing = 1.3.sp,

                    color =
                        Color(0xFF111111),

                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier = Modifier.height(22.dp)
                )


                // =================================================
                // HEADING
                // =================================================

                Text(
                    text = "Create Account",

                    fontFamily =
                        CinzelFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 26.sp,

                    color =
                        Color(0xFF111111),

                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier = Modifier.height(7.dp)
                )


                Text(
                    text =
                        "Create your account to access the factory management system.",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.Normal,

                    fontSize = 12.sp,

                    lineHeight = 18.sp,

                    color =
                        Color(0xFF777777),

                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier = Modifier.height(26.dp)
                )


                // =================================================
                // FIRST NAME
                // =================================================

                OutlinedTextField(
                    value = firstName,

                    onValueChange = {

                        firstName = it

                        firstNameError = null

                        registrationError = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isLoading &&
                                !registrationSuccessful,

                    label = {

                        Text(
                            text = "First Name",

                            fontFamily =
                                MontserratFontFamily
                        )
                    },

                    singleLine = true,

                    isError =
                        firstNameError != null,

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        registerFieldColors()
                )


                if (firstNameError != null) {

                    ErrorMessage(
                        message =
                            firstNameError ?: ""
                    )
                }


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =================================================
                // LAST NAME
                // =================================================

                OutlinedTextField(
                    value = lastName,

                    onValueChange = {

                        lastName = it

                        lastNameError = null

                        registrationError = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isLoading &&
                                !registrationSuccessful,

                    label = {

                        Text(
                            text = "Last Name",

                            fontFamily =
                                MontserratFontFamily
                        )
                    },

                    singleLine = true,

                    isError =
                        lastNameError != null,

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        registerFieldColors()
                )


                if (lastNameError != null) {

                    ErrorMessage(
                        message =
                            lastNameError ?: ""
                    )
                }


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =================================================
                // EMAIL
                // =================================================

                OutlinedTextField(
                    value = email,

                    onValueChange = {

                        email = it

                        emailError = null

                        registrationError = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isLoading &&
                                !registrationSuccessful,

                    label = {

                        Text(
                            text = "Work Email",

                            fontFamily =
                                MontserratFontFamily
                        )
                    },

                    placeholder = {

                        Text(
                            text =
                                "name@qafactory.co.za",

                            fontFamily =
                                MontserratFontFamily,

                            fontSize = 12.sp
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Email
                        ),

                    singleLine = true,

                    isError =
                        emailError != null,

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        registerFieldColors()
                )


                if (emailError != null) {

                    ErrorMessage(
                        message =
                            emailError ?: ""
                    )
                }


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =================================================
                // PASSWORD
                // =================================================

                OutlinedTextField(
                    value = password,

                    onValueChange = {

                        password = it

                        passwordError = null

                        registrationError = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isLoading &&
                                !registrationSuccessful,

                    label = {

                        Text(
                            text = "Password",

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

                            fontSize = 10.sp,

                            color =
                                Color(0xFF111111),

                            modifier =
                                Modifier
                                    .clickable(
                                        enabled =
                                            !isLoading &&
                                                    !registrationSuccessful
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

                    singleLine = true,

                    isError =
                        passwordError != null,

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        registerFieldColors()
                )


                if (passwordError != null) {

                    ErrorMessage(
                        message =
                            passwordError ?: ""
                    )
                }


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // =================================================
                // CONFIRM PASSWORD
                // =================================================

                OutlinedTextField(
                    value = confirmPassword,

                    onValueChange = {

                        confirmPassword = it

                        confirmPasswordError = null

                        registrationError = null
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !isLoading &&
                                !registrationSuccessful,

                    label = {

                        Text(
                            text =
                                "Confirm Password",

                            fontFamily =
                                MontserratFontFamily
                        )
                    },

                    trailingIcon = {

                        Text(
                            text =
                                if (confirmPasswordVisible) {
                                    "HIDE"
                                } else {
                                    "SHOW"
                                },

                            fontFamily =
                                MontserratFontFamily,

                            fontWeight =
                                FontWeight.SemiBold,

                            fontSize = 10.sp,

                            color =
                                Color(0xFF111111),

                            modifier =
                                Modifier
                                    .clickable(
                                        enabled =
                                            !isLoading &&
                                                    !registrationSuccessful
                                    ) {

                                        confirmPasswordVisible =
                                            !confirmPasswordVisible
                                    }
                                    .padding(12.dp)
                        )
                    },

                    visualTransformation =
                        if (confirmPasswordVisible) {

                            VisualTransformation.None

                        } else {

                            PasswordVisualTransformation()
                        },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Password
                        ),

                    singleLine = true,

                    isError =
                        confirmPasswordError != null,

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        registerFieldColors()
                )


                if (confirmPasswordError != null) {

                    ErrorMessage(
                        message =
                            confirmPasswordError ?: ""
                    )
                }


                // =================================================
                // BACKEND ERROR
                // =================================================

                if (registrationError != null) {

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    RegistrationMessage(
                        message =
                            registrationError ?: "",

                        isError = true
                    )
                }


                // =================================================
                // SUCCESS MESSAGE
                // =================================================

                if (registrationSuccessful) {

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    RegistrationMessage(
                        message =
                            successMessage,

                        isError = false
                    )
                }


                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                // =================================================
                // CREATE ACCOUNT BUTTON
                // =================================================

                Button(
                    onClick = {

                        // =========================================
                        // RESET ERRORS
                        // =========================================

                        firstNameError = null

                        lastNameError = null

                        emailError = null

                        passwordError = null

                        confirmPasswordError = null

                        registrationError = null


                        // =========================================
                        // VALIDATION
                        // =========================================

                        var validationPassed =
                            true


                        // -----------------------------------------
                        // FIRST NAME
                        // -----------------------------------------

                        if (firstName.isBlank()) {

                            firstNameError =
                                "Please enter your first name."

                            validationPassed =
                                false
                        }


                        // -----------------------------------------
                        // LAST NAME
                        // -----------------------------------------

                        if (lastName.isBlank()) {

                            lastNameError =
                                "Please enter your last name."

                            validationPassed =
                                false
                        }


                        // -----------------------------------------
                        // EMAIL
                        // -----------------------------------------

                        if (email.isBlank()) {

                            emailError =
                                "Please enter your work email."

                            validationPassed =
                                false

                        } else if (
                            !Patterns.EMAIL_ADDRESS
                                .matcher(
                                    email.trim()
                                )
                                .matches()
                        ) {

                            emailError =
                                "Please enter a valid email address."

                            validationPassed =
                                false
                        }


                        // -----------------------------------------
                        // PASSWORD
                        // -----------------------------------------

                        if (password.isBlank()) {

                            passwordError =
                                "Please enter a password."

                            validationPassed =
                                false

                        } else if (
                            password.length < 8
                        ) {

                            passwordError =
                                "Password must contain at least 8 characters."

                            validationPassed =
                                false
                        }


                        // -----------------------------------------
                        // CONFIRM PASSWORD
                        // -----------------------------------------

                        if (confirmPassword.isBlank()) {

                            confirmPasswordError =
                                "Please confirm your password."

                            validationPassed =
                                false

                        } else if (
                            password != confirmPassword
                        ) {

                            confirmPasswordError =
                                "Passwords do not match."

                            validationPassed =
                                false
                        }


                        // =========================================
                        // CALL BACKEND
                        // =========================================

                        if (validationPassed) {

                            isLoading = true


                            coroutineScope.launch {

                                when (
                                    val result =
                                        authRepository.register(
                                            firstName =
                                                firstName,

                                            lastName =
                                                lastName,

                                            email =
                                                email,

                                            password =
                                                password,

                                            confirmPassword =
                                                confirmPassword
                                        )
                                ) {

                                    is AuthRepository
                                    .RegisterResult
                                    .Success -> {

                                        isLoading =
                                            false

                                        registrationSuccessful =
                                            true

                                        registrationError =
                                            null


                                        // =================================
                                        // CLEAR SENSITIVE PASSWORD FIELDS
                                        // =================================

                                        password = ""

                                        confirmPassword = ""

                                        passwordVisible =
                                            false

                                        confirmPasswordVisible =
                                            false


                                        successMessage =
                                            result.response.message
                                    }


                                    is AuthRepository
                                    .RegisterResult
                                    .Error -> {

                                        isLoading =
                                            false

                                        registrationSuccessful =
                                            false

                                        registrationError =
                                            result.message
                                    }
                                }
                            }
                        }
                    },

                    enabled =
                        !isLoading &&
                                !registrationSuccessful,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF111111),

                            contentColor =
                                Color.White,

                            disabledContainerColor =
                                Color(0xFFCCCCCC),

                            disabledContentColor =
                                Color.White
                        )
                ) {

                    if (isLoading) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(22.dp),

                            strokeWidth =
                                2.dp,

                            color =
                                Color.White
                        )

                    } else {

                        Text(
                            text =
                                if (registrationSuccessful) {
                                    "ACCOUNT CREATED"
                                } else {
                                    "CREATE ACCOUNT"
                                },

                            fontFamily =
                                MontserratFontFamily,

                            fontWeight =
                                FontWeight.SemiBold,

                            fontSize = 13.sp,

                            letterSpacing = 1.sp
                        )
                    }
                }


                // =================================================
                // GO TO LOGIN AFTER SUCCESS
                // =================================================

                if (registrationSuccessful) {

                    Spacer(
                        modifier =
                            Modifier.height(14.dp)
                    )

                    Button(
                        onClick = {

                            onLoginClick()
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color.White,

                                contentColor =
                                    Color(0xFF111111)
                            )
                    ) {

                        Text(
                            text =
                                "RETURN TO SIGN IN",

                            fontFamily =
                                MontserratFontFamily,

                            fontWeight =
                                FontWeight.SemiBold,

                            fontSize = 11.sp,

                            letterSpacing = 1.sp
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // =================================================
                // LOGIN LINK
                // =================================================

                if (!registrationSuccessful) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "Already have an account? ",

                            fontFamily =
                                MontserratFontFamily,

                            fontSize = 12.sp,

                            color =
                                Color(0xFF777777)
                        )


                        Text(
                            text = "Sign In",

                            fontFamily =
                                MontserratFontFamily,

                            fontWeight =
                                FontWeight.SemiBold,

                            fontSize = 12.sp,

                            color =
                                Color(0xFF111111),

                            modifier =
                                Modifier.clickable(
                                    enabled =
                                        !isLoading
                                ) {

                                    onLoginClick()
                                }
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                Text(
                    text =
                        "Secure access to QA Clothing Factory",

                    fontFamily =
                        MontserratFontFamily,

                    fontSize = 9.sp,

                    color =
                        Color(0xFFAAAAAA),

                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier = Modifier.height(30.dp)
                )
            }
        }
    }
}


// ============================================================
// SHARED TEXT FIELD COLOURS
// ============================================================

@Composable
private fun registerFieldColors() =
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


// ============================================================
// FIELD ERROR MESSAGE
// ============================================================

@Composable
private fun ErrorMessage(
    message: String
) {

    Text(
        text = message,

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 10.dp,
                top = 5.dp
            ),

        fontFamily =
            MontserratFontFamily,

        fontSize = 11.sp,

        color =
            MaterialTheme
                .colorScheme
                .error
    )
}


// ============================================================
// REGISTRATION RESULT MESSAGE
// ============================================================

@Composable
private fun RegistrationMessage(
    message: String,
    isError: Boolean
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    if (isError) {
                        Color(0xFFFFF2F2)
                    } else {
                        Color(0xFFF2F7F2)
                    },

                shape =
                    RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {

        Text(
            text = message,

            fontFamily =
                MontserratFontFamily,

            fontWeight =
                FontWeight.Medium,

            fontSize = 11.sp,

            lineHeight = 17.sp,

            color =
                if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color(0xFF2E5D34)
                }
        )
    }
}