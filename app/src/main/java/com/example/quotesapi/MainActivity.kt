package com.example.quotesapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quotesapi.ui.theme.QuotesApiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavGraph()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {

    val viewModel = remember {
        MainViewModel(repository = Repository())
    }
    QuotesApiTheme {
        val isQuotes by remember {
            mutableStateOf(false)
        }
        var quotesData by remember {
            mutableStateOf<List<QuotesItem>?>(null)
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
                QuoteList(quotesData = it, navController = navController)

                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteList(quotesData: List<QuotesItem>,navController: NavController) {
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
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "")
                    Spacer(modifier = Modifier.padding(10.dp))
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "")
                })
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = it.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(top = 4.dp)
            ) {
                quotesData?.let {
                    items(it) { quote ->
                        Quotes(quotesItem = quote, navController)
                    }
                }
            }
        }
    }
}
@Composable
fun Quotes(quotesItem: QuotesItem, navController: NavController) {

    Card(modifier = Modifier
        .wrapContentSize()
        .padding(all = 6.dp)
        .clickable { navController.navigate(Screen.Second.route + "/${quotesItem.quote} /${quotesItem.author}") }) {
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
                Icon(imageVector = Icons.Outlined.FavoriteBorder, contentDescription = "Favoritr")
                Spacer(modifier = Modifier.width(5.dp))
                Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")

            }

        }
    }
}


