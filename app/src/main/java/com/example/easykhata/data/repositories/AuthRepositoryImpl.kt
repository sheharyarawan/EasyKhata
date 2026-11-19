package com.example.easykhata.data.repositories
import android.app.Activity
import com.example.easykhata.data.models.auth.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    private var userInfoListener: ListenerRegistration? = null
    override fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onVerificationCompleted: (PhoneAuthCredential) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(
                credential: PhoneAuthCredential
            ) {
                onVerificationCompleted(credential)
            }
            override fun onVerificationFailed(
                exception: FirebaseException
            ) {
                onError(exception)
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId)
            }
        }
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    override fun verifyOtp(
        verificationId: String,
        otp: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(
            verificationId,
            otp
        )
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(
                        task.exception ?: Exception("OTP verification failed")
                    )
                }
            }
    }
    override suspend fun hasBusiness(uid: String): Boolean {
        val snapshot = firestore
            .collection("users")
            .document(uid)
            .collection("business")
            .limit(1)
            .get()
            .await()
        return !snapshot.isEmpty
    }
    override fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }
    override fun getUserInfo(
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        firestore.collection("users")
            .document(currentUser.uid)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }
                if (document == null || !document.exists()) {
                    onFailure(Exception("User information not found"))
                    return@addSnapshotListener
                }
                val user = document.toObject(User::class.java)
                if (user != null) {
                    onSuccess(user.copy(userId = document.id))
                } else {
                    onFailure(Exception("Failed to retrieve user information"))
                }
            }
    }
    override fun removeUserInfoListener() {
        userInfoListener?.remove()
        userInfoListener = null
    }
}