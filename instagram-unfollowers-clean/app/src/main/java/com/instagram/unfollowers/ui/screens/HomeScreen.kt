package com.instagram.unfollowers.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instagram.unfollowers.ui.theme.*
import com.instagram.unfollowers.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var uploadMode by remember { mutableStateOf(UploadMode.ZIP) }
    var followersUri by remember { mutableStateOf<Uri?>(null) }
    var followingUri by remember { mutableStateOf<Uri?>(null) }

    val zipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.processZipFile(context, it) }
    }

    val followersLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> followersUri = uri }

    val followingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> followingUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo / Header
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(InstagramYellow, InstagramOrange, GradientEnd, GradientStart)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PersonSearch,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Unfollowers",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Kim sizi takip etmiyor öğrenin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mode selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ModeTab(
                label = "ZIP Dosyası",
                icon = Icons.Default.FolderZip,
                selected = uploadMode == UploadMode.ZIP,
                onClick = { uploadMode = UploadMode.ZIP },
                modifier = Modifier.weight(1f)
            )
            ModeTab(
                label = "Ayrı JSON",
                icon = Icons.Default.Description,
                selected = uploadMode == UploadMode.SEPARATE_JSON,
                onClick = { uploadMode = UploadMode.SEPARATE_JSON },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = uploadMode == UploadMode.ZIP,
            enter = fadeIn() + slideInVertically()
        ) {
            Column {
                DropZone(
                    label = "ZIP Dosyasını Yükle",
                    subtitle = "Instagram verilerini ZIP olarak yükleyin",
                    icon = Icons.Default.FolderZip,
                    hasFile = false,
                    onClick = { zipLauncher.launch("application/zip") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                InstructionCard()
            }
        }

        AnimatedVisibility(
            visible = uploadMode == UploadMode.SEPARATE_JSON,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DropZone(
                    label = "followers_1.json",
                    subtitle = if (followersUri != null) "✓ Dosya seçildi" else "Takipçiler dosyasını seçin",
                    icon = Icons.Default.People,
                    hasFile = followersUri != null,
                    onClick = { followersLauncher.launch("application/json") }
                )

                DropZone(
                    label = "following.json",
                    subtitle = if (followingUri != null) "✓ Dosya seçildi" else "Takip edilenler dosyasını seçin",
                    icon = Icons.Default.PersonAdd,
                    hasFile = followingUri != null,
                    onClick = { followingLauncher.launch("application/json") }
                )

                if (followersUri != null && followingUri != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            viewModel.processJsonFiles(
                                context,
                                followersUri!!,
                                followingUri!!
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analiz Et", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                InstructionCard()
            }
        }
    }
}

@Composable
private fun ModeTab(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

@Composable
private fun DropZone(
    label: String,
    subtitle: String,
    icon: ImageVector,
    hasFile: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (hasFile) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                if (hasFile) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (hasFile) Icons.Default.CheckCircle else icon,
                contentDescription = null,
                tint = if (hasFile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (hasFile) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InstructionCard() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Instagram verilerini nasıl indirirsiniz?",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            InstructionStep("1", "Instagram → Profil → ☰ Menü")
            InstructionStep("2", "Hesabınız → Bilgilerinizi İndirin")
            InstructionStep("3", "\"Bağlantılar\" kategorisini seçin")
            InstructionStep("4", "JSON formatını seçin ve indirin")
            InstructionStep("5", "ZIP dosyasını bu uygulamaya yükleyin")
        }
    }
}

@Composable
private fun InstructionStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class UploadMode { ZIP, SEPARATE_JSON }
