package com.example.easykhata.data.repositories
import com.example.easykhata.data.models.sales.SaleItem
import com.example.easykhata.data.models.sales.UnitItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
class ItemsRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ItemsRepository {
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
}