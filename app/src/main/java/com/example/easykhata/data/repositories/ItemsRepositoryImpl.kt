package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.data.models.sales.UnitItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
class ItemsRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ItemsRepository {
    private var itemsListener: ListenerRegistration? = null
    override fun addItem(
        businessId: String,
        item: SaleItem,
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
        val itemRef = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("items")
            .document()
        val itemToSave = item.copy(
            id = itemRef.id
        )
        itemRef.set(itemToSave)
            .addOnSuccessListener {
                onSuccess(itemRef.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun getUnits(): List<UnitItem> {
        return listOf(
            UnitItem("Piece", "pcs"),
            UnitItem("Kilogram", "kg"),
            UnitItem("Gram", "g"),
            UnitItem("Litre", "L"),
            UnitItem("Millilitre", "ml"),
            UnitItem("Meter", "m"),
            UnitItem("Centimeter", "cm"),
            UnitItem("Foot", "ft"),
            UnitItem("Dozen", "dozen"),
            UnitItem("Box", "box"),
            UnitItem("Packet", "pack"),
            UnitItem("Carton", "carton"),
            UnitItem("Bottle", "bottle"),
            UnitItem("Bag", "bag"),
            UnitItem("Pair", "pair"),
            UnitItem("Set", "set"),
            UnitItem("Roll", "roll"),
            UnitItem("Bundle", "bundle")
        )
    }
    override fun observeItems(
        businessId: String,
        onSuccess: (List<SaleItem>) -> Unit,
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
        itemsListener?.remove()
        itemsListener = firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(SaleItem::class.java)?.copy(
                        id = document.id
                    )
                } ?: emptyList()
                onSuccess(items)
            }
    }
    override fun getItemById(
        businessId: String,
        itemId: String,
        onSuccess: (SaleItem) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onFailure(Exception("User not authenticated"))
            return
        }
        if (businessId.isBlank() || itemId.isBlank()) {
            onFailure(Exception("Invalid item details"))
            return
        }
        firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("items")
            .document(itemId)
            .get()
            .addOnSuccessListener { document ->
                val item = document.toObject(SaleItem::class.java)
                if (item != null) {
                    onSuccess(item.copy(id = document.id))
                } else {
                    onFailure(Exception("Item not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun updateItem(
        businessId: String,
        item: SaleItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            onFailure(Exception("User not authenticated"))
            return
        }
        if (businessId.isBlank() || item.id.isBlank()) {
            onFailure(Exception("Invalid item details"))
            return
        }
        firebaseFirestore
            .collection("users")
            .document(userId)
            .collection("business")
            .document(businessId)
            .collection("items")
            .document(item.id)
            .set(item)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    override fun removeItemsListener() {
        itemsListener?.remove()
        itemsListener = null
    }
}