package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.expense.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
class ExpenseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ExpenseRepository {
    private var customerExpensesListener: ListenerRegistration? = null
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
    override fun getCustomerExpenses(
        businessId: String,
        customerId: String,
        onSuccess: (List<Expense>) -> Unit,
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
        customerExpensesListener?.remove()
        customerExpensesListener = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("expenses")
            .whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Expense::class.java)?.copy(
                        id = document.id
                    )
                } ?: emptyList()
                onSuccess(expenses)
            }
    }
    override fun removeCustomerExpensesListener() {
        customerExpensesListener?.remove()
        customerExpensesListener = null
    }
}