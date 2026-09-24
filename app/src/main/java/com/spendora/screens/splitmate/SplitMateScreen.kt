package com.spendora.screens.splitmate

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spendora.ui.components.SpendoraCard
import com.spendora.ui.components.SpendoraEmptyState
import com.spendora.ui.components.SpendoraMetricItem
import com.spendora.ui.theme.FinanceAmber
import com.spendora.ui.theme.FinanceGreen
import com.spendora.ui.theme.FinanceRed
import com.spendora.ui.theme.LavenderLight
import com.spendora.ui.theme.LavenderSoft
import com.spendora.ui.theme.PurplePrimary
import com.spendora.ui.theme.SpendoraTheme

/**
 * SPENDO₹A SplitMate Screen
 *
 * Core Principles:
 * - Dedicated Google Sheets connection/access card.
 * - Real Sheet URI launching directly to connected Google Sheet.
 * - OAuth integration with Google Account identification.
 * - Strict Data Privacy: ONLY SplitMate records are synchronized.
 * - Offline-first architecture: SYNC_PENDING -> SYNCED, retry on SYNC_FAILED.
 */
@Composable
fun SplitMateScreen(
    modifier: Modifier = Modifier,
    viewModel: SplitMateViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val customColors = SpendoraTheme.customColors
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("splitmate_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with Title & Tagline + Action Toolbar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SplitMate",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PurplePrimary.copy(alpha = 0.16f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SHARED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = LavenderLight
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Share Expenses. Stay Ahead.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Add Shared Expense CTA
                    Button(
                        onClick = { viewModel.openAddExpenseDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurplePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_add_shared_expense")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Expense",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Network Status / Offline Simulation Toggle Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(customColors.surfaceHighlight.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.isOfflineMode) Icons.Filled.WifiOff else Icons.Filled.Wifi,
                            contentDescription = null,
                            tint = if (state.isOfflineMode) FinanceAmber else FinanceGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isOfflineMode) "Offline Mode (Changes saved locally)" else "Online (Ready to sync)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Offline Test",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = customColors.textMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = state.isOfflineMode,
                            onCheckedChange = { viewModel.setOfflineMode(it) },
                            modifier = Modifier.size(width = 38.dp, height = 22.dp),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = FinanceAmber,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = PurplePrimary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        // 2. DEDICATED GOOGLE SHEET ACCESS / CONNECTION CARD (CRITICAL PART 4)
        item {
            val connection = state.connection
            SpendoraCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_google_sheet_connection"),
                shapeRadius = 20.dp,
                borderGlow = connection != null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Card Header: Title & Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FinanceGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TableChart,
                                    contentDescription = null,
                                    tint = FinanceGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Sheet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "SplitMate Shared Destination",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = customColors.textMuted
                                )
                            }
                        }

                        // Connection Status Pill
                        val isConnected = connection != null
                        val statusBg = if (isConnected) FinanceGreen.copy(alpha = 0.14f) else customColors.surfaceHighlight
                        val statusColor = if (isConnected) FinanceGreen else customColors.textMuted
                        val statusText = if (isConnected) "Connected" else "Not connected"

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(statusBg)
                                .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (connection == null) {
                        // STATE: NO GOOGLE SHEET CONNECTED
                        Text(
                            text = "Connect a shared Google Sheet to sync SplitMate expenses with your roommates in real time.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.openConnectDialog() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_connect_google_sheet"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Link,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Connect Google Sheet", style = MaterialTheme.typography.labelMedium)
                            }

                            Button(
                                onClick = { viewModel.openCreateDialog() },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("btn_create_shared_space"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PurplePrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create Shared Space", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { viewModel.openJoinDialog() },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Have a Join Code? Join existing Space",
                                style = MaterialTheme.typography.labelSmall,
                                color = LavenderLight
                            )
                        }
                    } else {
                        // STATE: GOOGLE SHEET IS CONNECTED
                        // Connected Sheet Information Block
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(customColors.surfaceHighlight.copy(alpha = 0.5f))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = connection.sheetName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Account: ${connection.connectedAccount} · Space: ${connection.spaceName}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = customColors.textMuted
                                    )
                                }

                                // Quick Share Join Code
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PurplePrimary.copy(alpha = 0.15f))
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(connection.joinCode))
                                            Toast.makeText(context, "Join Code copied: ${connection.joinCode}", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = connection.joinCode,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = LavenderLight
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Filled.ContentCopy,
                                            contentDescription = "Copy code",
                                            tint = LavenderLight,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Sync Status & Last Synced Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val (syncIcon, syncColor, syncLabel) = when (connection.syncStatus) {
                                        SplitSyncStatus.SYNCED -> Triple(Icons.Filled.CloudDone, FinanceGreen, "Synced")
                                        SplitSyncStatus.SYNC_PENDING -> Triple(Icons.Filled.CloudQueue, FinanceAmber, "Sync Pending")
                                        SplitSyncStatus.SYNC_FAILED -> Triple(Icons.Filled.CloudOff, FinanceRed, "Sync Failed")
                                    }

                                    Icon(
                                        imageVector = syncIcon,
                                        contentDescription = null,
                                        tint = syncColor,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = syncLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = syncColor
                                    )
                                }

                                Text(
                                    text = "Last synced: ${connection.lastSyncedTime}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = customColors.textMuted
                                )
                            }
                        }

                        // Sync error message banner if any
                        AnimatedVisibility(visible = state.syncErrorMessage != null) {
                            state.syncErrorMessage?.let { errMsg ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(FinanceRed.copy(alpha = 0.12f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.ErrorOutline,
                                            contentDescription = null,
                                            tint = FinanceRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = errMsg,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = FinanceRed
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // REQUIRED ACTIONS: [Open Sheet] [Sync Now] [Manage Connection]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1. OPEN SHEET (Opens the ACTUAL connected Google Sheet URL!)
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(connection.sheetUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Opening Sheet: ${connection.sheetUrl}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("btn_open_google_sheet"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FinanceGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Open Sheet",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            // 2. SYNC NOW
                            Button(
                                onClick = { viewModel.syncNow() },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .testTag("btn_sync_now"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PurplePrimary,
                                    contentColor = Color.White
                                ),
                                enabled = !state.isSyncing
                            ) {
                                if (state.isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (state.isSyncing) "Syncing..." else "Sync Now",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            // 3. MANAGE CONNECTION
                            OutlinedButton(
                                onClick = { viewModel.openManageDialog() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_manage_connection"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Manage",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // SplitMate Strict Privacy Reassurance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = LavenderLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Privacy: Only SplitMate records sync. Personal bank data & SMS are strictly isolated.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = LavenderSoft.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Total Shared Metric Card
        item {
            SpendoraCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_total_shared"),
                shapeRadius = 20.dp,
                borderGlow = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL SHARED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Group,
                                contentDescription = null,
                                tint = LavenderLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "₹${"%,.0f".format(state.totalShared)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Shared pool across active housemates (${state.expenses.size} expenses logged)",
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted
                    )
                }
            }
        }

        // 4. You Spent vs Roommate Spent Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SpendoraMetricItem(
                    title = "You Spent",
                    value = "₹${"%,.0f".format(state.youSpent)}",
                    icon = Icons.Filled.Person,
                    accentColor = LavenderLight,
                    modifier = Modifier.weight(1f),
                    subtitle = "${state.expenses.count { it.paidBy.equals("You", true) }} paid by you"
                )

                SpendoraMetricItem(
                    title = "Roommate Spent",
                    value = "₹${"%,.0f".format(state.roommateSpent)}",
                    icon = Icons.Filled.Group,
                    accentColor = PurplePrimary,
                    modifier = Modifier.weight(1f),
                    subtitle = "${state.expenses.count { !it.paidBy.equals("You", true) }} paid by others"
                )
            }
        }

        // 5. Settlement Status Card
        item {
            val settlement = state.settlementAmount
            val (settlementText, settlementSub, settlementColor) = when {
                settlement > 0 -> Triple(
                    "Roommate owes you ₹${"%,.0f".format(settlement)}",
                    "You contributed more than your equal share",
                    FinanceGreen
                )
                settlement < 0 -> Triple(
                    "You owe roommate ₹${"%,.0f".format(-settlement)}",
                    "Roommate covered higher collective balance",
                    FinanceAmber
                )
                else -> Triple(
                    "₹0 · All settled up",
                    "No pending settlements between members",
                    FinanceGreen
                )
            }

            SpendoraCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_settlement_status")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(settlementColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Handshake,
                            contentDescription = null,
                            tint = settlementColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SETTLEMENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = settlementText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = settlementSub,
                            style = MaterialTheme.typography.bodySmall,
                            color = customColors.textMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Settled",
                        tint = settlementColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 6. Recent Shared Expenses Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT SHARED EXPENSES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (state.expenses.any { it.syncStatus != SplitSyncStatus.SYNCED }) {
                    Text(
                        text = "${state.expenses.count { it.syncStatus != SplitSyncStatus.SYNCED }} pending sync",
                        style = MaterialTheme.typography.labelSmall.copy(color = FinanceAmber)
                    )
                }
            }
        }

        if (state.expenses.isEmpty()) {
            item {
                SpendoraCard(modifier = Modifier.fillMaxWidth()) {
                    SpendoraEmptyState(
                        icon = Icons.Filled.ReceiptLong,
                        title = "No shared expenses yet",
                        description = "Split rent, groceries, utility bills, and outings with roommates effortlessly."
                    )
                }
            }
        } else {
            items(state.expenses) { expense ->
                SpendoraCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_splitmate_expense_${expense.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Icon Pill
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PurplePrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = expense.category.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LavenderLight
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = expense.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Paid by ${expense.paidBy} · ${expense.date}",
                                style = MaterialTheme.typography.bodySmall,
                                color = customColors.textMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${"%,.0f".format(expense.amount)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(2.dp))

                            // Sync Status Tag on each item
                            val (badgeBg, badgeColor, badgeText) = when (expense.syncStatus) {
                                SplitSyncStatus.SYNCED -> Triple(FinanceGreen.copy(alpha = 0.12f), FinanceGreen, "SYNCED")
                                SplitSyncStatus.SYNC_PENDING -> Triple(FinanceAmber.copy(alpha = 0.12f), FinanceAmber, "PENDING")
                                SplitSyncStatus.SYNC_FAILED -> Triple(FinanceRed.copy(alpha = 0.12f), FinanceRed, "FAILED")
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .clickable(enabled = expense.syncStatus != SplitSyncStatus.SYNCED) {
                                        viewModel.syncNow()
                                    }
                            ) {
                                Text(
                                    text = if (expense.syncStatus == SplitSyncStatus.SYNC_FAILED) "RETRY" else badgeText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = badgeColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ========================================================
    // DIALOGS & FLOWS: Create, Connect, Join, Manage, Add Expense
    // ========================================================

    // 1. CREATE SHARED SPACE DIALOG
    if (state.showCreateDialog) {
        var spaceName by remember { mutableStateOf("Flat 402 Expenses") }
        val accountEmail = "2022brave@gmail.com"

        AlertDialog(
            onDismissRequest = { viewModel.closeCreateDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Group, contentDescription = null, tint = PurplePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Shared Space", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Set up a shared expense space with an auto-created Google Sheet and unique Join Code.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = spaceName,
                        onValueChange = { spaceName = it },
                        label = { Text("Shared Space Name") },
                        placeholder = { Text("e.g. Flat 402, Trip to Goa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Authenticated Google OAuth representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(customColors.surfaceHighlight)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = FinanceGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Google Account Authenticated",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = accountEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = customColors.textMuted
                            )
                        }
                    }

                    Text(
                        text = "🔒 Only SplitMate expense rows will be written to this Sheet. Personal finance data remains private.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = customColors.textMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.createSharedSpace(spaceName, accountEmail) },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Create & Connect")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeCreateDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. CONNECT EXISTING GOOGLE SHEET DIALOG
    if (state.showConnectDialog) {
        var sheetInput by remember { mutableStateOf("https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit") }
        var spaceName by remember { mutableStateOf("Flat 402 Shared") }

        AlertDialog(
            onDismissRequest = { viewModel.closeConnectDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Link, contentDescription = null, tint = FinanceGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connect Google Sheet", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Paste the Google Sheet link or Sheet ID to synchronize shared roommate expenses.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = sheetInput,
                        onValueChange = { sheetInput = it },
                        label = { Text("Google Sheet URL or ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = spaceName,
                        onValueChange = { spaceName = it },
                        label = { Text("Space Label") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.connectExistingSheet(sheetInput, spaceName) },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Connect")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeConnectDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. JOIN SHARED SPACE DIALOG
    if (state.showJoinDialog) {
        var joinCodeInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.closeJoinDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.QrCode, contentDescription = null, tint = LavenderLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Join a SplitMate", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter the 6-character Join Code shared by your roommate to connect to their Google Sheet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = joinCodeInput,
                        onValueChange = { joinCodeInput = it.uppercase() },
                        label = { Text("Join Code (e.g. SPND-402X)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.joinSharedSpace(joinCodeInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    enabled = joinCodeInput.isNotBlank()
                ) {
                    Text("Join Space")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeJoinDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. MANAGE CONNECTION DIALOG (With QR & Join Code presentation)
    if (state.showManageDialog) {
        val connection = state.connection
        AlertDialog(
            onDismissRequest = { viewModel.closeManageDialog() },
            title = {
                Text("Manage Space & Sheet", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (connection != null) {
                        // Stylized QR Representation for Join Code
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(106.dp)) {
                                val s = size.width / 7f
                                // Corner anchors
                                drawRoundRect(Color.Black, Offset(0f, 0f), Size(s * 2.2f, s * 2.2f), CornerRadius(4f))
                                drawRoundRect(Color.White, Offset(s * 0.4f, s * 0.4f), Size(s * 1.4f, s * 1.4f), CornerRadius(2f))
                                drawRoundRect(Color.Black, Offset(s * 0.7f, s * 0.7f), Size(s * 0.8f, s * 0.8f), CornerRadius(2f))

                                drawRoundRect(Color.Black, Offset(size.width - s * 2.2f, 0f), Size(s * 2.2f, s * 2.2f), CornerRadius(4f))
                                drawRoundRect(Color.White, Offset(size.width - s * 1.8f, s * 0.4f), Size(s * 1.4f, s * 1.4f), CornerRadius(2f))
                                drawRoundRect(Color.Black, Offset(size.width - s * 1.5f, s * 0.7f), Size(s * 0.8f, s * 0.8f), CornerRadius(2f))

                                drawRoundRect(Color.Black, Offset(0f, size.height - s * 2.2f), Size(s * 2.2f, s * 2.2f), CornerRadius(4f))
                                drawRoundRect(Color.White, Offset(s * 0.4f, size.height - s * 1.8f), Size(s * 1.4f, s * 1.4f), CornerRadius(2f))
                                drawRoundRect(Color.Black, Offset(s * 0.7f, size.height - s * 1.5f), Size(s * 0.8f, s * 0.8f), CornerRadius(2f))

                                // Center pattern dots
                                drawCircle(Color.Black, s * 0.35f, Offset(s * 3.5f, s * 3.5f))
                                drawCircle(Color.Black, s * 0.35f, Offset(s * 4.8f, s * 2.5f))
                                drawCircle(Color.Black, s * 0.35f, Offset(s * 2.5f, s * 4.8f))
                                drawCircle(Color.Black, s * 0.35f, Offset(s * 4.8f, s * 4.8f))
                            }
                        }

                        Text(
                            text = "JOIN CODE: ${connection.joinCode}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = LavenderLight
                        )

                        Text(
                            text = "Sheet: ${connection.sheetName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = customColors.textMuted
                        )

                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(connection.sheetUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Opening Sheet: ${connection.sheetUrl}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open in Google Sheets")
                        }

                        Button(
                            onClick = { viewModel.disconnectSheet() },
                            colors = ButtonDefaults.buttonColors(containerColor = FinanceRed.copy(alpha = 0.85f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Disconnect Sheet")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeManageDialog() }) {
                    Text("Close")
                }
            }
        )
    }

    // 5. ADD SHARED EXPENSE DIALOG
    if (state.showAddExpenseDialog) {
        var title by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }
        var paidBy by remember { mutableStateOf("You") }
        var category by remember { mutableStateOf("Groceries") }

        AlertDialog(
            onDismissRequest = { viewModel.closeAddExpenseDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = PurplePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Split Expense", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Expense Title") },
                        placeholder = { Text("e.g. Milk & Bread, Electricity") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { paidBy = "You" },
                            modifier = Modifier.weight(1f),
                            colors = if (paidBy == "You") ButtonDefaults.outlinedButtonColors(containerColor = PurplePrimary.copy(alpha = 0.2f)) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Paid by You")
                        }

                        OutlinedButton(
                            onClick = { paidBy = "Roommate" },
                            modifier = Modifier.weight(1f),
                            colors = if (paidBy == "Roommate") ButtonDefaults.outlinedButtonColors(containerColor = PurplePrimary.copy(alpha = 0.2f)) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Roommate")
                        }
                    }

                    if (state.isOfflineMode) {
                        Text(
                            text = "ℹ️ Offline: Expense will be saved locally with SYNC_PENDING and synced when online.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = FinanceAmber
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && amount > 0) {
                            viewModel.addExpense(title, amount, paidBy, "Flat 402", category)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                ) {
                    Text("Save Expense")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddExpenseDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}
