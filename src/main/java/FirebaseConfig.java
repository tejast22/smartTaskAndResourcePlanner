import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Prevents re-initializing if the application restarts internally
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;

                // Read the secure JSON string from Render environment variables
                String rawJson = System.getenv("FIREBASE_CONFIG_JSON");

                if (rawJson != null && !rawJson.isEmpty()) {
                    System.out.println("🌐 Production Environment Detected: Loading Firebase from environment variables...");
                    serviceAccount = new ByteArrayInputStream(rawJson.getBytes(StandardCharsets.UTF_8));
                } else {
                    System.out.println("💻 Local Environment Detected: Loading Firebase from resource file folder...");
                    // Fall back to your local file path when coding on your computer
                    serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Success: Firebase App [DEFAULT] has been fully initialized!");
            }
        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR: Failed to initialize Firebase App: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
