package com.spendora.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendora.ui.theme.LavenderLight
import com.spendora.ui.theme.LavenderSoft
import com.spendora.ui.theme.PurplePrimary
import com.spendora.ui.theme.SpendoraTheme
import com.spendora.ui.theme.SpendoraThemeMode

/**
 * Top App Header adhering to SPENDO₹A brand guidelines
 * - Wordmark: SPENDO₹A
 * - Tagline: Discipline today. Freedom tomorrow.
 * - Theme toggle button (Dark / Light)
 * - Safe status bar padding
 * - No offline pill
 */
@Composable
fun SpendoraTopHeader(
    themeMode: SpendoraThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val customColors = SpendoraTheme.customColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.96f)
                    )
                )
            )
            .padding(top = topInset + 12.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
            .testTag("spendora_top_header")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpendoraLogo(
                    size = 38.dp,
                    showAmbientDepth = false
                )

                Spacer(modifier = Modifier.width(12.dp))

                SpendoraWordmark(
                    size = SpendoraWordmarkSize.HEADER,
                    showTagline = true
                )
            }

            // Theme Mode Switcher (Black + Purple / White + Purple)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, customColors.border, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = PurplePrimary.copy(alpha = 0.3f)),
                        onClick = onToggleTheme
                    )
                    .testTag("theme_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(targetState = themeMode, label = "themeIcon") { mode ->
                    val isDark = mode == SpendoraThemeMode.BLACK_PURPLE
                    Icon(
                        imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = if (isDark) "Switch to Light Theme" else "Switch to Dark Theme",
                        tint = if (isDark) LavenderSoft else PurplePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
