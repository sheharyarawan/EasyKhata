package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.auth.Business
import com.example.easykhata.data.models.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
class BusinessRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : BusinessRepository {
    private var activeBusinessUserListener: ListenerRegistration? = null
    private var activeBusinessListener: ListenerRegistration? = null
    private var currentActiveBusinessId: String? = null
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
            phoneNumber = currentUser.phoneNumber ?: "",
            activeBusinessId = businessRef.id
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
    override fun getActiveBusinessId(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val businessId = document.getString("activeBusinessId")
                if (businessId.isNullOrBlank()) {
                    onFailure(Exception("No active business found"))
                } else {
                    onSuccess(businessId)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun createBusiness(
        businessName: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        val userRef = firestore.collection("users")
            .document(currentUser.uid)
        val businessRef = userRef.collection("business")
            .document()
        val business = Business(
            businessId = businessRef.id,
            businessName = businessName.trim()
        )
        val batch = firestore.batch()
        batch.set(businessRef, business)
        batch.update(
            userRef,
            "activeBusinessId",
            businessRef.id
        )
        batch.commit()
            .addOnSuccessListener {
                onSuccess(businessRef.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun getUserBusinesses(
        onSuccess: (List<Business>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        firestore.collection("users")
            .document(currentUser.uid)
            .collection("business")
            .get()
            .addOnSuccessListener { snapshot ->
                val businesses = snapshot.documents.mapNotNull { document ->
                    document.toObject(Business::class.java)?.copy(
                        businessId = document.id
                    )
                }
                onSuccess(businesses)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun switchBusiness(
        businessId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        firestore.collection("users")
            .document(currentUser.uid)
            .update("activeBusinessId", businessId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun getActiveBusinessInfo(
        onSuccess: (Business) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        removeActiveBusinessInfoListener()
        val userRef = firestore.collection("users")
            .document(currentUser.uid)
        activeBusinessUserListener = userRef.addSnapshotListener { userDocument, error ->
            if (error != null) {
                onFailure(error)
                return@addSnapshotListener
            }
            if (userDocument == null || !userDocument.exists()) {
                onFailure(Exception("User information not found"))
                return@addSnapshotListener
            }
            val activeBusinessId = userDocument.getString("activeBusinessId")
            if (activeBusinessId.isNullOrBlank()) {
                activeBusinessListener?.remove()
                activeBusinessListener = null
                currentActiveBusinessId = null
                onFailure(Exception("No active business found"))
                return@addSnapshotListener
            }
            if (currentActiveBusinessId == activeBusinessId) {
                return@addSnapshotListener
            }
            activeBusinessListener?.remove()
            activeBusinessListener = null
            currentActiveBusinessId = activeBusinessId
            activeBusinessListener = userRef
                .collection("business")
                .document(activeBusinessId)
                .addSnapshotListener { businessDocument, businessError ->
                    if (businessError != null) {
                        onFailure(businessError)
                        return@addSnapshotListener
                    }
                    if (businessDocument == null || !businessDocument.exists()) {
                        onFailure(Exception("Active business information not found"))
                        return@addSnapshotListener
                    }
                    val business = businessDocument.toObject(Business::class.java)
                    if (business != null) {
                        onSuccess(
                            business.copy(
                                businessId = businessDocument.id
                            )
                        )
                    } else {
                        onFailure(Exception("Failed to retrieve business information"))
                    }
                }
        }
    }
    override fun updateActiveBusinessInfo(
        businessName: String,
        businessType: String,
        businessAddress: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User is not authenticated"))
            return
        }
        val userRef = firestore.collection("users")
            .document(currentUser.uid)
        userRef.get()
            .addOnSuccessListener { userDocument ->
                if (!userDocument.exists()) {
                    onFailure(Exception("User information not found"))
                    return@addOnSuccessListener
                }
                val activeBusinessId =
                    userDocument.getString("activeBusinessId")
                if (activeBusinessId.isNullOrBlank()) {
                    onFailure(Exception("No active business found"))
                    return@addOnSuccessListener
                }
                userRef.collection("business")
                    .document(activeBusinessId)
                    .update(
                        mapOf(
                            "businessName" to businessName.trim(),
                            "businessType" to businessType.trim(),
                            "businessAddress" to businessAddress.trim()
                        )
                    )
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
    override fun removeActiveBusinessInfoListener() {
        activeBusinessUserListener?.remove()
        activeBusinessUserListener = null
        activeBusinessListener?.remove()
        activeBusinessListener = null
        currentActiveBusinessId = null
    }
}