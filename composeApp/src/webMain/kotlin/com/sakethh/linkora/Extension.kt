package com.sakethh.linkora

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sakethh.linkora.components.AddANewFolderDialogBox
import com.sakethh.linkora.components.CreateATagBtmSheet
import com.sakethh.linkora.components.FolderSelectorComponent
import com.sakethh.linkora.components.SelectableFolderUIComponent
import com.sakethh.linkora.theme.LinkoraTheme
import com.sakethh.linkora.theme.gspTextStyle
import kotlinx.browser.window
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Extension() {

    if (ExtensionVM.showExtensionSettings) {
        Settings()
        return
    }

    /*
    https://stackoverflow.com/a/79577354
    https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html#using-viewmodel-in-common-code
    > On non-JVM platforms, objects cannot be instantiated using type reflection.
    So in common code you cannot call the viewModel() function without parameters:
    every time a ViewModel instance is created, you need to provide at least an initializer as an argument.
    * */
    val extensionVM = remember {
        ExtensionVM()
    }

    val selectedTags = extensionVM.selectedTags
    val tagsState by extensionVM.tagsState.collectAsStateWithLifecycle()
    val rootFoldersState by extensionVM.rootFoldersState.collectAsStateWithLifecycle()
    var showBtmSheetForNewTagAddition by mutableStateOf(false)
    var showFolderCreationDialogBox by mutableStateOf(false)
    var selectedFolderId by rememberSaveable {
        mutableLongStateOf(Constants.SAVED_LINKS_ID)
    }
    var selectedFolderName by mutableStateOf("Saved Links")
    var showTags by rememberSaveable {
        mutableStateOf(false)
    }
    var linkNote by rememberSaveable {
        mutableStateOf("")
    }

    val addLinkState by extensionVM.addLinkState.collectAsStateWithLifecycle()

    var isDropDownMenuIconClicked by rememberSaveable {
        mutableStateOf(false)
    }
    val createTagBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val childFoldersState by extensionVM.childFoldersState.collectAsStateWithLifecycle()
    var addTheFolderInRoot by rememberSaveable {
        mutableStateOf(false)
    }
    var showChildFoldersBtmSheet by rememberSaveable {
        mutableStateOf(false)
    }
    val childFoldersBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lazyRowState = rememberLazyListState()
    var showFolderSearchBtmSheet by rememberSaveable {
        mutableStateOf(false)
    }
    val folderSearchBtmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val foldersSearchQueryState by extensionVM.foldersSearchQueryResult.collectAsStateWithLifecycle()
    val foldersSearchTextFieldFocusRequester = remember {
        FocusRequester()
    }
    var linkAddress by rememberSaveable {
        mutableStateOf("")
    }
    var linkTitle: String by rememberSaveable {
        mutableStateOf("")
    }
    LinkoraTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).safeContentPadding().fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                ),
        ) {
            Row(
                modifier = Modifier.padding(top = 15.dp, bottom = 15.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier.pointerHoverIcon(PointerIcon.Hand), onClick = {
                    ExtensionVM.showSettings()
                }) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onSurface,
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
                Text(
                    text = "Add A New Link",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            OutlinedTextField(
                textStyle = gspTextStyle,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Text).fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp), value = linkAddress, onValueChange = {
                    linkAddress = it
                }, label = {
                    Text(text = "Link Address", style = MaterialTheme.typography.titleSmall)
                })

            OutlinedTextField(
                textStyle = gspTextStyle,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Text).fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp, top = 7.5.dp),
                value = linkTitle,
                onValueChange = {
                    linkTitle = it
                },
                label = {
                    Text(text = "Title for the link", style = MaterialTheme.typography.titleSmall)
                }, supportingText = {
                    Text(
                        text = "Leave blank to automatically retrieve the title while saving",
                        style = MaterialTheme.typography.titleSmall
                    )
                })

            OutlinedTextField(
                textStyle = gspTextStyle,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Text).fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, bottom = 7.5.dp), value = linkNote, onValueChange = {
                    linkNote = it
                }, label = {
                    Text(text = "Note for saving the link", style = MaterialTheme.typography.titleSmall)
                })
            if (tagsState.isLoading || rootFoldersState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(15.dp)
                )
                return@Column
            }
            val (isError, errorMessage) = when {
                tagsState.isError -> true to tagsState.errorMessage
                rootFoldersState.isError -> true to rootFoldersState.errorMessage
                addLinkState.isError -> true to addLinkState.errorMessage
                else -> false to ""
            }
            if (isError) {
                SelectionContainer {
                    Text(
                        text = errorMessage, modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleLarge
                    )
                }
                return@Column
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth().clickable(onClick = {
                    showTags = !showTags
                }, indication = null, interactionSource = null)
            ) {

                IconButton(
                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).padding(
                        start = 5.dp
                    ), onClick = {
                        showTags = !showTags
                    }) {
                    Icon(
                        imageVector = if (showTags) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = "Attach Tags",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 18.sp
                )
            }
            Box(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                if (showTags) {
                    TagSelectionComponent(allTags = tagsState.items, selectedTags = selectedTags, onTagClick = {
                        extensionVM.onTagClick(it)
                    }, onNewIconBtnClick = {
                        showBtmSheetForNewTagAddition = true
                    })
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).padding(
                        start = 5.dp
                    ), onClick = {
                        showFolderSearchBtmSheet = true
                    }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                Text(
                    text = "Add in",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 18.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp)
            ) {
                FilledTonalButton(
                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).pressScaleEffect().fillMaxWidth(0.8f),
                    onClick = {
                        if (!addLinkState.isLoading) {
                            isDropDownMenuIconClicked = !isDropDownMenuIconClicked
                            extensionVM.subFoldersList.clear()
                        }
                    }) {
                    Text(
                        text = selectedFolderName,
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 18.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                FilledTonalIconButton(
                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).pressScaleEffect(
                        0.75f
                    ), onClick = {
                        if (!addLinkState.isLoading) {
                            isDropDownMenuIconClicked = !isDropDownMenuIconClicked
                            extensionVM.subFoldersList.clear()
                        }
                    }) {
                    Icon(
                        imageVector = if (isDropDownMenuIconClicked) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
            Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                if (isDropDownMenuIconClicked) {
                    SelectableFolderUIComponent(
                        onClick = {
                            isDropDownMenuIconClicked = false
                            selectedFolderId = Constants.SAVED_LINKS_ID
                            selectedFolderName = "Saved Links"
                        },
                        folderName = "Saved Links",
                        imageVector = Icons.Outlined.Link,
                        isComponentSelected = selectedFolderId == Constants.SAVED_LINKS_ID
                    )

                    SelectableFolderUIComponent(
                        onClick = {
                            isDropDownMenuIconClicked = false
                            selectedFolderId = Constants.IMPORTANT_LINKS_ID
                            selectedFolderName = "Important Links"
                        },
                        folderName = "Important Links",
                        imageVector = Icons.Outlined.StarOutline,
                        isComponentSelected = selectedFolderId == Constants.IMPORTANT_LINKS_ID
                    )

                    rootFoldersState.items.forEach {
                        key(it) {
                            FolderSelectorComponent(onItemClick = {
                                isDropDownMenuIconClicked = false
                                selectedFolderName = it.name
                                selectedFolderId = it.id
                            }, isCurrentFolderSelected = rememberSaveable(selectedFolderId == it.id) {
                                mutableStateOf(selectedFolderId == it.id)
                            }, folderName = it.name, onSubDirectoryIconClick = {
                                extensionVM.loadChildFolders(
                                    it.id
                                )
                                extensionVM.subFoldersList.add(it)
                                showChildFoldersBtmSheet = true
                                coroutineScope.launch {
                                    childFoldersBtmSheetState.expand()
                                    try {
                                        if (lazyRowState.layoutInfo.totalItemsCount - 1 < 0) return@launch
                                        lazyRowState.animateScrollToItem(lazyRowState.layoutInfo.totalItemsCount - 1)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                selectedFolderId = it.id
                                selectedFolderName = it.name
                            })
                        }
                    }
                }
            }
            FilledTonalButton(
                modifier = Modifier.animateContentSize().pointerHoverIcon(icon = PointerIcon.Hand).padding(
                    end = 20.dp, top = if (isDropDownMenuIconClicked) 20.dp else 5.dp, start = 20.dp
                ).fillMaxWidth().pressScaleEffect(), onClick = {
                    showFolderCreationDialogBox = true
                    addTheFolderInRoot = true
                }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CreateNewFolder, null)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Create A New Folder", style = MaterialTheme.typography.titleSmall, fontSize = 16.sp
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 7.5.dp, bottom = 7.5.dp))

            if (!addLinkState.isLoading) {
                OutlinedButton(
                    modifier = Modifier.pressScaleEffect().pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp), onClick = {
                        window.close()
                    }) {
                    Text(text = "Cancel", style = MaterialTheme.typography.titleSmall)
                }
                Button(
                    onClick = {
                        extensionVM.saveLink(
                            linkAddress = linkAddress,
                            linkTitle = linkTitle.ifBlank { null },
                            linkNote = linkNote,
                            parentFolderId = if (addTheFolderInRoot) null else selectedFolderId,
                            onCompletion = {
                                window.close()
                            },
                            selectedTags = extensionVM.selectedTags.map { it.id })
                    },
                    modifier = Modifier.pressScaleEffect().pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)
                ) {
                    Text(text = "Save", style = MaterialTheme.typography.titleSmall)
                }
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp))
            }

            Spacer(modifier = Modifier.height(50.dp))
            val hideTagCreationBtmSheet: () -> Unit = {
                coroutineScope.launch {
                    createTagBtmSheetState.hide()
                }.invokeOnCompletion {
                    showBtmSheetForNewTagAddition = false
                }
            }
            if (showBtmSheetForNewTagAddition) {
                CreateATagBtmSheet(
                    sheetState = createTagBtmSheetState, onCancel = hideTagCreationBtmSheet, onCreateClick = {
                        extensionVM.createTag(name = it, onCompletion = hideTagCreationBtmSheet)
                    })
            }
            val parentFolderId = remember(selectedFolderId, addTheFolderInRoot) {
                if (selectedFolderId > 0 && !addTheFolderInRoot) selectedFolderId else null
            }
            if (showFolderCreationDialogBox) {
                AddANewFolderDialogBox(
                    onDismiss = { showFolderCreationDialogBox = false },
                    onFolderCreateClick = { folderName: String, folderNote: String, onCompletion: () -> Unit ->
                        extensionVM.createFolder(
                            name = folderName,
                            note = folderNote,
                            parentFolderId = parentFolderId,
                            onCompletion = onCompletion
                        )
                    },
                    currentFolderId = parentFolderId,
                    currentFolderName = selectedFolderName
                )
            }

            if (showChildFoldersBtmSheet) {
                ModalBottomSheet(sheetState = childFoldersBtmSheetState, onDismissRequest = {
                    extensionVM.subFoldersList.clear()
                    showChildFoldersBtmSheet = false
                }) {
                    LazyColumn(
                        Modifier.fillMaxWidth().wrapContentHeight()
                    ) {
                        stickyHeader {
                            Column(
                                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                            ) {
                                TopAppBar(title = {
                                    Text(
                                        text = extensionVM.subFoldersList.last().name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 24.sp
                                    )
                                })
                                LazyRow(
                                    state = lazyRowState, modifier = Modifier.padding(
                                        start = 15.dp, end = 15.dp, bottom = 15.dp
                                    ), verticalAlignment = Alignment.CenterVertically
                                ) {
                                    item {
                                        Text(
                                            text = "/", style = MaterialTheme.typography.titleMedium, fontSize = 16.sp
                                        )
                                    }
                                    itemsIndexed(extensionVM.subFoldersList) { index, subFolder ->
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowRight, contentDescription = ""
                                        )
                                        Text(
                                            text = subFolder.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 16.sp,
                                            modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).clickable {
                                                if (childFoldersState.isLoading) return@clickable

                                                extensionVM.loadChildFolders(
                                                    subFolder.id,
                                                )
                                                selectedFolderId = subFolder.id
                                                selectedFolderName = subFolder.name
                                                if (extensionVM.subFoldersList.indexOf(
                                                        subFolder
                                                    ) != extensionVM.subFoldersList.indexOf(
                                                        extensionVM.subFoldersList.last()
                                                    )
                                                ) {
                                                    extensionVM.subFoldersList.removeAll(
                                                        extensionVM.subFoldersList.toList().subList(
                                                            fromIndex = extensionVM.subFoldersList.indexOf(
                                                                extensionVM.subFoldersList.find {
                                                                    it.id == selectedFolderId
                                                                }) + 1, toIndex = extensionVM.subFoldersList.size
                                                        ).toSet()
                                                    )
                                                }
                                            })
                                    }
                                }
                                HorizontalDivider(color = LocalContentColor.current.copy(0.25f))
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                        if (childFoldersState.isError) {
                            item {
                                SelectionContainer {
                                    Text(
                                        text = childFoldersState.errorMessage,
                                        modifier = Modifier.padding(15.dp),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                            return@LazyColumn
                        }
                        if (childFoldersState.isLoading) {
                            item {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(15.dp))
                            }
                            return@LazyColumn
                        }
                        if (childFoldersState.items.isNotEmpty()) {
                            items(childFoldersState.items) {
                                FolderSelectorComponent(onItemClick = {
                                    selectedFolderId = it.id
                                    selectedFolderName = it.name
                                    isDropDownMenuIconClicked = false
                                    extensionVM.subFoldersList.clear()
                                    coroutineScope.launch {
                                        childFoldersBtmSheetState.hide()
                                    }
                                    showChildFoldersBtmSheet = false
                                }, isCurrentFolderSelected = rememberSaveable(it.id == selectedFolderId) {
                                    mutableStateOf(it.id == selectedFolderId)
                                }, folderName = it.name, onSubDirectoryIconClick = {
                                    selectedFolderId = it.id
                                    selectedFolderName = it.name
                                    extensionVM.subFoldersList.add(it)
                                    extensionVM.loadChildFolders(
                                        selectedFolderId,
                                    )
                                    coroutineScope.launch {
                                        try {
                                            if (lazyRowState.layoutInfo.totalItemsCount - 1 < 0) return@launch
                                            lazyRowState.animateScrollToItem(lazyRowState.layoutInfo.totalItemsCount - 1)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                })
                            }
                        } else {
                            item {
                                Text(
                                    text = "This folder has no subfolders.",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    lineHeight = 36.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxSize().padding(15.dp)
                                )
                            }
                            item {
                                FilledTonalButton(
                                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).padding(
                                        top = 5.dp, end = 15.dp, start = 15.dp
                                    ).fillMaxWidth().pressScaleEffect(), onClick = {
                                        addTheFolderInRoot = false
                                        showFolderCreationDialogBox = true
                                    }) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CreateNewFolder, null)
                                        Spacer(Modifier.width(5.dp))
                                        Text(
                                            text = "Create a New Folder",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Button(
                                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).fillMaxWidth()
                                        .padding(start = 15.dp, end = 15.dp), onClick = {
                                        isDropDownMenuIconClicked = false
                                        extensionVM.subFoldersList.clear()
                                        coroutineScope.launch {
                                            childFoldersBtmSheetState.hide()
                                        }
                                        showChildFoldersBtmSheet = false
                                    }) {
                                    Text(
                                        text = "Save in this folder", style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                            item {
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                        if (childFoldersState.items.isNotEmpty()) {
                            item {
                                FilledTonalButton(
                                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand).padding(
                                        end = 20.dp, top = 15.dp, start = 20.dp, bottom = 15.dp
                                    ).fillMaxWidth().pressScaleEffect(), onClick = {
                                        addTheFolderInRoot = false
                                        showFolderCreationDialogBox = true
                                    }) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CreateNewFolder, null)
                                        Spacer(Modifier.width(5.dp))
                                        Text(
                                            text = "Create A New Folder",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            val hideSearchBtmSheet: () -> Unit = {
                coroutineScope.launch {
                    folderSearchBtmSheetState.hide()
                }.invokeOnCompletion {
                    showFolderSearchBtmSheet = false
                }
            }
            if (showFolderSearchBtmSheet) {
                ModalBottomSheet(
                    sheetState = folderSearchBtmSheetState, onDismissRequest = hideSearchBtmSheet
                ) {
                    Scaffold(bottomBar = {
                        OutlinedTextField(
                            textStyle = gspTextStyle,
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            },
                            value = extensionVM.foldersSearchQuery,
                            onValueChange = {
                                extensionVM.foldersSearchQuery = it
                            },
                            shape = RoundedCornerShape(25.dp),
                            label = {
                                Text(
                                    text = "Folder name",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Search for Folders...",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            },
                            modifier = Modifier.fillMaxWidth().padding(15.dp)
                                .focusRequester(foldersSearchTextFieldFocusRequester)
                        )
                        LaunchedEffect(Unit) {
                            foldersSearchTextFieldFocusRequester.requestFocus()
                        }
                    }) { paddingValues ->
                        LazyColumn(
                            Modifier.padding(paddingValues).fillMaxWidth().wrapContentHeight()
                        ) {
                            stickyHeader {
                                Text(
                                    text = "Search for Folders",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 24.sp,
                                    modifier = Modifier.background(BottomSheetDefaults.ContainerColor).padding(15.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (foldersSearchQueryState.isLoading) {
                                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                }
                            }
                            if (foldersSearchQueryState.isError) {
                                item {
                                    SelectionContainer {
                                        Text(
                                            text = foldersSearchQueryState.errorMessage,
                                            modifier = Modifier.padding(15.dp),
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                }
                                return@LazyColumn
                            }
                            if (foldersSearchQueryState.items.isEmpty()) {
                                item {
                                    Text(
                                        text = "No folders found.",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 24.sp,
                                        lineHeight = 36.sp,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxSize().padding(15.dp)
                                    )
                                }
                            } else {
                                items(items = foldersSearchQueryState.items) {
                                    FolderSelectorComponent(
                                        onItemClick = {
                                            hideSearchBtmSheet()
                                            selectedFolderId = it.id
                                            selectedFolderName = it.name
                                        }, isCurrentFolderSelected = rememberSaveable(it.id == selectedFolderId) {
                                            mutableStateOf(it.id == selectedFolderId)
                                        }, folderName = it.name, onSubDirectoryIconClick = null
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
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
private fun TagSelectionComponent(
    paddingValues: PaddingValues = PaddingValues(start = 15.dp, end = 25.dp),
    allTags: List<Tag>,
    selectedTags: List<Tag>,
    onTagClick: (tag: Tag) -> Unit,
    onNewIconBtnClick: () -> Unit
) {
    FlowRow(
        modifier = Modifier.padding(
            paddingValues
        ).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IconButton(modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand), onClick = {
            onNewIconBtnClick()
        }) {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.Default.AddCircle,
                contentDescription = null
            )
        }
        allTags.forEach {
            val isTagSelected by rememberSaveable(selectedTags.contains(it)) {
                mutableStateOf(selectedTags.contains(it))
            }
            key("TAG_ID_${it.id}") {
                AssistChip(
                    colors = AssistChipDefaults.assistChipColors(containerColor = if (isTagSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = if (isTagSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onSecondary
                    ),
                    onClick = {
                        onTagClick(it)
                    },
                    label = {
                        Text(
                            text = it.name,
                            color = if (isTagSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isTagSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            tint = if (isTagSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.secondary,
                            imageVector = Icons.Default.Tag,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.pointerHoverIcon(icon = PointerIcon.Hand)
                )
            }
        }
    }
}