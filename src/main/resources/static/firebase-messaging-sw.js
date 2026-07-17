// Load the Firebase compatibility scripts into the background thread safely
importScripts('https://www.gstatic.com/firebasejs/9.22.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.22.0/firebase-messaging-compat.js');

// ⚠️ IMPORTANT: I put your real Firebase keys back in here so it can connect to FCM!
const firebaseConfig = {
    apiKey: "AIzaSyA5uop4pkbD10GD2S-KnyPZigHWyLQmzhI",
    authDomain: "smarttaskplanner-769f3.firebaseapp.com",
    projectId: "smarttaskplanner-769f3",
    storageBucket: "smarttaskplanner-769f3.firebasestorage.app",
    messagingSenderId: "288776496731",
    appId: "1:288776496731:web:f1c448834fbac70537417e"
};

// Initialize Firebase background instance cleanly using compatibility format
firebase.initializeApp(firebaseConfig);

try {
    // Defined globally within the file context to prevent ReferenceErrors
    const messaging = firebase.messaging();

    // Catch incoming messages when your website dashboard tab is closed or minimized
    messaging.onBackgroundMessage((payload) => {
        console.log('🤖 Background message received in worker: ', payload);

        const notificationTitle = payload.notification.title || "⏰ Task Reminder";
        const notificationOptions = {
            body: payload.notification.body || "Your scheduled task requires attention.",
            icon: 'https://cdn-icons-png.flaticon.com/512/3119/3119338.png',
            badge: 'https://cdn-icons-png.flaticon.com/512/3119/3119338.png'
        };

        self.registration.showNotification(notificationTitle, notificationOptions);
    });
} catch (error) {
    console.error("Failed to initialize messaging engine inside Service Worker:", error);
}

// Wakes up the background thread when a user clicks on the native taskbar notification card
self.addEventListener('notificationclick', (event) => {
    // DYNAMIC DOMAIN: Automatically detects if you are on localhost or Render!
    const targetUrl = self.location.origin;
    console.log(`🎯 Notification clicked! Target URL: ${targetUrl}`);

    // 1. Immediately dismiss the notification banner from the taskbar
    event.notification.close();

    // 2. Query all active browser tabs to see if our dashboard is already running
    event.waitUntil(
        clients.matchAll({ type: 'window', includeUncontrolled: true })
            .then((windowClients) => {
                // Check if our website domain is already open in any tab
                for (let i = 0; i < windowClients.length; i++) {
                    const client = windowClients[i];
                    if (client.url.startsWith(targetUrl) && 'focus' in client) {
                        // Bring the user straight to that already-open tab
                        return client.focus();
                    }
                }
                // If the tab was closed, open a brand-new one pointing to our dynamic domain
                if (clients.openWindow) {
                    return clients.openWindow(targetUrl);
                }
            })
    );
});