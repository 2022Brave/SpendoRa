package com.spendora.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendora.navigation.SpendoraDestination
import com.spendora.ui.theme.LavenderLight
import com.spendora.ui.theme.LavenderSoft
import com.spendora.ui.theme.PurpleDark
import com.spendora.ui.theme.PurpleElectric
import com.spendora.ui.theme.PurplePrimary
import com.spendora.ui.theme.SpendoraTheme

/**
 * EXACTLY ONE Floating Pill-Style Bottom Navigation Bar
 * Destinations: Home | Transactions | + | Analytics | SplitMate
 * With visually distinct central "+" button.
 */
@Composable
fun SpendoraBottomBar(
    currentDestination: SpendoraDestination,
    onNavigate: (SpendoraDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = SpendoraTheme.customColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("spendora_bottom_navigation_bar"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            color = if (customColors.isDark) Color(0xFF130D21) else Color(0xFFFFFFFF),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (customColors.isDark) Color(0xFF2E1F4B) else Color(0xFFE2D9F3)
            ),
            shadowElevation = 12.dp,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                BottomNavItem(
                    destination = SpendoraDestination.HOME,
                    isSelected = currentDestination == SpendoraDestination.HOME,
                    onClick = { onNavigate(SpendoraDestination.HOME) }
                )

                // 2. Transactions
                BottomNavItem(
                    destination = SpendoraDestination.TRANSACTIONS,
                    isSelected = currentDestination == SpendoraDestination.TRANSACTIONS,
                    onClick = { onNavigate(SpendoraDestination.TRANSACTIONS) }
                )

                // 3. Central Prominent Action Button (+)
                CentralAddButton(
                    isSelected = currentDestination == SpendoraDestination.ADD_TRANSACTION,
                    onClick = { onNavigate(SpendoraDestination.ADD_TRANSACTION) }
                )

                // 4. Analytics
                BottomNavItem(
                    destination = SpendoraDestination.ANALYTICS,
                    isSelected = currentDestination == SpendoraDestination.ANALYTICS,
                    onClick = { onNavigate(SpendoraDestination.ANALYTICS) }
                )

                // 5. SplitMate
                BottomNavItem(
                    destination = SpendoraDestination.SPLITMATE,
                    isSelected = currentDestination == SpendoraDestination.SPLITMATE,
                    onClick = { onNavigate(SpendoraDestination.SPLITMATE) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    destination: SpendoraDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) LavenderLight else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        animationSpec = tween(180),
        label = "tabColor"
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) PurplePrimary.copy(alpha = 0.16f) else Color.Transparent,
        animationSpec = tween(180),
        label = "tabIndicator"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tabScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(indicatorColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = PurplePrimary.copy(alpha = 0.25f)),
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("nav_item_${destination.route}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.title,
                tint = contentColor,
                modifier = Modifier.size(21.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = destination.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

/**
 * Visually distinct central '+' action button
 */
@Composable
private fun CentralAddButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "plusScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .size(48.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(LavenderLight, PurplePrimary, PurpleDark)
                )
            )
            .border(1.5.dp, LavenderSoft.copy(alpha = 0.6f), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.35f)),
                onClick = onClick
            )
            .testTag("nav_item_add_transaction"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = SpendoraDestination.ADD_TRANSACTION.icon,
            contentDescription = "Add Transaction",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}
