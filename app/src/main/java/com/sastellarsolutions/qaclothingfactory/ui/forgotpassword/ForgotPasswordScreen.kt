package com.sastellarsolutions.qaclothingfactory.ui.forgotpassword

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.R
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit = {}
) {

    // ========================================================
    // SCREEN STATE
    // ========================================================

    var email by remember {
        mutableStateOf("")
    }

    var emailError by remember {
        mutableStateOf<String?>(null)
    }

    var requestSubmitted by remember {
        mutableStateOf(false)
    }

    var contentVisible by remember {
        mutableStateOf(false)
    }

    // ========================================================
    // ENTRANCE ANIMATION
    // ========================================================

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
                    modifier = Modifier.height(40.dp)
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
                        .size(100.dp)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
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

                    fontSize = 17.sp,

                    letterSpacing = 1.4.sp,

                    color =
                        Color(0xFF111111),

                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(35.dp)
                )

                // =================================================
                // HEADING
                // =================================================

                Text(
                    text = "Forgot Password?",

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
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text =
                        "Enter your work email address and we'll help you reset your password.",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.Normal,

                    fontSize = 13.sp,

                    lineHeight = 20.sp,

                    color =
                        Color(0xFF777777),

                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.height(35.dp)
                )

                // =================================================
                // EMAIL
                // =================================================

                OutlinedTextField(
                    value = email,

                    onValueChange = {

                        email = it

                        emailError = null

                        requestSubmitted = false
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

                // =================================================
                // EMAIL ERROR
                // =================================================

                if (emailError != null) {

                    Text(
                        text =
                            emailError ?: "",

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

                // =================================================
                // SUCCESS INFORMATION
                // =================================================

                if (requestSubmitted) {

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Text(
                        text =
                            "Email validated. Password reset will be connected to the authentication service.",

                        modifier = Modifier
                            .fillMaxWidth(),

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.Medium,

                        fontSize = 11.sp,

                        lineHeight = 17.sp,

                        color =
                            Color(0xFF555555),

                        textAlign =
                            TextAlign.Center
                    )
                }

                Spacer(
                    modifier = Modifier.height(25.dp)
                )

                // =================================================
                // RESET PASSWORD BUTTON
                // =================================================

                Button(
                    onClick = {

                        emailError = null

                        requestSubmitted = false

                        // -----------------------------------------
                        // EMAIL VALIDATION
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

                        } else {

                            /*
                             * The email format is valid.
                             *
                             * Later, this is where our real
                             * authentication service will send
                             * the password-reset email.
                             */

                            requestSubmitted = true
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
                        text = "RESET PASSWORD",

                        fontFamily =
                            MontserratFontFamily,

                        fontWeight =
                            FontWeight.SemiBold,

                        fontSize = 13.sp,

                        letterSpacing = 1.sp
                    )
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                // =================================================
                // BACK TO LOGIN
                // =================================================

                Text(
                    text = "Back to Sign In",

                    fontFamily =
                        MontserratFontFamily,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 12.sp,

                    color =
                        Color(0xFF111111),

                    modifier =
                        Modifier.clickable {

                            onBackToLogin()
                        }
                )

                Spacer(
                    modifier = Modifier.height(35.dp)
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
            }
        }
    }
}