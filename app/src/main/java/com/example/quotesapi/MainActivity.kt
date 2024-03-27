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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumberedRtl
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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
        context, DataBase::class.java, "demo.db"
    ).allowMainThreadQueries().build()
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
    quotesData: List<QuotesItem>, navController: NavController, viewModel: MainViewModel
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
    var searchState by remember {
        mutableStateOf("")
    }
    var searchBarState by remember {
        mutableStateOf(false)
    }
    QuotesApiTheme(darkTheme = switchState) {

        if (searchBarState) {
            SearchBar(
                query = searchState,
                onQueryChange = {
                    searchState = it
                },
                onSearch = { bar ->
                    quotesData?.let { search ->
                        search.forEach {
                            it.quote.contains(bar)
                        }

                    }
                },
                active = true,
                onActiveChange = {
                },
                Modifier
                    .wrapContentWidth()
                    .padding(12.dp),
                placeholder = {
                    Text(text = "Search")
                },
                shadowElevation = 4.dp,
                tonalElevation = 4.dp,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "",
                        modifier = Modifier.clickable { })
                }
            ) {

            }

        } else {
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

                            Icon(imageVector = if (list) Icons.Default.List else Icons.Default.FormatListNumberedRtl,
                                contentDescription = "",
                                modifier = Modifier.clickable {
                                    icons = !icons
                                    val gridSection = listPreferences.edit()
                                    gridSection.putBoolean("listmode", icons).apply()

                                })
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "",
                                modifier = Modifier.clickable { searchBarState = !searchBarState })
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "")
                            Spacer(modifier = Modifier.width(8.dp))


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
                        visible = icons == false, enter = fadeIn(
                            tween(durationMillis = 1000, delayMillis = 1000), initialAlpha = 1f
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
                    Icon(imageVector = if (icon) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
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
        context, DataBase::class.java, "demo.db"
    ).allowMainThreadQueries().build()
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            favData?.let {
                items(it) { fav ->
                    FavItem(fav = fav)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                .padding(14.dp, bottom = 14.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 14.dp),
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


        Scaffold(topBar = {
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
        }) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(
                        top = it.calculateTopPadding(),
                        start = 14.dp,
                        end = 14.dp,
                        bottom = 14.dp
                    ),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)
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



            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier.padding(top = 120.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = it.calculateTopPadding(),
                            start = 14.dp,
                            end = 14.dp,
                            bottom = 14.dp
                        )
                        .clickable { navController.navigate(bottomScreen.Favourite.route) },
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
                            text = "Your Favourite",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }

            }




            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier.padding(top = 220.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = it.calculateTopPadding(),
                            start = 14.dp,
                            end = 14.dp,
                            bottom = 14.dp
                        )
                        .clickable { navController.navigate(bottomScreen.UpgradeScreen.route) },
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
                            text = "Upgrade",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }

            }





            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier.padding(top = 320.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = it.calculateTopPadding(),
                            start = 14.dp,
                            end = 14.dp,
                            bottom = 14.dp
                        )
                        .clickable { navController.navigate(bottomScreen.FeatureScreen.route) },
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
                            text = "Feature Preview",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }

            }



            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier.padding(top = 420.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = it.calculateTopPadding(),
                            start = 14.dp,
                            end = 14.dp,
                            bottom = 14.dp
                        )
                        .clickable { navController.navigate(bottomScreen.CommunityScreen.route) },
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
                            text = "Quotes Community",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }

            }



            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier.padding(top = 620.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = it.calculateTopPadding(),
                            start = 14.dp,
                            end = 14.dp,
                            bottom = 14.dp
                        )
                        .clickable { navController.navigate(bottomScreen.PrivacyScreen.route) },
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
                            text = "privacy policy page",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }

            }

            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier.padding(top = 520.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = it.calculateTopPadding(),
                            start = 14.dp,
                            end = 14.dp,
                            bottom = 14.dp
                        )
                        .clickable { navController.navigate(bottomScreen.AboutScreen.route) },
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
                            text = "About", fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }

            }
        }
    }
}

