function displayMessage(type, message = '') {
    let targetElement = document.getElementById('home_page');
    if (!targetElement) {
        targetElement = document.body;
    }

    if (targetElement.id === 'home_page') {
        targetElement.style.paddingLeft = '0px';
        targetElement.style.alignItems = 'center';
        targetElement.style.justifyContent = 'center';
    }
    if (type === 'loading') {
        if (targetElement.id === 'home_page') {
            targetElement.style.paddingLeft = '25px';
            targetElement.style.alignItems = 'flex-start';
            targetElement.style.justifyContent = 'center';
        }
        targetElement.innerHTML = `
        <div class="spinner"></div>
        <div class="label">Retrieving data...</div>
        `;
    } else if (type === 'refreshing') {
        if (targetElement.id === 'home_page') {
            targetElement.style.paddingLeft = '25px';
            targetElement.style.alignItems = 'flex-start';
            targetElement.style.justifyContent = 'center';
        }
        targetElement.innerHTML = `
        <div class="spinner"></div>
        <div class="label">Data is being refreshed...</div>
        `;
    } else if (type === 'config_needed') {
        targetElement.innerHTML = `
        <div style="text-align: center; color: #E4E1E9; font-family: 'Inter'; padding: 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%;">
        <div style="font-weight: 700; font-size: 20px; margin-bottom: 10px;">Configuration Required</div>
        <div style="font-size: 14px;">${message || 'Please set up your server URL and auth token. These values are saved locally on your device and never leave it.'}</div>
        <button type="button" id="change-extension-config-action" class="config-button" style="margin-top: 20px; background-color: #1e1e2e; color: #E0E0FF;">
        <div>Change Extension Config</div>
        </button>
        </div>
        `;
        const configActionButton = document.getElementById('change-extension-config-action');
        if (configActionButton) {
            configActionButton.addEventListener('click', async () => {
                const configSet = handleConfigChange();
                if (configSet) {
                    await loadBrowserExtensionContent();
                } else {
                    displayMessage('config_needed', 'Configuration cancelled. Please try again.');
                }
            });
        }
    } else if (type === 'error') {
        targetElement.innerHTML = `
        <div style="text-align: center; color: #E4E1E9; font-family: 'Inter'; padding: 20px; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%;">
        <div style="font-weight: 700; font-size: 20px; color: #FF6B6B; margin-bottom: 10px;">Error</div>
        <div style="font-size: 14px;">${message || 'Failed to load content. Please reconfigure.'}</div>
        <button type="button" id="change-extension-config-action" class="config-button" style="margin-top: 20px; background-color: #1e1e2e; color: #E0E0FF;">
        <div>Change Extension Config</div>
        </button>
        </div>
        `;
        const configActionButton = document.getElementById('change-extension-config-action');
        if (configActionButton) {
            configActionButton.addEventListener('click', async () => {
                const configSet = handleConfigChange();
                if (configSet) {
                    await loadBrowserExtensionContent();
                } else {
                    displayMessage('config_needed', 'Configuration cancelled. Please try again.');
                }
            });
        }
    } else if (type === 'success') {
        targetElement.innerHTML = `
        <div style="text-align: center; color: #E4E1E9; font-family: 'Inter'; padding: 40px; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);">
        <div style="font-weight: 700; font-size: 24px; color: #6BFF6B; margin-bottom: 15px;">Success!</div>
        <div style="font-size: 16px; color: #B8B5C3; margin-bottom: 30px; max-width: 300px; line-height: 1.4;">${message || 'Operation completed successfully!'}</div>
        <div style="font-size: 12px; color: #7A7A8A;">Window will close automatically...</div>
        </div>
        `;
    } else if (type === 'folder_creating') {
        targetElement.innerHTML = `
        <div style="text-align: center; color: #E4E1E9; font-family: 'Inter'; padding: 40px; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);">
        <div class="spinner" style="margin-bottom: 20px;"></div>
        <div style="font-weight: 700; font-size: 24px; color: #8F8FFF; margin-bottom: 15px;">Folder is being created...</div>
        <div style="font-size: 16px; color: #B8B5C3; max-width: 300px; line-height: 1.4;">${message || 'Please wait while your new folder is being added. The data will refresh automatically.'}</div>
        </div>
        <style>
        .spinner {
            border: 4px solid rgba(255, 255, 255, 0.3);
            border-top: 4px solid #8F8FFF;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        </style>
        `;
    } else if (type === 'saving_link') {
        targetElement.innerHTML = `
        <div style="text-align: center; color: #E4E1E9; font-family: 'Inter'; padding: 40px; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);">
        <div class="spinner" style="margin-bottom: 20px;"></div>
        <div style="font-weight: 700; font-size: 24px; color: #8F8FFF; margin-bottom: 15px;">Link is being saved...</div>
        <div style="font-size: 16px; color: #B8B5C3; max-width: 300px; line-height: 1.4;">${message || 'Please wait while your link is being saved. The window will close automatically on success.'}</div>
        </div>
        <style>
        .spinner {
            border: 4px solid rgba(255, 255, 255, 0.3);
            border-top: 4px solid #8F8FFF;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        </style>
        `;
    } else if (type === 'folder_error') {
        targetElement.innerHTML = `
        <div style="text-align: center; color: #E4E1E9; font-family: 'Inter'; padding: 40px; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);">
        <div style="font-size: 48px; color: #FF6B6B; margin-bottom: 20px;">X</div>
        <div style="font-weight: 700; font-size: 24px; color: #FF6B6B; margin-bottom: 15px;">Something went wrong!</div>
        <div style="font-size: 16px; color: #B8B5C3; margin-bottom: 30px; max-width: 300px; line-height: 1.4;">${message || 'Failed to create the folder. Please try again.'}</div>
        <button type="button" id="close-extension-window" class="config-button" style="margin-top: 20px; background-color: #FF6B6B; color: #E0E0FF;">
        <div>Close</div>
        </button>
        </div>
        `;
        const closeButton = document.getElementById('close-extension-window');
        if (closeButton) {
            closeButton.addEventListener('click', () => {
                window.close();
            });
        }
    }
}

