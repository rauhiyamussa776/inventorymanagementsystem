
"use strict";

// Display the shared sidebar and navbar
renderAppLayout("companies");

const pageContent = document.getElementById("pageContent");

// Sample data for frontend testing
let companies = [
    {
        id: 1,
        name: "Zanzibar Hotel",
        phone: "0771234567",
        address: "Stone Town, Zanzibar"
    },
    {
        id: 2,
        name: "Government Institution",
        phone: "0621234567",
        address: "Maisara, Zanzibar"
    },
    {
        id: 3,
        name: "Sunrise Restaurant",
        phone: "0712345678",
        address: "Mwanakwerekwe, Zanzibar"
    }
];

let editingCompanyId = null;

pageContent.innerHTML = `
    <div class="company-page">

        <div class="company-heading">
            <div>
                <h1>Company Details</h1>
                <p>Manage companies and institutions that purchase from RAHAN SUPPLIES.</p>
            </div>

            <button class="company-primary-btn" id="addCompanyBtn">
                <i class="fa-solid fa-plus"></i>
                Add Company
            </button>
        </div>

        <div class="company-stats">
            <div class="company-stat-card">
                <div class="company-stat-icon">
                    <i class="fa-solid fa-building"></i>
                </div>
                <div>
                    <p>Total Companies</p>
                    <h2 id="totalCompanies">0</h2>
                </div>
            </div>
        </div>

        <section class="company-card">

            <div class="company-toolbar">
                <div>
                    <h3>Registered Companies</h3>
                    <p>View and manage company information.</p>
                </div>

                <div class="company-search">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <input
                        type="search"
                        id="companySearch"
                        placeholder="Search company..."
                        aria-label="Search companies"
                    >
                </div>
            </div>

            <div class="table-wrapper">
                <table class="company-table">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Company Name</th>
                            <th>Phone Number</th>
                            <th>Address</th>
                            <th>Actions</th>
                        </tr>
                    </thead>

                    <tbody id="companyTableBody"></tbody>
                </table>
            </div>

            <p class="company-note">
                <i class="fa-solid fa-circle-info"></i>
                These are sample records for frontend testing.
            </p>
        </section>

        <footer class="dashboard-footer">
            RAHAN SUPPLIES &copy; 2026. Inventory Management System.
        </footer>
    </div>

    <!-- Add/Edit Company Modal -->
    <div class="company-modal" id="companyModal">
        <div class="company-modal-card">

            <div class="company-modal-heading">
                <div>
                    <h2 id="companyModalTitle">Add Company</h2>
                    <p>Enter the company information below.</p>
                </div>

                <button type="button" id="closeCompanyModal"
                        class="company-close-btn"
                        aria-label="Close modal">
                    <i class="fa-solid fa-xmark"></i>
                </button>
            </div>

            <form id="companyForm">
                <label for="companyName">Company Name</label>
                <input
                    type="text"
                    id="companyName"
                    placeholder="Enter company name"
                    required
                    maxlength="100"
                >

                <label for="companyPhone">Phone Number</label>
                <input
                    type="tel"
                    id="companyPhone"
                    placeholder="Enter phone number"
                    required
                    maxlength="20"
                >

                <label for="companyAddress">Address</label>
                <textarea
                    id="companyAddress"
                    placeholder="Enter company address"
                    required
                    maxlength="200"
                    rows="3"
                ></textarea>

                <p id="companyFormMessage" class="company-form-message"></p>

                <div class="company-form-actions">
                    <button type="button" class="company-cancel-btn"
                            id="cancelCompanyBtn">
                        Cancel
                    </button>

                    <button type="submit" class="company-primary-btn">
                        <i class="fa-solid fa-floppy-disk"></i>
                        Save Company
                    </button>
                </div>
            </form>
        </div>
    </div>
`;

const companyTableBody = document.getElementById("companyTableBody");
const companyModal = document.getElementById("companyModal");
const companyForm = document.getElementById("companyForm");
const companyFormMessage = document.getElementById("companyFormMessage");

