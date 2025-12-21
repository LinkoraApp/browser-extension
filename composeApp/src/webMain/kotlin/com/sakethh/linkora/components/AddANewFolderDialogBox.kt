package com.sakethh.linkora.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.pressScaleEffect
import com.sakethh.linkora.theme.gspTextStyle

@Composable
fun AddANewFolderDialogBox(
    onDismiss: () -> Unit,
    onFolderCreateClick: (folderName: String, folderNote: String, onCompletion: () -> Unit) -> Unit,
    currentFolderId: Long?,
    currentFolderName: String?
) {
    val scrollState = rememberScrollState()
    val isFolderCreationInProgress = rememberSaveable {
        mutableStateOf(false)
    }
    val folderNameTextFieldValue = rememberSaveable {
        mutableStateOf("")
    }
    val noteTextFieldValue = rememberSaveable {
        mutableStateOf("")
    }
    val focusRequester = remember {
        FocusRequester()
    }
    AlertDialog(dismissButton = {
        if (!isFolderCreationInProgress.value) {
            OutlinedButton(
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth().pressScaleEffect(),
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel", style = MaterialTheme.typography.titleSmall, fontSize = 16.sp
                )
            }
        }
    }, confirmButton = {
        if (!isFolderCreationInProgress.value) {
            Button(
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth().pressScaleEffect(),
                onClick = {
                    isFolderCreationInProgress.value = true
                    onFolderCreateClick(
                        folderNameTextFieldValue.value, noteTextFieldValue.value, {
                            onDismiss()
                            isFolderCreationInProgress.value = false
                        })
                }) {
                Text(
                    text = "Create", style = MaterialTheme.typography.titleSmall, fontSize = 16.sp
                )
            }
        }
    }, modifier = Modifier.animateContentSize().wrapContentHeight(), onDismissRequest = {
        if (!isFolderCreationInProgress.value) {
            onDismiss()
        }
    }, text = {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            OutlinedTextField(
                textStyle = gspTextStyle,
                readOnly = isFolderCreationInProgress.value,
                modifier = Modifier.focusRequester(focusRequester).fillMaxWidth(),
                maxLines = 1,
                label = {
                    Text(
                        text = "Folder name", style = MaterialTheme.typography.titleSmall, fontSize = 12.sp
                    )
                },
                singleLine = true,
                value = folderNameTextFieldValue.value,
                onValueChange = {
                    folderNameTextFieldValue.value = it
                })
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                textStyle = gspTextStyle,
                readOnly = isFolderCreationInProgress.value, modifier = Modifier.fillMaxWidth(), label = {
                    Text(
                        text = "Note for creating the folder",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 12.sp
                    )
                }, value = noteTextFieldValue.value, onValueChange = {
                    noteTextFieldValue.value = it
                })
            if (isFolderCreationInProgress.value) {
                Spacer(modifier = Modifier.height(40.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }, title = {
        Text(
            text = if (currentFolderId != null) "Create a new folder in \"$currentFolderName\""
            else "Create a new root folder",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 22.sp,
            lineHeight = 28.sp
        )
    })
}