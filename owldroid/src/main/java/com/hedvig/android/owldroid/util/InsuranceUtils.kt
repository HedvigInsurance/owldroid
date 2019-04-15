package com.hedvig.android.owldroid.util

import com.hedvig.android.owldroid.type.InsuranceType

object InsuranceUtils {
    fun isStudentInsurance(insuranceType: InsuranceType) = when (insuranceType) {
        InsuranceType.STUDENT_RENT,
        InsuranceType.STUDENT_BRF -> true
        InsuranceType.RENT,
        InsuranceType.BRF,
        InsuranceType.`$UNKNOWN` -> false
    }

    fun isApartmentOwner(insuranceType: InsuranceType) = when (insuranceType) {
        InsuranceType.BRF,
        InsuranceType.STUDENT_BRF -> true
        InsuranceType.RENT,
        InsuranceType.STUDENT_RENT,
        InsuranceType.`$UNKNOWN` -> false
    }
}
