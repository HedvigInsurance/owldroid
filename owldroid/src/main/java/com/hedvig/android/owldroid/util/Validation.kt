package com.hedvig.android.owldroid.util

import android.support.annotation.StringRes
import com.hedvig.android.owldroid.R

object Regexes {
    val emailRegex = Regex("^\\S+@\\S+\$")
    val phoneNumberRegex = Regex("([+]*[0-9]+[+. -]*)")
}

fun validateEmail(email: CharSequence): ValidationResult {
    if (!Regexes.emailRegex.matches(email)) {
        return ValidationResult(false, R.string.invalid_email)
    }

    return ValidationResult(true, null)
}

fun validatePhoneNumber(phoneNumber: CharSequence): ValidationResult {

    if (!Regexes.phoneNumberRegex.matches(phoneNumber)) {
        return ValidationResult(false, R.string.invalid_phone_number)
    }
    return ValidationResult(true, null)
}

data class ValidationResult(val isSuccessful: Boolean, @StringRes val errorTextKey: Int?)