@Composable
fun PrivacyScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.padding(20.dp, bottom = 40.dp, top = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text(
                text = "At Quotes App, we are committed to protecting your privacy and ensuring the security of your personal information. This Privacy Policy outlines the types of information we collect, how we use it, and the measures we take to safeguard your data.\n" +
                        "\n" +
                        "By using Quotes App, you consent to the collection and use of your information as described in this Privacy Policy. If you do not agree with any part of this policy, please refrain from using the app.\n" +
                        "\n" +
                        "Information We Collect:\n" +
                        "\n" +
                        "Personal Information: When you use Quotes App, we may collect personal information such as your name, email address, and any other information you voluntarily provide.\n" +
                        "Usage Data: We may collect information about how you use the app, including which features you access and how often you use them.\n" +
                        "Device Information: We may collect information about your device, including the type of device you use, unique device identifiers, IP address, operating system version, and mobile network information.\n" +
                        "Cookies and Similar Technologies: Like many websites and mobile applications, we may use cookies and similar technologies to collect information about your usage and preferences.\n" +
                        "How We Use Your Information:\n" +
                        "\n" +
                        "We may use the information we collect for the following purposes:\n" +
                        "\n" +
                        "To provide and improve Quotes App and its features.\n" +
                        "To personalize your experience and deliver content tailored to your interests.\n" +
                        "To communicate with you, respond to your inquiries, and provide customer support.\n" +
                        "To analyze usage patterns and trends to enhance the functionality and performance of the app.\n" +
                        "To prevent fraud and ensure the security of our users and the app.\n" +
                        "Sharing of Information:\n" +
                        "\n" +
                        "We do not sell, trade, or otherwise transfer your personal information to third parties without your consent, except as described below:\n" +
                        "\n" +
                        "Service Providers: We may share your information with trusted third-party service providers who assist us in operating Quotes App and providing services to you. These service providers are contractually obligated to use your information only for the purposes of providing services to us and are required to maintain the confidentiality and security of your information.\n" +
                        "Legal Compliance: We may disclose your information if required to do so by law or in response to valid legal requests, such as subpoenas or court orders.\n" +
                        "Business Transfers: In the event of a merger, acquisition, or sale of all or a portion of our assets, your information may be transferred to the acquiring entity.\n" +
                        "Data Security:\n" +
                        "\n" +
                        "We take reasonable measures to protect the security of your information and prevent unauthorized access, use, or disclosure. However, please be aware that no method of transmission over the internet or electronic storage is completely secure, and we cannot guarantee the absolute security of your information.\n" +
                        "\n" +
                        "Children's Privacy:\n" +
                        "\n" +
                        "Quotes App is not intended for use by children under the age of 13. We do not knowingly collect personal information from children under 13. If you are a parent or guardian and believe that your child has provided us with personal information, please contact us immediately, and we will take steps to remove such information from our systems.\n" +
                        "\n" +
                        "Changes to this Privacy Policy:\n" +
                        "\n" +
                        "We reserve the right to update or modify this Privacy Policy at any time. Any changes will be effective immediately upon posting the revised policy on Quotes App. We encourage you to review this Privacy Policy periodically for any updates.\n" +
                        "\n" +
                        "Contact Us:\n" +
                        "\n" +
                        "If you have any questions or concerns about this Privacy Policy or our practices regarding your information, please contact us at contact@quotesapp.com."
            )
            Spacer(modifier = Modifier.padding(35.dp))
        }
    }
}

@Composable
fun AboutScreen(navController: NavController) {

    LazyColumn(
        modifier = Modifier.padding(25.dp, top = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "At [Quotes App], we believe in the power of words to inspire, uplift, and transform lives. Our mission is to curate a collection of the most profound, thought-provoking, and motivational quotes from throughout history and across cultures, all in one convenient location. Whether you're seeking a spark of creativity, a dose of encouragement, or simply a moment of reflection, our app is your go-to destination.\n" +
                        "\n" +
                        "What Sets Us Apart\n" +
                        "\n" +
                        "What makes [App Name] stand out in the crowded landscape of quote apps? It's simple: our commitment to quality and diversity. We handpick each quote with care, ensuring that it resonates with our users on a deep and meaningful level. From ancient wisdom to modern insights, from renowned philosophers to everyday heroes, our collection represents a rich tapestry of human experience.\n" +
                        "\n" +
                        "Features\n" +
                        "\n" +
                        "Personalized Recommendations: Tailored just for you, our app learns your preferences over time to deliver quotes that match your interests and mood.\n" +
                        "\n" +
                        "Save and Share: Found a quote that speaks to you? Save it to your favorites or share it with friends and family on social media, spreading inspiration far and wide.\n" +
                        "\n" +
                        "Daily Reminders: Start your day on a positive note with our daily quote reminders, delivered straight to your device.\n" +
                        "\n" +
                        "Explore Categories: Dive into specific themes or topics that interest you, whether it's love, courage, success, or happiness.\n" +
                        "\n" +
                        "Our Vision\n" +
                        "\n" +
                        "At [App Name], we envision a world where everyone has access to the wisdom and encouragement they need to live their best lives. We believe that by harnessing the power of words, we can empower individuals to overcome challenges, pursue their dreams, and make a positive impact on the world around them.\n" +
                        "\n" +
                        "Join Our Community\n" +
                        "\n" +
                        "Join the [App Name] community today and embark on a journey of self-discovery, growth, and inspiration. Download our app now and let the transformative power of quotes enrich your life every day. Together, let's ignite the spark of possibility and create a brighter tomorrow, one quote at a time."
            )
            Spacer(modifier = Modifier.padding(30.dp))
        }
    }
}

