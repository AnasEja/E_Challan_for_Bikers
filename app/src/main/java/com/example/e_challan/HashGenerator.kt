import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun generateParams(): Map<String, String> {
    val merchantId = "MC239333"
    val password = "vv2u894xdc"
    val integritySalt = "12w42svttv"

    val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    val dateTime = dateFormat.format(Date())

    val cal = Calendar.getInstance()
    cal.add(Calendar.HOUR, 1)
    val expiryDateTime = dateFormat.format(cal.time)

    val txnRef = "T$dateTime"
    val amount = "10000" // = 100.00 PKR

    val params = mutableMapOf(
        "pp_Version" to "1.1",
        "pp_TxnType" to "MWALLET",
        "pp_Language" to "EN",
        "pp_MerchantID" to merchantId,
        "pp_Password" to password,
        "pp_TxnRefNo" to txnRef,
        "pp_Amount" to amount,
        "pp_TxnCurrency" to "PKR",
        "pp_TxnDateTime" to dateTime,
        "pp_TxnExpiryDateTime" to expiryDateTime,
        "pp_BillReference" to "CH12345",
        "pp_Description" to "E-Challan Payment",
        "pp_ReturnURL" to "https://sandbox.jazzcash.com.pk/CustomerPortal/TransactionManagement/Return/Index"
    )

    // Secure Hash
    val secureHash = generateSecureHash(integritySalt, params)
    params["pp_SecureHash"] = secureHash
    return params
}

fun generateSecureHash(salt: String, params: Map<String, String>): String {
    val sortedKeys = params.keys.sorted()
    var hashString = salt
    for (key in sortedKeys) {
        val value = params[key]
        if (!value.isNullOrEmpty()) {
            hashString += "&$value"
        }
    }
    val sha256_HMAC = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(salt.toByteArray(), "HmacSHA256")
    sha256_HMAC.init(secretKey)
    val hashBytes = sha256_HMAC.doFinal(hashString.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }.uppercase()
}
