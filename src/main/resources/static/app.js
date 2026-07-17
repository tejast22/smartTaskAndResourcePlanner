// ==========================================
// 1. GLOBAL APPLICATION STATE
// ==========================================
let currentUserId = localStorage.getItem('currentUserId') || null;
let currentUsername = localStorage.getItem('currentUsername') || "Guest";

// Chart Handles
let pieChartInstance = null;
let barChartInstance = null;

// ==========================================
// 2. DOM INITIALIZATION & EVENT BINDINGS
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    // A. Theme Switch Engine Setup
    const themeBtn = document.getElementById('theme-btn');
    let savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeButtonUI(savedTheme);

    if (themeBtn) {
        themeBtn.addEventListener('click', () => {
            let currentTheme = document.documentElement.getAttribute('data-theme');
            let newTheme = currentTheme === 'dark' ? 'light' : 'dark';

            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeButtonUI(newTheme);

            // Force refresh charts to recalculate label color values for the new theme
            if (window.lastLoadedTasks) {
                updateDashboardMetrics(window.lastLoadedTasks);
            }
        });
    }

    // B. Password Visibility Eyeball Toggle Setup
    const togglePasswordBtn = document.getElementById('toggle-password-btn');
    if (togglePasswordBtn) {
        togglePasswordBtn.addEventListener('click', function() {
            const passwordField = document.getElementById('auth-password');
            if (passwordField.type === 'password') {
                passwordField.type = 'text';
                this.innerText = '🙈';
            } else {
                passwordField.type = 'password';
                this.innerText = '👁️';
            }
        });
    }

    // C. Authentication Submission Routing Connectors
    const loginBtn = document.getElementById('btn-login');
    if (loginBtn) loginBtn.addEventListener('click', handleLogin);

    const registerBtn = document.getElementById('btn-register');
    if (registerBtn) registerBtn.addEventListener('click', handleRegister);

    // D. Core Task Data Form Submission Connector
    const taskForm = document.getElementById('task-form');
    if (taskForm) {
        taskForm.addEventListener('submit', handleTaskSubmit);
    }

    // E. Initial App Workspace Render Sequence
    loadTasks();
    updateAuthUI();

    // F. Delayed Background Push Registration Setup
    setTimeout(() => {
        if (currentUserId) {
            setupPushNotifications();
        }
    }, 1500);
});

// Helper to handle updating the explicit text on the theme switch button target
function updateThemeButtonUI(theme) {
    const themeBtn = document.getElementById('theme-btn');
    if (themeBtn) {
        themeBtn.innerText = theme === 'dark' ? '☀️ Light Mode' : '🌙 Dark Mode';
    }
}

