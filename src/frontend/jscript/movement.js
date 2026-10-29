
/* RAHAN SUPPLIES - STOCK MOVEMENTS DEMO */

renderAppLayout("stock-movements");

const template = document.getElementById("stockMovementsPageTemplate");
document.getElementById("pageContent").innerHTML = template.innerHTML;

// Demo items: IDs represent item IDs in the backend.
const items = [
    { id: 1, name: "Mchele" },
    { id: 2, name: "Nyama" },
    { id: 3, name: "Mafuta ya kupikia" },
    { id: 4, name: "Sukari" },
    { id: 5, name: "Unga" }
];

// Demo invoices: IDs represent invoice IDs in the backend.
const invoices = [
    { id: 1, invoiceNumber: "INV-001" },
    { id: 2, invoiceNumber: "INV-002" },
    { id: 3, invoiceNumber: "INV-003" }
];

const storageKey = "rahanDemoStockMovementsV2";

function getCurrentDateTime() {
    const now = new Date();
    const localDate = new Date(
        now.getTime() - now.getTimezoneOffset() * 60000
    );

    return localDate.toISOString().slice(0, 16);
}

function getTodayDateTime(offsetHours = 0) {
    const date = new Date();
    date.setHours(date.getHours() + offsetHours);

    const localDate = new Date(
        date.getTime() - date.getTimezoneOffset() * 60000
    );

    return localDate.toISOString().slice(0, 16);
}

// Sample movements matching the StockMovement fields.
const sampleMovements = [
    {
        id: 1,
        item: { id: 1, name: "Mchele" },
        type: "IN",
        quantity: 100,
        movementDate: getTodayDateTime(-5),
        invoice: null
    },
    {
        id: 2,
        item: { id: 2, name: "Nyama" },
        type: "IN",
        quantity: 50,
        movementDate: getTodayDateTime(-4),
        invoice: null
    },
    {
        id: 3,
        item: { id: 1, name: "Mchele" },
        type: "OUT",
        quantity: 10,
        movementDate: getTodayDateTime(-3),
        invoice: { id: 1, invoiceNumber: "INV-001" }
    },
    {
        id: 4,
        item: { id: 3, name: "Mafuta ya kupikia" },
        type: "IN",
        quantity: 30,
        movementDate: getTodayDateTime(-2),
        invoice: null
    },
    {
        id: 5,
        item: { id: 4, name: "Sukari" },
        type: "OUT",
        quantity: 5,
        movementDate: getTodayDateTime(-1),
        invoice: { id: 2, invoiceNumber: "INV-002" }
    }
];

let movements;

try {
    const savedData = localStorage.getItem(storageKey);
    movements = savedData ? JSON.parse(savedData) : sampleMovements;

    if (!Array.isArray(movements)) {
        movements = sampleMovements;
    }
} catch (error) {
    movements = sampleMovements;
}

if (!localStorage.getItem(storageKey)) {
    localStorage.setItem(storageKey, JSON.stringify(movements));
}

// Page elements
const tableBody = document.getElementById("movementsTableBody");
const searchInput = document.getElementById("movementSearch");
const typeFilter = document.getElementById("movementTypeFilter");
const modal = document.getElementById("movementModal");
const form = document.getElementById("movementForm");
const itemSelect = document.getElementById("movementItem");
const invoiceSelect = document.getElementById("movementInvoice");
const dateInput = document.getElementById("movementDate");
const message = document.getElementById("movementMessage");

// Fill item dropdown
items.forEach(function (item) {
    const option = document.createElement("option");
    option.value = item.id;
    option.textContent = item.name;
    itemSelect.appendChild(option);
});

// Fill optional invoice dropdown
invoices.forEach(function (invoice) {
    const option = document.createElement("option");
    option.value = invoice.id;
    option.textContent = invoice.invoiceNumber;
    invoiceSelect.appendChild(option);
});

function saveMovements() {
    localStorage.setItem(storageKey, JSON.stringify(movements));
}

function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, function (character) {
        const replacements = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        };

        return replacements[character];
    });
}

function formatDateTime(value) {
    if (!value) return "-";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return escapeHTML(value);
    }

    return date.toLocaleString();
}

function getItemName(movement) {
    return movement.item?.name || "Unknown item";
}

