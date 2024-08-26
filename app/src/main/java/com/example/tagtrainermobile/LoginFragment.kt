package com.example.tagtrainermobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.tagtrainermobile.models.User
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    val user = User.sigleUser.instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Analytics
        firebaseAnalytics = Firebase.analytics

        setProgressBar()
        setButtonLoginConfig()
    }

    fun setProgressBar() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.progress = 33
    }

    fun setButtonLoginConfig() {
        val button = view?.findViewById<Button>(R.id.enterLoginId)
        button?.setOnClickListener { view ->
            // Log login event
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)

            user.isLogged = true
            val paymentFragment = PaymentFragment()
            parentFragmentManager.beginTransaction()
                .add(R.id.fragmentPaymentId, paymentFragment)
                .commit()
        }
    }
    private fun logLoginFailedEvent(loginMethod: String, errorMessage: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, loginMethod)
            putString(FirebaseAnalytics.Param.ERROR_MESSAGE, errorMessage)
        }
        firebaseAnalytics.logEvent("login_failed", params)
    }
}