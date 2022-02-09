package com.example.kotlinexample.ViewModelClass

import android.util.Patterns




class SignupUser {

    private var strCountry: String? = null
    private var strMobile: String? = null

    fun LoginUser(EmailAddress: String, Password: String) {
        strCountry = EmailAddress
        strMobile = Password
    }

    fun getStrEmailAddress(): String? {
        return strCountry
    }

    fun getStrPassword(): String {
        return strMobile!!
    }

    fun isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches()
    }


    fun isPasswordLengthGreaterThan5(): Boolean {
        return getStrPassword().length > 5
    }


}