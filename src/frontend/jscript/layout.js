
"use strict";

function renderAppLayout(activePage = "dashboard") {
    const app = document.getElementById("app");

    if (!app) return;

    const menuItems = [
        { id: "dashboard", label: "Dashboard", icon: "fa-chart-pie", url: "dashboard.html" },
        { id: "companies", label: "Company Details", icon: "fa-building", url: "companies.html" },
        { id: "items", label: "Items / Products", icon: "fa-box", url: "items.html" },
        { id: "batches", label: "Stock Batches", icon: "fa-warehouse", url: "batches.html" },
        { id: "movements", label: "Stock Movements", icon: "fa-right-left", url: "movements.html" },
        { id: "loads", label: "Loads", icon: "fa-truck", url: "loads.html" },
        { id: "invoices", label: "Invoices", icon: "fa-file-invoice", url: "invoices.html" },
        { id: "payments", label: "Payments", icon: "fa-credit-card", url: "payments.html" },
        { id: "reports", label: "Reports", icon: "fa-chart-column", url: "reports.html" }
    ];

    const titles = {
        dashboard: "Dashboard",
        companies: "Company Details",
        items: "Items / Products",
        batches: "Stock Batches",
        movements: "Stock Movements",
        loads: "Loads",
        invoices: "Invoices",
        payments: "Payments",
        reports: "Reports"
    };

    app.innerHTML = `
        <aside class="sidebar" id="sidebar">
            <div class="sidebar-brand">
                <i class="fa-solid fa-boxes-stacked"></i>
                <span class="brand-highlight">RAHAN SUPPLIES</span>
            </div>

            <p class="menu-label">MAIN MENU</p>

            <nav class="sidebar-menu">
                ${menuItems.map(item => `
                    <a href="${item.url}"
                       class="menu-link ${item.id === activePage ? "active" : ""}">
                        <i class="fa-solid ${item.icon}"></i>
                        <span>${item.label}</span>
                    </a>
                `).join("")}
            </nav>

            <div class="sidebar-footer">
                <i class="fa-solid fa-shield-halved"></i>
                <span>Administrator Panel</span>
            </div>
        </aside>

        <main class="main-content">
            <header class="top-navbar">
                <div class="navbar-left">
                    <button class="icon-button menu-toggle"
                            id="menuToggle"
                            type="button"
                            aria-label="Toggle sidebar">
                        <i class="fa-solid fa-bars"></i>
                    </button>

                    <div>
                        <h2 id="pageTitle">${titles[activePage] || "Dashboard"}</h2>
                        <p class="navbar-subtitle">Inventory Management System</p>
                    </div>
                </div>

                <div class="navbar-right">
                    <div class="clock">
                        <i class="fa-regular fa-clock"></i>
                        <span id="currentTime">00:00:00</span>
                    </div>

                    <div class="profile-wrapper">
                        <button class="profile-button"
                                id="profileButton"
                                type="button"
                                aria-expanded="false">
                            <span class="profile-avatar">
                                <i class="fa-solid fa-user"></i>
                            </span>

                            <span class="profile-info">
                                <strong>Administrator</strong>
                                <small>System Admin</small>
                            </span>

                            <i class="fa-solid fa-chevron-down"></i>
                        </button>

                        <div class="profile-dropdown" id="profileDropdown">
                            <button type="button" id="changePasswordBtn">
                                <i class="fa-solid fa-key"></i>
                                Change Password
                            </button>

                            <button type="button" id="logoutBtn" class="logout-option">
                                <i class="fa-solid fa-right-from-bracket"></i>
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            <section class="dashboard-page" id="pageContent"></section>
        </main>
    `;

    initializeAppLayout();
}

function initializeAppLayout() {
    const timeElement = document.getElementById("currentTime");

    function updateClock() {
        if (timeElement) {
            timeElement.textContent =
                new Date().toLocaleTimeString("en-GB");
        }
    }

    updateClock();
    setInterval(updateClock, 1000);

    const sidebar = document.getElementById("sidebar");
    const menuToggle = document.getElementById("menuToggle");

    if (sidebar && menuToggle) {
        menuToggle.addEventListener("click", () => {
            sidebar.classList.toggle("open");
        });
    }

    const profileButton = document.getElementById("profileButton");
    const dropdown = document.getElementById("profileDropdown");

    if (profileButton && dropdown) {
        profileButton.addEventListener("click", event => {
            event.stopPropagation();

            const isOpen = dropdown.classList.toggle("show");
            profileButton.setAttribute("aria-expanded", String(isOpen));
        });

        document.addEventListener("click", event => {
            if (
                !dropdown.contains(event.target) &&
                !profileButton.contains(event.target)
            ) {
                dropdown.classList.remove("show");
                profileButton.setAttribute("aria-expanded", "false");
            }
        });
    }

    const changePasswordButton =
        document.getElementById("changePasswordBtn");

    if (changePasswordButton) {
        changePasswordButton.addEventListener("click", () => {
            alert("Change Password will be implemented in a later step.");

            if (dropdown) dropdown.classList.remove("show");
        });
    }

    const logoutButton = document.getElementById("logoutBtn");

    if (logoutButton) {
        logoutButton.addEventListener("click", () => {
            if (confirm("Are you sure you want to log out?")) {
                sessionStorage.removeItem("inventoryDemoLogin");
                window.location.href = "index.html";
            }
        });
    }
}
