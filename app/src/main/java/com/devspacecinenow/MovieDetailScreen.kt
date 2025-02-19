package com.devspacecinenow

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun MovieDetailScreen(
    movieId: String,
    navHostController: NavHostController
) {
    var movieDto by remember { mutableStateOf<MovieDto?>(null) }
    val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)

    apiService.getMovieById(movieId).enqueue(
        object : Callback<MovieDto> {
            override fun onResponse(call: Call<MovieDto>, response: Response<MovieDto>) {
                if (response.isSuccessful) {
                    movieDto = response.body()
                } else {
                    Log.d("MainActivity", "Request Error :: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<MovieDto>, t: Throwable) {
                Log.d("MainActivity", "Network Error :: ${t.message}")
            }
        }
    )

    movieDto?.let { movie ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Usa a cor de fundo do tema
        ) {
            // Barra superior com botão e título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = MaterialTheme.colorScheme.onBackground // Garante contraste no Dark Mode
                    )
                }
                Text(
                    text = movie.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground, // Usa a cor correta do tema
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Conteúdo do detalhe do filme
            MovieDetailContent(movie)
        }
    }
}

@Composable
private fun MovieDetailContent(movie: MovieDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Imagem do filme
        AsyncImage(
            model = movie.posterfullPath,
            contentDescription = "${movie.title} Poster Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sinopse com formatação melhorada
        Text(
            text = "Sinopse",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground // Cor adaptável ao tema
        )

        Text(
            text = movie.overview,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground, // Usa cor dinâmica
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
