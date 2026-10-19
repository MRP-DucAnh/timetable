package com.pear.timetable.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pear.timetable.ui.main.page1.HomeScreen
import com.pear.timetable.ui.main.page2.NotesScreen
import com.pear.timetable.ui.main.page3.TasksScreen
import com.pear.timetable.ui.main.page4.ProfileScreen


data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    // Đồng bộ ViewModel → Pager
    LaunchedEffect(selectedTab) {
        if (pagerState.currentPage != selectedTab) {
            pagerState.animateScrollToPage(selectedTab)
        }
    }

    // Đồng bộ Pager → ViewModel (khi người dùng vuốt)
    LaunchedEffect(pagerState.currentPage) {
        if (selectedTab != pagerState.currentPage) {
            viewModel.onTabSelected(pagerState.currentPage)
        }
    }

    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Ghi chú", Icons.Default.EditNote),
        BottomNavItem("Công việc", Icons.Default.CheckCircle),
        BottomNavItem("Cá nhân", Icons.Default.Person)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Timetable") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { viewModel.onTabSelected(index) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> HomeScreen()
                1 -> NotesScreen()
                2 -> TasksScreen()
                3 -> ProfileScreen()
            }
        }
    }
}