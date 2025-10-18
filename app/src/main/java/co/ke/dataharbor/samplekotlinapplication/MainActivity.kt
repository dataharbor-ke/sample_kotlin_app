package co.ke.dataharbor.samplekotlinapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vibelinc.credencesdk.CredenceSDK
import com.vibelinc.credencesdk.consent.ConsentDialog

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQ_SMS = 1001
        private const val TAG = "CredenceSampleApp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        checkSmsPermission()
    }

    /** Check and request READ_SMS permission at runtime */
    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                REQ_SMS
            )
        } else {
            initializeWithCustomDialog()   // or initializeWithDefaultDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_SMS && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            initializeWithCustomDialog()
        } else {
            Log.e(TAG, "READ_SMS permission denied.")
        }
    }

    /** --- Option 1: Default SDK consent dialog --- */
    private fun initializeWithDefaultDialog() {
        CredenceSDK.init(
            context = this,
            orgKey = BuildConfig.CREDENCE_ORG_KEY,
            name = "",
            email = "",
            phone = "",
            externalId = "",
            consentDialog = null      // use SDK’s internal dialog
        ) { success, error ->
            if (success) Log.i(TAG, "SDK initialized (default dialog).")
            else Log.e(TAG, "SDK init failed: $error")
        }
    }

    /** --- Option 2: Custom ConsentDialog for demo --- */
    private fun initializeWithCustomDialog() {
        val customDialog = ConsentDialog(
            context = this,
            backgroundColorHex = "#FFFFFF",
            titleColorHex = "#000000",
            messageColorHex = "#333333",
            buttonColorHex = "#1976D2",
            buttonCornerRadius = 16f,
            iconColorHex = "#1976D2",
            closeIconColorHex = "#999999",
            titleText = "Data Usage Consent",
            introText = "We securely analyze your SMS transaction messages to provide insights.",
            smsTitleText = "SMS Access",
            smsDescText = "Used only to detect financial transactions.",
            privacyTitleText = "Privacy",
            privacyDescText = "Your data is encrypted and never shared.",
            buttonText = "Agree & Continue",
            onConsentGiven = {
                Log.i(TAG, "Consent accepted → initializing SDK.")
                startSdk()
            }
        )

        CredenceSDK.init(
            context = this,
            orgKey = BuildConfig.CREDENCE_ORG_KEY,
            name = "",
            email = "",
            phone = "",
            externalId = "",
            consentDialog = customDialog
        ) { success, error ->
            if (success) Log.i(TAG, "SDK initialized (custom dialog).")
            else Log.e(TAG, "SDK init failed: $error")
        }
    }

    /** Trigger background syncing once registered */
    private fun startSdk() {
        CredenceSDK.start(this)
    }
}