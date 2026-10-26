
"use strict";

// Display the shared layout first
renderAppLayout("dashboard");

// Add dashboard-specific content
const pageContent = document.getElementById("pageContent");

pageContent.innerHTML = `
    <div class="welcome-section">
        <div>
            <h1>Welcome back, Administrator!</h1>
            <p>Here is an overview of your inventory business.</p>
        </div>

        <span class="today-date" id="dashboardDate"></span>
    </div>

    <div class="summary-cards">
        <article class="summary-card">
            <div class="summary-icon blue-icon">
                <i class="fa-solid fa-boxes-stacked"></i>
            </div>
            <p>Total Products</p>
            <h3 id="totalProducts">24</h3>
            <small>Sample registered products</small>
        </article>

        <article class="summary-card">
            <div class="summary-icon green-icon">
                <i class="fa-solid fa-warehouse"></i>
            </div>
            <p>Stock Available</p>
            <h3 id="stockAvailable">1,250 kg</h3>
            <small>Sample stock quantity</small>
        </article>

        <article class="summary-card">
            <div class="summary-icon orange-icon">
                <i class="fa-solid fa-file-invoice"></i>
            </div>
            <p>Unpaid Invoices</p>
            <h3 id="unpaidInvoices">6</h3>
            <small>Awaiting payment</small>
        </article>

        <article class="summary-card">
            <div class="summary-icon purple-icon">
                <i class="fa-solid fa-money-bill-wave"></i>
            </div>
            <p>Outstanding Amount</p>
            <h3 id="outstandingAmount">TZS 850,000</h3>
            <small>Sample outstanding amount</small>
        </article>
    </div>

    <section class="content-card">
        <div class="section-heading">
            <div>
                <h3>Recent Activity</h3>
                <p>Latest inventory system activities</p>
            </div>

            <button class="secondary-button" id="viewAllBtn" type="button">
                View All
            </button>
        </div>

        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Activity</th>
                        <th>Item</th>
                        <th>Quantity</th>
                        <th>Status</th>
                    </tr>
                </thead>

                <tbody id="activityTable">
                    <tr>
                        <td>Stock In</td>
                        <td>Mchele</td>
                        <td>100 kg</td>
                        <td>
                            <span class="status-badge status-completed">
                                Completed
                            </span>
                        </td>
                    </tr>

                    <tr>
                        <td>Invoice Created</td>
                        <td>Nyama</td>
                        <td>50 kg</td>
                        <td>
                            <span class="status-badge status-pending">
                                Pending
                            </span>
                        </td>
                    </tr>

                    <tr>
                        <td>Stock Out</td>
                        <td>Mchele</td>
                        <td>20 kg</td>
                        <td>
                            <span class="status-badge status-completed">
                                Completed
                            </span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </section>

    <footer class="dashboard-footer">
        RAHAN SUPPLIES &copy; 2026. Inventory Management System.
    </footer>
`;

// Display today's date
function updateDashboardDate() {
    const dateElement = document.getElementById("dashboardDate");

    if (dateElement) {
        dateElement.textContent = new Date().toLocaleDateString("en-GB", {
            weekday: "long",
            day: "numeric",
            month: "long",
            year: "numeric"
        });
    }
}

updateDashboardDate();

// Demo button
document.getElementById("viewAllBtn").addEventListener("click", () => {
    alert("The full activity history will be implemented later.");
});
