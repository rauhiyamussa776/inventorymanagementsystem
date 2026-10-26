
"use strict";

// Display the shared sidebar and navbar
renderAppLayout("items");

// Load the Items HTML from items.html
const itemsTemplate = document.getElementById("itemsPageTemplate");
const pageContent = document.getElementById("pageContent");

if (!itemsTemplate || !pageContent) {
    throw new Error("Items template or page content container was not found.");
}

pageContent.innerHTML = itemsTemplate.innerHTML;

let items = [
    { id: 1, name: "Nyama", code: "ITM-001", unit: "kg", price: 12000 },
    { id: 2, name: "Mchele", code: "ITM-002", unit: "kg", price: 3500 },
    { id: 3, name: "Mafuta ya Kupikia", code: "ITM-003",  unit: "litre", price: 7000 }
];

let editingItemId = null;

const itemsTableBody = document.getElementById("itemsTableBody");
const itemModal = document.getElementById("itemModal");
const itemForm = document.getElementById("itemForm");
const itemFormMessage = document.getElementById("itemFormMessage");

function escapeItemHTML(value) {
    return String(value).replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    })[character]);
}

function formatTZS(value) {
    return "TZS " + Number(value).toLocaleString("en-TZ", {
        maximumFractionDigits: 2
    });
}

function renderItems(searchText = "") {
    const search = searchText.trim().toLowerCase();

    const filteredItems = items.filter(item =>
        item.name.toLowerCase().includes(search) ||
        item.code.toLowerCase().includes(search) 
      
    );

    document.getElementById("totalItems").textContent = items.length;

    if (filteredItems.length === 0) {
        itemsTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="items-empty">
                    <i class="fa-solid fa-box-open"></i>
                    <p>No items found.</p>
                </td>
            </tr>`;
        return;
    }

    itemsTableBody.innerHTML = filteredItems.map(item => `
        <tr>
            <td>
                <span class="item-code">${escapeItemHTML(item.code)}</span>
            </td>
            <td>
                <div class="items-name-cell">
                    <span class="items-row-icon">
                        <i class="fa-solid fa-box"></i>
                    </span>
                    <strong>${escapeItemHTML(item.name)}</strong>
                </div>
            </td>
        
            <td><span class="item-unit">${escapeItemHTML(item.unit)}</span></td>
            <td class="item-price">${formatTZS(item.price)}</td>
            <td>
                <div class="items-actions">
                    <button type="button"
                            class="items-action-btn edit-item-btn"
                            data-id="${item.id}" title="Edit item">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </button>

                    <button type="button"
                            class="items-action-btn delete-item-btn"
                            data-id="${item.id}" title="Delete item">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join("");
}

function openItemModal(item = null) {
    itemForm.reset();
    itemFormMessage.textContent = "";
    editingItemId = item ? item.id : null;

    document.getElementById("itemModalTitle").textContent =
        item ? "Edit Item" : "Add Item";

    document.getElementById("itemName").value = item?.name || "";
    document.getElementById("itemCode").value = item?.code || "";
   
    document.getElementById("itemUnit").value = item?.unit || "kg";
    document.getElementById("itemPrice").value = item?.price ?? "";

    itemModal.classList.add("show");
    document.getElementById("itemName").focus();
}

function closeItemModal() {
    itemModal.classList.remove("show");
    itemForm.reset();
    itemFormMessage.textContent = "";
    editingItemId = null;
}

document.getElementById("addItemBtn").addEventListener("click", () => {
    openItemModal();
});

document.getElementById("closeItemModal").addEventListener("click", closeItemModal);
document.getElementById("cancelItemBtn").addEventListener("click", closeItemModal);

itemModal.addEventListener("click", event => {
    if (event.target === itemModal) closeItemModal();
});

document.getElementById("itemSearch").addEventListener("input", event => {
    renderItems(event.target.value);
});

itemForm.addEventListener("submit", event => {
    event.preventDefault();

    const name = document.getElementById("itemName").value.trim();
    const code = document.getElementById("itemCode").value.trim();
    const unit = document.getElementById("itemUnit").value;
    const price = Number(document.getElementById("itemPrice").value);

    if (!name || !code || !Number.isFinite(price) || price < 0) {
        itemFormMessage.textContent = "Please enter valid information in all fields.";
        return;
    }

    const duplicateCode = items.some(item =>
        item.code.toLowerCase() === code.toLowerCase() &&
        item.id !== editingItemId
    );

    if (duplicateCode) {
        itemFormMessage.textContent = "This item code already exists.";
        return;
    }

    if (editingItemId !== null) {
        items = items.map(item =>
            item.id === editingItemId
                ? { ...item, name, code, unit, price }
                : item
        );
    } else {
        const newId = items.length
            ? Math.max(...items.map(item => item.id)) + 1
            : 1;

        items.push({ id: newId, name, code,  unit, price });
    }

    closeItemModal();
    renderItems(document.getElementById("itemSearch").value);
});

itemsTableBody.addEventListener("click", event => {
    const editButton = event.target.closest(".edit-item-btn");
    const deleteButton = event.target.closest(".delete-item-btn");

    if (editButton) {
        const item = items.find(
            item => item.id === Number(editButton.dataset.id)
        );

        if (item) openItemModal(item);
    }

    if (deleteButton) {
        const item = items.find(
            item => item.id === Number(deleteButton.dataset.id)
        );

        if (item && confirm(`Delete "${item.name}"?`)) {
            items = items.filter(existing => existing.id !== item.id);
            renderItems(document.getElementById("itemSearch").value);
        }
    }
});

renderItems();