// ==========================================
// 3. CORE WORKSPACE RENDERING ENGINE
// ==========================================
function loadTasks(){
    fetch('/tasks', {
        method: 'GET',
        headers: { 'X-User-Id': currentUserId || '' }
    })
        .then(response => response.json())
        .then(tasks => {
            window.lastLoadedTasks = tasks; // Cache to memory

            const activeGrid = document.getElementById('active-task-grid');
            const completedGrid = document.getElementById('completed-task-grid');

            if (!activeGrid || !completedGrid) return;

            // Clear out both containers before structural rendering logic
            activeGrid.innerHTML = '';
            completedGrid.innerHTML = '';

            tasks.forEach(task => {
                let actionButtonHtml = `<div class="action-buttons" style="display:flex; gap:8px; width:100%; justify-content:flex-end;">`;

                if(task.status.toLowerCase() === 'pending'){
                    actionButtonHtml += `<button onclick="changeStatus('${task.id}', 'working')" class="btn-small btn-blue">Start Working</button>`;
                } else if(task.status.toLowerCase() === 'working'){
                    actionButtonHtml += `
                        <button onclick="changeStatus('${task.id}', 'pending')" class="btn-small btn-gray">⏪ Pending</button>
                        <button onclick="changeStatus('${task.id}', 'completed')" class="btn-small btn-green">Mark Completed</button>`;
                } else if(task.status.toLowerCase() === 'completed'){
                    actionButtonHtml += `
                        <span class="status-done">✔️ Done</span>
                        <button onclick="changeStatus('${task.id}', 'working')" class="btn-small btn-yellow">⏪ Re-open</button>`;
                }

                actionButtonHtml += `<button onclick="deleteTask(${task.id})" class="btn-small btn-red">Delete</button></div>`;

                const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
                const pendingBg = isDark ? 'background:#1e293b; color:#9ca3af;' : 'background:#f3f4f6; color:#374151;';
                const workingBg = isDark ? 'background:#713f12; color:#fef08a;' : 'background:#fef9c3; color:#a16207;';
                const completedBg = isDark ? 'background:#064e3b; color:#4ade80;' : 'background:#dcfce7; color:#15803d;';

                let statusStyle = pendingBg;
                if(task.status.toLowerCase() === 'working') statusStyle = workingBg;
                if(task.status.toLowerCase() === 'completed') statusStyle = completedBg;

                let timeColumnData = "";
                let isCompletedTask = task.status.toLowerCase() === 'completed';

                if (isCompletedTask) {
                    if (task.completedAt) {
                        const compDate = new Date(task.completedAt);
                        timeColumnData = "🎉 " + compDate.toLocaleString([], { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' });
                    } else {
                        timeColumnData = "✔️ Done";
                    }
                } else {
                    const rawDateStr = task.dueDate ? task.dueDate.substring(0, 16) : "";
                    timeColumnData = `
                        <input type="datetime-local" class="flatpickr-input"
                            value="${rawDateStr}"
                            onchange="updateDueDate('${task.id}', this.value)"
                            style="border: 1px dashed var(--border-color); background: transparent; color: var(--text-main); padding: 6px; border-radius: 4px; font-family: inherit; cursor: pointer; width: 100%;">
                    `;
                }

                // Modern Responsive Grid Card Structure Block
                const cardHTML = `
                    <div class="task-card">
                        <div class="card-header" style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:12px;">
                            <h4 class="card-title" style="margin:0; font-size:16px;">${task.title}</h4>
                            <span class="status-badge" style="padding: 4px 10px; border-radius: 20px; font-size:11px; font-weight:700; text-transform:uppercase; ${statusStyle}">${task.status}</span>
                        </div>
                        <div class="card-details" style="display:flex; flex-direction:column; gap:8px; font-size:13px; color:var(--text-muted); margin-bottom:15px;">
                            <span><strong>Task ID:</strong> #${task.id}</span>
                            <span><strong>Priority Level:</strong> ${task.priority}/10</span>
                            <span><strong>Schedule:</strong> <br>${timeColumnData}</span>
                        </div>
                        <div class="card-actions" style="border-top:1px solid var(--border-color); padding-top:12px;">
                            ${actionButtonHtml}
                        </div>
                    </div>
                `;

                if (isCompletedTask) {
                    completedGrid.innerHTML += cardHTML;
                } else {
                    activeGrid.innerHTML += cardHTML;
                }
            });

            // Dynamically instantiate clean premium pickers
            flatpickr("input[type='datetime-local']", {
                enableTime: true,
                dateFormat: "Y-m-d H:i",
                theme: document.documentElement.getAttribute('data-theme') === 'dark' ? "dark" : "light"
            });

            updateDashboardMetrics(tasks);
        })
        .catch(error => console.error('Error fetching tasks', error));
}

// ==========================================
// 4. PIPELINE FORM CONTROLLER SUBSYSTEM
// ==========================================
function handleTaskSubmit(event) {
    event.preventDefault();

    const titleInput = document.querySelector('input[name="title"]');
    const titleValue = titleInput.value.trim();
    const priorityInput = document.querySelector('input[name="priority"]');
    const priorityValue = parseInt(priorityInput.value);

    const statusSelect = document.querySelector('select[name="status"]');
    const statusValue = statusSelect ? statusSelect.value.toUpperCase() : "PENDING";

    const dueDateInput = document.querySelector('input[name="dueDate"]');
    let dueDateValue = dueDateInput.value;

    if (dueDateValue) {
        dueDateValue = dueDateValue.replace(' ', 'T');
        if (dueDateValue.length === 16) {
            dueDateValue += ':00';
        }
    } else {
        dueDateValue = null;
    }

    if (titleValue === "") { alert("Task name is required!"); return; }
    if (priorityValue < 1 || priorityValue > 10 || isNaN(priorityValue)){ alert("Priority must be between 1 to 10"); return; }

    const newTask = {
        title: titleValue,
        status: statusValue,
        priority: priorityValue,
        dueDate: dueDateValue
    };

    console.log("🚀 Sending payload to backend:", newTask);

    fetch('/tasks',{
        method: 'POST',
        headers: {
            'Content-Type':'application/json',
            'X-User-Id': currentUserId || '1'
        },
        body: JSON.stringify(newTask)
    })
        .then(async response => {
            if (!response.ok) {
                console.error("❌ Backend rejected payload with status:", response.status);
                return;
            }
            loadTasks();
            titleInput.value = '';
            priorityInput.value = '';
            dueDateInput.value = '';
        })
        .catch(error => console.error('Network error:', error));
}

// ==========================================
// 5. SECURITY & ACCESS IDENTITY CONTROLLER SYSTEM
// ==========================================
function handleRegister() {
    const usernameInput = document.getElementById('auth-username').value.trim();
    const passwordInput = document.getElementById('auth-password').value.trim();
    if (!usernameInput || !passwordInput) { alert("Enter credentials!"); return; }

    fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: usernameInput, password: passwordInput })
    })
        .then(async response => {
            const data = await response.json();
            alert(data.message);
            if(response.ok) {
                document.getElementById('auth-username').value = '';
                document.getElementById('auth-password').value = '';
            }
        });
}