@Composable
fun UpgradeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text(
                text = "Welcome to the Quotes App Upgrade!\n" +
                        "\n" +
                        "Unlock Premium Features\n" +
                        "\n" +
                        "Upgrade now to unlock exclusive features and enhance your quote browsing experience.\n" +
                        "\n" +
                        "Premium Features Include:\n" +
                        "\n" +
                        "Ad-Free Experience: Enjoy uninterrupted browsing with zero advertisements.\n" +
                        "\n" +
                        "Unlimited Favorites: Save as many quotes as you want without any restrictions.\n" +
                        "\n" +
                        "Customizable Themes: Personalize your app with a variety of beautiful themes.\n" +
                        "\n" +
                        "Daily Quote Notifications: Receive inspirational quotes daily to uplift your spirits.\n" +
                        "\n" +
                        "Offline Access: Access your favorite quotes even without an internet connection.\n" +
                        "\n" +
                        "Why Upgrade?\n" +
                        "\n" +
                        "Enhanced Experience: Get rid of distractions and immerse yourself in a seamless browsing experience.\n" +
                        "\n" +
                        "Support Development: By upgrading, you support the continuous improvement and maintenance of the Quotes App.\n" +
                        "\n" +
                        "Upgrade Now\n" +
                        "\n" +
                        "Unlock premium features for only X.XX per month and elevate your quote journey to the next level."
            )
        }
    }
}

@Composable
fun Feature_Preview(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to QuoteMaster – the ultimate app designed to elevate your daily routine with a curated collection of wisdom, motivation, and inspiration! Whether you seek a spark of creativity, a dose of motivation, or simply a moment of reflection, QuoteMaster is your go-to companion.\n" +
                    "\n" +
                    "Discover Limitless Inspiration:\n" +
                    "Explore a vast library of handpicked quotes from renowned thinkers, authors, leaders, and celebrities spanning across various categories such as love, life, success, happiness, and more. With daily updates and fresh content, there’s always something new to discover and inspire you.\n" +
                    "\n" +
                    "Personalized Experience:\n" +
                    "Tailor QuoteMaster to your preferences with customizable settings. Favorite the quotes that resonate with you the most, create personalized collections, and set up daily reminders to receive your dose of inspiration at the perfect time.\n" +
                    "\n" +
                    "Seamless Sharing:\n" +
                    "Spread positivity and wisdom effortlessly with QuoteMaster’s easy sharing feature. Share your favorite quotes with friends, family, and social networks to inspire those around you and spark meaningful conversations.\n" +
                    "\n" +
                    "Enhanced Accessibility:\n" +
                    "QuoteMaster is designed for convenience and accessibility. Enjoy a user-friendly interface that makes browsing, saving, and sharing quotes a breeze. Whether you’re on your smartphone, tablet, or desktop, QuoteMaster adapts seamlessly to your device.\n" +
                    "\n" +
                    "Stay Motivated Anywhere, Anytime:\n" +
                    "Whether you’re commuting to work, taking a break between tasks, or winding down before bed, QuoteMaster is there to uplift and inspire you. With offline access, you can enjoy your favorite quotes anytime, anywhere, even without an internet connection.\n" +
                    "\n" +
                    "Coming Soon: Community Features!\n" +
                    "Get ready for even more excitement as QuoteMaster prepares to introduce community features, allowing you to connect with like-minded individuals, share insights, and participate in discussions around your favorite quotes and topics.\n" +
                    "\n" +
                    "Get Ready to Be Inspired:\n" +
                    "With QuoteMaster, inspiration is just a tap away. Elevate your mindset, fuel your creativity, and embark on a journey of personal growth and enlightenment. Download QuoteMaster now and unlock the power of words to transform your life."
        )
    }
}

@Composable
fun Quotes_Community(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\"Quotables: Where Words Inspire, Connect, and Resonate.\"\n" +
                    "\n" +
                    "\"Unlocking Wisdom, One Quote at a Time.\"\n" +
                    "\n" +
                    "\"Words That Bind: Building Bridges Through Quotes.\"\n" +
                    "\n" +
                    "\"Echoes of Insight: Where Every Quote Tells a Story.\"\n" +
                    "\n" +
                    "\"Quotidian: Where Daily Quotes Fuel Inspiration.\"\n" +
                    "\n" +
                    "\"Inspire, Share, Repeat: The Power of Quotations.\"\n" +
                    "\n" +
                    "\"Quotiverse: Where Every Quote Finds its Home.\"\n" +
                    "\n" +
                    "\"From Thought to Quote: Uniting Minds Across the Globe.\"\n" +
                    "\n" +
                    "\"Quotient: Where Words Multiply Inspiration.\"\n" +
                    "\n" +
                    "\"Quotopia: Where Ideas Converge and Quotes Flourish.\""
        )
    }

}


