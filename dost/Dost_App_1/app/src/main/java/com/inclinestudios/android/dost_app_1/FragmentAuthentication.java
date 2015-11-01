package com.inclinestudios.android.dost_app_1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FragmentAuthentication extends ActionBarActivity {
    Button genButton;
    Button goButton;
    Button resendButton;
    Button mNextButton;
    AlertDialog alertDialog;
    ImageView mImageVerify;
    ImageView mImageVerified;
    String user_reff;
    EditText OTPtext;
    String user_id;
    String api_key;
    String getUrl = "http://192.168.2.82/dost/v1/getUserByEmail/";
    int flag_check;
    EditText phNumber;
    int count = 3;
    int randomNum = 0;
    TextView verifyHeading;
    TextView verifyContent;
    TextView confirmHeading;
    TextView resendContent;
    String url, url1;
    String useremail = "";

    String refCode;

    String PhoneNum = "9552229315";
    String postUrl = "http://192.168.2.82/dost/v1/register";
    String[] data_in = new String[3];
    public static final String PREFS_NAME = "MyPrefsFile";
    String chkEmail;

    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    boolean isDisplayed = false;


    //GoogleCloudMessaging gcm;
    Context context;
    String regId;

    public static final String REG_ID = "regId";
    public static final String APP_VERSION = "appVersion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////Check if user already exists
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        chkEmail = settings.getString("Useremail", "");


        setContentView(R.layout.fragment_authentication);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xFFDA4541));
        Toast.makeText(getApplicationContext(), "Press for OTP", Toast.LENGTH_LONG).show();
        genButton = (Button) findViewById(R.id.btnGenerateOTP);
        goButton = (Button) findViewById(R.id.btnSendOTP);
        OTPtext = (EditText) findViewById(R.id.editOTP);
        phNumber = (EditText) findViewById(R.id.editNumber);
        mImageVerify = (ImageView) findViewById(R.id.image_verify);
        resendButton = (Button) findViewById(R.id.btnResend);
        mImageVerified = (ImageView) findViewById(R.id.image_verified);
        mNextButton = (Button) findViewById(R.id.nextButton);
        verifyHeading = (TextView) findViewById(R.id.verify_heading);
        verifyContent = (TextView) findViewById(R.id.verify_content);
        confirmHeading = (TextView) findViewById(R.id.confirm_heading);
        resendContent = (TextView) findViewById(R.id.resend_content);

        resendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                resendOTP();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "LOADING GENERAL CONFIGURATION", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FragmentAuthentication.this, MainActivity.class);
                //intent.putExtras(b);
                startActivity(intent);
                FragmentAuthentication.this.finish();
            }
        });
        genButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(phNumber.getText().toString() != "")
                    PhoneNum = phNumber.getText().toString();
                OTPtext.setVisibility(View.VISIBLE);
                resendButton.setVisibility(View.VISIBLE);
                goButton.setVisibility(View.VISIBLE);
                genButton.setVisibility(View.INVISIBLE);
                phNumber.setVisibility(View.INVISIBLE);
                mImageVerify.setVisibility(View.INVISIBLE);
                verifyContent.setVisibility(View.INVISIBLE);
                verifyHeading.setVisibility(View.INVISIBLE);
                resendContent.setVisibility(View.VISIBLE);
                confirmHeading.setVisibility(View.VISIBLE);
                generateOTP();

            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("Click button pressed", url);
                String num = OTPtext.getText().toString();
                Log.d("Entered Number =", num);
                checkOTP(num);
                if(flag_check == 1) {
                    resendButton.setVisibility(View.INVISIBLE);
                    OTPtext.setVisibility(View.INVISIBLE);
                    goButton.setVisibility(View.INVISIBLE);
                    mImageVerified.setVisibility(View.VISIBLE);
                    mNextButton.setVisibility(View.VISIBLE);
                    resendContent.setVisibility(View.INVISIBLE);
                    confirmHeading.setVisibility(View.INVISIBLE);
                }

            }
        });


    }
    protected void resendOTP() {
        if(randomNum == 0)
            Toast.makeText(getApplicationContext(), "First generate OTP to send", Toast.LENGTH_LONG).show();
        else {
            if(count == 0) {
                Toast.makeText(getApplicationContext(), "OTP can be generated maximum 3 times", Toast.LENGTH_LONG).show();
            }
            else {
                count--;
                Toast.makeText(getApplicationContext(), Integer.toString(randomNum), Toast.LENGTH_LONG).show();
                url = "https://inclinestudios:b597152d992cfeef2e367d9238f090a1402aa20d@twilix.exotel.in/v1/Accounts/inclinestudios/Sms/send";
                OTPSend otpSend = new OTPSend();
                cd = new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if (!isInternetPresent && !isDisplayed) {
                    showAlertDialog("You don't have internet connection.", false);
                    isDisplayed = true;

                } else if (isInternetPresent) {
                    otpSend.execute();
                }

                if(count == 0) {
                    resendButton.setVisibility(View.INVISIBLE);
                }

            }
        }
    }

    public void showAlertDialog(String message, final boolean finish) {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (finish)
                            finish();
                    }
                });
        alertDialog.show();
    }


    protected void checkOTP(String num1) {

        //String num = OTPtext.getText().toString();
        Log.d("Entered Number : =", num1);
        String otpgen = Integer.toString(randomNum);
        Log.d("Random Num = ", otpgen);
        if(num1.equals(otpgen)) {
            Toast.makeText(getApplicationContext(), "Correct OTP", Toast.LENGTH_LONG).show();
            // b.putStringArray("string12", data_in);
            postData postdata = new postData();
            postdata.execute();
            //GCMClass gcmClass = new GCMClass();

            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            if (!isInternetPresent && !isDisplayed) {
                showAlertDialog("You don't have internet connection.", false);
                isDisplayed = true;

            } else if (isInternetPresent) {
                //gcmClass.execute();


            }



            flag_check = 1;

        }
        else {
            Toast.makeText(getApplicationContext(), "Wrong OTP entered.", Toast.LENGTH_LONG).show();
            flag_check = 2;
        }
    }

    protected void generateOTP() {
        Random rand = new Random();
        int max = 9999, min = 1001;
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        randomNum = rand.nextInt((max - min) + 1) + min;
        Toast.makeText(getApplicationContext(), Integer.toString(randomNum), Toast.LENGTH_LONG).show();
        url = "https://inclinestudios:b597152d992cfeef2e367d9238f090a1402aa20d@twilix.exotel.in/v1/Accounts/inclinestudios/Sms/send";
        OTPSend otpSend = new OTPSend();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent && !isDisplayed) {
            showAlertDialog("You don't have internet connection.", false);
            isDisplayed = true;

        } else if (isInternetPresent) {
            otpSend.execute();
        }

        //Send an SMS using the link above
    }

    public class OTPSend extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            try {
                List<NameValuePair> nameValPair = new ArrayList<NameValuePair>(3);
                nameValPair.add(new BasicNameValuePair("From", "08039511983"));
                nameValPair.add(new BasicNameValuePair("To", PhoneNum));
                nameValPair.add(new BasicNameValuePair("Body", String.valueOf(randomNum)));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValPair));

                HttpResponse response = client.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                //int StatusCode = statusLine.getStatusCode();
                if (resEntity != null) {
                    //Toast.makeText(getApplicationContext(), EntityUtils.toString(resEntity), Toast.LENGTH_LONG).show();
                    Log.i("RESPONSE", EntityUtils.toString(resEntity));
                }
                StatusLine statusLine = response.getStatusLine();
                int StatusCode = statusLine.getStatusCode();
                if(StatusCode == 200) {
                    Log.d("Success", url);
                }
                else{
                    return null;
                }

            }
            catch (ClientProtocolException e) {


            }catch(IOException e) {

            }

            return null;
        }
    }

    public class postData extends AsyncTask<Void, Void, Void> {

        String username, useremailid, userpic;
        @Override
        protected Void doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postUrl);
            //MultipartEntity entity = new MultipartEntity();
            SharedPreferences setting = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean hasLoggedIn = setting.getBoolean("hasLoggedIn", false);
            if(hasLoggedIn) {
                username = setting.getString("Username", "");
                userpic = setting.getString("Userpic", "");
                useremailid = setting.getString("Useremail", "");
                Log.e("USERNAME + ", username);
                Log.e("USERPIC + ", userpic);
                Log.e("USEREMAIL + ", useremailid);
                Log.e("PHONENUM + ", PhoneNum);
                // = "http://192.168.2.82/dost/v1/getUserByEmail/";
                getUrl = getUrl + useremail;
                //Log.e("REGID + ", regId);

                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                    nameValuePairs.add(new BasicNameValuePair("email", useremailid));
                    nameValuePairs.add(new BasicNameValuePair("name", username ));
                    nameValuePairs.add(new BasicNameValuePair("pic_url", userpic));
                    nameValuePairs.add(new BasicNameValuePair("number", PhoneNum));
                    nameValuePairs.add(new BasicNameValuePair("sms", "1"));
                    //nameValuePairs.add(new BasicNameValuePair("reg_key", regId));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    /*JSONObject jsonobj = new JSONObject();
                    jsonobj.put("name", "a@b.com");
                    jsonobj.put("email", "306");
                    jsonobj.put("contact_no", "123");

                    StringEntity se = new StringEntity(jsonobj.toString());
                    /*se.setContentType("application/json;charset=UTF-8");
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
                    httpPost.setEntity(se);*/

                    HttpResponse response = client.execute(httpPost);
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        //Toast.makeText(getApplicationContext(), EntityUtils.toString(resEntity), Toast.LENGTH_LONG).show();
                        Log.i("RESPONSE from AUTHENTICATION", EntityUtils.toString(resEntity));
                    }
                    useremail = useremailid;
                    StatusLine statusLine = response.getStatusLine();
                    int StatusCode = statusLine.getStatusCode();
                    if(StatusCode == 200) {
                        Log.d("Success", postUrl);
                    }
                    else{
                        return null;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void  onPostExecute(Void result) {
            super.onPostExecute(result);


            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            if (!isInternetPresent && !isDisplayed) {
                showAlertDialog("You don't have internet connection.", false);
                isDisplayed = true;

            } else if (isInternetPresent) {
                Log.e("ENTERED ONPEXECUTE : ", postUrl);
                getAPIkey getapikey = new getAPIkey();
                getapikey.execute();
            }

            Intent intent = new Intent(FragmentAuthentication.this, MainActivity.class);
        }
    }

    public class GCMClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            context = getApplicationContext();
            if (TextUtils.isEmpty(regId)) {
                //regId = registerGCM();
                Log.d("RegisterActivity", "GCM RegId: " + regId);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Already Registered with GCM Server!",
                        Toast.LENGTH_LONG).show();
            }

            //});

            //btnAppShare.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {


            return null;
        }

        /*public String registerGCM() {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regId = getRegistrationId(context);

            if (TextUtils.isEmpty(regId)) {
                // registerInBackground();
                new DownloadWebPageTask().execute();
                Log.d("RegisterActivity",
                        "registerGCM - successfully registered with GCM server - regId: "
                                + regId);
            } else {

                Log.e("RegId available.: ", regId);
            }
            return regId;
        }*/

        /*public String getRegistrationId(Context conetxt) {
            final SharedPreferences prefs = getSharedPreferences(
                    FullscreenActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            String registrationId = prefs.getString(REG_ID, "");
            if (registrationId.isEmpty()) {
                // Log.i(TAG, "Registration not found.");
                return "";
            }
            int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
            int currentVersion = getAppVersion(context);
            if (registeredVersion != currentVersion) {
                // Log.i(TAG, "App version changed.");
                return "";
            }
            return registrationId;
        }*/

        private int getAppVersion(Context context) {
            try {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0);
                return packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("RegisterActivity",
                        "I never expected this! Going down, going down!" + e);
                throw new RuntimeException(e);
            }
        }

        private void storeRegistrationId(Context context, String regId) {
            //final SharedPreferences prefs = getSharedPreferences(
                    //FullscreenActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            int appVersion = getAppVersion(context);
            //Log.i(TAG, "Saving regId on app version " + appVersion);
            //SharedPreferences.Editor editor = prefs.edit();
            //editor.putString(REG_ID, regId);
            //editor.putInt(APP_VERSION, appVersion);
            //editor.commit();
        }

       /* class DownloadWebPageTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
            }


          /*  @Override
            protected String doInBackground(String... urls) {
                String msg = "";
                //if (gcm == null) {
                //    gcm = GoogleCloudMessaging.getInstance(context);
                }
                //regId = gcm.register(com.inclinestudios.android.bhookkad_alpha_30.Config.GOOGLE_PROJECT_ID);
                Log.d("RegisterActivity", "registerInBackground - regId: "
                        + regId);
                msg = "Device registered, registration ID=" + regId;

                storeRegistrationId(context, regId);
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }


            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();

                if (TextUtils.isEmpty(regId)) {

                    Log.e("REDID is EMPTY!", regId);
                } else {
                    postData postdata = new postData();
                    postdata.execute();

                    //new ShareClass().execute();
                    /*Intent i = new Intent(getApplicationContext(),
                            FullscreenActivity.class);
                    i.putExtra("regId", regId);
                    Log.d("RegisterActivity",
                            "onClick of Share: Before starting main activity.");
                    startActivity(i);
                    //finish();
                  //  Log.d("RegisterActivity", "onClick of Share: After finish.");
                }
            }
        }*/
    }
    /*public class ShareClass extends AsyncTask<Void, Void, Void>{
        //ShareExternalServer appUtil;
        String GCMmsg;
        @Override
        protected Void doInBackground(Void... params) {
            //regId = getIntent().getStringExtra("regId");
            GCMmsg = getIntent().getStringExtra("Message");
            Log.d("MainActivity", "regId: " + regId);


            //new ShareTask().execute();
            return null;
        }
        private void showAlert(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message).setTitle("Response from Servers")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        class ShareTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
            }


            @Override
            protected String doInBackground(String... urls) {
                return uploadFile();
            }

            @SuppressWarnings("deprecation")
            private String uploadFile() {
                String responseString = null;
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Config.APP_SERVER_URL);

                    Log.v("Enter regId: ", regId );

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("regId", regId));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    //httppost.setEntity(regId);
                    HttpResponse response;
                    response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }

                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                } catch (IOException e) {
                    responseString = e.toString();
                }
                return responseString;
            }


            @Override
            protected void onPostExecute(String msg) {
                //showAlert(msg);
                Log.e("SHOW MESSAGE, ", msg);

                //Log.d("RegisterActivity", "onClick of Share: After finish.");
                //  Toast.makeText(getApplicationContext(), msg,
                //     Toast.LENGTH_LONG).show();
                //   Toast.makeText(getApplicationContext(),GCMmsg,Toast.LENGTH_LONG).show();
            }

        }

    }*/

    public class getAPIkey extends AsyncTask<Void, Void, Void> {

        //String emailid = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getUrl = "http://192.168.2.82/dost/v1/getUserByEmail/";
            getUrl = getUrl + useremail;
            Log.e("GETURL + ", getUrl);
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(getUrl);
            try {
                HttpResponse response = client.execute(request);
                //HttpEntity resEntity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if(statusCode != 200) {
                    return null;
                }
                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while((line = reader.readLine())!= null){
                    builder.append(line);
                }
                String jsonData = builder.toString();
                JSONObject json = new JSONObject(jsonData);

                user_id = json.getString("u_id");
                api_key = json.getString("api_key");
                Log.e("API_KEY = ", api_key);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            if (!isInternetPresent && !isDisplayed) {
                showAlertDialog("You don't have internet connection.", false);
                isDisplayed = true;

            } else if (isInternetPresent) {
                Log.d("USER_ID = ", user_id);
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("User_Id", user_id);
                editor.putString("API_KEY", api_key);
                editor.apply();
                SharedPreferences setting = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                //boolean hasLoggedIn = setting.getBoolean("hasLoggedIn", false);
                String api_key = setting.getString("API_KEY", "");
                Log.d("API_KEY" , api_key);

                //getReff getReff = new getReff();
                cd = new ConnectionDetector(getApplicationContext());
                isInternetPresent = cd.isConnectingToInternet();
                if (!isInternetPresent && !isDisplayed) {
                    showAlertDialog("You don't have internet connection.", false);
                    isDisplayed = true;

                } else if (isInternetPresent) {
                    //getReff.execute();
                }
            }

        }
    }

   /* private class getReff extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {

                HttpClient client = new DefaultHttpClient();
                HttpGet getRequest = new HttpGet(url1);


                HttpResponse response = client.execute(getRequest);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode != 200) {
                    return null;
                }
                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String jsonData = builder.toString();
                JSONObject json = new JSONObject(jsonData);

                user_reff = json.getString("refference_no");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            url1 = "http://bhookkad.com/API/v1/getUser/" + user_id;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            cd = new ConnectionDetector(getApplicationContext());
            isInternetPresent = cd.isConnectingToInternet();
            if (!isInternetPresent && !isDisplayed) {
                showAlertDialog("You don't have internet connection.", false);
                isDisplayed = true;

            } else if (isInternetPresent) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("User_Reff", user_reff);
                editor.apply();
                editor.commit();
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(FragmentAuthentication.this, ReferralActivity.class);
                        //intent.putExtras(b);
                        startActivity(intent);
                        FragmentAuthentication.this.finish();
                    }
                });
            }
        }
    }//End of this asynctask*/
}