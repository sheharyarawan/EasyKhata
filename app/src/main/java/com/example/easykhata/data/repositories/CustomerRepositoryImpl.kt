package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.customers.Customer
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
class CustomerRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : CustomerRepository {
    private var customersListener: ListenerRegistration? = null
    override fun addCustomer(
        businessId: String,
        customer: Customer,
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
        val customerRef = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("customers")
            .document()
        val customerToSave = customer.copy(
            id = customerRef.id,
            createdAt = Timestamp.now()
        )
        customerRef.set(customerToSave)
            .addOnSuccessListener {
                onSuccess(customerRef.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun getCustomers(
        businessId: String,
        onSuccess: (List<Customer>) -> Unit,
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
        firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("customers")
            .get()
            .addOnSuccessListener { snapshot ->
                val customers = snapshot.toObjects(Customer::class.java)
                onSuccess(customers)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun getCustomerById(
        businessId: String,
        customerId: String,
        onSuccess: (Customer) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
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
            .collection("customers")
            .document(customerId)
            .get()
            .addOnSuccessListener { document ->

                val customer = document.toObject(Customer::class.java)

                if (customer != null) {
                    onSuccess(customer.copy(id = document.id))
                } else {
                    onFailure(Exception("Customer not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun updateCustomer(
        businessId: String,
        customer: Customer,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            onFailure(Exception("User not authenticated"))
            return
        }

        if (businessId.isBlank() || customer.id.isBlank()) {
            onFailure(Exception("Business or customer not selected"))
            return
        }

        firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("customers")
            .document(customer.id)
            .update(
                mapOf(
                    "name" to customer.name,
                    "phone" to customer.phone
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun observeCustomers(
        businessId: String,
        onSuccess: (List<Customer>) -> Unit,
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
        customersListener?.remove()
        customersListener = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("customers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }
                val customers = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Customer::class.java)?.copy(
                        id = document.id
                    )
                } ?: emptyList()
                onSuccess(customers)
            }
    }
    override fun removeCustomersListener() {
        customersListener?.remove()
        customersListener = null
    }
    override fun deleteCustomer(
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
        val businessRef = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
        val expensesRef = businessRef.collection("expenses")
        expensesRef
            .whereEqualTo("customerId", customerId)
            .get()
            .addOnSuccessListener { expensesSnapshot ->
                val deleteTasks = expensesSnapshot.documents
                    .chunked(500)
                    .map { documents ->
                        val batch = firebaseFirestore.batch()
                        documents.forEach { document ->
                            batch.delete(document.reference)
                        }
                        batch.commit()
                    }
                com.google.android.gms.tasks.Tasks.whenAll(deleteTasks)
                    .addOnSuccessListener {
                        val finalBatch = firebaseFirestore.batch()
                        val reminderRef = businessRef
                            .collection("wasooliReminders")
                            .document(customerId)
                        val customerRef = businessRef
                            .collection("customers")
                            .document(customerId)
                        finalBatch.delete(reminderRef)
                        finalBatch.delete(customerRef)
                        finalBatch.commit()
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}