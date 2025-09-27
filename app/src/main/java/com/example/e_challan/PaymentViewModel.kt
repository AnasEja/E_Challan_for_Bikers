package com.example.e_challan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PayFastRepository
) : ViewModel() {

    private val _paymentState = MutableLiveData<PaymentState>()
    val paymentState: LiveData<PaymentState> = _paymentState

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> = _accessToken

    fun initiatePayment(paymentRequest: PaymentRequest) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading

            repository.getAccessToken(
                basketId = paymentRequest.basketId,
                amount = paymentRequest.amount
            ).fold(
                onSuccess = { token ->
                    _accessToken.value = token
                    _paymentState.value = PaymentState.TokenReceived(token)

                    // Generate form data for WebView
                    val formData = repository.generatePaymentFormData(token, paymentRequest)
                    _paymentState.value = PaymentState.ReadyForPayment(formData)
                },
                onFailure = { error ->
                    _paymentState.value = PaymentState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun validatePaymentResponse(
        basketId: String,
        errorCode: String,
        validationHash: String
    ): Boolean {
        return repository.validatePaymentResponse(basketId, errorCode, validationHash)
    }
}

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class TokenReceived(val token: String) : PaymentState()
    data class ReadyForPayment(val formData: Map<String, String>) : PaymentState()
    data class PaymentSuccess(val transactionId: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}