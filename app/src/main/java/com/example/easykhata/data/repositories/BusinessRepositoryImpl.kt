package com.example.easykhata.data.repositories

import com.example.easykhata.data.models.auth.Business
import com.example.easykhata.data.models.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
class BusinessRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : BusinessRepository {
    override fun createUserWithBusiness(
        user: User,
        business: Business,
        onSuccess: (String) -> Unit,
        onException: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onException(Exception("User is not authenticated"))
            return
        }
        val uid = currentUser.uid
        val userRef = firestore.collection("users")
            .document(uid)
        val businessRef = userRef.collection("business")
            .document()
        val userData = user.copy(
            userId = uid,
            phoneNumber = currentUser.phoneNumber ?: ""
        )
        val businessData = business.copy(
            businessId = businessRef.id
        )
        val batch = firestore.batch()
        batch.set(userRef, userData)
        batch.set(businessRef, businessData)
        batch.commit()
            .addOnSuccessListener {
                onSuccess(businessRef.id)
            }
            .addOnFailureListener { exception ->
                onException(exception)
            }
    }
    override fun getKarobarTypes(): List<String> {
        return listOf(
            "Hardware Shop",
            "Kiryana",
            "Medical Store",
            "Milk Shop",
            "Mobile & Accessories",
            "Others",
            "Pan Shop",
            "Real Estate",
            "Restaurant / Hotel",
            "Stationary",
            "Aluminum / Steel / Glass",
            "Apparels / Garments",
            "Automobile Shop",
            "Bakery",
            "Cafe",
            "Chemical Shop",
            "Cosmetics",
            "Easyload Shop",
            "Electronics",
            "Gold & Jewellery"
        )
    }
}