let ogImageUrl = '';
let serverUrl = localStorage.getItem('linkoraServerUrl') || '';
let serverAuthToken = localStorage.getItem('linkoraAuthToken') || '';

async function loadBrowserExtensionContent() {
    displayMessage('loading');
    if (!serverUrl) {
        displayMessage('config_needed');
        return;
    }
    try {
        console.log("Attempting to fetch from URL:", `${serverUrl}/browser-extension`);
        const fetchOptions = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        };
        if (serverAuthToken) {
            fetchOptions.headers['Authorization'] = `Bearer ${serverAuthToken}`;
            console.log("Authorization header for GET will be sent.");
        } else {
            console.log("No Authorization token found for GET, header will not be sent.");
        }
        const res = await fetch(`${serverUrl}/browser-extension`, fetchOptions);
        if (!res.ok) {
            console.error(`Network response not OK: ${res.status} ${res.statusText}`);
            if (res.status === 403) {
                displayMessage('error', 'Access Forbidden. Your authentication token might be invalid or expired. Please reconfigure.');
            } else {
                displayMessage('error', `Failed to load content: ${res.status} ${res.statusText}. Please reconfigure.`);
            }
            throw new Error('Network response error');
        }
        const html = await res.text();
        document.body.style.transition = 'opacity 0.3s';
        document.body.style.opacity = '0';
        setTimeout(() => {
            const parser = new DOMParser();
            const newDoc = parser.parseFromString(html, 'text/html');
            document.head.innerHTML = newDoc.head.innerHTML;
            document.body.innerHTML = newDoc.body.innerHTML;
            const newBodyStyles = newDoc.body.getAttribute('style');
            if (newBodyStyles) {
                document.body.setAttribute('style', newBodyStyles);
            }
            document.body.className = newDoc.body.className;
            requestAnimationFrame(() => {
                document.body.style.opacity = '1';
                setActiveTabUrlAndMeta();

                const addNewRootFolderButton = document.getElementById('add-new-root-folder');
                if (addNewRootFolderButton) {
                    addNewRootFolderButton.addEventListener('click', async () => {
                        console.log("Add New Root Folder button clicked.");
                        await createFolder("Root", null);
                    });
                }


                const foldersList = document.getElementById('folders-list');
                if (foldersList) {
                    console.log("Folders list element found. Attaching click listener.");
                    foldersList.addEventListener('click', async (event) => {
                        console.log("Click event detected on folders list.");
                        const clickedElement = event.target;
                        console.log("Clicked element:", clickedElement);

                        const closestContainer = clickedElement.closest('.folder-component, .child-folder-addition');
                        console.log("Closest container (folder-component or child-folder-addition):", closestContainer);

                        if (!closestContainer) {
                            console.error("Clicked element is NOT part of a folder component or child-folder-addition. No action taken.");
                            return;
                        }

                        let targetFolderComponent = null;
                        if (closestContainer.classList.contains('folder-component')) {
                            targetFolderComponent = closestContainer;
                        } else {
                            targetFolderComponent = closestContainer.querySelector('.folder-component');
                        }

                        if (!targetFolderComponent) {
                            console.error("Could not find a .folder-component within the clicked container. No action taken.");
                            return;
                        }
                        console.log("Resolved targetFolderComponent:", targetFolderComponent);

                        if (clickedElement.classList.contains('material-icons-outlined') &&
                            clickedElement.textContent.trim() === 'create_new_folder') {
                            console.log("Detected click on folder creation icon.");

                        const innerIdDiv = targetFolderComponent.querySelector('div[id]:not([class])');

                        if (!innerIdDiv) {
                            console.error("Could not find the inner ID div within the folder component for folder creation.");
                            return;
                        }

                        const parentFolderIdString = innerIdDiv.id;
                        const parentFolderNameElement = targetFolderComponent.querySelector('div[style*="font-size: 16px"]');
                        const parentFolderName = parentFolderNameElement ? parentFolderNameElement.textContent.trim() : 'Unknown Folder';

                        console.log(`DEBUG: Extracted Parent Folder Name: '${parentFolderName}'`);
                        console.log(`DEBUG: Extracted Parent Folder ID String (from inner ID div): '${parentFolderIdString}'`);

                        let parentFolderId = null;
                        const parsedId = parseInt(parentFolderIdString, 10);

                        if (!isNaN(parsedId) && parsedId !== -1 && parsedId !== 0) {
                            parentFolderId = parsedId;
                        }
                        console.log(`DEBUG: Parsed parentFolderId (to be sent to server): ${parentFolderId}`);

                        await createFolder(parentFolderName, parentFolderId);
                            } else {
                                console.log("Detected click on folder for saving link.");

                                const innerIdDivForLink = targetFolderComponent.querySelector('div[id]:not([class])');
                                if (!innerIdDivForLink) {
                                    console.error("Could not find the inner ID div for saving a link.");
                                    return;
                                }
                                const itemId = innerIdDivForLink.id;

                                console.log("Saving link to folder with ID:", itemId);
                                await save(itemId);
                            }
                    });
                } else {
                    console.error("Folders list element (id='folders-list') not found in the DOM.");
                }
                const configButton = document.getElementById('change-extension-config');
                if (configButton) {
                    configButton.addEventListener('click', async () => {
                        console.log("Change Extension Config button clicked.");
                        const configSet = handleConfigChange();
                        if (configSet) {
                            await loadBrowserExtensionContent();
                        }
                    });
                }
            });
        }, 300);
    } catch (error) {
        console.error('Failed to load content:', error);
        displayMessage('error', error.message || 'Failed to connect. Please check configuration.');
    }
}

