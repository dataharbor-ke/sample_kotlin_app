# Credence Sample Kotlin App

A simple Android app demonstrating integration of the **CredenceSDK**.
It shows how to request permissions, handle consent, and initialize the SDK.

---

## Usage

1. Open the project in **Android Studio**.
2. Add your organization key to `gradle.properties`:

   ```properties
   CREDENCE_ORG_KEY=your_org_key_here
   ```
3. Run the app on a **physical device** (preferred) or emulator.
4. Grant the **READ_SMS** permission when prompted.
5. Accept the consent dialog to initialize the SDK.

The SDK will register the client and upload SMS data.

---

## Testing with an Emulator

Since emulators don’t receive real SMS messages, you can **inject mock SMS** for testing:

1. Open **Device Manager** → select your running emulator.

2. Click **“...”** → choose **“Phone”** → **“SMS”** tab.

3. Send a mock transaction message, for example:

   ```
   MPESA confirmed. Ksh 2,000 sent to John Doe 254712345678 on 18/10/2025.
   ```

4. The SDK will process the message automatically and upload it once network connectivity is available.

---

## Notes

* `READ_SMS` is requested at runtime (not in the manifest).
* Only `INTERNET` permission should be declared in `AndroidManifest.xml`.
* Use a physical device for the most accurate behavior.
