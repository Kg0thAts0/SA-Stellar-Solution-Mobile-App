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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit = {}
) {

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

            enter = fadeIn() +
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
                    painter = painterResource(
                        id = R.drawable.qa_clothing_factory_logo
                    ),

                    contentDescription =
                        "QA Clothing Factory Logo",

                    modifier = Modifier
                        .size(90.dp)
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
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

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
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

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
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

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
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

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
                                    .clickable {

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
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

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
                                    .clickable {

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


                Spacer(
                    modifier = Modifier.height(25.dp)
                )


                // =================================================
                // CREATE ACCOUNT BUTTON
                // =================================================

                Button(
                    onClick = {

                        // Reset errors

                        firstNameError = null

                        lastNameError = null

                        emailError = null

                        passwordError = null

                        confirmPasswordError = null


                        // -----------------------------------------
                        // FIRST NAME
                        // -----------------------------------------

                        if (firstName.isBlank()) {

                            firstNameError =
                                "Please enter your first name."
                        }


                        // -----------------------------------------
                        // LAST NAME
                        // -----------------------------------------

                        if (lastName.isBlank()) {

                            lastNameError =
                                "Please enter your last name."
                        }


                        // -----------------------------------------
                        // EMAIL
                        // -----------------------------------------

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


                        // -----------------------------------------
                        // PASSWORD
                        // -----------------------------------------

                        if (password.isBlank()) {

                            passwordError =
                                "Please enter a password."

                        } else if (
                            password.length < 8
                        ) {

                            passwordError =
                                "Password must contain at least 8 characters."
                        }


                        // -----------------------------------------
                        // CONFIRM PASSWORD
                        // -----------------------------------------

                        if (confirmPassword.isBlank()) {

                            confirmPasswordError =
                                "Please confirm your password."

                        } else if (
                            password != confirmPassword
                        ) {

                            confirmPasswordError =
                                "Passwords do not match."
                        }


                        // -----------------------------------------
                        // REGISTRATION
                        // -----------------------------------------

                        if (
                            firstNameError == null &&
                            lastNameError == null &&
                            emailError == null &&
                            passwordError == null &&
                            confirmPasswordError == null
                        ) {

                            /*
                             * Validation passed.
                             *
                             * Later this will send the
                             * registration request to our
                             * authentication/backend service.
                             *
                             * We are intentionally not creating
                             * fake accounts locally.
                             */
                        }
                    },

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
                                Color.White
                        )
                ) {

                    Text(
                        text = "CREATE ACCOUNT",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize = 13.sp,

                        letterSpacing = 1.sp
                    )
                }


                Spacer(
                    modifier = Modifier.height(24.dp)
                )


                // =================================================
                // LOGIN LINK
                // =================================================

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
                            Modifier.clickable {

                                onLoginClick()
                            }
                    )
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
// ERROR MESSAGE
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