package com.sakethh.linkora.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FolderSelectorComponent(
    onItemClick: () -> Unit,
    isCurrentFolderSelected: MutableState<Boolean>,
    folderName: String,
    onSubDirectoryIconClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier.then(
            if (isCurrentFolderSelected.value) Modifier.background(
                MaterialTheme.colorScheme.primary.copy(
                    0.25f
                )
            ) else Modifier
        ).pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth().clickable {
            onItemClick()
        }) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                tint = if (isCurrentFolderSelected.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                imageVector = Icons.Outlined.Folder,
                contentDescription = null,
                modifier = Modifier.padding(
                    start = 20.dp, end = 20.dp, top = 0.dp
                ).size(28.dp)
            )
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isCurrentFolderSelected.value) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = if (isCurrentFolderSelected.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (onSubDirectoryIconClick != null) {
                        Spacer(modifier = Modifier.width(20.dp))
                        IconButton(
                            modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand), onClick = {
                                onSubDirectoryIconClick()
                            }) {
                            Icon(
                                imageVector = Icons.Filled.SubdirectoryArrowRight, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }
        Text(
            text = folderName,
            color = if (isCurrentFolderSelected.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            maxLines = 1,
            modifier = Modifier.padding(
                start = 20.dp, end = 20.dp
            ),
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(0.1f)
        )
    }
}