package com.primeme.app.data.auth

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.primeme.app.R
import kotlinx.coroutines.tasks.await

class GoogleSignInManager(
    context: Context
) {

    private val credentialManager =
        CredentialManager.create(context.applicationContext)

    private val firebaseAuth =
        FirebaseAuth.getInstance()

    suspend fun signIn(activityContext: Context): Result<Unit> {
        return try {
            Log.d(TAG, "Starting Google Sign-In")

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(
                    activityContext.getString(
                        R.string.default_web_client_id
                    )
                )
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Requesting Google credential")

            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )

            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type ==
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                val firebaseCredential =
                    GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken,
                        null
                    )

                firebaseAuth
                    .signInWithCredential(firebaseCredential)
                    .await()

                Log.d(TAG, "Google Sign-In successful")

                Result.success(Unit)
            } else {
                Log.w(TAG, "Credential was not a Google ID token")

                Result.failure(
                    IllegalStateException(
                        "Invalid Google credential"
                    )
                )
            }

        } catch (exception: GoogleIdTokenParsingException) {
            Log.e(
                TAG,
                "Unable to parse Google ID token",
                exception
            )

            Result.failure(exception)

        } catch (exception: Exception) {
            Log.e(
                TAG,
                "Google Sign-In failed",
                exception
            )

            Result.failure(exception)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        Log.d(TAG, "Firebase user signed out")
    }

    fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    companion object {
        private const val TAG = "GoogleSignInManager"
    }
}
