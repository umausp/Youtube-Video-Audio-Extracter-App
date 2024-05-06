package com.vedic.img.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vedic.img.R
import com.vedic.img.analytics.SendEvents
import com.vedic.img.viewmodel.YoutubeLoadViewModel


fun isValidYouTubeUrl(url: String): Boolean {
    val regexPattern = """^(https?://)?(www\.)?(youtube\.com|youtu\.be)/.+$""".toRegex()
    return regexPattern.matches(url)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchYoutubeBox() {
    val viewModel = viewModel<YoutubeLoadViewModel>()
    var text by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }
    val errorMessage = "Text input too long"
    var message by rememberSaveable { mutableStateOf("Copy Youtube Url and paste above") }
    val keyboardController = LocalSoftwareKeyboardController.current


    fun validate(text: String) {
        isError = text.isEmpty() || !isValidYouTubeUrl(text)
        message = if (isError) {
            "Please correct the Youtube Url"
        } else {
            "Copy Youtube Url and paste above"
        }
    }

    OutlinedTextField(
        colors = TextFieldDefaults.textFieldColors(),
        value = text,
        placeholder = {
            Text(
                text,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
            )
        },
        onValueChange = {
            text = it
            isError = false
            message = "Copy Youtube Url and paste above"
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    text = ""
                    isError = false
                    message = "Copy Youtube Url and paste above"
                    SendEvents.sendSearchClearClickEvent()
                }) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        leadingIcon = {
            IconButton(onClick = {
                validate(text)
                if (!isError) {
                    viewModel.youtubeUrl.value = text
                    SendEvents.sendSearchClickEvent(text, "search_icon")
                }
                keyboardController?.hide()
            }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Localized description"
                )
            }
        },
        supportingText = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message,
                color = Color.Magenta,
                textAlign = TextAlign.Start,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
            )
        },
        isError = isError,
        singleLine = true,
        keyboardActions = KeyboardActions {
            validate(text)
            if (!isError) {
                viewModel.youtubeUrl.value = text
                SendEvents.sendSearchClickEvent(text, "keyboard_enter")
            }
            keyboardController?.hide()
        },
        modifier = Modifier
            .semantics {
            }
            .fillMaxWidth()
            .padding(8.dp)
    )
}
