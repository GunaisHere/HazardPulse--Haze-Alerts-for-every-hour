package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HazardNavTab
import com.example.ui.HazardViewModel
import com.example.ui.screens.AlertHistoryScreen
import com.example.ui.screens.HazeFocusScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HourlyMonitoringScreen
import com.example.ui.screens.SettingsDndScreen
import com.example.ui.screens.SimulationScreen
import com.example.ui.theme.HazardAmber
import com.example.ui.theme.HazardBlue
import com.example.ui.theme.HazardGreen
import com.example.ui.theme.HazardOrange
import com.example.ui.theme.HazardRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: HazardViewModel = viewModel()
                val snapshot by viewModel.liveSnapshot.collectAsStateWithLifecycle()
                val alerts by viewModel.allAlerts.collectAsStateWithLifecycle()
                val hourlyReadings by viewModel.hourlyReadings.collectAsStateWithLifecycle()
                val dndSettings by viewModel.dndSettings.collectAsStateWithLifecycle()
                val thresholds by viewModel.thresholds.collectAsStateWithLifecycle()
                val voiceSettings by viewModel.voiceSettings.collectAsStateWithLifecycle()
                val dataSourceSettings by viewModel.dataSourceSettings.collectAsStateWithLifecycle()
                val isHourlyEnabled by viewModel.hourlyMonitoringEnabled.collectAsStateWithLifecycle()
                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
                val isDndActiveNow by viewModel.isDndActiveNow.collectAsStateWithLifecycle()

                // Runtime Permissions Launcher
                val permissionsLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    if (fineGranted || coarseGranted) {
                        viewModel.refreshHazards()
                    }
                }

                LaunchedEffect(Unit) {
                    val permissionsToRequest = mutableListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    permissionsLauncher.launch(permissionsToRequest.toTypedArray())
                }

                // Handle back button on sub-tabs
                BackHandler(enabled = currentTab != HazardNavTab.DASHBOARD) {
                    viewModel.selectTab(HazardNavTab.DASHBOARD)
                }

                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(HazardOrange),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = "App Logo",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "HazardPulse",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                }
                            },
                            actions = {
                                if (isDndActiveNow) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF312E81),
                                        modifier = Modifier.padding(end = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DarkMode,
                                                contentDescription = "Night DND",
                                                tint = Color(0xFFA5B4FC),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "DND",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFA5B4FC)
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = SlateDark900,
                                titleContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = SlateDark900,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("hazard_bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == HazardNavTab.DASHBOARD,
                                onClick = { viewModel.selectTab(HazardNavTab.DASHBOARD) },
                                icon = {
                                    val activeCount = alerts.count { !it.isAcknowledged }
                                    if (activeCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = HazardRed) {
                                                    Text("$activeCount")
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Radar, contentDescription = "Radar")
                                        }
                                    } else {
                                        Icon(Icons.Default.Radar, contentDescription = "Radar")
                                    }
                                },
                                label = { Text("Radar", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HazardOrange,
                                    selectedTextColor = HazardOrange,
                                    indicatorColor = SlateDark800,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_tab_dashboard")
                            )

                            NavigationBarItem(
                                selected = currentTab == HazardNavTab.HAZE_DETAIL,
                                onClick = { viewModel.selectTab(HazardNavTab.HAZE_DETAIL) },
                                icon = { Icon(Icons.Default.Air, contentDescription = "Haze") },
                                label = { Text("Haze", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HazardOrange,
                                    selectedTextColor = HazardOrange,
                                    indicatorColor = SlateDark800,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_tab_haze")
                            )

                            NavigationBarItem(
                                selected = currentTab == HazardNavTab.HOURLY,
                                onClick = { viewModel.selectTab(HazardNavTab.HOURLY) },
                                icon = { Icon(Icons.Default.AccessTime, contentDescription = "Hourly") },
                                label = { Text("Hourly", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HazardGreen,
                                    selectedTextColor = HazardGreen,
                                    indicatorColor = SlateDark800,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_tab_hourly")
                            )

                            NavigationBarItem(
                                selected = currentTab == HazardNavTab.DND_SETTINGS,
                                onClick = { viewModel.selectTab(HazardNavTab.DND_SETTINGS) },
                                icon = { Icon(Icons.Default.DarkMode, contentDescription = "DND & Voice") },
                                label = { Text("DND & Voice", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF818CF8),
                                    selectedTextColor = Color(0xFF818CF8),
                                    indicatorColor = SlateDark800,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_tab_dnd")
                            )

                            NavigationBarItem(
                                selected = currentTab == HazardNavTab.SIMULATE,
                                onClick = { viewModel.selectTab(HazardNavTab.SIMULATE) },
                                icon = { Icon(Icons.Default.Warning, contentDescription = "Simulate") },
                                label = { Text("Simulate", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HazardAmber,
                                    selectedTextColor = HazardAmber,
                                    indicatorColor = SlateDark800,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_tab_simulate")
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentTab) {
                        HazardNavTab.DASHBOARD -> HomeScreen(
                            snapshot = snapshot,
                            alerts = alerts,
                            dndSettings = dndSettings,
                            thresholds = thresholds,
                            voiceSettings = voiceSettings,
                            isDndActiveNow = isDndActiveNow,
                            onRefresh = { viewModel.refreshHazards() },
                            onPlayVoice = { type -> viewModel.testVoiceSound(type) },
                            onDismissAlert = { id -> viewModel.dismissAlert(id) },
                            onNavigateToSimulate = { viewModel.selectTab(HazardNavTab.SIMULATE) },
                            onNavigateToHaze = { viewModel.selectTab(HazardNavTab.HAZE_DETAIL) },
                            modifier = Modifier.padding(innerPadding)
                        )

                        HazardNavTab.HAZE_DETAIL -> HazeFocusScreen(
                            snapshot = snapshot,
                            thresholds = thresholds,
                            voiceSettings = voiceSettings,
                            dataSourceSettings = dataSourceSettings,
                            onUpdateThresholds = { newThresholds -> viewModel.updateThresholds(newThresholds) },
                            onTestHazeVoice = { viewModel.testVoiceSound(com.example.data.local.HazardType.HAZE) },
                            onSimulateCriticalHaze = {
                                viewModel.injectSimulation(com.example.data.local.HazardType.HAZE, forceBypassDnd = true)
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        HazardNavTab.HOURLY -> HourlyMonitoringScreen(
                            isHourlyEnabled = isHourlyEnabled,
                            hourlyReadings = hourlyReadings,
                            onToggleHourly = { enabled -> viewModel.toggleHourlyMonitoring(enabled) },
                            onRunManualCheck = { viewModel.refreshHazards() },
                            modifier = Modifier.padding(innerPadding)
                        )

                        HazardNavTab.DND_SETTINGS -> SettingsDndScreen(
                            dndSettings = dndSettings,
                            thresholds = thresholds,
                            voiceSettings = voiceSettings,
                            dataSourceSettings = dataSourceSettings,
                            isDndActiveNow = isDndActiveNow,
                            onUpdateDnd = { updatedDnd -> viewModel.updateDndSettings(updatedDnd) },
                            onUpdateThresholds = { updatedThresholds -> viewModel.updateThresholds(updatedThresholds) },
                            onUpdateVoiceSettings = { updatedVoice -> viewModel.updateVoiceSettings(updatedVoice) },
                            onUpdateDataSourceSettings = { updatedDs -> viewModel.updateDataSourceSettings(updatedDs) },
                            onTestVoiceAlert = { type -> viewModel.testVoiceSound(type) },
                            modifier = Modifier.padding(innerPadding)
                        )

                        HazardNavTab.SIMULATE -> SimulationScreen(
                            isDndActiveNow = isDndActiveNow,
                            onSimulate = { type, bypassDnd -> viewModel.injectSimulation(type, forceBypassDnd = bypassDnd) },
                            onClearAllAlerts = { viewModel.clearAllAlerts() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
