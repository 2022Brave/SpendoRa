package com.spendora.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spendora.R

/**
 * Official SPENDO₹A Master Logo Symbol
 *
 * Displays the exact production 3D icon asset matching user design:
 * - Glowing neon rounded-square container with magenta-violet gradient
 * - 3D dimensional metallic ribbon S in vibrant lavender, purple, and violet
 * - Electric cyan/blue fold on lower ribbon loop
 * - Sharp metallic Indian Rupee (₹) symbol nestled inside upper loop
 */
@Composable
fun SpendoraLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showAmbientDepth: Boolean = false,
    ambientAlpha: Float = 0.08f,
    isDark: Boolean = true,
    solidWhite: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .testTag("spendora_logo"),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_spendora_master),
            contentDescription = "Spendo₹a Logo",
            modifier = Modifier.fillMaxSize()
        )
    }
}
