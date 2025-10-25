package com.developerstring.nexpay.ui.screens.applock

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.developerstring.nexpay.ui.theme.AppColors
import com.developerstring.nexpay.viewmodel.SharedViewModel
import kotlinx.serialization.Serializable

@Serializable
data object AppLockSettingsScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLockSettingsScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    val appLockState by sharedViewModel.appLockState.collectAsState()
    var showDisableDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Clean white top bar
            CleanTopAppBar(
                title = "App Lock Settings",
                onNavigationClick = {
                    navController.popBackStack()
                }
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Card with clean design
                CleanHeaderCard()

                // App Lock Status Card
                CleanAppLockStatusCard(
                    isAppLockEnabled = appLockState.isAppLockEnabled,
                    onSetupClick = {
                        navController.navigate(PinSetupScreenRoute)
                    },
                    onDisableClick = { showDisableDialog = true }
                )

                // Biometric Settings Card (only show if app lock is enabled)
                if (appLockState.isAppLockEnabled) {
                    CleanBiometricCard(
                        isBiometricAvailable = appLockState.isBiometricAvailable,
                        isBiometricEnabled = appLockState.isBiometricEnabled,
                        onToggleBiometric = { enabled ->
                            sharedViewModel.toggleBiometric(enabled)
                        }
                    )
                }

                // Security Information Card
                CleanSecurityInfoCard()

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Disable App Lock Confirmation Dialog with clean design
    if (showDisableDialog) {
        CleanConfirmationDialog(
            title = "Disable App Lock?",
            message = "This will remove your PIN and disable biometric authentication. Your app will no longer be protected.",
            onConfirm = {
                sharedViewModel.disableAppLock()
                showDisableDialog = false
            },
            onDismiss = { showDisableDialog = false }
        )
    }
}

@Composable
fun CleanTopAppBar(
    title: String,
    onNavigationClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun CleanHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Security",
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Secure Your App",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = "Protect your financial data with PIN and biometric authentication",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun CleanAppLockStatusCard(
    isAppLockEnabled: Boolean,
    onSetupClick: () -> Unit,
    onDisableClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAppLockEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "App Lock Status",
                        tint = if (isAppLockEnabled) Color(0xFF10B981) else Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "App Lock",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = if (isAppLockEnabled) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isAppLockEnabled) Color(0xFF10B981) else Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }

                if (isAppLockEnabled) {
                    FilledTonalButton(
                        onClick = onDisableClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFF3F4F6),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Disable")
                    }
                } else {
                    Button(
                        onClick = onSetupClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Setup PIN")
                    }
                }
            }
        }
    }
}

@Composable
fun CleanBiometricCard(
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    onToggleBiometric: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Biometric Authentication",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = if (isBiometricAvailable) {
                                if (isBiometricEnabled) "Touch ID/Face ID enabled" else "Touch ID/Face ID available"
                            } else "Not available on this device",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }

                Switch(
                    checked = isBiometricEnabled,
                    onCheckedChange = onToggleBiometric,
                    enabled = isBiometricAvailable,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Black,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Black.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

@Composable
fun CleanSecurityInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Security Information",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "• Your PIN is stored securely on your device\n" +
                       "• Biometric data never leaves your device\n" +
                       "• App lock activates when you close the app\n" +
                       "• You can change your PIN anytime in settings",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun CleanConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White
                )
            ) {
                Text("Disable")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SpaceHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.deepSpaceBlue.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.brightCyan.copy(alpha = 0.3f),
                                AppColors.primaryBlue.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = AppColors.brightCyan
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "SECURE YOUR APP",
                    style = MaterialTheme.typography.titleLarge.copy(
                        letterSpacing = 1.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
                Text(
                    text = "Protect your data with PIN and biometric authentication",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.textSecondary
                )
            }
        }
    }
}

@Composable
fun SpaceAppLockStatusCard(
    isAppLockEnabled: Boolean,
    onSetupClick: () -> Unit,
    onDisableClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.cosmicBlue.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AppColors.primaryBlue.copy(alpha = 0.3f),
                                        AppColors.brightCyan.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = AppColors.primaryBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "App Lock",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.textPrimary
                        )
                        Text(
                            text = if (isAppLockEnabled) "ENABLED" else "DISABLED",
                            style = MaterialTheme.typography.bodySmall.copy(
                                letterSpacing = 0.5.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = if (isAppLockEnabled) AppColors.brightCyan else AppColors.textSecondary
                        )
                    }
                }

                if (!isAppLockEnabled) {
                    ElevatedButton(
                        onClick = onSetupClick,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = AppColors.brightCyan.copy(alpha = 0.2f),
                            contentColor = AppColors.brightCyan
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Set Up",
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onDisableClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.marsRed
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Disable",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpaceBiometricCard(
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    onToggleBiometric: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.midnightBlue.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AppColors.spacePurple.copy(alpha = 0.3f),
                                        AppColors.nebulaBlue.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = AppColors.spacePurple
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Biometric Authentication",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.textPrimary
                        )
                        Text(
                            text = if (isBiometricAvailable) {
                                if (isBiometricEnabled) "ENABLED" else "AVAILABLE"
                            } else {
                                "NOT AVAILABLE"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                letterSpacing = 0.5.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = if (isBiometricEnabled) AppColors.brightCyan else AppColors.textSecondary
                        )
                    }
                }

                Switch(
                    checked = isBiometricEnabled,
                    onCheckedChange = onToggleBiometric,
                    enabled = isBiometricAvailable,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppColors.brightCyan,
                        checkedTrackColor = AppColors.brightCyan.copy(alpha = 0.5f),
                        uncheckedThumbColor = AppColors.textSecondary,
                        uncheckedTrackColor = AppColors.textSecondary.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

@Composable
fun SpaceSecurityInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.backgroundDark2.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AppColors.nebulaBlue.copy(alpha = 0.3f),
                                    AppColors.electricBlue.copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = AppColors.nebulaBlue
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "SECURITY INFORMATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        letterSpacing = 1.sp
                    ),
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SecurityInfoItem("Your PIN is encrypted and stored securely on your device")
                SecurityInfoItem("Biometric data never leaves your device")
                SecurityInfoItem("App lock automatically activates when the app is closed")
            }
        }
    }
}

@Composable
fun SecurityInfoItem(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    AppColors.brightCyan,
                    shape = RoundedCornerShape(3.dp)
                )
                .padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.textSecondary,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun SpaceConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = AppColors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                color = AppColors.textSecondary
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppColors.marsRed
                )
            ) {
                Text(
                    text = "Disable",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppColors.brightCyan
                )
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = AppColors.deepSpaceBlue,
        shape = RoundedCornerShape(20.dp)
    )
}

