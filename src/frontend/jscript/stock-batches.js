
const STOCK_BATCHES_API_URL = "/api/v1/stock-batches";

// Hakikisha prefix hii inalingana na @RequestMapping
// iliyo juu ya ItemsController yako.
const ITEMS_API_URL = "/api/v1/items";

let stockBatches = [];
let items = [];

document.addEventListener("DOMContentLoaded", () => {
    renderAppLayout("stock-batches");

    const pageContent = document.getElementById("pageContent");
    const template = document.getElementById("stockBatchesPageTemplate");

    if (!pageContent || !template) {
        console.error("Stock Batches page template haijapatikana.");
        return;
    }

    pageContent.innerHTML = template.innerHTML;

    setDefaultReceivedDate();
    attachStockBatchEvents();

    loadItems();
    loadStockBatches();
});

function setDefaultReceivedDate() {
    const receivedDate = document.getElementById("receivedDate");

    if (receivedDate) {
        receivedDate.value = new Date().toISOString().split("T")[0];
    }
}

function attachStockBatchEvents() {
    const addButton = document.getElementById("addStockBatchBtn");
    const modal = document.getElementById("stockBatchModal");

    console.log("Add button:", addButton);
    console.log("Stock Batch modal:", modal);

    if (!addButton || !modal) {
        console.error("Add button au modal haijapatikana kwenye HTML.");
        return;
    }

    addButton.onclick = openStockBatchModal;

    document.getElementById("stockBatchForm")
        ?.addEventListener("submit", saveStockBatch);

    document.getElementById("closeStockBatchModal")
        ?.addEventListener("click", closeStockBatchModal);

    document.getElementById("cancelStockBatchBtn")
        ?.addEventListener("click", closeStockBatchModal);

    document.getElementById("stockBatchSearch")
        ?.addEventListener("input", renderStockBatches);
}

