package com.example.tagtrainermobile

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtrainermobile.models.Product
import com.example.tagtrainermobile.models.cartProductsAdapter
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class PurchaseActivity : AppCompatActivity() {

    private val cartProducts = Product.SingleCart.singleCartinstance
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TagTrainerMobile)
        setContentView(R.layout.activity_purchase)

        firebaseAnalytics = Firebase.analytics

        cartTotalPrice()
        setRandomTransactionCode()
        setTransactionInfo()
        setProgressBar()
        displayPurchaseItems()
        logPurchaseEvent()
    }

    private fun cartTotalPrice(): Double {
        var totalValue = 0.0
        for (product in cartProducts) {
            totalValue += product.price * product.quantity
        }
        return totalValue
    }

    private fun setRandomTransactionCode(): String {
        val numbers = (0..40103430).random()
        val character = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val randChar = character.random()
        return numbers.toString() + randChar
    }

    private fun setTransactionInfo() {
        val df = DecimalFormat("#.00")
        val txtTransactioId = findViewById<TextView>(R.id.transactioId)
        txtTransactioId.text = "Sua Compra: " + setRandomTransactionCode()
        val txtTransactionTotal = findViewById<TextView>(R.id.transactioTotalId)
        txtTransactionTotal.text = "Total: R$ " + df.format(cartTotalPrice())
    }

    private fun setProgressBar() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        progressBar.progress = 100
    }

    private fun displayPurchaseItems() {
        val purchaseTable = findViewById<ListView>(R.id.diplayPurchaseId)
        val adapter = cartProductsAdapter(this, cartProducts)
        purchaseTable.adapter = adapter
    }

    private fun logPurchaseEvent() {
        val totalValue = cartTotalPrice()
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.TRANSACTION_ID, setRandomTransactionCode())
            putDouble(FirebaseAnalytics.Param.VALUE, totalValue)
            putString(FirebaseAnalytics.Param.CURRENCY, "BRL")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, params)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
