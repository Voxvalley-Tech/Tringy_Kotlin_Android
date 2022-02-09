package com.example.kotlinexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ca.Utils.CSConstants
import com.ca.Utils.CSEvents
import com.ca.wrapper.CSClient
import com.ca.wrapper.CSDataProvider
import com.ca.wrapper.CSGroups
import com.example.kotlinexample.Utils.GlobalVariables
import com.example.kotlinexample.databinding.ActivityPasswordBinding
import com.google.android.gms.common.util.DataUtils
import com.google.firebase.messaging.Constants.TAG
import java.lang.Exception
import java.util.logging.Logger

class PasswordActivity : AppCompatActivity() {

    lateinit var databinding : ActivityPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_password)
        databinding = DataBindingUtil.setContentView(this,R.layout.activity_password)

    }

    override fun onResume() {
        super.onResume()

        try {

            val filter = IntentFilter(CSEvents.CSCLIENT_NETWORKERROR)
            val filter1 = IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE)
            val filter2 = IntentFilter(CSEvents.CSCLIENT_ACTIVATION_RESPONSE)
            val filter3 = IntentFilter(CSEvents.CSCLIENT_SIGNUP_RESPONSE)
            val filter4 = IntentFilter(CSEvents.CSCLIENT_INITILIZATION_RESPONSE)
            val filter5 = IntentFilter(CSEvents.CSCLIENT_UPDATEPASSWORD_RESPONSE)
            LocalBroadcastManager.getInstance(this@PasswordActivity)
                .registerReceiver(passwordReceiver, filter)
            LocalBroadcastManager.getInstance(this@PasswordActivity)
                .registerReceiver(passwordReceiver, filter1)
            LocalBroadcastManager.getInstance(this@PasswordActivity)
                .registerReceiver(passwordReceiver, filter2)
            LocalBroadcastManager.getInstance(this@PasswordActivity)
                .registerReceiver(passwordReceiver, filter3)
            LocalBroadcastManager.getInstance(this@PasswordActivity)
                .registerReceiver(passwordReceiver, filter4)
            LocalBroadcastManager.getInstance(this@PasswordActivity)
                .registerReceiver(passwordReceiver, filter5)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val passwordReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val CSClientObj = CSClient()
            Logger.getLogger("Intent--->").warning(intent.action)
            try {
                Log.i(TAG, "Yes Something receieved in RecentReceiver " + intent.action)
                if (intent.action == CSEvents.CSCLIENT_NETWORKERROR) {
                    // //dismissProgressbar()
                    //Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();
                } else if (intent.action == CSEvents.CSCLIENT_LOGIN_RESPONSE) {
                    Log.i(
                        TAG,
                        "Activation Login result" + intent.getStringExtra(CSConstants.RESULT)
                    )
                    if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {
                        if (databinding.etSetPassword.text.toString().equals("")) {
                             //dismissProgressbar()
                            val pf = PreferenceProvider()
                            pf.setPrefboolean(pf.IS_PASSWORD_UPDATED, true)
                            pf.setPrefboolean(
                                getString(R.string.splash_pref_is_already_login),
                                true
                            )
                            val CSGroupsObj = CSGroups()
                            CSGroupsObj.pullMyGroupsList()
                            //closeActivity()
                        } else {
                            CSClientObj.updatePassword(
                                CSDataProvider.getPassword(),
                                databinding.etSetPassword.text.toString()
                            )
                        }
                    } else {
                         //dismissProgressbar()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.request_timeout),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (intent.action == CSEvents.CSCLIENT_ACTIVATION_RESPONSE) {
                    if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {
                        var region = getIntent().getStringExtra("region")
                        Log.i(TAG, "Activation Success")
                        if (region == null || region == "") {
                            region = "+91"
                        }
                        Log.i(
                            TAG,
                            "Activation Success Values" + GlobalVariables.phoneNumber.toString() + " " + GlobalVariables.password
                        )
                        CSClientObj.login(GlobalVariables.phoneNumber, GlobalVariables.password)
                    } else {
                        Log.i(TAG, "Activation Failure")
                         //dismissProgressbar()
                        Toast.makeText(applicationContext, "Wrong Code.", Toast.LENGTH_SHORT).show()
                    }
                } else if (intent.action == CSEvents.CSCLIENT_SIGNUP_RESPONSE) {
                    if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {
                         //dismissProgressbar()
                    } else {
                         //dismissProgressbar()
                        Toast.makeText(this@PasswordActivity, "SignUp failure", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else if (intent.action == CSEvents.CSCLIENT_INITILIZATION_RESPONSE) {
                    if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {
                         //dismissProgressbar()
                        CSClientObj.reSendActivationCode(true)
                        databinding.tvResendPassword.setVisibility(View.VISIBLE)
                       // count = 1
                       // startTimer()
                        /*if (!CSDataProvider.getSignUpstatus()) {
                            CSClientObj.signUp(GlobalVariables.phoneNumber, GlobalVariables.password, false);
                            mllResendPassword.setVisibility(View.GONE);
                            mTvTimer.setVisibility(View.VISIBLE);
                            startTimer();
                        }else {
                            CSClientObj.reSendActivationCode(true);

                        }*/
                    } else {
                         //dismissProgressbar()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.initialisation_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (intent.action == CSEvents.CSCLIENT_UPDATEPASSWORD_RESPONSE) {
                    GlobalVariables.password = databinding.etSetPassword.text.toString()
                     //dismissProgressbar()
                    val pf = PreferenceProvider()
                    pf.setPrefboolean(pf.IS_PASSWORD_UPDATED, true)
                    pf.setPrefboolean(getString(R.string.splash_pref_is_already_login), true)


                    val CSGroupsObj = CSGroups()
                    CSGroupsObj.pullMyGroupsList()
                  //  closeActivity()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }


        /*  private fun getStatusCode(phoneNumber: String): String? {
              val phoneUtil = PhoneNumberUtil.getInstance()
              try {
                  // phone must begin with '+'
                  val numberProto = phoneUtil.parse(phoneNumber, "")
                  countryCode = "" + numberProto.countryCode
                  return "" + countryCode
              } catch (e: Exception) {
                  System.err.println("NumberParseException was thrown: $e")
              }

              // If above case fails we get region from telephone manager.
              val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
              val devicestatus: String = chkStatus()
              try {
                  if (devicestatus == getString(R.string.signup_network_type_wifi)) {
                      countryCodeNumber = telephonyManager.networkCountryIso
                  } else if (devicestatus == getString(R.string.signup_network_type_data)) {
                      countryCodeNumber = telephonyManager.simCountryIso
                  }
                  if (!countryCodeNumber?.isEmpty()!!) {
                      val l = Locale("", countryCodeNumber)
                      countryCodeNumber =
                          getTwoCharCountryCode(countryCodeNumber!!.uppercase(Locale.getDefault()))
                      countryCode = countryCodeNumber
                  } else {
                      countryCodeNumber = getString(R.string.signup_default_country_code)
                      countryCode = countryCodeNumber
                  }
              } catch (ex: Throwable) {
                  countryCodeNumber = getString(R.string.signup_default_country_code)
                  countryCode = countryCodeNumber
                  ex.printStackTrace()
              }
              return countryCode
          }

          private fun chkStatus(): String? {
              var dataType = getString(R.string.signup_network_type_no_internet)
              try {
                  val connMgr = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                  val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                  val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                  return if (wifi!!.isConnectedOrConnecting) {
                      //Toast.makeText(this, "Wifi", Toast.LENGTH_LONG).show();
                      dataType = getString(R.string.signup_network_type_wifi)
                      dataType
                  } else if (mobile!!.isConnectedOrConnecting) {
                      dataType = getString(R.string.signup_network_type_data)
                      dataType
                  } else {
                      dataType = getString(R.string.signup_network_type_no_internet)
                      dataType
                  }
              } catch (e: Exception) {
                  e.printStackTrace()
              }
              return dataType
          }*/


        /**
         * This is used for retrieve country code
         *
         * @param code this is used for get country code
         * @return which returns country code
         */
        private fun getTwoCharCountryCode(code: String): String? {
            /* for ((key, value) in ZoneSelectActivity.country2Phone2.entrySet()) {
            if (key.equals(code, ignoreCase = true)) {
                return value
            }
        }*/
            return ""
        }


    }
}