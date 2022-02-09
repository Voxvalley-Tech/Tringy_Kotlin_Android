package com.example.kotlinexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ca.Utils.CSConstants
import com.ca.Utils.CSEvents
import com.ca.dao.CSAppDetails
import com.ca.wrapper.CSClient
import com.example.kotlinexample.Utils.GlobalVariables
import com.example.kotlinexample.databinding.ActivityMainBinding
import java.lang.Exception
import java.util.*
import java.util.logging.Logger

class LoginActivity : AppCompatActivity() {

    private lateinit var databinding: ActivityMainBinding
    val CSClientObj = CSClient()
    private var countryCodeNumber: String? = null
    private var countryCode: String? = null
    var username: String? = null
    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        try {
            val filter = IntentFilter(CSEvents.CSCLIENT_NETWORKERROR)
            val filter1 = IntentFilter(CSEvents.CSCLIENT_SIGNUP_RESPONSE)
            val filter2 = IntentFilter(CSEvents.CSCLIENT_LOGIN_RESPONSE)
            val filter3 = IntentFilter(CSEvents.CSCLIENT_INITILIZATION_RESPONSE)
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(MainActivityReceiver, filter)
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(MainActivityReceiver, filter1)
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(MainActivityReceiver, filter2)
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(MainActivityReceiver, filter3)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        databinding.tvLoginButton.setOnClickListener {


            username = databinding.etLoginUsername.text.toString()
            password = databinding.etLoginPassword.text.toString()
            if (!username!!.trim().equals("") && !password.equals("")) {

                Toast.makeText(this@LoginActivity, "Sucessfully", Toast.LENGTH_LONG).show()


                val csAppDetails =
                    CSAppDetails(
                        GlobalVariables.SDK_APP_NAME,
                        GlobalVariables.SDK_APP_ID
                    ) // Tringy

                // CSAppDetails  csAppDetails=new CSAppDetails("iamLive","aid_8656e0f6_c982_4485_8ca7_656780b53d34"); // Konverz
                // CSAppDetails  csAppDetails=new CSAppDetails("iamLive","aid_8656e0f6_c982_4485_8ca7_656780b53d34"); // Konverz
                CSClientObj.initialize(
                    GlobalVariables.SERVER,
                    GlobalVariables.PORT,
                    csAppDetails
                )


            } else if (username.equals("")) {
                Toast.makeText(
                    this@LoginActivity,
                    R.string.login_username_not_empty,
                    Toast.LENGTH_LONG
                ).show()
            } else if (password.equals("")) {
                Toast.makeText(
                    this@LoginActivity,
                    R.string.login_password_not_empty,
                    Toast.LENGTH_LONG
                ).show()
            }


        }


    }

    override fun onPause() {
        super.onPause()
        try {
            LocalBroadcastManager.getInstance(applicationContext)
                .unregisterReceiver(MainActivityReceiver)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val MainActivityReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val CSClientObj = CSClient()
            Logger.getLogger("Intent--->").warning(intent.action)
            try {
                if (intent.action == CSEvents.CSCLIENT_NETWORKERROR) {
                    //dismissProgressbar()
                    //Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();
                } else if (intent.action == CSEvents.CSCLIENT_INITILIZATION_RESPONSE) {
                    //dismissProgressbar()
                    if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {
                        /* var countryCode: String = getStatusCode(username)
                         countryCode = countryCode.replace("+", "")
                         println("Contacts Sync$countryCode")
                         val preferenceProvider = PreferenceProvider(this)
                         preferenceProvider.setPrefString(
                             PreferenceProvider.USER_REGISTRED_COUNTRY_CODE,
                             countryCode.replace("+", "")
                         )*/
                        CSClientObj.registerForPSTNCalls()
                        // CSClientObj.enableNativeContacts(true, countryCode.toInt())
                        CSClientObj.login(username, password)
                    } else {
                         val retcode = intent.getIntExtra(CSConstants.RESULTCODE, 0)
                         if (retcode == CSConstants.E_409_NOINTERNET) {
                            Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.internet_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.initialisation_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (intent.action == CSEvents.CSCLIENT_LOGIN_RESPONSE) {
                    // dismissProgressbar()
                    /*if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {
                        val pf = PreferenceProvider(getApplicationContext())
                        pf.setPrefboolean(getString(R.string.splash_pref_is_already_login), true)
                        val CSGroupsObj = CSGroups()
                        CSGroupsObj.pullMyGroupsList()
                        val intentt = Intent(getApplicationContext(), StatusActivity::class.java)
                        intentt.putExtra("isFreshLogin", true)
                        startActivityForResult(intentt, 933)
                        finish()
                    } else {
                        Toast.makeText(
                            getApplicationContext(),
                            "Invalid username/password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }*/
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