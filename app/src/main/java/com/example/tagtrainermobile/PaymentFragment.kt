package com.example.tagtrainermobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tagtrainermobile.models.User
import com.example.tagtrainermobile.models.Product
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class PaymentFragment : Fragment(), CartActivity.setRadioButtonsConfig {

    private val user = User.sigleUser.instance
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val cartProducts: List<Product> = Product.SingleCart.singleCartinstance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        setProgressBar()
        setRadioButtonsConfig()
    }

    private fun setProgressBar() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.progress = if (user.isLogged) 66 else 33
    }

    override fun setRadioButtonsConfig() {
        val btnView = view?.findViewById<RadioButton>(R.id.bankSlipId)
        val txtDados = view?.findViewById<TextView>(R.id.dataConfId)
        val inputName = view?.findViewById<TextInputLayout>(R.id.textInputLayout)
        val inputCpf = view?.findViewById<TextInputLayout>(R.id.cpfInputLayout)
        val buttonFinish = view?.findViewById<Button>(R.id.finalizarCompraBkslp)

        btnView?.setOnClickListener {
            if (btnView.isChecked) {
                txtDados?.visibility = View.VISIBLE
                inputName?.visibility = View.VISIBLE
                inputCpf?.visibility = View.VISIBLE
                buttonFinish?.visibility = View.VISIBLE

                buttonFinish?.setOnClickListener {
                    val transactionFragment = TransactionFragment()
                    fragmentManager?.beginTransaction()?.replace(R.id.fragmentPaymentId, transactionFragment)?.commit()

                    logBeginCheckoutEvent()
                }
            }
        }
    }

    private fun logBeginCheckoutEvent() {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, "Cart")
            putDouble(FirebaseAnalytics.Param.VALUE, calculateTotalValue()) // Calcule o valor total da compra
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, params)
    }

    private fun calculateTotalValue(): Double {
        var totalValue = 0.0
        for (product in cartProducts) {
            totalValue += product.price * product.quantity
        }
        return totalValue
    }
}
