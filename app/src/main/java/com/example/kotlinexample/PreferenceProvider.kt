package com.example.kotlinexample

import android.content.Context
import android.content.SharedPreferences
import com.example.kotlinexample.Utils.GlobalVariables

class PreferenceProvider {

    val SAVED_AUDIO_MODE = "savedAudioMode"
    val IS_PASSWORD_UPDATED = "isPassWordUpdated"
    val IN_CALL_CALLER_ID = "inCallCallerId"
    val RUNNING_CALL_TYPE = "callType"
    val IN_CALL_NUMBER = "inCallNumber"
    val IS_CALL_RUNNING = "isCalLRunning"
    val LAST_DIAL_NUMBER = "lastDialNumber"
    val USER_REGISTRED_COUNTRY_CODE = "userRegistredCountry"
    val IS_RETURNING_FROM_CALL = "isReturningFromCall"
    val NEED_TO_SHOW_CALL_END_REASON = "needToShowCallEndReason"
    val DONT_SHOW_DEFAULT_APP_SETTINGS = "dont_show_defauilt_app_settings"
    val IS_FILE_SHARE_AVAILABLE = "is_file_share_available"
    val DONT_SHOW_LOCK_SCREEN_NOTIFICATION_MIUI = "dont_show_lock_screen_notification_miui"
    var Pref: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null

    fun PreferenceProvider(applicationContext: Context) {
        Pref = applicationContext.getSharedPreferences(
            GlobalVariables.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    fun setPrefString(key: String?, `val`: String?) {
        edit = Pref!!.edit()
        edit!!.putString(key, `val`)
        edit!!.commit()
    }

    fun remove(key: String?) {
        edit = Pref!!.edit()
        edit!!.remove(key)
        edit!!.commit()
    }

    fun setPrefboolean(key: String?, `val`: Boolean) {
        edit = Pref!!.edit()
        edit!!.putBoolean(key, `val`)
        edit!!.commit()
    }

    fun setPrefint(key: String?, `val`: Int) {
        edit = Pref!!.edit()
        edit!!.putInt(key, `val`)
        edit!!.commit()
    }

    fun setPreffloat(key: String?, `val`: Float) {
        edit = Pref!!.edit()
        edit!!.putFloat(key, `val`)
        edit!!.commit()
    }

    fun getPrefString(key: String?): String? {
        return Pref!!.getString(key, "")
    }

    fun getPrefBoolean(key: String?): Boolean {
        return Pref!!.getBoolean(key, false)
    }

    fun getPrefInt(key: String?): Int {
        return Pref!!.getInt(key, 0)
    }

    fun getPrefFloat(key: String?): Float {
        return Pref!!.getFloat(key, 0f)
    }

    fun getPrefInt(key: String?, i: Int): Int {
        return Pref!!.getInt(key, -1)
    }
}