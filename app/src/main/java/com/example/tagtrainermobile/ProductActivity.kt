package com.example.tagtrainermobile

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

        // Inicializar Firebase Analytics
        firebaseAnalytics = Firebase.analytics

        setViewProducts()
    }

    fun cartEmpty(): Boolean {
        return cartProducts.size <= 0
    }

    fun cartNotEmpty(button: ImageButton) {
        if (cartEmpty()) {
            return
        } else {
            button.visibility = ImageButton.VISIBLE
            button.setOnClickListener {
                val intent = Intent(applicationContext, CartActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun setViewProducts() {
        val idProduct = intent.getIntExtra("id", 34)
        val productImage = findViewById<ImageView>(R.id.productImageSrcID)
        val textView = findViewById<TextView>(R.id.prodLabel)
        val productDesc = findViewById<TextView>(R.id.prodDescriptionId)
        val productPrice = findViewById<TextView>(R.id.prodPriceId)
        val addProdButton = findViewById<Button>(R.id.buttonAddId)
        val cartButton = findViewById<ImageButton>(R.id.cartButtonId)

        cartNotEmpty(cartButton)

        val product = listingProducts.get(idProduct)
        productImage.setImageDrawable(product.listProdImg.drawable)
        productImage.visibility = View.VISIBLE

        textView.text = product.listProdName
        productDesc.text = product.listProdDesc
        productPrice.text = "R$ ${product.listProdPrice}"

        logViewItemEvent(product)

        addProdButton.setOnClickListener {
            addToCart(addProdButton, product)
        }
    }

    private fun logViewItemEvent(product: ListingProduct) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, product.listProdName)  // ID do produto
            putString(FirebaseAnalytics.Param.ITEM_NAME, product.listProdName)  // Nome do produto
            putDouble(FirebaseAnalytics.Param.PRICE, product.listProdPrice)  // Preço do produto
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product")  // Tipo de conteúdo
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
    }

    fun addToCart(v: View, p: ListingProduct) {
        val cartButton = findViewById<ImageButton>(R.id.cartButtonId)
        val productAdded = Product(p.listProdImg, p.listProdName, 1, p.listProdPrice)
        val existingProduct = cartProducts.find { it.name == productAdded.name }

        if (existingProduct != null) {
            cartProducts.forEach {
                if (it.name == productAdded.name) {
                    it.price += productAdded.price
                    it.quantity++
                }
            }
        } else {
            cartProducts.add(productAdded)
            cartNotEmpty(cartButton)
        }

        Snackbar.make(v, "Produto Adicionado ao carrinho: ${productAdded.name}", Snackbar.LENGTH_LONG).show()

        logAddToCartEvent(productAdded.name, productAdded.price, 1)
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