loadBrowserExtensionContent();

let activeTabUrl = "No active tab URL found.";
let pageTitle = "";

async function setActiveTabUrlAndMeta() {
    const linkElem = document.getElementById("link");
    const linkTitleElem = document.getElementById("link_title");
    if (!linkElem) return;
    activeTabUrl = "No active tab URL found.";
    pageTitle = "";
    try {
        const api = typeof chrome !== "undefined" ? chrome : browser;
        if (!api || !api.tabs || !api.tabs.query) {
            linkElem.textContent = "Browser tabs API not available.";
            if (linkTitleElem) {
                linkTitleElem.value = "N/A";
            }
            return;
        }
        const tabs = await new Promise((resolve, reject) => {
            api.tabs.query({ active: true, currentWindow: true }, (tabs) => {
                if (api.runtime.lastError) {
                    reject(api.runtime.lastError);
                } else {
                    resolve(tabs);
                }
            });
        });
        if (tabs && tabs[0] && tabs[0].url) {
            activeTabUrl = tabs[0].url;
            pageTitle = tabs[0].title || '';
        }
        linkElem.textContent = activeTabUrl;
        if (activeTabUrl !== "No active tab URL found." && activeTabUrl.startsWith('http')) {
            try {
                const pageRes = await fetch(activeTabUrl);
                if (!pageRes.ok) throw new Error('Failed to fetch page content');
                const pageHtml = await pageRes.text();
                const pageParser = new DOMParser();
                const pageDoc = pageParser.parseFromString(pageHtml, 'text/html');
                const ogTitleMeta = pageDoc.querySelector('meta[property="og:title"]');
                if (ogTitleMeta && ogTitleMeta.content) {
                    pageTitle = ogTitleMeta.content;
                } else if (pageDoc.title) {
                    pageTitle = pageDoc.title;
                }
                const ogImageMeta = pageDoc.querySelector('meta[property="og:image"]');
                if (ogImageMeta && ogImageMeta.content) {
                    ogImageUrl = ogImageMeta.content;
                }
            } catch (pageFetchError) {
                console.warn("Could not fetch page content for meta tags:", pageFetchError);
            }
        }
        if (linkTitleElem) {
            linkTitleElem.value = pageTitle;
        }
        console.log("--- Link Data ---");
        console.log("Link URL:", activeTabUrl);
        console.log("Link Title:", pageTitle);
        console.log("OG Image URL:", ogImageUrl);
        console.log("-----------------");
    } catch (error) {
        console.error("Error fetching tab URL or meta data:", error);
        linkElem.textContent = "Error loading URL/meta.";
        if (linkTitleElem) {
            linkTitleElem.value = "Error";
        }
    }
}

