package com.spendora.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendora.ui.theme.LavenderLight
import com.spendora.ui.theme.LavenderSoft
import com.spendora.ui.theme.PurplePrimary
import com.spendora.ui.theme.SpendoraTheme

enum class SpendoraWordmarkSize {
    SPLASH,
    HERO,
    HEADER,
    COMPACT
}

/**
 * Official SPENDO₹A Typographic Wordmark & Tagline
 *
 * Rules:
 * - App Name: SPENDO₹A
 * - Integrated Indian Rupee (₹) symbol harmonized with surrounding glyphs and accented in Lavender/Violet.
 * - Official Tagline: "Discipline today. Freedom tomorrow."
 * - Consistent baseline, tracking, and optical weight.
 */
@Composable
fun SpendoraWordmark(
    modifier: Modifier = Modifier,
    size: SpendoraWordmarkSize = SpendoraWordmarkSize.HEADER,
    showTagline: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    textColor: Color? = null,
    accentColor: Color? = null
) {
    val isDark = SpendoraTheme.customColors.isDark

    val resolvedTextColor = textColor ?: if (isDark) Color.White else MaterialTheme.colorScheme.onBackground
    val resolvedAccentColor = accentColor ?: if (isDark) LavenderLight else PurplePrimary
    val resolvedTaglineColor = if (isDark) LavenderSoft.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant

    val fontSize: TextUnit
    val rupeeFontSize: TextUnit
    val letterSpacing: TextUnit
    val taglineFontSize: TextUnit
    val spacingBelowWordmark = when (size) {
        SpendoraWordmarkSize.SPLASH -> 10.dp
        SpendoraWordmarkSize.HERO -> 8.dp
        SpendoraWordmarkSize.HEADER -> 2.dp
        SpendoraWordmarkSize.COMPACT -> 1.dp
    }

    when (size) {
        SpendoraWordmarkSize.SPLASH -> {
            fontSize = 32.sp
            rupeeFontSize = 34.sp
            letterSpacing = 2.2.sp
            taglineFontSize = 13.sp
        }
        SpendoraWordmarkSize.HERO -> {
            fontSize = 26.sp
            rupeeFontSize = 28.sp
            letterSpacing = 1.8.sp
            taglineFontSize = 12.sp
        }
        SpendoraWordmarkSize.HEADER -> {
            fontSize = 21.sp
            rupeeFontSize = 22.sp
            letterSpacing = 1.2.sp
            taglineFontSize = 11.sp
        }
        SpendoraWordmarkSize.COMPACT -> {
            fontSize = 17.sp
            rupeeFontSize = 18.sp
            letterSpacing = 0.8.sp
            taglineFontSize = 10.sp
        }
    }

    Column(
        modifier = modifier.testTag("spendora_wordmark"),
        horizontalAlignment = horizontalAlignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (horizontalAlignment == Alignment.CenterHorizontally) Arrangement.Center else Arrangement.Start
        ) {
            Text(
                text = "SPENDO",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = letterSpacing,
                    fontSize = fontSize
                ),
                color = resolvedTextColor
            )
            Text(
                text = "₹",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = rupeeFontSize
                ),
                color = resolvedAccentColor
            )
            Text(
                text = "A",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = letterSpacing,
                    fontSize = fontSize
                ),
                color = resolvedTextColor
            )
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(spacingBelowWordmark))

            Text(
                text = "Discipline today. Freedom tomorrow.",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = taglineFontSize,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.4.sp
                ),
                color = resolvedTaglineColor
            )
        }
    }
}
