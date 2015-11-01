package com.inclinestudios.android.dost_app_1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//vYqK0m4g5/lPCj4J1bgbBbZ6wfU= keyhash
public class LoginActivity extends FragmentActivity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "MainActivity";
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;

    public Button btnFb;

    public FragmentManager fragmentManager = getSupportFragmentManager();

    public String personName;
    public String personPhotoUrl;
    public String email;
    public String[] data = new String[3];


    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    boolean isDisplayed = false;
    AlertDialog alertDialog;

    public static final String PREFS_NAME = "MyPrefsFile";
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DatabaseHandler db = new DatabaseHandler(this);

        setContentView(R.layout.activity_login);

        progress = new ProgressDialog(this);

        SharedPreferences setting = getSharedPreferences(PREFS_NAME, 0);
        boolean hasLoggedIn = setting.getBoolean("hasLoggedIn", false);
        if (hasLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        //btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        //btnSignIn.setOnClickListener(this);
        //btnSignOut.setOnClickListener(this);
        //btnRevokeAccess.setOnClickListener(this);

        Log.e("In login activity", "before client");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


    }

    public void open(View view) {
        Log.e("In login activity", "before signInWithGplus");

        signInWithGplus();
        Log.e("In login activity", "after signInWithGplus");

        progress.setMessage("Logging with GPlus :) ");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.show();

        final int totalProgressTime = 100;

        final Thread t = new Thread() {

            @Override
            public void run() {

                int jumpTime = 0;
                while (jumpTime < totalProgressTime) {
                    try {
                        sleep(200);
                        jumpTime += 5;
                        progress.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        };
        t.start();

    }

    protected void onStart() {
        Log.e("In login activity", "before mGoogleapiclient.connect");

        mGoogleApiClient.connect();
        Log.e("In login activity", "after mGoogleapiclient.connect");

        super.onStart();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        Log.e("In login activity", "after resolvesigninerror");

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e("In login activity", "after connectionfailed");

        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                Toast.makeText(this, "YOU ARE A JOKE", Toast.LENGTH_SHORT).show();
                mIntentInProgress = false;
                mGoogleApiClient.connect();
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

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
///For fb
///
        Log.e("In onActivityResult", "login activity");


        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;

            } else {

                mSignInClicked = true;
                mGoogleApiClient.connect();
            }


            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.e("In on connected", "login activity");

        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();


        // Get user's information
        getProfileInformation();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.putString("Username", personName);
        editor.putString("Userpic", personPhotoUrl);
        //Log.e("EMAIL ==", email);
        editor.putString("Useremail", email);
        editor.putString("FbGoogle", "g");
        editor.commit();

        checkReg checkReg = new checkReg();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent && !isDisplayed) {
            showAlertDialog("You don't have internet connection.", false);
            isDisplayed = true;

        } else if (isInternetPresent) {
            checkReg.execute();
        }

        //Bundle b = new Bundle();
        //getProfileInformation();
        //b.putStringArray("string1", data);
        // Update the UI after signin
        //updateUI(true);

    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);

            //btnSignOut.setVisibility(View.VISIBLE);
            //btnRevokeAccess.setVisibility(View.VISIBLE);
            //llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }

    String userId;

    public class checkReg extends AsyncTask<Void, Void, Void> {
        String checkUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            checkUrl = "http://192.168.2.82/dost/v1/getUserByEmail/" + email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(checkUrl);
            Log.e("IN BACKGROUND", "CheckReg");
            try {
                HttpResponse response = client.execute(request);
                //HttpEntity resEntity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode != 200) {
                    return null;
                }
                Log.e("IN BACKGROUND", "CheckReg 2");

                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String jsonData = builder.toString();
                JSONObject json = new JSONObject(jsonData);
                userId = json.getString("u_id");
                //Log.e("compare userid", if(userId == null));
                Log.e("UserId", userId);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
             if(userId != null) {
                Log.e("In else", "on post check reg");

                //Old user. No need to go to Referral and Auth pages
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //intent.putExtras(b);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                Log.e("In if", "on post check reg");
                Intent intent = new Intent(LoginActivity.this, FragmentAuthentication.class);
                //intent.putExtras(b);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }
    }


    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                personName = currentPerson.getDisplayName();
                personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                data[0] = personName;
                data[1] = personPhotoUrl;
                data[2] = email;
                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }


    /**
     * Button on click listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.btn_sign_in:
            // Signin button clicked
            //  break;
            /*case R.id.btn_sign_out:
                // Signout button clicked
                //signOutFromGplus();
                break;
            case //R.id.btn_revoke_access:
                // Revoke access button clicked
                //revokeGplusAccess();
                break;*/
        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        Log.e("In login activity", "after signinwithgplus fun");

        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
        }
    }

    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                        }

                    });
        }
    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}