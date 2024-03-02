package com.rajit.richcontexttextfield_compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.content.MediaType
import androidx.compose.foundation.content.receiveContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.rajit.richcontexttextfield_compose.ui.theme.RichContextTextFieldComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RichContextTextFieldComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {

    var selectedContentURL by remember {
        mutableStateOf<String?>(null)
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {

        selectedContentURL?.let { url ->
            Text("Selected Image", modifier = Modifier.padding(16.dp))
            CustomImage(url = url, modifier = Modifier.fillMaxWidth())
        }

        MessageTextField { uri ->
            selectedContentURL = uri
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageTextField(onContentReceived: (String) -> Unit) {
    var fieldValue by remember {
        mutableStateOf("")
    }

    BasicTextField2(
        value = fieldValue,
        onValueChange = { fieldValue = it },
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            ) {
                if (fieldValue.isBlank()) {
                    Text(
                        text = "Write or select image from Keyboard",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                innerTextField()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .receiveContent(setOf(MediaType.Image)) { content ->
                // This is the main part where we handle image input
                content.platformTransferableContent
                    ?.linkUri
                    ?.toString()
                    ?.let(onContentReceived)
                null
            }
    )
}

@Composable
fun CustomImage(url: String, modifier: Modifier) {
    val context = LocalContext.current

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Selected Image",
        contentScale = ContentScale.Fit,
        imageLoader = imageLoader,
        modifier = modifier
    )
}