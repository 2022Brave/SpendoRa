package com.spendora.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendora.ui.components.SpendoraLogo
import com.spendora.ui.components.SpendoraWordmark
import com.spendora.ui.components.SpendoraWordmarkSize
import com.spendora.ui.theme.LavenderSoft
import com.spendora.ui.theme.MidnightBlack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * SPENDO₹A Splash Screen — Clean, Minimal, Native Production Quality
 *
 * Visual Direction:
 * - Deep midnight black background with minimal ambient depth.
 * - Restrained S + ₹ symbol alone (no outer container, no glowing rounded-square mockup).
 * - Generous breathing room.
 * - SPENDO₹A wordmark with integrated ₹.
 * - Subtle secondary tagline: "Discipline today. Freedom tomorrow."
 *
 * Hierarchy:
 * [S + ₹]
 *
 * SPENDO₹A
 *
 * Discipline today. Freedom tomorrow.
 *
 * Animation Sequence:
 * 1. S + ₹ fades and gently scales in (0.94 -> 1.0)
 * 2. SPENDO₹A wordmark fades in smoothly
 * 3. Tagline fades in slightly afterward
 * 4. Brief hold then seamless transition into Home
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logoScale = remember { Animatable(0.94f) }
    val logoAlpha = remember { Animatable(0f) }
    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffsetY = remember { Animatable(8f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: S + ₹ symbol gentle reveal
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )
        }

        // Step 2: SPENDO₹A wordmark fades in smoothly
        delay(220)
        launch {
            wordmarkAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            wordmarkOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }

        // Step 3: Tagline fades in slightly afterward
        delay(180)
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 320, easing = LinearOutSlowInEasing)
        )

        // Step 4: Restrained hold and seamless transition into Home
        delay(550)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Refined S + ₹ symbol by itself (restrained size, no container, no neon bloom)
            SpendoraLogo(
                size = 76.dp,
                showAmbientDepth = false,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            )

            // Generous breathing room
            Spacer(modifier = Modifier.height(26.dp))

            // 2. Official SPENDO₹A Wordmark
            SpendoraWordmark(
                size = SpendoraWordmarkSize.SPLASH,
                showTagline = false,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset { IntOffset(0, wordmarkOffsetY.value.roundToInt()) }
                    .alpha(wordmarkAlpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Official Tagline: Subtle, secondary, legible
            Text(
                text = "Discipline today. Freedom tomorrow.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp
                ),
                color = LavenderSoft.copy(alpha = 0.82f),
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}
