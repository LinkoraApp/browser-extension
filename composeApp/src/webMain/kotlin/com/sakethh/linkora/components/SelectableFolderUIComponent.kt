package com.sakethh.linkora.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectableFolderUIComponent(
    onClick: () -> Unit,
    folderName: String,
    imageVector: ImageVector,
    isComponentSelected: Boolean,
    forBtmSheetUI: Boolean = false,
) {
    val componentSelectedState = rememberSaveable(inputs = arrayOf(isComponentSelected)) {
        mutableStateOf(isComponentSelected)
    }
    val forBtmSheetUIState = rememberSaveable(inputs = arrayOf(forBtmSheetUI)) {
        mutableStateOf(forBtmSheetUI)
    }
    Column {
        Row(
            modifier = Modifier.then(
            if (isComponentSelected) Modifier.background(
                MaterialTheme.colorScheme.primary.copy(
                    0.25f
                )
            ) else Modifier
        ).clickable {
            onClick()
        }.pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth().requiredHeight(75.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    tint = if (componentSelectedState.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(
                        start = 20.dp, bottom = 20.dp, end = 20.dp, top = if (forBtmSheetUIState.value) 0.dp else 20.dp
                    ).size(28.dp)
                )
            }
            Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.80f), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = folderName,
                    color = if (componentSelectedState.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    maxLines = if (forBtmSheetUIState.value) 6 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (componentSelectedState.value) {
                Box(
                    modifier = Modifier.requiredHeight(75.dp).fillMaxWidth(), contentAlignment = Alignment.CenterEnd
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (componentSelectedState.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 25.dp, end = 25.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.1f)
        )
    }
}