function renderCompanies(searchText = "") {
    const search = searchText.trim().toLowerCase();

    const filteredCompanies = companies.filter(company =>
        company.name.toLowerCase().includes(search) ||
        company.phone.toLowerCase().includes(search) ||
        company.address.toLowerCase().includes(search)
    );

    document.getElementById("totalCompanies").textContent = companies.length;

    if (filteredCompanies.length === 0) {
        companyTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="company-empty">
                    <i class="fa-solid fa-building-circle-exclamation"></i>
                    <p>No companies found.</p>
                </td>
            </tr>
        `;
        return;
    }

    companyTableBody.innerHTML = filteredCompanies.map((company, index) => `
        <tr>
            <td>${index + 1}</td>
            <td>
                <div class="company-name-cell">
                    <span class="company-row-icon">
                        <i class="fa-solid fa-building"></i>
                    </span>
                    <strong>${escapeHTML(company.name)}</strong>
                </div>
            </td>
            <td>${escapeHTML(company.phone)}</td>
            <td>${escapeHTML(company.address)}</td>
            <td>
                <div class="company-actions">
                    <button class="company-action-btn edit-company-btn"
                            data-id="${company.id}"
                            title="Edit company"
                            aria-label="Edit ${escapeHTML(company.name)}">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </button>

                    <button class="company-action-btn delete-company-btn"
                            data-id="${company.id}"
                            title="Delete company"
                            aria-label="Delete ${escapeHTML(company.name)}">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join("");
}

// Escape text before displaying user-entered values in HTML
function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    })[character]);
}

function openCompanyModal(company = null) {
    companyForm.reset();
    companyFormMessage.textContent = "";
    editingCompanyId = company ? company.id : null;

    document.getElementById("companyModalTitle").textContent =
        company ? "Edit Company" : "Add Company";

    document.getElementById("companyName").value = company?.name || "";
    document.getElementById("companyPhone").value = company?.phone || "";
    document.getElementById("companyAddress").value = company?.address || "";

    companyModal.classList.add("show");
    document.getElementById("companyName").focus();
}

function closeCompanyModal() {
    companyModal.classList.remove("show");
    companyForm.reset();
    companyFormMessage.textContent = "";
    editingCompanyId = null;
}

document.getElementById("addCompanyBtn").addEventListener("click", () => {
    openCompanyModal();
});

document.getElementById("closeCompanyModal").addEventListener("click", closeCompanyModal);
document.getElementById("cancelCompanyBtn").addEventListener("click", closeCompanyModal);

companyModal.addEventListener("click", event => {
    if (event.target === companyModal) {
        closeCompanyModal();
    }
});

document.getElementById("companySearch").addEventListener("input", event => {
    renderCompanies(event.target.value);
});

companyForm.addEventListener("submit", event => {
    event.preventDefault();

    const name = document.getElementById("companyName").value.trim();
    const phone = document.getElementById("companyPhone").value.trim();
    const address = document.getElementById("companyAddress").value.trim();

    if (!name || !phone || !address) {
        companyFormMessage.textContent = "Please fill in all fields.";
        return;
    }

    const duplicate = companies.some(company =>
        company.name.toLowerCase() === name.toLowerCase() &&
        company.id !== editingCompanyId
    );

    if (duplicate) {
        companyFormMessage.textContent =
            "A company with this name already exists.";
        return;
    }

    if (editingCompanyId !== null) {
        companies = companies.map(company =>
            company.id === editingCompanyId
                ? { ...company, name, phone, address }
                : company
        );
    } else {
        const newId = companies.length
            ? Math.max(...companies.map(company => company.id)) + 1
            : 1;

        companies.push({ id: newId, name, phone, address });
    }

    closeCompanyModal();
    renderCompanies(document.getElementById("companySearch").value);
});

companyTableBody.addEventListener("click", event => {
    const editButton = event.target.closest(".edit-company-btn");
    const deleteButton = event.target.closest(".delete-company-btn");

    if (editButton) {
        const id = Number(editButton.dataset.id);
        const company = companies.find(item => item.id === id);

        if (company) openCompanyModal(company);
    }

    if (deleteButton) {
        const id = Number(deleteButton.dataset.id);
        const company = companies.find(item => item.id === id);

        if (company && confirm(`Delete "${company.name}"?`)) {
            companies = companies.filter(item => item.id !== id);
            renderCompanies(document.getElementById("companySearch").value);
        }
    }
});

renderCompanies();
