package com.example.quotesapi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumberedRtl
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import com.example.quotesapi.room.DataBase
import com.example.quotesapi.room.Fav
import com.example.quotesapi.ui.theme.QuotesApiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

            val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
            var switchState by remember {
                mutableStateOf(isDarkValue)
            }
            QuotesApiTheme(darkTheme = switchState) {
                MainScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        DataBase::class.java,
        "demo.db"
    ).allowMainThreadQueries()
        .build()
    val viewModel = remember {
        MainViewModel(repository = Repository(db))
    }
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    QuotesApiTheme(darkTheme = switchState) {
        val isfav by remember {
            mutableStateOf(false)
        }

        var quotesData by remember {
            mutableStateOf<List<QuotesItem>?>(null)
        }
        var favData by remember {
            mutableStateOf<List<Fav>?>(null)
        }

        LaunchedEffect(key1 = isfav) {
            viewModel.getAllFav()
        }
        val favState by viewModel.allFav.collectAsState()
        when (favState) {
            is ResultState.Error -> {
                val error = (favState as ResultState.Error).error
                Text(text = error.toString())
            }

            ResultState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ResultState.Success -> {
                val success = (favState as ResultState.Success).repository
                favData = success
            }
        }

        LaunchedEffect(key1 = Unit) {
            viewModel.getAllQuotes()
        }
        val state by viewModel.allQuotes.collectAsState()
        when (state) {
            is ResultState.Error -> {
                val error = (state as ResultState.Error).error
                Text(text = error.toString())
            }

            ResultState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ResultState.Success -> {
                val success = (state as ResultState.Success).repository
                quotesData = success
                quotesData?.let {
                    QuoteList(quotesData = it, navController = navController, viewModel)

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteList(
    quotesData: List<QuotesItem>,
    navController: NavController,
    viewModel: MainViewModel
) {
    var list by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    val listPreferences = context.getSharedPreferences("List", Context.MODE_PRIVATE)
    val isListValue = listPreferences.getBoolean("listmode", false)
    var icons by remember {
        mutableStateOf(isListValue)
    }

    QuotesApiTheme(darkTheme = switchState) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(
                        text = "Quotes",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                },
                    colors = TopAppBarDefaults.topAppBarColors(Color(0XFF5EEBFC)),
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "",
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    actions = {

                        Icon(
                            imageVector = if (list) Icons.Default.List else Icons.Default.FormatListNumberedRtl,
                            contentDescription = "", modifier = Modifier.clickable {
                                icons = !icons
                                val gridSection = listPreferences.edit()
                                gridSection.putBoolean("listmode", icons).apply()

                            }
                        )
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "")


                        Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "")


                    })
            },
        ) {

            if (icons) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = it.calculateTopPadding())
                ) {
                    AnimatedVisibility(
                        visible = icons, enter = fadeIn(
                            tween(durationMillis = 1000, delayMillis = 1000), initialAlpha = 1f
                        ), exit = fadeOut(tween(easing = LinearEasing))
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(top = 4.dp)
                        ) {
                            quotesData?.let {
                                items(it) { quote ->
                                    Quotes(quotesItem = quote, navController, viewModel)
                                }
                            }
                        }
                    }
                }

            } else {

                AnimatedVisibility(
                    visible = icons == false,
                    enter = fadeIn(
                        tween(durationMillis = 1000, delayMillis = 1000),
                        initialAlpha = 1f
                    ), exit = fadeOut(tween(easing = LinearEasing))
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.padding(top = it.calculateTopPadding())
                    ) {
                        quotesData?.let {
                            items(it) { quote ->
                                Quotes(quotesItem = quote, navController, viewModel)

                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun Quotes(quotesItem: QuotesItem, navController: NavController, viewModel: MainViewModel) {
    var icon by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }

    val isFav by remember {
        mutableStateOf(false)
    }
    var favData by remember {
        mutableStateOf<List<Fav>?>(null)
    }
    LaunchedEffect(key1 = isFav) {
        viewModel.getAllFav()
    }

    val state by viewModel.allFav.collectAsState()
    when (state) {
        is ResultState.Error -> {
            val error = (state as ResultState.Error).error
            Text(text = error.toString())
        }

        ResultState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            val success = (state as ResultState.Success).repository
            favData = success
        }
    }
    QuotesApiTheme(darkTheme = switchState) {

        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(all = 6.dp)
                .clickable { navController.navigate(bottomScreen.SecondScreen.route + "/${quotesItem.quote} /${quotesItem.author}") },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = quotesItem.quote)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = quotesItem.author,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,

                    )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (icon) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favoritr",
                        modifier = Modifier.clickable {
                            val fav = Fav(null, quotesItem.quote, quotesItem.author)
                            icon = !icon
                            viewModel.Insert(fav)
                        })
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")

                }

            }
        }
    }

}


@Composable
fun FavouriteScreen(navController: NavController) {

    BottomFav()


}

@Composable
fun BottomFav() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }

    val db = Room.databaseBuilder(
        context,
        DataBase::class.java,
        "demo.db"
    ).allowMainThreadQueries()
        .build()
    val repository = remember {
        Repository(db)
    }
    val viewModel = remember {
        MainViewModel(repository)
    }
    var favData by remember {
        mutableStateOf<List<Fav>?>(null)
    }
    val isFav by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = isFav) {
        viewModel.getAllFav()
    }
    val favState by viewModel.allFav.collectAsState()
    when (favState) {
        is ResultState.Error -> {
            val error = (favState as ResultState.Error).error
            Text(text = error.toString())
        }

        ResultState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            val success = (favState as ResultState.Success).repository
            favData = success
        }
    }
    QuotesApiTheme(darkTheme = switchState) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(top = 4.dp)
        ) {
            favData?.let {
                items(it) { fav ->
                    FavItem(fav = fav)
                }
            }
        }
    }

}

@Composable
fun FavItem(fav: Fav) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)
    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    QuotesApiTheme(darkTheme = switchState) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(all = 6.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "",
                        modifier = Modifier.rotate(180f)
                    )
                    Text(
                        text = fav.titttle,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.W600
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = fav.des,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,

                    )


            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingScreen(navController: NavController) {
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    val isDarkValue = sharedPreferences.getBoolean("darkMode", false)

    var switchState by remember {
        mutableStateOf(isDarkValue)
    }
    QuotesApiTheme(darkTheme = switchState) {


        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Setting",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(Color(0XFF3492eb)),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = it.calculateTopPadding(),
                        start = 14.dp,
                        end = 14.dp,
                        bottom = 14.dp
                    ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dark Theme | $isDarkValue",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Switch(checked = switchState, onCheckedChange = {
                        switchState = it
                        sharedPreferences.edit().putBoolean("darkMode", it).apply()

                    })
                }
            }
        }
    }


}


