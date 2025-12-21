package com.sakethh.linkora

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.localStorage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(FlowPreview::class)
class ExtensionVM : ViewModel() {

    companion object {
        const val VERSION = "0.2.0"
        var showExtensionSettings by mutableStateOf(false)
            private set

        fun showSettings() {
            showExtensionSettings = true
        }

        fun showMainScreen() {
            showExtensionSettings = false
        }

        fun getServerDetails() = ServerDetails(
            address = localStorage["linkoraServerUrl"], authToken = localStorage["linkoraAuthToken"]
        )

        val debouncingThreshold get() = (localStorage["debouncingThreshold"])?.toLong() ?: 500L
        val userAgent get() = (localStorage["userAgent"]) ?: "Twitterbot/1.0"

        fun updateDebouncingThreshold(value: Long) {
            localStorage["debouncingThreshold"] = value.toString()
        }

        fun updateUserAgent(value: String) {
            localStorage["userAgent"] = value
        }

        fun updateServerDetails(serverDetails: ServerDetails) {
            if (serverDetails.address != null && serverDetails.authToken != null) {
                localStorage.set(key = "linkoraServerUrl", value = serverDetails.address)
                localStorage.set(key = "linkoraAuthToken", value = serverDetails.authToken)
            }
        }
    }

    val selectedTags: List<Tag>
        field = mutableStateListOf<Tag>()

    val subFoldersList = mutableStateListOf<Folder>()


    private var changeParentFolderIdJob: Job? = null

    val childFoldersState: StateFlow<ItemsState<Folder>>
        field = MutableStateFlow(
            ItemsState<Folder>(
                items = emptyList(),
                isLoading = true,
                isError = false,
                errorMessage = ""
            )
        )


    fun loadChildFolders(parentFolderId: Long) {
        changeParentFolderIdJob?.cancel()
        changeParentFolderIdJob = viewModelScope.launch {
            postToServer(
                route = Route.GET_CHILD_FOLDERS,
                body = IDBasedDTO(id = parentFolderId, eventTimestamp = getSystemEpochSeconds()),
                onStart = {
                    childFoldersState.emit(
                        ItemsState(
                            items = emptyList(), isLoading = true, isError = false, errorMessage = ""
                        )
                    )
                },
                onSuccess = { response ->
                    val childFolders = response.body<List<Folder>>()
                    childFoldersState.emit(
                        ItemsState(
                            items = childFolders, isLoading = false, isError = false, errorMessage = ""
                        )
                    )
                },
                onFailure = {
                    childFoldersState.emit(
                        ItemsState(
                            items = emptyList(), isLoading = false, isError = true, errorMessage = it
                        )
                    )
                })
        }
    }

    var foldersSearchQuery by mutableStateOf("")
    val foldersSearchQueryResult: StateFlow<ItemsState<Folder>>
        field = MutableStateFlow(
            ItemsState<Folder>(
                items = emptyList(), isLoading = false, isError = false, errorMessage = ""
            )
        )

