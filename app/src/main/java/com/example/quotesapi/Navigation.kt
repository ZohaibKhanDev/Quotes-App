package com.example.quotesapi

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quotesapi.ui.theme.QuotesApiTheme


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    QuotesApiTheme(darkTheme = switchState) {
        val navController = rememberNavController()
        Scaffold(bottomBar = { BottomNavigation(navController = navController) }) {
            Navigation(navController = navController)
        }
    }
}

@Composable
fun Navigation(navController: NavHostController) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    QuotesApiTheme(darkTheme = switchState) {
        NavHost(navController = navController, startDestination = bottomScreen.Home.route) {
            composable(bottomScreen.Home.route) {
                Home(navController)
            }
            composable(bottomScreen.SecondScreen.route + "/{quotes}/{author}",
                arguments = listOf(
                    navArgument("quotes") {
                        type = NavType.StringType
                    },
                    navArgument("author") {
                        type = NavType.StringType
                    }
                )
            ) {
                val quotes = it.arguments?.getString("quotes")
                val author = it.arguments?.getString("author")
                SecondScreen(navController = navController, quotes, author)
            }
            composable(bottomScreen.Favourite.route) {
                FavouriteScreen(navController)
            }
            composable(bottomScreen.Setting.route) {
                SettingScreen(navController)
            }
        }
    }


}

sealed class bottomScreen(
    val tittle: String,
    val route: String,
    val selectIcon: ImageVector,
    val unselect: ImageVector,

    ) {
    object Home :
        bottomScreen(
            "Home",
            "Home",
            selectIcon = Icons.Default.Home,
            unselect = Icons.Outlined.Home
        )

    object SecondScreen : bottomScreen(
        "Second",
        "Second",
        selectIcon = Icons.Default.ShoppingCart,
        unselect = Icons.Outlined.ShoppingCart
    )

    object Favourite :
        bottomScreen(
            "Favourite",
            "Favourite",
            selectIcon = Icons.Default.Favorite,
            unselect = Icons.Outlined.FavoriteBorder
        )

    object Setting :
        bottomScreen(
            "Setting",
            "Setting",
            selectIcon = Icons.Default.Settings,
            unselect = Icons.Outlined.Settings
        )
}


@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        bottomScreen.Home,
        bottomScreen.Favourite,
        bottomScreen.Setting
    )
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    QuotesApiTheme (darkTheme = switchState){
        NavigationBar(contentColor = Color.White) {
            val navStack by navController.currentBackStackEntryAsState()
            val current = navStack?.destination?.route

            items.forEach {
                NavigationBarItem(selected = current == it.route,
                    onClick = {
                        navController.navigate(it.route) {
                            navController.graph.let {
                                it.route?.let { it1 -> popUpTo(it1) }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Image(
                            imageVector = if (current == it.route) it.selectIcon else it.unselect,
                            contentDescription = ""
                        )

                    },
                    label = {
                        Text(text = it.route)
                    }
                )
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SecondScreen(navController: NavController, quotes: String?, author: String?) {
    var icon by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    QuotesApiTheme(darkTheme = switchState) {
        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Quotes",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
            }, colors = TopAppBarDefaults.topAppBarColors(Color(0XFF5EEBFC)),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "",
                        modifier = Modifier.clickable { navController.popBackStack() })
                },
                actions = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "")
                    Spacer(modifier = Modifier.padding(10.dp))
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "")

                }
            )
        }) {

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .wrapContentHeight()

            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),

                    ) {


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "",
                            Modifier.rotate(180f)
                        )
                        Text(
                            text = "$quotes",
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,

                            )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = ("$author"),
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (icon) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            modifier = Modifier.clickable { icon = !icon }
                        )

                        Spacer(modifier = Modifier.padding(10.dp))
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            modifier = Modifier.clickable { })

                    }

                }
            }

        }
    }

}
