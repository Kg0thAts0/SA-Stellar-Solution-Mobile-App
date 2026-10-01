package com.sastellarsolutions.qaclothingfactory.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.R
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {

    // ========================================================
    // ANIMATION VALUES
    // ========================================================

    // Controls how visible the logo is.
    val logoAlpha = remember {
        Animatable(0f)
    }

    // Controls the size of the logo.
    // The logo starts smaller and grows to normal size.
    val logoScale = remember {
        Animatable(0.72f)
    }

    // Controls the visibility of the company name.
    val titleAlpha = remember {
        Animatable(0f)
    }

    // Controls the visibility of the supporting text
    // and sewing animation.
    val subtitleAlpha = remember {
        Animatable(0f)
    }

    // Controls the fade-out of the entire splash screen.
    val screenAlpha = remember {
        Animatable(1f)
    }


    // ========================================================
    // START SPLASH SCREEN ANIMATIONS
    // ========================================================

    LaunchedEffect(Unit) {

        // ----------------------------------------------------
        // STEP 1:
        // Fade the logo into the screen.
        // ----------------------------------------------------

        launch {

            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 700
                )
            )
        }


        // ----------------------------------------------------
        // STEP 2:
        // Scale the logo from small to normal size.
        // ----------------------------------------------------

        launch {

            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 900,
                    easing = EaseOutCubic
                )
            )
        }


        // Wait before displaying the company name.
        delay(450)


        // ----------------------------------------------------
        // STEP 3:
        // Display QA CLOTHING FACTORY.
        // ----------------------------------------------------

        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600
            )
        )


        // Small delay before supporting information.
        delay(200)


        // ----------------------------------------------------
        // STEP 4:
        // Display subtitle and sewing loader.
        // ----------------------------------------------------

        subtitleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500
            )
        )


        // ----------------------------------------------------
        // STEP 5:
        // Keep splash screen visible while sewing animation
        // is running.
        // ----------------------------------------------------

        delay(2200)


        // ----------------------------------------------------
        // STEP 6:
        // Fade the entire splash screen away.
        // ----------------------------------------------------

        screenAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 450
            )
        )


        // ----------------------------------------------------
        // STEP 7:
        // Continue to the Login Screen.
        // ----------------------------------------------------

        onSplashFinished()
    }


    // ========================================================
    // SPLASH SCREEN
    // ========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .alpha(screenAlpha.value)
    ) {


        // ====================================================
        // MAIN CONTENT
        // ====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // =================================================
            // QA CLOTHING FACTORY LOGO
            // =================================================

            Image(
                painter = painterResource(
                    id = R.drawable.qa_clothing_factory_logo
                ),

                contentDescription = "QA Clothing Factory Logo",

                modifier = Modifier
                    .size(215.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // =================================================
            // COMPANY NAME
            // Uses Cinzel font for strong branding.
            // =================================================

            Text(
                text = "QA CLOTHING FACTORY",

                fontFamily = CinzelFontFamily,

                fontWeight = FontWeight.SemiBold,

                fontSize = 23.sp,

                letterSpacing = 2.2.sp,

                color = Color(0xFF111111),

                textAlign = TextAlign.Center,

                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(titleAlpha.value)
            )


            Spacer(
                modifier = Modifier.height(13.dp)
            )


            // =================================================
            // MAIN SYSTEM DESCRIPTION
            // =================================================

            Text(
                text = "SMART FACTORY MANAGEMENT",

                fontFamily = MontserratFontFamily,

                fontWeight = FontWeight.Medium,

                fontSize = 11.sp,

                letterSpacing = 2.sp,

                color = Color(0xFF555555),

                textAlign = TextAlign.Center,

                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(subtitleAlpha.value)
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =================================================
            // MAIN FACTORY FUNCTIONS
            // =================================================

            Text(
                text = "Production  •  Quality  •  Inventory",

                fontFamily = MontserratFontFamily,

                fontWeight = FontWeight.Normal,

                fontSize = 10.sp,

                letterSpacing = 0.8.sp,

                color = Color(0xFF888888),

                textAlign = TextAlign.Center,

                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(subtitleAlpha.value)
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =================================================
            // CUSTOM SEWING LOADING ANIMATION
            //
            // This replaces the normal Android progress bar.
            // SewingLoader.kt contains the actual needle and
            // stitching animation.
            // =================================================

            SewingLoader(
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =================================================
            // LOADING MESSAGE
            // =================================================

            Text(
                text = "Preparing your workspace...",

                fontFamily = MontserratFontFamily,

                fontWeight = FontWeight.Normal,

                fontSize = 9.sp,

                letterSpacing = 0.5.sp,

                color = Color(0xFF999999),

                modifier = Modifier
                    .alpha(subtitleAlpha.value)
            )
        }


        // ====================================================
        // BOTTOM BRANDING
        // ====================================================

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 34.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // ------------------------------------------------
            // SYSTEM NAME
            // ------------------------------------------------

            Text(
                text = "FACTORY MANAGEMENT SYSTEM",

                fontFamily = MontserratFontFamily,

                fontWeight = FontWeight.Medium,

                fontSize = 9.sp,

                letterSpacing = 1.5.sp,

                color = Color(0xFF777777),

                modifier = Modifier
                    .alpha(subtitleAlpha.value)
            )


            Spacer(
                modifier = Modifier.height(5.dp)
            )


            // ------------------------------------------------
            // SYSTEM TAGLINE
            // ------------------------------------------------

            Text(
                text = "Secure • Connected • Efficient",

                fontFamily = MontserratFontFamily,

                fontWeight = FontWeight.Normal,

                fontSize = 9.sp,

                letterSpacing = 0.3.sp,

                color = Color(0xFFAAAAAA),

                modifier = Modifier
                    .alpha(subtitleAlpha.value)
            )
        }
    }
}