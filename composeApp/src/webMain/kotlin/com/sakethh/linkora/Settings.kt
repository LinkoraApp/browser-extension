package com.sakethh.linkora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakethh.linkora.theme.LinkoraTheme
import com.sakethh.linkora.theme.gspTextStyle
import linkorabrowserextension.composeapp.generated.resources.Res
import linkorabrowserextension.composeapp.generated.resources.discord
import linkorabrowserextension.composeapp.generated.resources.github
import linkorabrowserextension.composeapp.generated.resources.twitter
import org.jetbrains.compose.resources.painterResource

@Composable
fun Settings() {
    val serverDetails = remember {
        ExtensionVM.getServerDetails()
    }
    var debouncingThreshold by remember {
        mutableStateOf(ExtensionVM.debouncingThreshold.toString())
    }
    var userAgent by remember {
        mutableStateOf(ExtensionVM.userAgent)
    }
    var serverAddress by remember {
        mutableStateOf(serverDetails.address ?: "")
    }
    var serverKey by remember {
        mutableStateOf(serverDetails.authToken ?: "")
    }
    val uriHandler = LocalUriHandler.current
    LinkoraTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).safeContentPadding().fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),
        ) {
            Row(
                modifier = Modifier.padding(top = 15.dp, bottom = 7.5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier.pointerHoverIcon(PointerIcon.Hand), onClick = {
                    ExtensionVM.showMainScreen()
                }) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSurface,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Column(
                modifier = Modifier.padding(
                    start = 15.dp, end = 15.dp, top = 7.5.dp, bottom = 7.5.dp
                ).clip(
                    RoundedCornerShape(15.dp)
                ).fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer).padding(top = 7.5.dp)
            ) {
                Row(modifier = Modifier.padding(start = 15.dp, top = 15.dp)) {
                    Text(
                        text = "Linkora",
                        modifier = Modifier.alignByBaseline(),
                        fontSize = 18.sp, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text = "v${ExtensionVM.VERSION}",
                        modifier = Modifier.alignByBaseline(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 12.sp, style = MaterialTheme.typography.titleSmall
                    )
                }
                Row(
                    modifier = Modifier.padding(start = 10.dp, top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).pressScaleEffect(), onClick = {
                            uriHandler.openUri("https://www.github.com/LinkoraApp")
                        }) {
                        Icon(
                            painter = painterResource(Res.drawable.github),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    FilledIconButton(
                        modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).pressScaleEffect(), onClick = {
                            uriHandler.openUri("https://discord.gg/ZDBXNtv8MD")
                        }) {
                        Icon(
                            painter = painterResource(Res.drawable.discord),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    FilledIconButton(
                        modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).pressScaleEffect(), onClick = {
                            uriHandler.openUri("https://www.twitter.com/LinkoraApp")
                        }) {
                        Icon(
                            painter = painterResource(Res.drawable.twitter),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 7.5.dp, bottom = 7.5.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.25f),
                    thickness = 0.25.dp,
                )
                Button(
                    onClick = {
                        uriHandler.openUri("https://ko-fi.com/sakethpathike")
                    },
                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).padding(start = 15.dp, bottom = 15.dp)
                        .pressScaleEffect()
                ) {
                    Icon(imageVector = Icons.Default.Coffee, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.5.dp))
                    Text(
                        text = "Buy me a Coffee", style = MaterialTheme.typography.titleMedium, fontSize = 16.5.sp
                    )
                }
            }
            OutlinedTextField(
                textStyle = gspTextStyle,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Text).fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp), value = serverAddress, onValueChange = {
                serverAddress = it
            }, label = {
                Text(text = "Server Address", style = MaterialTheme.typography.titleSmall)
            })
            OutlinedTextField(
                textStyle = gspTextStyle,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Text).fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp), value = serverKey, onValueChange = {
                serverKey = it
            }, label = {
                Text(text = "Server Auth token", style = MaterialTheme.typography.titleSmall)
            })
            OutlinedTextField(
                textStyle = gspTextStyle,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Text).fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp), value = userAgent, onValueChange = {
                userAgent = it
            }, label = {
                Text(text = "User Agent", style = MaterialTheme.typography.titleSmall)
            })
            OutlinedTextField(
                textStyle = gspTextStyle,
                supportingText = {
                    Text(text = "Only numericals are accepted in this field.", style = MaterialTheme.typography.titleSmall)
                },
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Text).fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp),
                value = debouncingThreshold,
                onValueChange = {
                    try {
                        debouncingThreshold = it.toLong().toString()
                    } catch (_: Exception) {

                    }
                },
                label = {
                    Text(text = "Debouncing Threshold for folders Search", style = MaterialTheme.typography.titleSmall)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = {
                    ExtensionVM.updateServerDetails(
                        serverDetails = ServerDetails(
                            address = serverAddress, authToken = serverKey
                        )
                    )
                    ExtensionVM.updateDebouncingThreshold(value = debouncingThreshold.toLong())
                    ExtensionVM.updateUserAgent(value = userAgent)
                    ExtensionVM.showMainScreen()
                },
                modifier = Modifier.pressScaleEffect().pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp)
            ) {
                Text(text = "Save", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}