async function loadItems() {
    const itemSelect = document.getElementById("batchItem");

    try {
        const response = await fetch(ITEMS_API_URL);

        if (!response.ok) {
            throw new Error(`Imeshindikana kupata Items: ${response.status}`);
        }

        const data = await response.json();
        items = Array.isArray(data) ? data : [];

        itemSelect.innerHTML = '<option value="">Select item</option>';

        items.forEach((item) => {
            const option = document.createElement("option");

            option.value = item.id;
            option.textContent = item.itemsNames;

            itemSelect.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading Items:", error);

        itemSelect.innerHTML =
            '<option value="">Failed to load items</option>';

        showStockBatchMessage(
            "Imeshindikana kupata Items. Hakikisha backend imewashwa na Items API URL ni sahihi.",
            "error"
        );
    }
}

async function loadStockBatches() {
    const tableBody = document.getElementById("stockBatchesTableBody");

    tableBody.innerHTML = `
        <tr>
            <td colspan="7">Loading stock batches...</td>
        </tr>
    `;

    try {
        const response = await fetch(STOCK_BATCHES_API_URL);

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const data = await response.json();
        stockBatches = Array.isArray(data) ? data : [];

        renderStockBatches();
    } catch (error) {
        console.error("Error loading Stock Batches:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="7">
                    Failed to load stock batches. Check the backend and API URL.
                </td>
            </tr>
        `;

        updateStockBatchSummary();
    }
}

function renderStockBatches() {
    const tableBody = document.getElementById("stockBatchesTableBody");
    const searchInput = document.getElementById("stockBatchSearch");

    if (!tableBody) return;

    const searchTerm = (searchInput?.value || "").trim().toLowerCase();

    const filteredBatches = stockBatches.filter((batch) => {
        const itemName = batch.item?.itemsNames || "";

        return (
            (batch.batchNumber || "").toLowerCase().includes(searchTerm) ||
            itemName.toLowerCase().includes(searchTerm)
        );
    });

    updateStockBatchSummary();

    if (filteredBatches.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7">No stock batches found.</td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = filteredBatches.map((batch) => {
        const itemName = batch.item?.itemsNames || "Unknown item";
        const status = getBatchStatus(batch.expiryDate);

        return `
            <tr>
                <td>${escapeHTML(batch.batchNumber)}</td>
                <td>${escapeHTML(itemName)}</td>
                <td>${escapeHTML(batch.quantityReceived ?? 0)}</td>
                <td>${escapeHTML(batch.quantityRemaining ?? 0)}</td>
                <td>${escapeHTML(batch.receivedDate || "-")}</td>
                <td>${escapeHTML(batch.expiryDate || "-")}</td>
                <td>
                    <span class="stock-batch-status ${status.className}">
                        ${status.label}
                    </span>
                </td>
            </tr>
        `;
    }).join("");
}

function getBatchStatus(expiryDate) {
    if (!expiryDate) {
        return {
            label: "Unknown",
            className: "status-unknown"
        };
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const expiry = new Date(`${expiryDate}T00:00:00`);

    if (Number.isNaN(expiry.getTime())) {
        return {
            label: "Unknown",
            className: "status-unknown"
        };
    }

    if (expiry < today) {
        return {
            label: "Expired",
            className: "status-expired"
        };
    }

    return {
        label: "Valid",
        className: "status-valid"
    };
}

function updateStockBatchSummary() {
    const totalElement = document.getElementById("totalBatches");
    const expiredElement = document.getElementById("expiredBatches");

    if (totalElement) {
        totalElement.textContent = stockBatches.length;
    }

    if (expiredElement) {
        expiredElement.textContent = stockBatches.filter((batch) => {
            return getBatchStatus(batch.expiryDate).label === "Expired";
        }).length;
    }
}

function openStockBatchModal() {
    const modal = document.getElementById("stockBatchModal");
    const form = document.getElementById("stockBatchForm");

    if (!modal || !form) {
        console.error("Modal au form haijapatikana.");
        return;
    }

    form.reset();
    setDefaultReceivedDate();

    const message = document.getElementById("stockBatchMessage");

    if (message) {
        message.textContent = "";
    }

    modal.classList.add("active");
    console.log("Add Stock Batch button imebonyezwa.");
}

function closeStockBatchModal() {
    document.getElementById("stockBatchModal")?.classList.remove("active");
}

async function saveStockBatch(event) {
    event.preventDefault();

    const batchNumber = document
        .getElementById("batchNumber").value.trim();

    const itemId = document.getElementById("batchItem").value;
    const quantityReceived = document
        .getElementById("quantityReceived").value;

    const receivedDate = document.getElementById("receivedDate").value;
    const expiryDate = document.getElementById("expiryDate").value;

    if (!batchNumber || !itemId || !quantityReceived || !expiryDate) {
        showStockBatchMessage("Tafadhali jaza sehemu zote zinazohitajika.", "error");
        return;
    }

    if (Number(quantityReceived) <= 0) {
        showStockBatchMessage("Quantity Received lazima iwe zaidi ya sifuri.", "error");
        return;
    }

    if (receivedDate && expiryDate < receivedDate) {
        showStockBatchMessage(
            "Expiry Date haiwezi kuwa kabla ya Received Date.",
            "error"
        );
        return;
    }

    const stockBatchData = {
        item: {
            id: Number(itemId)
        },
        batchNumber,
        quantityReceived: Number(quantityReceived),
        receivedDate: receivedDate || null,
        expiryDate
    };

    const saveButton = document.querySelector(
        '#stockBatchForm button[type="submit"]'
    );

    if (saveButton) {
        saveButton.disabled = true;
        saveButton.textContent = "Saving...";
    }

    try {
        const response = await fetch(STOCK_BATCHES_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(stockBatchData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error: ${response.status}`);
        }

        closeStockBatchModal();
        await loadStockBatches();

        showStockBatchMessage("Stock batch imehifadhiwa vizuri.", "success");
    } catch (error) {
        console.error("Error saving Stock Batch:", error);

        showStockBatchMessage(
            "Imeshindikana kuhifadhi stock batch. Angalia backend na ujumbe wa error kwenye Console.",
            "error"
        );
    } finally {
        if (saveButton) {
            saveButton.disabled = false;
            saveButton.textContent = "Save Stock Batch";
        }
    }
}

function showStockBatchMessage(message, type) {
    const messageElement = document.getElementById("stockBatchMessage");

    if (!messageElement) {
        alert(message);
        return;
    }

    messageElement.textContent = message;
    messageElement.className = `stock-batch-message ${type}`;
}

function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, (character) => {
        const characters = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        };

        return characters[character];
    });
}