function handleLogin() {
    const usernameInput = document.getElementById('auth-username').value.trim();
    const passwordInput = document.getElementById('auth-password').value.trim();
    if (!usernameInput || !passwordInput) { alert("Enter credentials!"); return; }

    fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: usernameInput, password: passwordInput })
    })
        .then(async response => {
            if (!response.ok) { const data = await response.json(); alert(data.message); return; }
            const data = await response.json();
            localStorage.setItem('currentUserId', data.userId);
            localStorage.setItem('currentUsername', data.username);
            currentUserId = data.userId;
            currentUsername = data.username;

            updateAuthUI();
            loadTasks();
            setupPushNotifications();
            alert("Welcome, " + currentUsername + "!");
        });
}

function updateAuthUI() {
    const loginScreen = document.getElementById('login-screen');
    const mainContainer = document.querySelector('.main-container');
    const desktopNav = document.querySelector('.desktop-nav');
    const mobileNav = document.querySelector('.mobile-nav');

    const taskForm = document.getElementById('task-form');
    const sidebarMsg = document.getElementById('sidebar-guest-msg');

    if (!loginScreen || !mainContainer) return;

    if (currentUserId) {
        loginScreen.style.display = "none";
        mainContainer.style.display = "flex";

        if (taskForm) taskForm.style.display = "flex";
        if (sidebarMsg) sidebarMsg.style.display = "none";

        if (desktopNav && mobileNav) {
            desktopNav.style.display = window.innerWidth > 768 ? "flex" : "none";
            mobileNav.style.display = window.innerWidth > 768 ? "none" : "block";
        }

        switchTab('active');
    } else {
        loginScreen.style.display = "flex";
        mainContainer.style.display = "none";
        if (desktopNav && mobileNav) {
            desktopNav.style.display = "none";
            mobileNav.style.display = "none";
        }

        if (taskForm) taskForm.style.display = "none";
        if (sidebarMsg) sidebarMsg.style.display = "block";
    }
}

function logout() {
    localStorage.removeItem('currentUserId');
    localStorage.removeItem('currentUsername');
    currentUserId = null;
    currentUsername = "Guest";
    updateAuthUI();
    loadTasks();
    alert("Logged out successfully!");
}

