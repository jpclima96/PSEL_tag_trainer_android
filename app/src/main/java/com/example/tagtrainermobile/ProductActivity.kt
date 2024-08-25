import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtrainermobile.models.ListingProduct
import com.example.tagtrainermobile.models.Product
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class ProductActivity : AppCompatActivity() {

    var cartProducts = Product.SingleCart.singleCartinstance
    var listingProducts = ListingProduct.SingleList.singleListInstance
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TagTrainerMobile)
        setContentView(R.layout.activity_product)

        // Initialize Firebase Analytics
        firebaseAnalytics = Firebase.analytics

        // Log view_item event
        val product = listingProducts.get(0) // Supondo que você tenha um produto específico
        logViewItemEvent(product)

        setViewProducts()
    }

    // Método para registrar o evento view_item
    private fun logViewItemEvent(product: ListingProduct) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, product.listProdName)  // O ID do produto
            putString(FirebaseAnalytics.Param.ITEM_NAME, product.listProdName)  // Nome do produto
            putDouble(FirebaseAnalytics.Param.PRICE, product.listProdPrice)  // Preço do produto
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product")  // Tipo de conteúdo
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
    }

    // Outros métodos permanecem inalterados
    fun addToCart(v: View, p: ListingProduct ) {
        val cartButton = findViewById(R.id.cartButtonId) as ImageButton
        val productAdded = Product(p.listProdImg,p.listProdName, 1, p.listProdPrice)
        val existingProduct = cartProducts.find({ it.name == productAdded.name })
        if (existingProduct !== null) {
            cartProducts.forEach {
                if (it.name == productAdded.name) {
                    it.price = it.price + productAdded.price
                    it.quantity++

                    // Log add_to_cart event for existing product
                    logAddToCartEvent(it.name, it.price, 1)
                }
            }
        } else {
            cartProducts.add(productAdded)
            cartNotEmpty(cartButton)

            // Log add_to_cart event for new product
            logAddToCartEvent(productAdded.name, productAdded.price, 1)
        }
        Snackbar.make(v, "Produto Adicionado ao carrinho: " + productAdded.name, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun logAddToCartEvent(itemName: String, itemPrice: Double, quantity: Int) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, itemName)
            putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
            putDouble(FirebaseAnalytics.Param.PRICE, itemPrice)
            putInt(FirebaseAnalytics.Param.QUANTITY, quantity)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, params)
    }
}
