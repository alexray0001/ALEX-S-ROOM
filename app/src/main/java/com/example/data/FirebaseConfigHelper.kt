package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfigHelper {
  private const val TAG = "FirebaseConfigHelper"

  fun isFirebaseAvailable(context: Context): Boolean {
    return try {
      val apps = FirebaseApp.getApps(context)
      apps.isNotEmpty()
    } catch (e: Exception) {
      Log.w(TAG, "Firebase is not configured yet: ${e.message}")
      false
    }
  }

  fun getAuth(context: Context): FirebaseAuth? {
    return if (isFirebaseAvailable(context)) {
      try {
        FirebaseAuth.getInstance()
      } catch (e: Exception) {
        null
      }
    } else {
      null
    }
  }

  fun getFirestore(context: Context): FirebaseFirestore? {
    return if (isFirebaseAvailable(context)) {
      try {
        FirebaseFirestore.getInstance()
      } catch (e: Exception) {
        null
      }
    } else {
      null
    }
  }

  const val FIREBASE_SETUP_INSTRUCTIONS = """
Step-by-Step Android Studio & Firebase Setup:

1. Open this project in Android Studio (Giraffe, Hedgehog, or Ladybug).
2. Go to https://console.firebase.google.com and create a project named 'AlexsRoom'.
3. Click 'Add App' -> Android.
4. Set Android package name to:
   com.aistudio.alexsroom.vztk
5. Download 'google-services.json'.
6. Move 'google-services.json' into the '/app/' directory of this project.
7. In Firebase Console:
   - Enable 'Authentication' -> Email/Password & Google.
   - Enable 'Cloud Firestore' -> Start in Test Mode.
8. Rebuild and Run the app in Android Studio!
"""
}