// ==========================================
// 6. DASHBOARD ANALYTICS CHART SUBSYSTEM
// ==========================================
function updateDashboardMetrics(tasks) {
    if (!currentUserId) return;

    let total = tasks.length;
    let pendingOrWorking = tasks.filter(t => t.status.toLowerCase() === 'pending' || t.status.toLowerCase() === 'working').length;

    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth();

    let completedThisMonthCount = 0;
    let completedLastMonthCount = 0;

    tasks.forEach(t => {
        if (t.status.toLowerCase() === 'completed' && t.completedAt) {
            const compDate = new Date(t.completedAt);

            if (compDate.getFullYear() === currentYear && compDate.getMonth() === currentMonth) {
                completedThisMonthCount++;
            } else if (
                (currentMonth === 0 && compDate.getFullYear() === currentYear - 1 && compDate.getMonth() === 11) ||
                (compDate.getFullYear() === currentYear && compDate.getMonth() === currentMonth - 1)
            ) {
                completedLastMonthCount++;
            }
        }
    });

    document.getElementById('stat-total-tasks').innerText = total;
    document.getElementById('stat-pending-tasks').innerText = pendingOrWorking;
    document.getElementById('stat-completed-month').innerText = completedThisMonthCount;

    let pendingCount = tasks.filter(t => t.status.toLowerCase() === 'pending').length;
    let workingCount = tasks.filter(t => t.status.toLowerCase() === 'working').length;
    let totalCompletedCount = tasks.filter(t => t.status.toLowerCase() === 'completed').length;

    if (pieChartInstance) pieChartInstance.destroy();
    if (barChartInstance) barChartInstance.destroy();

    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
    const labelColor = isDark ? '#9ca3af' : '#4b5563';
    const gridColor = isDark ? '#374151' : '#e5e7eb';

    const ctxPie = document.getElementById('statusPieChart').getContext('2d');
    pieChartInstance = new Chart(ctxPie, {
        type: 'doughnut',
        data: {
            labels: ['Pending', 'Working', 'Completed'],
            datasets: [{
                data: [pendingCount, workingCount, totalCompletedCount],
                backgroundColor: ['#6b7280', '#eab308', '#22c55e'],
                borderColor: isDark ? '#111827' : '#ffffff',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'bottom', labels: { color: labelColor } }
            }
        }
    });

    const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    const lastMonthLabel = monthNames[currentMonth === 0 ? 11 : currentMonth - 1];
    const thisMonthLabel = monthNames[currentMonth];

    const ctxBar = document.getElementById('monthlyBarChart').getContext('2d');
    barChartInstance = new Chart(ctxBar, {
        type: 'bar',
        data: {
            labels: [lastMonthLabel + ' (Last Month)', thisMonthLabel + ' (This Month)'],
            datasets: [{
                label: 'Tasks Completed',
                data: [completedLastMonthCount, completedThisMonthCount],
                backgroundColor: ['#6b7280', '#22c55e'],
                borderRadius: 6,
                maxBarThickness: 45
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1, color: labelColor },
                    grid: { color: gridColor }
                },
                x: {
                    ticks: { color: labelColor },
                    grid: { display: false }
                }
            }
        }
    });
}

// ==========================================
// 7. IN-LINE ACTION MODIFIERS
// ==========================================
function changeStatus(id , newStatus){
    fetch(`/tasks/${id}/status?status=${newStatus}`,{
        method: 'PUT',
        headers: { 'X-User-Id': currentUserId || 1 }
    }).then(response => { if(response.ok) loadTasks(); });
}

function deleteTask(id){
    if(confirm("Are you sure you want to delete this task?")){
        fetch(`/tasks/${id}`,{
            method: 'DELETE',
            headers: { 'X-User-Id': currentUserId || 1 }
        }).then(response => { if(response.ok) loadTasks(); });
    }
}

function updateDueDate(taskId, newDateValue) {
    if (!newDateValue) return;

    console.log("📅 Rescheduling task ID: " + taskId + " to " + newDateValue);

    fetch(`/tasks/${taskId}/due-date?date=${newDateValue}`, {
        method: 'PUT',
        headers: {
            'X-User-Id': currentUserId || 1
        }
    })
        .then(async response => {
            if (response.ok) {
                console.log("💾 SUCCESS: Database updated.");
                loadTasks();
            } else {
                alert("Failed to update the time status.");
            }
        })
        .catch(error => console.error("Error updating due date:", error));
}

// ==========================================
// 8. INTERACTIVE SPA NAVIGATION ROUTERS
// ==========================================
function toggleMenu() {
    document.getElementById("mobile-dropdown").classList.toggle("show");
}

