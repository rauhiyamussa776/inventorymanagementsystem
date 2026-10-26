
"use strict";

// =====================================
// 1. LOGIN PAGE
// =====================================

const loginForm = document.getElementById("loginForm");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const togglePassword = document.getElementById("togglePassword");
const loginMessage = document.getElementById("loginMessage");

if (togglePassword && passwordInput) {
    togglePassword.addEventListener("click", function () {
        const isHidden = passwordInput.type === "password";

        passwordInput.type = isHidden ? "text" : "password";
        togglePassword.textContent = isHidden ? "Hide" : "Show";
        togglePassword.setAttribute(
            "aria-label",
            isHidden ? "Hide password" : "Show password"
        );
    });
}

if (loginForm && usernameInput && passwordInput && loginMessage) {
    loginForm.addEventListener("submit", function (event) {
        event.preventDefault();

        loginMessage.textContent = "";
        loginMessage.classList.remove("visible");

        if (
            usernameInput.value.trim() === "admin" &&
            passwordInput.value === "demo123"
        ) {
            sessionStorage.setItem("inventoryDemoLogin", "true");
            window.location.href = "dashboard.html";
        } else {
            loginMessage.textContent =
                "Invalid demo username or password. Please try again.";
            loginMessage.classList.add("visible");
        }
    });
}


// =====================================
// 2. LIVE CLOCK AND DATE
// =====================================

const currentTime = document.getElementById("currentTime");
const currentDate = document.getElementById("currentDate");

function updateDateTime() {
    const now = new Date();

    if (currentTime) {
        currentTime.textContent = now.toLocaleTimeString("en-GB");
    }

    if (currentDate) {
        currentDate.textContent = now.toLocaleDateString("en-GB", {
            weekday: "short",
            day: "numeric",
            month: "short",
            year: "numeric"
        });
    }
}

updateDateTime();

if (currentTime || currentDate) {
    setInterval(updateDateTime, 1000);
}


// =====================================
// 3. PROFILE DROPDOWN
// =====================================

const profileButton = document.getElementById("profileButton");
const profileDropdown = document.getElementById("profileDropdown");

if (profileButton && profileDropdown) {
    profileButton.setAttribute("aria-expanded", "false");

    profileButton.addEventListener("click", function (event) {
        event.stopPropagation();

        const isOpen = profileDropdown.classList.toggle("show");

        profileButton.setAttribute("aria-expanded", String(isOpen));
    });

    document.addEventListener("click", function (event) {
        if (
            !profileDropdown.contains(event.target) &&
            !profileButton.contains(event.target)
        ) {
            profileDropdown.classList.remove("show");
            profileButton.setAttribute("aria-expanded", "false");
        }
    });
}


// =====================================
// 4. MOBILE SIDEBAR
// =====================================

const sidebar = document.getElementById("sidebar");
const menuToggle = document.getElementById("menuToggle");

if (sidebar && menuToggle) {
    menuToggle.addEventListener("click", function (event) {
        event.stopPropagation();
        sidebar.classList.toggle("open");
    });

    document.addEventListener("click", function (event) {
        if (
            window.innerWidth <= 900 &&
            !sidebar.contains(event.target) &&
            !menuToggle.contains(event.target)
        ) {
            sidebar.classList.remove("open");
        }
    });
}


// =====================================
// 5. SIDEBAR MENU
// =====================================

const menuLinks = document.querySelectorAll(".sidebar a");
const pageTitle = document.getElementById("pageTitle");

menuLinks.forEach(function (link) {
    link.addEventListener("click", function (event) {
        if (link.getAttribute("href") === "#") {
            event.preventDefault();
        }

        menuLinks.forEach(function (item) {
            item.classList.remove("active");
        });

        link.classList.add("active");

        const linkText = link.textContent.trim();

        if (pageTitle && linkText) {
            pageTitle.textContent = linkText;
        }

        if (sidebar) {
            sidebar.classList.remove("open");
        }
    });
});


// =====================================
// 6. CHANGE PASSWORD MODAL
// =====================================

const changePasswordBtn =
    document.getElementById("changePasswordBtn");

const passwordModal = document.getElementById("passwordModal");
const closePasswordModal =
    document.getElementById("closePasswordModal");

const cancelPasswordBtn =
    document.getElementById("cancelPasswordBtn");

const changePasswordForm =
    document.getElementById("changePasswordForm");

const passwordMessage =
    document.getElementById("passwordMessage");

function openPasswordModal() {
    if (!passwordModal) return;

    passwordModal.classList.add("show");
    passwordModal.classList.add("active");

    if (profileDropdown) {
        profileDropdown.classList.remove("show");
    }

    if (passwordMessage) {
        passwordMessage.textContent = "";
    }
}

function closePasswordModalWindow() {
    if (passwordModal) {
        passwordModal.classList.remove("show");
        passwordModal.classList.remove("active");
    }

    if (changePasswordForm) {
        changePasswordForm.reset();
    }

    if (passwordMessage) {
        passwordMessage.textContent = "";
    }
}

if (changePasswordBtn) {
    changePasswordBtn.addEventListener("click", openPasswordModal);
}

if (closePasswordModal) {
    closePasswordModal.addEventListener(
        "click",
        closePasswordModalWindow
    );
}

if (cancelPasswordBtn) {
    cancelPasswordBtn.addEventListener(
        "click",
        closePasswordModalWindow
    );
}

if (changePasswordForm) {
    changePasswordForm.addEventListener("submit", function (event) {
        event.preventDefault();

        const currentPassword =
            document.getElementById("currentPassword")?.value || "";

        const newPassword =
            document.getElementById("newPassword")?.value || "";

        const confirmPassword =
            document.getElementById("confirmPassword")?.value || "";

        if (!passwordMessage) return;

        if (!currentPassword || !newPassword || !confirmPassword) {
            passwordMessage.textContent =
                "Please fill in all password fields.";
            return;
        }

        if (newPassword.length < 8) {
            passwordMessage.textContent =
                "The new password must contain at least 8 characters.";
            return;
        }

        if (newPassword !== confirmPassword) {
            passwordMessage.textContent =
                "The new passwords do not match.";
            return;
        }

        passwordMessage.textContent =
            "Demo only: connect the backend to change your password. No password has been changed.";
    });
}


// =====================================
// 7. LOGOUT
// =====================================

const logoutBtn = document.getElementById("logoutBtn");

if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        const confirmed = window.confirm(
            "Are you sure you want to log out?"
        );

        if (confirmed) {
            sessionStorage.removeItem("inventoryDemoLogin");
            window.location.href = "index.html";
        }
    });
}


// =====================================
// 8. VIEW ALL ACTIVITY
// =====================================

const viewAllBtn = document.getElementById("viewAllBtn");

if (viewAllBtn) {
    viewAllBtn.addEventListener("click", function () {
        window.alert(
            "The full activity page will be added later."
        );
    });
}