async function save(itemId) {
    console.log("Action initiated for item with ID:", itemId);
    if (!serverUrl || !serverAuthToken) {
        console.log("Config not set. Showing alert.");
        displayMessage('error', 'Server URL and Auth Token are not configured. Please set them in the extension settings.');
        return;
    }
    let linkType;
    let idOfLinkedFolder = null;
    let markedAsImportant = false;
    if (itemId === '-1') {
        linkType = "SAVED_LINK";
    } else if (itemId === '-2') {
        linkType = "IMPORTANT_LINK";
        markedAsImportant = true;
    } else {
        linkType = "FOLDER_LINK";
        idOfLinkedFolder = parseInt(itemId, 10);
    }
    let baseURL = '';
    try {
        const urlObj = new URL(activeTabUrl);
        baseURL = urlObj.hostname;
    } catch (e) {
        console.error("Invalid URL for baseURL extraction:", e);
        baseURL = activeTabUrl;
    }
    const noteElement = document.getElementById('link_note');
    const note = noteElement ? noteElement.value : '';
    const titleElement = document.getElementById('link_title');
    const title = titleElement ? titleElement.value : '';
    const userAgent = navigator.userAgent;
    const correlationId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
    const clientName = "LinkoraBrowserExtension";
    const eventTimestamp = Math.floor(Date.now() / 1000);
    const payload = {
        linkType: linkType,
        title: title,
        url: activeTabUrl,
        baseURL: baseURL,
        imgURL: ogImageUrl,
        note: note,
        idOfLinkedFolder: idOfLinkedFolder,
        userAgent: userAgent,
        markedAsImportant: markedAsImportant,
        mediaType: "IMAGE",
        correlation: {
            id: correlationId,
            clientName: clientName
        },
        eventTimestamp: eventTimestamp
    };
    console.log("Sending payload for link save:", payload);
    console.log("Authorization header for link save POST:", `Bearer ${serverAuthToken}`);

    displayMessage('saving_link', 'Please wait while your link is being saved. The window will close automatically on success.');

    const startTime = Date.now();

    try {
        const response = await fetch(`${serverUrl}/CREATE_A_NEW_LINK`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${serverAuthToken}`
            },
            body: JSON.stringify(payload)
        });

        const elapsedTime = Date.now() - startTime;
        const minimumDisplayTime = 1500;
        const timeToWait = Math.max(0, minimumDisplayTime - elapsedTime);

        if (response.ok) {
            console.log("Link save successful. Displaying success message and setting close timer.");
            displayMessage('success', 'Link saved successfully!');
            setTimeout(() => {
                const overlay = document.getElementById('success-overlay');
                if (overlay) overlay.remove();
                window.close();
            }, timeToWait);
        } else {
            const errorText = await response.text();
            console.error('Failed to save link:', response.status, errorText);
            console.log("Link save failed. Displaying error message.");
            displayMessage('error', `Failed to save link: ${response.status} ${response.statusText}. ${errorText}`);
        }
    } catch (error) {
        console.error('Error sending data to server for link save:', error);
        console.log("Network error during link save. Displaying error message.");
        displayMessage('error', `An error occurred while connecting to the server: ${error.message}. Please check your connection and configuration.`);
    }
}

async function createFolder(parentFolderName, parentFolderId) {
    console.log(`Attempting to create folder inside: ${parentFolderName} (ID: ${parentFolderId})`);
    if (!serverUrl || !serverAuthToken) {
        console.log("Config not set for folder creation. Showing error message.");
        displayMessage('error', 'Server URL and Auth Token are not configured. Please set them in the extension settings.');
        return;
    }
    const folderDisplayName = parentFolderId === null ? "new root folder" : `new folder (inside '${parentFolderName}')`;
    console.log(`Prompting for name for ${folderDisplayName}.`);
    const newFolderName = prompt(`Enter the name for the ${folderDisplayName}:`);

    if (newFolderName === null || newFolderName.trim() === '') {
        console.log("Folder creation cancelled or name was empty. Showing alert.");
        displayMessage('error', 'Folder creation cancelled or the name was empty.');
        return;
    }
    console.log(`Prompting for note for new folder '${newFolderName}'.`);
    const newFolderNote = prompt(`Enter a note for '${newFolderName}' (optional):`);
    const note = newFolderNote === null ? '' : newFolderNote.trim();
    const correlationId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
    const clientName = "LinkoraBrowserExtension";
    const eventTimestamp = Math.floor(Date.now() / 1000);
    const payload = {
        id: 0,
        name: newFolderName,
        note: note,
        parentFolderId: parentFolderId,
        isArchived: false,
        correlation: {
            id: correlationId,
            clientName: clientName
        },
        eventTimestamp: eventTimestamp
    };
    console.log("Sending folder creation payload:", payload);
    console.log("Authorization header for CREATE_FOLDER POST:", `Bearer ${serverAuthToken}`);

    displayMessage('folder_creating', `Creating folder '${newFolderName}'... The data will refresh automatically after creation.`);

    try {
        const response = await fetch(`${serverUrl}/CREATE_FOLDER`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${serverAuthToken}`
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            console.log("Folder creation successful. Refreshing content.");
            await loadBrowserExtensionContent();
            console.log("Content refreshed after folder creation.");
        } else {
            const errorText = await response.text();
            console.error('Failed to create folder:', response.status, errorText);
            console.log("Folder creation failed. Displaying error message.");
            displayMessage('folder_error', `Failed to create folder: ${response.status} ${response.statusText}. ${errorText}`);
        }
    } catch (error) {
        console.error('Error sending folder creation data to server:', error);
        console.log("Network error during folder creation. Displaying error message.");
        displayMessage('folder_error', `An error occurred while connecting to the server: ${error.message}. Please check your connection and configuration.`);
    }
}

