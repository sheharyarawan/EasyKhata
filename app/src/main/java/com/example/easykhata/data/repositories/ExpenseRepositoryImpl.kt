package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.expense.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
class ExpenseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ExpenseRepository {
    override fun addExpense(
        businessId: String,
        expense: Expense,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            onFailure(Exception("User not authenticated"))
            return
        }
        if (businessId.isBlank()) {
            onFailure(Exception("Business not selected"))
            return
        }
        val expenseRef = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("expenses")
            .document()
        val expenseToSave = expense.copy(
            id = expenseRef.id
        )
        expenseRef.set(expenseToSave)
            .addOnSuccessListener {
                onSuccess(expenseRef.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}