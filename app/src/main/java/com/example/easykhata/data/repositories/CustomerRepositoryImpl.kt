package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.customers.Customer
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
class CustomerRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : CustomerRepository {
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
}