function handleConfigChange() {
    console.log("handleConfigChange initiated.");
    let newServerUrl = prompt("Enter the server URL:", serverUrl);
    if (newServerUrl === null) {
        console.log("Server URL prompt cancelled.");
        serverUrl = '';
        serverAuthToken = '';
        localStorage.removeItem('linkoraServerUrl');
        localStorage.removeItem('linkoraAuthToken');
        return false;
    }
    if (!newServerUrl.startsWith('http://') && !newServerUrl.startsWith('https://')) {
        newServerUrl = 'http://' + newServerUrl;
    }
    console.log("Prompting for auth token.");
    const newAuthToken = prompt("Enter the auth token:", serverAuthToken);
    if (newAuthToken === null) {
        console.log("Auth token prompt cancelled.");
        serverUrl = '';
        serverAuthToken = '';
        localStorage.removeItem('linkoraServerUrl');
        localStorage.removeItem('linkoraAuthToken');
        return false;
    }
    localStorage.setItem('linkoraServerUrl', newServerUrl);
    localStorage.setItem('linkoraAuthToken', newAuthToken);
    serverUrl = newServerUrl;
    serverAuthToken = newAuthToken;
    console.log("Configuration saved:");
    console.log("Server URL:", serverUrl);
    console.log("Auth Token:", serverAuthToken);
    return true;
}