    fun onTagClick(tag: Tag) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag)
        } else {
            selectedTags.add(tag)
        }
    }

    val tagsState: StateFlow<ItemsState<Tag>>
        field = MutableStateFlow(
            ItemsState<Tag>(
                items = emptyList(),
                isLoading = true,
                isError = false, errorMessage = ""
            )
        )

    val rootFoldersState: StateFlow<ItemsState<Folder>>
        field = MutableStateFlow(
            ItemsState<Folder>(
                items = emptyList(), isLoading = true, isError = false, errorMessage = ""
            )
        )

    var createTagState by mutableStateOf(CreateAnItemState(isSuccess = false, isInProcess = false))

    var createFolderState by mutableStateOf(CreateAnItemState(isSuccess = false, isInProcess = false))


    private val ktorClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
    }

    init {
        loadTags()
        loadRootFolders()

        viewModelScope.launch {
            snapshotFlow {
                foldersSearchQuery
            }.debounce(debouncingThreshold).collectLatest { query ->
                if (query.isBlank()) {
                    foldersSearchQueryResult.value =
                        ItemsState(emptyList(), isLoading = false, isError = false, errorMessage = "")

                    return@collectLatest
                }

                postToServer(body = query, route = Route.SEARCH_FOR_FOLDERS, onFailure = {
                    foldersSearchQueryResult.emit(
                        ItemsState<Folder>(
                            items = emptyList(), isLoading = false, isError = true, errorMessage = it
                        )
                    )
                }, onSuccess = {
                    foldersSearchQueryResult.emit(
                        ItemsState(
                            items = it.body(), isLoading = false, isError = false, errorMessage = ""
                        )
                    )
                }, onStart = {
                    foldersSearchQueryResult.emit(
                        ItemsState(
                            items = emptyList(), isLoading = true, isError = false, errorMessage = ""
                        )
                    )
                })
            }
        }
    }

    val addLinkState: StateFlow<ItemsState<Unit>>
        field = MutableStateFlow(
            ItemsState<Unit>(
                items = emptyList(),
                isLoading = false,
                isError = false,
                errorMessage = ""
            )
        )

    fun saveLink(
        linkAddress: String,
        linkTitle: String?,
        linkNote: String,
        selectedTags: List<Long>,
        parentFolderId: Long?,
        onCompletion: () -> Unit
    ) {
        val eventTimestamp = getSystemEpochSeconds()
        val host = linkAddress.host(throwOnException = false)
        viewModelScope.launch {
            addLinkState.emit(
                ItemsState(
                    items = emptyList(),
                    isLoading = true,
                    isError = false,
                    errorMessage = ""
                )
            )

            postToServer(
                onStart = {

                }, onFailure = {
                    addLinkState.emit(
                        ItemsState(
                            items = emptyList(),
                            isLoading = false,
                            isError = true,
                            errorMessage = it
                        )
                    )
                    onCompletion()
                }, route = Route.CREATE_A_NEW_LINK, onSuccess = {
                    addLinkState.emit(
                        ItemsState(
                            items = emptyList(),
                            isLoading = false,
                            isError = false,
                            errorMessage = ""
                        )
                    )
                    onCompletion()
                }, body = AddLinkDTO(
                    linkType = when (parentFolderId) {
                        Constants.SAVED_LINKS_ID -> LinkType.SAVED_LINK
                        Constants.IMPORTANT_LINKS_ID -> LinkType.IMPORTANT_LINK
                        else -> LinkType.FOLDER_LINK
                    },
                    title = linkTitle,
                    url = linkAddress,
                    baseURL = host,
                    imgURL = "",
                    note = linkNote,
                    idOfLinkedFolder = parentFolderId,
                    userAgent = userAgent,
                    markedAsImportant = false,
                    mediaType = MediaType.IMAGE,
                    correlation = currentCorrelation,
                    eventTimestamp = eventTimestamp,
                    tags = selectedTags, forceRetrieveOGMetaInfo = true
                )
            )
        }
    }

    fun loadTags() {
        viewModelScope.launch {
            getFromServer(
                route = Route.GET_TAGS,
                onStart = {
                    tagsState.emit(
                        ItemsState(
                            items = emptyList(), isLoading = true, isError = false, errorMessage = ""
                        )
                    )
                },
                onSuccess = { response ->
                    val tags = response.body<List<Tag>>()
                    tagsState.emit(
                        ItemsState(
                            items = tags, isLoading = false, isError = false, errorMessage = ""
                        )
                    )
                },
                onFailure = { e ->
                    tagsState.emit(
                        ItemsState(
                            items = emptyList(), isLoading = false, isError = true, errorMessage = e
                        )
                    )
                },
            )
        }
    }

    fun loadRootFolders() {
        viewModelScope.launch {
            getFromServer(route = Route.GET_ROOT_FOLDERS, onStart = {
                rootFoldersState.emit(
                    ItemsState(
                        items = emptyList(), isLoading = true, isError = false, errorMessage = ""
                    )
                )
            }, onSuccess = { response ->
                val rootFolders = response.body<List<Folder>>()
                rootFoldersState.emit(
                    ItemsState(
                        items = rootFolders, isLoading = false, isError = false, errorMessage = ""
                    )
                )
            }, onFailure = { e ->
                rootFoldersState.emit(
                    ItemsState(
                        items = emptyList(), isLoading = false, isError = true, errorMessage = e
                    )
                )
            })
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    fun createTag(name: String, onCompletion: () -> Unit) {
        val createTagDTO = CreateTagDTO(
            name = name,
            eventTimestamp = getSystemEpochSeconds(),
        )
        viewModelScope.launch {
            postToServer(body = createTagDTO, route = Route.CREATE_TAG, onStart = {
                createTagState = CreateAnItemState(isInProcess = true, isSuccess = false)
            }, onSuccess = {
                onCompletion()
                loadTags()
            }, onFailure = {
                onCompletion()
                tagsState.emit(
                    ItemsState(
                        items = emptyList(), isLoading = false, isError = true, errorMessage = it
                    )
                )
            })
        }
    }

    fun createFolder(
        name: String, note: String, parentFolderId: Long?, onCompletion: () -> Unit
    ) {
        val addFolderDTO = AddFolderDTO(
            name = name,
            note = note,
            parentFolderId = parentFolderId,
            isArchived = false,
            eventTimestamp = getSystemEpochSeconds(),
        )
        viewModelScope.launch {
            postToServer(body = addFolderDTO, route = Route.CREATE_FOLDER, onStart = {
                createFolderState = CreateAnItemState(isInProcess = true, isSuccess = false)
            }, onSuccess = {
                createFolderState = CreateAnItemState(isInProcess = false, isSuccess = true)
                if (parentFolderId != null && parentFolderId > 0) {
                    loadChildFolders(parentFolderId)
                } else {
                    loadRootFolders()
                }
                onCompletion()
            }, onFailure = {
                onCompletion()
            })
        }
    }

    private suspend fun postToServer(
        body: Any,
        route: Route,
        onStart: suspend () -> Unit,
        onSuccess: suspend (response: HttpResponse) -> Unit,
        onFailure: suspend (msg: String) -> Unit
    ) {
        try {
            onStart()
            val serverDetails = getServerDetails()
            require(serverDetails.authToken != null && serverDetails.address != null) {
                "serverDetails.authToken and serverDetails.address are null."
            }
            val postResponse = ktorClient.post(urlString = serverDetails.address.run {
                if (endsWith("/")) this else "$this/"
            } + route.name) {
                bearerAuth(serverDetails.authToken)
                contentType(ContentType.Application.Json)
                setBody(body = body)
            }
            if (postResponse.status.isSuccess()) onSuccess(postResponse) else onFailure(postResponse.bodyAsText())
        } catch (e: Exception) {
            onFailure(e.stackTraceToString())
        }
    }

    private suspend fun getFromServer(
        route: Route,
        onStart: suspend () -> Unit,
        onSuccess: suspend (HttpResponse) -> Unit,
        onFailure: suspend (msg: String) -> Unit
    ) {
        try {
            onStart()
            val serverDetails = getServerDetails()
            require(serverDetails.authToken != null && serverDetails.address != null) {
                "serverDetails.authToken and serverDetails.address are null."
            }
            val getResponse = ktorClient.get(urlString = serverDetails.address.run {
                if (endsWith("/")) this else "$this/"
            } + route.name) {
                bearerAuth(serverDetails.authToken)
            }
            if (getResponse.status.isSuccess()) onSuccess(getResponse) else onFailure(getResponse.bodyAsText())
        } catch (e: Exception) {
            onFailure(e.stackTraceToString())
        }
    }

    override fun onCleared() {
        super.onCleared()
        ktorClient.close()
    }
}

fun String.host(throwOnException: Boolean = true): String {
    return try {
        this.split("/")[2]
    } catch (e: Exception) {
        if (throwOnException) {
            throw e
        }
        this
    }
}

data class ServerDetails(val address: String?, val authToken: String?)

@OptIn(ExperimentalTime::class)
fun getSystemEpochSeconds() = Clock.System.now().epochSeconds

@OptIn(ExperimentalUuidApi::class)
val currentCorrelation = Correlation(id = Uuid.random().toString(), clientName = "LINKORA_BROWSER_EXTENSION")

@Serializable
data class IDBasedDTO(
    val id: Long, val eventTimestamp: Long, val correlation: Correlation = currentCorrelation
)

@Serializable
data class Folder(
    val id: Long,
    val name: String,
    val note: String,
    val parentFolderId: Long?,
    val isArchived: Boolean,
    val eventTimestamp: Long
)

@Serializable
data class AddFolderDTO(
    val name: String,
    val note: String,
    val parentFolderId: Long?,
    val isArchived: Boolean,
    val eventTimestamp: Long,
    val correlation: Correlation = currentCorrelation
)

@Serializable
data class CreateTagDTO(
    val name: String, val eventTimestamp: Long, val correlation: Correlation = currentCorrelation
)

@Serializable
data class Correlation(
    val id: String, val clientName: String
)

enum class Route {
    GET_TAGS, CREATE_TAG, CREATE_A_NEW_LINK, CREATE_FOLDER, GET_ROOT_FOLDERS, GET_CHILD_FOLDERS, SEARCH_FOR_FOLDERS
}

data class ItemsState<T>(
    val items: List<T>, val isLoading: Boolean, val isError: Boolean, val errorMessage: String
)

data class CreateAnItemState(
    val isSuccess: Boolean, val isInProcess: Boolean
)

@Serializable
data class AddLinkDTO(
    val linkType: LinkType,
    val title: String?,
    val url: String,
    val baseURL: String,
    val imgURL: String,
    val note: String,
    val idOfLinkedFolder: Long?,
    val userAgent: String?,
    val markedAsImportant: Boolean,
    val mediaType: MediaType,
    val correlation: Correlation,
    val eventTimestamp: Long,
    val tags: List<Long>,
    val forceRetrieveOGMetaInfo: Boolean
)

enum class LinkType {
    SAVED_LINK, FOLDER_LINK, IMPORTANT_LINK
}

enum class MediaType {
    IMAGE, GIF, VIDEO
}

@Serializable
data class Tag(
    val id: Long, val name: String, val eventTimestamp: Long
)