window.onclick = function(event) {
    if (!event.target.matches('.kebab-btn')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        for (var i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}

function switchTab(tabName) {
    console.log("Switching to tab: " + tabName);

    const activeSec = document.getElementById('active-tasks-section');
    const compSec = document.getElementById('completed-tasks-section');
    const analyticsSec = document.getElementById('analytics-dashboard');

    if (!activeSec || !compSec || !analyticsSec) return;

    activeSec.style.display = 'none';
    compSec.style.display = 'none';
    analyticsSec.style.display = 'none';

    if (tabName === 'active') activeSec.style.display = 'block';
    if (tabName === 'completed') compSec.style.display = 'block';
    if (tabName === 'analytics') analyticsSec.style.display = 'block';

    const tabButtons = document.querySelectorAll('.desktop-nav .tab-btn');
    tabButtons.forEach(btn => {
        btn.style.borderBottom = "none";
        btn.style.color = "var(--text-main)";
        if (btn.innerText.toLowerCase().includes(tabName)) {
            btn.style.borderBottom = "3px solid #3b82f6";
            btn.style.color = "#3b82f6";
        }
    });

    const mobileDropdown = document.getElementById("mobile-dropdown");
    if (mobileDropdown) mobileDropdown.classList.remove("show");
}

window.addEventListener('resize', () => {
    if (currentUserId) {
        const desktopNav = document.querySelector('.desktop-nav');
        const mobileNav = document.querySelector('.mobile-nav');
        if (desktopNav && mobileNav) {
            desktopNav.style.display = window.innerWidth > 768 ? "flex" : "none";
            mobileNav.style.display = window.innerWidth > 768 ? "none" : "block";
        }
    }
});

// ==========================================
// 9. BACKGROUND MESSAGE ENGINES (FIREBASE)
// ==========================================
const firebaseConfig = {
    apiKey: "AIzaSyA5uop4pkbD10GD2S-KnyPZigHWyLQmzhI",
    authDomain: "smarttaskplanner-769f3.firebaseapp.com",
    projectId: "smarttaskplanner-769f3",
    storageBucket: "smarttaskplanner-769f3.firebasestorage.app",
    messagingSenderId: "288776496731",
    appId: "1:288776496731:web:f1c448834fbac70537417e"
};

if (!firebase.apps.length) {
    firebase.initializeApp(firebaseConfig);
}
const messaging = firebase.messaging();

function setupPushNotifications() {
    if (!currentUserId) return;
    if (!('serviceWorker' in navigator) || !('Notification' in window)) {
        console.warn('Push notifications are not supported by this browser.');
        return;
    }

    Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
            navigator.serviceWorker.register('/firebase-messaging-sw.js')
                .then(registration => {
                    messaging.getToken({
                        serviceWorkerRegistration: registration,
                        vapidKey: 'BKfByGWsP8J7jKjy6BF046pruU4nV_JKZ3KUb4pMW_15_i5vUnmnrGcsjDNOaBXd_3DBGotqrGOELxrp3IoUssY'
                    })
                        .then(currentToken => {
                            if (currentToken) {
                                sendTokenToBackend(currentToken);
                            }
                        })
                        .catch(err => console.error('❌ AN ERROR OCCURRED WHILE RETRIEVING FCM TOKEN: ', err));
                });
        }
    });
}

function sendTokenToBackend(token) {
    const userId = currentUserId || 1;
    fetch(`/api/auth/update-fcm-token?userId=${userId}&token=${token}`, {
        method: 'PUT'
    })
        .then(response => {
            if (response.ok) {
                console.log("💾 MULTI-DEVICE DEVICE TOKEN SYNCED.");
            }
        })
        .catch(err => console.error("Error updating tracking configuration token:", err));
}

// 🟢 NEW: Premium Dynamic HTML Toast Alert Engine
function showToastAlert(title, body) {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = 'toast-card';

    toast.innerHTML = `
        <h4>🔔 ${title}</h4>
        <p>${body}</p>
    `;

    container.appendChild(toast);

    // Auto-remove element from memory after fade animation closes
    setTimeout(() => {
        toast.remove();
    }, 4000);
}

// 🟢 NEW: Integrated Foreground Messaging Routing
messaging.onMessage((payload) => {
    console.log('🔔 Foreground message received: ', payload);

    // 1. Pop up our beautiful design Toast Alert!
    showToastAlert(payload.notification.title, payload.notification.body);

    // 2. Keep the OS push banner working as a secondary indicator
    new Notification(payload.notification.title, {
        body: payload.notification.body,
        icon: 'https://cdn-icons-png.flaticon.com/512/3119/3119338.png'
    });

    // 3. Live update the workflow cards pipeline grid on screen instantly!
    loadTasks();
});