function getInvoiceNumber(movement) {
    return movement.invoice?.invoiceNumber || "-";
}

function displayMovements() {
    const searchText = searchInput.value.toLowerCase().trim();
    const selectedType = typeFilter.value;

    const filteredMovements = movements.filter(function (movement) {
        const itemName = getItemName(movement).toLowerCase();
        const invoiceNumber = getInvoiceNumber(movement).toLowerCase();

        const matchesSearch =
            itemName.includes(searchText) ||
            invoiceNumber.includes(searchText) ||
            movement.type.toLowerCase().includes(searchText);

        const matchesType =
            selectedType === "ALL" || movement.type === selectedType;

        return matchesSearch && matchesType;
    });

    document.getElementById("totalMovements").textContent =
        movements.length;

    document.getElementById("stockInTotal").textContent =
        movements.filter(function (movement) {
            return movement.type === "IN";
        }).length;

    document.getElementById("stockOutTotal").textContent =
        movements.filter(function (movement) {
            return movement.type === "OUT";
        }).length;

    if (filteredMovements.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="5">No stock movements found.</td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = filteredMovements.map(function (movement) {
        const isStockIn = movement.type === "IN";
        const badgeClass = isStockIn
            ? "movement-type-in"
            : "movement-type-out";
        const quantityClass = isStockIn
            ? "movement-quantity-in"
            : "movement-quantity-out";
        const sign = isStockIn ? "+" : "-";

        return `
            <tr>
                <td>
                    <span class="movement-item-name">
                        ${escapeHTML(getItemName(movement))}
                    </span>
                </td>

                <td>
                    <span class="movement-type-badge ${badgeClass}">
                        ${isStockIn ? "Stock In" : "Stock Out"}
                    </span>
                </td>

                <td>
                    <span class="${quantityClass}">
                        ${sign}${Number(movement.quantity).toLocaleString()}
                    </span>
                </td>

                <td>${formatDateTime(movement.movementDate)}</td>

                <td>${escapeHTML(getInvoiceNumber(movement))}</td>
            </tr>
        `;
    }).join("");
}

function showMessage(text, type) {
    message.textContent = text;
    message.className = `movement-message show ${type}`;
}

function openModal() {
    form.reset();
    dateInput.value = getCurrentDateTime();

    message.textContent = "";
    message.className = "movement-message";

    modal.classList.add("show");
}

function closeModal() {
    modal.classList.remove("show");
}

document.getElementById("addMovementBtn").addEventListener(
    "click",
    openModal
);

document.getElementById("closeMovementModal").addEventListener(
    "click",
    closeModal
);

document.getElementById("cancelMovementBtn").addEventListener(
    "click",
    closeModal
);

modal.addEventListener("click", function (event) {
    if (event.target === modal) {
        closeModal();
    }
});

searchInput.addEventListener("input", displayMovements);
typeFilter.addEventListener("change", displayMovements);

// Record a demo movement
form.addEventListener("submit", function (event) {
    event.preventDefault();

    const selectedItem = items.find(function (item) {
        return item.id === Number(itemSelect.value);
    });

    const selectedInvoice = invoices.find(function (invoice) {
        return invoice.id === Number(invoiceSelect.value);
    });

    const type = document.getElementById("movementType").value;
    const quantity = Number(
        document.getElementById("movementQuantity").value
    );
    const movementDate = dateInput.value;

    if (!selectedItem || !["IN", "OUT"].includes(type)) {
        showMessage("Please select a valid item and movement type.", "error");
        return;
    }

    if (!Number.isFinite(quantity) || quantity <= 0) {
        showMessage("Quantity must be greater than zero.", "error");
        return;
    }

    if (!movementDate) {
        showMessage("Please select the movement date and time.", "error");
        return;
    }

    const newMovement = {
        id: Date.now(),
        item: {
            id: selectedItem.id,
            name: selectedItem.name
        },
        type: type,
        quantity: quantity,
        movementDate: movementDate,
        invoice: selectedInvoice
            ? {
                id: selectedInvoice.id,
                invoiceNumber: selectedInvoice.invoiceNumber
            }
            : null
    };

    movements.unshift(newMovement);
    saveMovements();
    displayMovements();

    showMessage("Demo movement saved successfully.", "success");

    setTimeout(closeModal, 600);
});

displayMovements();
