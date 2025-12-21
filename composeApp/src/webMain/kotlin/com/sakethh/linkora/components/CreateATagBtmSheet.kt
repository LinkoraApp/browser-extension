package com.sakethh.linkora.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.pressScaleEffect
import com.sakethh.linkora.theme.gspTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateATagBtmSheet(
    sheetState: SheetState, onCancel: () -> Unit, onCreateClick: (tagName: String) -> Unit
) {
    val focusRequester = remember {
        FocusRequester()
    }
    var newTag by rememberSaveable {
        mutableStateOf("")
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    var showLoadingProgressBar by rememberSaveable {
        mutableStateOf(false)
    }
    var isTagNameBlank by rememberSaveable {
        mutableStateOf(false)
    }
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onCancel) {
        Column {
            Text(
                text = "Create A Tag",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 15.dp)
            )
            TextField(
                textStyle = gspTextStyle,
                isError = isTagNameBlank,
                supportingText = {
                    if (isTagNameBlank) {
                        Text(
                            text = "Tag name can't be empty.",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                    }
                },
                enabled = !showLoadingProgressBar,
                label = {
                    Text(
                        text = "Tag Name", style = MaterialTheme.typography.titleSmall
                    )
                },
                value = newTag,
                onValueChange = {
                    newTag = it
                    isTagNameBlank = newTag.isBlank()
                },
                modifier = Modifier.animateContentSize().fillMaxWidth().padding(15.dp)
                    .focusRequester(focusRequester = focusRequester)
            )
            if (showLoadingProgressBar) {
                LinearProgressIndicator(modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp))
                return@Column
            }
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).pressScaleEffect().fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp)
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Button(
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth().pressScaleEffect()
                    .padding(start = 15.dp, end = 15.dp, bottom = 5.dp), onClick = {
                    isTagNameBlank = newTag.isBlank()
                    if (!isTagNameBlank) {
                        showLoadingProgressBar = true
                        onCreateClick(newTag)
                    }
                }) {
                Text(
                    text = "Create", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium
                )
            }
        }
    }
}