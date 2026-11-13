package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.account.WasooliReminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
class WasooliRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : WasooliRepository {
    private var remindersListener: ListenerRegistration? = null
    override fun addReminder(
        businessId: String,
        reminder: WasooliReminder,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onFailure(Exception("User not authenticated"))
            return
        }
        if (businessId.isBlank() || reminder.customerId.isBlank()) {
            onFailure(Exception("Business or customer not selected"))
            return
        }
        val reminderRef = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("wasooliReminders")
            .document(reminder.customerId)
        val reminderToSave = reminder.copy(
            id = reminderRef.id
        )
        reminderRef.set(reminderToSave)
            .addOnSuccessListener {
                onSuccess(reminderRef.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun observeReminders(
        businessId: String,
        onSuccess: (List<WasooliReminder>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onFailure(Exception("User not authenticated"))
            return
        }
        if (businessId.isBlank()) {
            onFailure(Exception("Business not selected"))
            return
        }
        remindersListener?.remove()
        remindersListener = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("wasooliReminders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }
                val reminders = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(WasooliReminder::class.java)?.copy(
                        id = document.id
                    )
                } ?: emptyList()
                onSuccess(reminders)
            }
    }
    override fun removeRemindersListener() {
        remindersListener?.remove()
        remindersListener = null
    }
    override fun deleteReminder(
        businessId: String,
        customerId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onFailure(Exception("User not authenticated"))
            return
        }
        if (businessId.isBlank() || customerId.isBlank()) {
            onFailure(Exception("Business or customer not selected"))
            return
        }
        firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("wasooliReminders")
            .document(customerId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}