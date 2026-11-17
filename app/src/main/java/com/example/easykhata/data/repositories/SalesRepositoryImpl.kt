package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.ExpenseDirection
import com.example.easykhata.data.models.PaymentMethod
import com.example.easykhata.data.models.sales.Sale
import com.example.easykhata.data.models.expense.Expense
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
class SalesRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : SalesRepository {
    override fun addSale(
        businessId: String,
        sale: Sale,
        onSuccess: (String) -> Unit,
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
        if (sale.items.isEmpty()) {
            onFailure(Exception("Sale must contain at least one item"))
            return
        }
        if (sale.paymentMethod == PaymentMethod.UDHAAR &&
            (sale.customerId.isBlank() || sale.customerName.isBlank())
        ) {
            onFailure(Exception("Customer is required for Udhaar sale"))
            return
        }
        val businessRef = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
        val saleRef = businessRef
            .collection("sales")
            .document()
        val invoiceCounterRef = businessRef
            .collection("salesMetadata")
            .document("invoiceCounter")
        val expenseRef = businessRef
            .collection("expenses")
            .document()
        firebaseFirestore.runTransaction { transaction ->
            val counterSnapshot = transaction.get(invoiceCounterRef)
            val lastInvoiceNumber =
                counterSnapshot.getLong("lastInvoiceNumber") ?: 0L
            val nextInvoiceNumber = lastInvoiceNumber + 1
            val saleToSave = sale.copy(
                id = saleRef.id,
                invoiceNumber = nextInvoiceNumber.toString()
            )
            transaction.set(
                invoiceCounterRef,
                mapOf("lastInvoiceNumber" to nextInvoiceNumber)
            )
            transaction.set(saleRef, saleToSave)
            if (sale.paymentMethod == PaymentMethod.UDHAAR) {
                val expense = Expense(
                    id = expenseRef.id,
                    customerId = sale.customerId,
                    date = Timestamp.now(),
                    description = "Sale #$nextInvoiceNumber",
                    amount = saleToSave.totalPrice,
                    direction = ExpenseDirection.DIYE
                )
                transaction.set(expenseRef, expense)
            }
            saleRef.id
        }.addOnSuccessListener { saleId ->
            onSuccess(saleId)
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
    override fun getSale(
        businessId: String,
        saleId: String,
        onSuccess: (Sale) -> Unit,
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
        if (saleId.isBlank()) {
            onFailure(Exception("Invalid sale ID"))
            return
        }
        firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("sales")
            .document(saleId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    onFailure(Exception("Sale not found"))
                    return@addOnSuccessListener
                }
                val sale = document.toObject(Sale::class.java)
                if (sale == null) {
                    onFailure(Exception("Failed to read sale details"))
                    return@addOnSuccessListener
                }
                onSuccess(sale.copy(id = document.id))
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
