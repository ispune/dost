package com.inclinestudios.android.dost_app_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class SecondActivity extends ActionBarActivity {


    Spinner cat_spnr;
    String category;
    String url = "http://192.168.2.82/dost/v1/tasks";
    String titleQ;
    String descQ;
    Button save;
    EditText editTextName, editTextDesc;
    public static final String PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final String[] cat_array = {"Agriculture","Banking","Medical","Automobile","General"};
        cat_spnr = (Spinner)findViewById(R.id.cat_spin);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cat_array);
        cat_spnr.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat_spnr.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {

                        int position = cat_spnr.getSelectedItemPosition();
                        category = cat_array[+position];

                        Toast.makeText(getApplicationContext(), "You have selected " + cat_array[+position], Toast.LENGTH_LONG).show();

                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                }
        );
       editTextName = (EditText) findViewById(R.id.titletext12);
         editTextDesc = (EditText) findViewById(R.id.questiontext12);


        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                titleQ = editTextName.getText().toString();
                descQ = editTextDesc.getText().toString();
                Log.d("TITLE", titleQ);
                Log.d("DESCR", descQ);
                postData postdata = new postData();
                postdata.execute();

            }
        });

    }


    public class postData extends AsyncTask<Void, Void, Void> {

        String username, useremailid, userpic;
        @Override
        protected Void doInBackground(Void... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            //MultipartEntity entity = new MultipartEntity();
            SharedPreferences setting = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean hasLoggedIn = setting.getBoolean("hasLoggedIn", false);
            String api_key = setting.getString("API_KEY", "");
            Log.d("API_KEY", api_key);
            httpPost.setHeader("Authorization", api_key);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("title", titleQ));
                nameValuePairs.add(new BasicNameValuePair("description", descQ ));
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
                    //String quest_id = EntityUtils.toString(resEntity);
                    //Toast.makeText(getApplicationContext(), EntityUtils.toString(resEntity), Toast.LENGTH_LONG).show();
                    Log.i("RESPONSE from AUTHENTICATION", EntityUtils.toString(resEntity));
                    //String q_id = quest_id.substring(64);
                    //Log.d("Q_ID", q_id);
                }
                //useremail = useremailid;
                StatusLine statusLine = response.getStatusLine();
                int StatusCode = statusLine.getStatusCode();
                if(StatusCode == 200) {
                    Log.d("Success", url);
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

            return null;
        }
        @Override
        protected void  onPostExecute(Void result) {
            super.onPostExecute(result);



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
