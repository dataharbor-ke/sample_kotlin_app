package co.ke.dataharbor.samplekotlinapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vibelinc.credencesdk.CredenceSDK
import com.vibelinc.credencesdk.consent.ConsentDialog
import java.util.UUID

/**
 * Test app demonstrating how to integrate and test the Credence SDK.
 *
 * Core developer workflow:
 * 1. Request the READ_SMS permission at runtime (required for transaction analysis).
 * 2. Initialize the SDK using `CredenceSDK.init()`.
 * 3. Start the SDK using `CredenceSDK.start()` once initialization succeeds.
 *
 * The SDK handles all backend communication, consent storage, and background syncing.
 * The developer only needs to manage permission checks and initialization calls.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQ_SMS = 1001
        private const val TAG = "CredenceTestApp"
    }

    // User info (sample data for testing)
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var externalId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Step 1: Generate mock user data for testing
        generateTestUser()

        // Step 2: Request SMS permission as soon as the app starts
        // Developers should do this before initializing the SDK.
        checkAndRequestSmsPermission()

        // Step 3: Attach button listeners for SDK test options
        findViewById<Button>(R.id.btn_default_dialog).setOnClickListener {
            // Use the SDK’s built-in consent dialog
            if (hasSmsPermission()) startSdkWithDefaultDialog() else checkAndRequestSmsPermission()
        }

        findViewById<Button>(R.id.btn_custom_dialog).setOnClickListener {
            // Use a custom consent dialog (app-defined UI)
            if (hasSmsPermission()) startSdkWithCustomDialog() else checkAndRequestSmsPermission()
        }
    }

    /**
     * Generates a temporary test user for demo purposes.
     * In production, this data should come from your authenticated user.
     */
    private fun generateTestUser() {
        val id = UUID.randomUUID().toString().take(6)
        name = "User_$id"
        email = "user_$id@test.dev"
        phone = "07${(10000000..99999999).random()}"
        externalId = "EXT_$id"
        Log.i(TAG, "Generated test user → $name, $email, $phone, $externalId")
    }

    /**
     * Utility method to check if the READ_SMS permission is already granted.
     */
    private fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests the READ_SMS permission from the user.
     * This permission is mandatory for the SDK to read and analyze transaction SMS messages.
     */
    private fun checkAndRequestSmsPermission() {
        if (!hasSmsPermission()) {
            Log.i(TAG, "Requesting READ_SMS permission from user.")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                REQ_SMS
            )
        } else {
            Log.i(TAG, "READ_SMS permission already granted.")
        }
    }

    /**
     * Receives the user’s response to the permission request.
     * If granted, the SDK can now be initialized.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == REQ_SMS && results.isNotEmpty() &&
            results[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "READ_SMS permission granted by user.")
        } else {
            Log.e(TAG, "READ_SMS permission denied. SDK cannot function without it.")
        }
    }

    // --------------------------------------------------------------------------
    // SDK Initialization Options
    // --------------------------------------------------------------------------

    /**
     * Example 1: Initialize the SDK using the default built-in consent dialog.
     *
     * The SDK will automatically handle:
     * - Displaying the consent screen
     * - Registering the user with the backend
     * - Uploading and syncing SMS messages
     */
    private fun startSdkWithDefaultDialog() {
        Log.i(TAG, "Initializing CredenceSDK with default dialog.")

        CredenceSDK.init(
            context = this,
            orgKey = BuildConfig.CREDENCE_ORG_KEY,  // Provided by DataHarbor
            name = name,
            email = email,
            phone = phone,
            externalId = externalId,
            consentDialog = null // null means use SDK's default dialog
        ) { success, error ->
            if (success) {
                Log.i(TAG, "CredenceSDK initialized successfully (default dialog).")
                CredenceSDK.start(this)
            } else {
                Log.e(TAG, "CredenceSDK initialization failed: $error")
            }
        }
    }

    /**
     * Example 2: Initialize the SDK with a custom consent dialog.
     *
     * The app defines the consent UI (branding, colors, copy),
     * while the SDK handles backend logic and storage once the user accepts.
     */
    private fun startSdkWithCustomDialog() {
        Log.i(TAG, "Initializing CredenceSDK with custom consent dialog.")

        val customDialog = ConsentDialog(
            context = this,
            titleText = "Custom Consent",
            introText = "We’ll analyze your SMS messages for financial insights.",
            smsTitleText = "SMS Access",
            smsDescText = "We only scan messages to detect and process transactions.",
            buttonText = "Agree"
        )

        CredenceSDK.init(
            context = this,
            orgKey = BuildConfig.CREDENCE_ORG_KEY,
            name = name,
            email = email,
            phone = phone,
            externalId = externalId,
            consentDialog = customDialog
        ) { success, error ->
            if (success) {
                Log.i(TAG, "CredenceSDK initialized successfully (custom dialog).")
                CredenceSDK.start(this)
            } else {
                Log.e(TAG, "CredenceSDK initialization failed: $error")
            }
        }
    }
}