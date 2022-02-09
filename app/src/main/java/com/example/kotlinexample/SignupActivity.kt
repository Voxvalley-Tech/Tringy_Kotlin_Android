package com.example.kotlinexample

import android.app.AlertDialog
import android.content.*
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ca.Utils.CSConstants
import com.ca.Utils.CSEvents
import com.ca.dao.CSAppDetails
import com.ca.wrapper.CSClient
import com.ca.wrapper.CSDataProvider
import com.cacore.receivers.MyReceiver
import com.example.kotlinexample.Utils.GlobalVariables
import com.example.kotlinexample.databinding.ActivitySignupBinding
import java.lang.Exception
import java.util.*
import java.util.logging.Logger

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    var countrycode: String? = null
    var mobilenumber: String? = null
    private val CSClientObj = CSClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_signup)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)


        binding.tvSignupButton.setOnClickListener {

            countrycode = binding.tvSignupSelectCountry.text.trim().toString()
            mobilenumber = binding.tvSignupMobile.text.trim().toString()

          //  if (!countrycode!!.trim().equals("")  &&!mobilenumber!!.trim().equals("")) {
               // if (!mobilenumber!!.trim().equals("")) {


                    val countrycode: String = countrycode!!.replace("+", "")
                    CSClientObj.enableNativeContacts(true, countrycode.toInt())
                    showalert()

               /* } else {
                    binding.tvSignupMobile.setError(getString(R.string.signup_valid_mobile_number))
                }*/
            /*} else {

                binding.tvSignupMobile.setError(getString(R.string.signup_enter_mobile_number))
            }*/


        }


    }

    /*
    This is used to show popup for verification code sent given number
     */
    fun showalert(): Boolean {


        return try {
            val successfullyLogin = AlertDialog.Builder(this@SignupActivity)
            successfullyLogin.setTitle(getString(R.string.signup_popup_confirm_title))
            successfullyLogin.setMessage(getString(R.string.signup_popup_message) + mobilenumber)
            successfullyLogin.setPositiveButton(
                getString(R.string.splash_network_alert_ok)
            ) { dialog, which ->
                dialog?.dismiss()
                // phoneNumber = phoneNumber.replace("+", "");
                val csAppDetails =
                    CSAppDetails(GlobalVariables.SDK_APP_NAME, GlobalVariables.SDK_APP_ID) // Tringy
                //  CSAppDetails  csAppDetails=new CSAppDetails("iamLive","aid_8656e0f6_c982_4485_8ca7_656780b53d34"); // Konverz
                CSClientObj.initialize(GlobalVariables.SERVER, GlobalVariables.PORT, csAppDetails)
            }
            successfullyLogin.setNegativeButton(
                getString(R.string.signup_popup_cancel)
            ) { dialog, which -> }
            successfullyLogin.show()
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
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


    override fun onResume() {
        super.onResume()
        try {

            val filter = IntentFilter(CSEvents.CSCLIENT_NETWORKERROR)
            val filter1 = IntentFilter(CSEvents.CSCLIENT_SIGNUP_RESPONSE)
            val filter2 = IntentFilter(CSEvents.CSCLIENT_INITILIZATION_RESPONSE)
            LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(MainActivityReceiver, filter)
            LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(MainActivityReceiver, filter1)
            LocalBroadcastManager.getInstance(applicationContext)
                .registerReceiver(MainActivityReceiver, filter2)
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
                    //Toast.makeText(getApplicationContext(), "NetworkError", Toast.LENGTH_SHORT).show();
                    //dismissprogressbar()
                } else if (intent.action == CSEvents.CSCLIENT_SIGNUP_RESPONSE) {
                    if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {
                        //dismissprogressbar()
                       /* val preferenceProvider = PreferenceProvider(this@SignupActivity)
                        preferenceProvider.setPrefString(
                            PreferenceProvider.USER_REGISTRED_COUNTRY_CODE,
                            countryCode.replace("+", "")
                        )*/
                        // System.out.println("retcode"+intent.getIntExtra("responsecode",0));
                        val responsecode = intent.getStringExtra("responsecode")
                        println("responsecode$responsecode")

                        // if (!responsecode.equals("E_202_OK")) {
                      /*  val intentt = Intent(applicationContext, PasswordActivity::class.java)
                        intentt.putExtra("phoneNumber", GlobalVariables.phoneNumber)
                        intentt.putExtra("region", countryCode)
                        startActivityForResult(intentt, 998)*/
                        /* } else {
                            showalertForLogin("You have already registered. Please Login");
                        }*/
                    } else {
                      //  dismissprogressbar()
                        val retcode = intent.getIntExtra("retcode", 0)
                        if (retcode == CSConstants.E_422_UNPROCESSABLE_ENTITY) {
                            Toast.makeText(
                                this@SignupActivity,
                                "Invalid Number",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            //Some times getting E_403_FORBIDDEN
                            Toast.makeText(
                                this@SignupActivity,
                                "SignUp Failure",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (intent.action == CSEvents.CSCLIENT_INITILIZATION_RESPONSE) {
                    if (intent.getStringExtra(CSConstants.RESULT) == CSConstants.RESULT_SUCCESS) {

                        CSClientObj.registerForPSTNCalls()
                        val countrycode: String = countrycode!!.replace("+", "")
                        if (countrycode != null) {
                            CSClientObj.enableNativeContacts(true, countrycode.toInt())
                        }
                        /*if (!CSDataProvider.getSignUpstatus()) {
                            CSClientObj.signUp(
                                GlobalVariables.phoneNumber,
                                GlobalVariables.password,
                                false
                            )
                        }*/
                    } else {
                       // dismissprogressbar()
                        val retcode = intent.getIntExtra(CSConstants.RESULTCODE, 0)
                        if (retcode == CSConstants.E_409_NOINTERNET) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.internet_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Some times we are getting E_202_OK error
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.initialisation_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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