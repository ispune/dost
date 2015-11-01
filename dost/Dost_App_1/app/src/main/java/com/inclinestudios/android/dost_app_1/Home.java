package com.inclinestudios.android.dost_app_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String feedUrl = "http://192.168.2.82/dost/v1/getAllQuestions";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    ImageButton FAB;

    ListView homeQuestionsList;
    VivzAdapter adapter;
    Integer[] q_id_ar;
    String upvotesPut;

    String[] tags_ar, date_ar, img_url_ar, name_ar, q_title_ar, q_desc_ar, q_upvotes_ar;
    Communicator comm;

    int incomeTot; int expenseTot; int balanceTot;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        VideoListTask loaderTask  = new VideoListTask();
        loaderTask.execute();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        q_id_ar = new Integer[2];
        tags_ar = new String[2];
        date_ar = new String[2];
        img_url_ar = new String[2];
        name_ar = new String[2];
        q_title_ar = new String[2];
        q_desc_ar = new String[2];
        q_upvotes_ar = new String[2];
        homeQuestionsList = (ListView) getActivity().findViewById(R.id.fragment_home_list_view);
        for(int i = 0; i < 2; i++) {
            q_id_ar[i] = i;
            tags_ar[i] = "" + i +": tag";
            date_ar[i] = "" + i+ ": date";
            img_url_ar[i] = "" + i +": img_url";
            name_ar[i] = "" + i +": name";
            q_title_ar[i] = "" + i+ ": title";
            q_desc_ar[i] = "" + i +": desc";
            q_upvotes_ar[i] = "" + i;
        }

        FAB = (ImageButton) getActivity().findViewById(R.id.imageButton1);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), SecondActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().getApplicationContext().startActivity(myIntent);
            }
        });
        //adapter = new VivzAdapter(q_id_ar, tags_ar, date_ar, img_url_ar, name_ar, q_title_ar, q_desc_ar, q_upvotes_ar);
        /*homeQuestionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*for(int i = 0; i < name[position].length(); i++){
                    if(name[position].charAt(i) != ' '){
                        restroName = restroName + name[position].charAt(i);
                    }
                    else {
                        restroName = restroName + "%20";
                    }
                }
                restroLink = restroLink + restroName;
                Log.d("RestroLink = ", restroLink);
                comm.respond(restroLink, area[position], restroName1);
                Toast.makeText(getActivity().getApplicationContext(), "You clicked Q_ID, Q_TITLE:"
                        + q_id_ar[position] + ", " + q_title_ar[position], Toast.LENGTH_SHORT).show();
            }
        });
        homeQuestionsList.setAdapter(adapter);*/
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public class VideoListTask extends AsyncTask<Void, Void, Void> {

        String insideQLink = "";
        //ProgressDialog dialog;
        VivzAdapter adapter;
        //ListView videoList;
        //Context context;
        @Override
        protected void onPostExecute(Void result) {
            // dialog.dismiss();
            //adapter.imageLoader.clearCache();
            try {
                // dialog.dismiss();
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }catch (NullPointerException e) {
                Log.e("FeedUrl", feedUrl);
                e.printStackTrace();
            }catch (RuntimeException e) {
                e.printStackTrace();
            }

        }



        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d("Do in background", feedUrl);
            int j , k;
            int l, index;
            HttpClient client = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(feedUrl);
            try {
                HttpResponse response = client.execute(getRequest);
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
                //Log.i("YouJsonData", jsonData);
                JSONObject json = new JSONObject(jsonData);
                JSONArray results = json.getJSONArray("tasks");
                //Log.d(results.length() + "RESULTS = ", results.getString(0));
                final String[] quest_id = new String[results.length()];
                final String[] title = new String[results.length()];
                final String[] description = new String[results.length()];
                final String[] upvotes = new String[results.length()];
                final String[] name_user = new String[results.length()];
                final String[] pic_url = new String[results.length()];
                final String[] t_id = new String[results.length()];
                final String[] name = new String[results.length()];
                final String[] a_id = new String[results.length()];
                final String[] ans = new String[results.length()];
                final String[] ans_upvotes = new String[results.length()];



                for(int i = 0; i < results.length(); i++){
                    j = i;
                    k = i;
                    l = i;
                    JSONObject videos = results.getJSONObject(i);
                    //String title = videos.getString("title");
                    //videoArrayList.add(videos.getString("image"));
                    quest_id[i] = videos.getString("q_id");

                    HttpClient client1 = new DefaultHttpClient();
                    String feedUrl1 = "http://192.168.2.82/dost/v1/getTagsOfQuestion/" + quest_id[i];
                    //Log.d("FEEDURL! = ", feedUrl1);
                    HttpGet httpGet = new HttpGet(feedUrl1);
                    //try {
                    HttpResponse response1 = client1.execute(httpGet);
                    StatusLine statusLine1 = response1.getStatusLine();
                    int statusCode1 = statusLine1.getStatusCode();
                    if (statusCode1 != 200) {
                        return null;
                    }
                    InputStream jsonStream1 = response1.getEntity().getContent();
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(jsonStream1));
                    StringBuilder builder1 = new StringBuilder();
                    String line1;
                    while ((line1 = reader1.readLine()) != null) {
                        builder1.append(line1);
                    }
                    String jsonData1 = builder1.toString();
                    //Log.i("YouJsonData", jsonData);
                    JSONObject json1 = new JSONObject(jsonData1);
                    JSONArray results1 = json1.getJSONArray("tasks");
                    //Log.d(results1.length() + "RESULTS1 = ", results1.getString(0));



                    //for (int j = 0; j < results1.length(); j++) {
                    if(results1.length() != 0) {
                    JSONObject videos1 = results1.getJSONObject(0);
                    t_id[j] = videos1.getString("t_id");
                    name[j] = videos1.getString("name");
                    Log.d(j +"Task_ID", t_id[j]);
                    Log.d(j +"TAG_NAME", name[j]);
                    }
                    //Log.e("TAGS = ", name[0]);
                    /*}catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e){
                        e.printStackTrace();
                    }*/
                    HttpClient client2 = new DefaultHttpClient();
                    String feedUrl2 = "http://192.168.2.82/dost/v1/getUserOfQuestion/" + quest_id[i];
                    //Log.d("FEEDURL@ = ", feedUrl2);
                    HttpGet httpGet1 = new HttpGet(feedUrl2);
                    //try {
                    HttpResponse response2 = client2.execute(httpGet1);
                    StatusLine statusLine2 = response2.getStatusLine();
                    int statusCode2 = statusLine2.getStatusCode();
                    if (statusCode2 != 200) {
                        return null;
                    }
                    InputStream jsonStream2 = response2.getEntity().getContent();
                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(jsonStream2));
                    StringBuilder builder2 = new StringBuilder();
                    String line2;
                    while ((line2 = reader2.readLine()) != null) {
                        builder2.append(line2);
                    }
                    String jsonData2 = builder2.toString();
                    //Log.i("YouJsonData", jsonData);
                    JSONObject json2 = new JSONObject(jsonData2);
                    JSONArray results2 = json2.getJSONArray("tasks");
                    //Log.d(results2.length() + "RESULTS2 = ", results2.getString(0));



                    //if(k < results2.length() ){
                    JSONObject videos2 = results2.getJSONObject(0);
                    name_user[k] = videos2.getString("name");
                    pic_url[k] = videos2.getString("pic_url");
                    Log.d(k+"Name_USEr", name_user[k]);
                    Log.d(k+"PIC_URL", pic_url[k]);
                    //}
                    /*}catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e){
                        e.printStackTrace();
                    }*/
                    HttpClient client3 = new DefaultHttpClient();
                    String feedUrl3 = "http://192.168.2.82/dost/v1/getAnsOfQuestion/" + quest_id[i];
                    //Log.d("FEEDURL@ = ", feedUrl2);
                    HttpGet httpGet3 = new HttpGet(feedUrl3);
                    //try {
                    HttpResponse response3 = client3.execute(httpGet3);
                    StatusLine statusLine3 = response3.getStatusLine();
                    int statusCode3 = statusLine3.getStatusCode();
                    if (statusCode3 != 200) {
                        return null;
                    }
                    InputStream jsonStream3 = response3.getEntity().getContent();
                    BufferedReader reader3 = new BufferedReader(new InputStreamReader(jsonStream3));
                    StringBuilder builder3 = new StringBuilder();
                    String line3;
                    while ((line3 = reader3.readLine()) != null) {
                        builder3.append(line3);
                    }
                    String jsonData3 = builder3.toString();
                    //Log.i("YouJsonData", jsonData);
                    JSONObject json3 = new JSONObject(jsonData3);
                    JSONArray results3 = json3.getJSONArray("tasks");
                    //Log.d(results2.length() + "RESULTS2 = ", results2.getString(0));



                    //if(k < results2.length() ){
                    if(results3.length() != 0) {
                        index = results3.length() - 1;

                        JSONObject videos3 = results3.getJSONObject(index);
                        a_id[l] = videos3.getString("a_id");
                        ans[l] = videos3.getString("ans");
                        ans_upvotes[l] = videos3.getString("ans_upvotes");
                        Log.d(k + "Name_USEr", name_user[k]);
                        Log.d(k + "PIC_URL", pic_url[k]);
                    }
                    //Log.e( i + "imageURLs =", imageURLs[i]);
                    title[i] = videos.getString("title");
                    description[i] = videos.getString("description");
                    upvotes[i] = videos.getString("upvotes");
                    Log.d(i+"Title", title[i]);
                    Log.d(i+"Q_Id", quest_id[i]);
                    Log.d(i+"Description", description[i]);
                    Log.d(i+"upvotes", upvotes[i]);


                }

                feedUrl = "http://192.168.2.82/dost/v1/getAllQuestions";
                homeQuestionsList = (ListView) getActivity().findViewById(R.id.fragment_home_list_view);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), title[0], Toast.LENGTH_LONG).show();

                        adapter = new VivzAdapter(quest_id, name, pic_url, name_user, title, description, upvotes, ans);
                        comm = (Communicator) getActivity();

                        homeQuestionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String q_id = quest_id[position], tag = name[position], picture = pic_url[position], nameUser = name_user[position], titleQ = title[position], descQ = description[position], upvotesQ = upvotes[position];
                                comm.respond(q_id, tag, picture, nameUser, titleQ, descQ, upvotesQ);
                                //insideQLink = "http://192.168.2.82/dost/v1/getAllAnswers/" + quest_id[position];
                                Toast.makeText(getActivity().getApplicationContext(), q_id, Toast.LENGTH_SHORT).show();

                            }
                        });
                        homeQuestionsList.setAdapter(adapter);

                    }
                });




            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (RuntimeException e){
                e.printStackTrace();
            }

            return null;
        }


    }

    public void upVoteIt() {
        incPoints incpoints = new incPoints();
        incpoints.execute();
    }


    private class incPoints extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {

                HttpClient httpClient1 = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(upvotesPut);

                // Execute HTTP Put Request
                HttpResponse response1 = httpClient1.execute(httpPut);
                StatusLine statusLine1 = response1.getStatusLine();
                int statusCode1 = statusLine1.getStatusCode();
                if (statusCode1 != 200) {
                    return null;
                }
                InputStream jsonStream1 = response1.getEntity().getContent();
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(jsonStream1));
                StringBuilder builder1 = new StringBuilder();
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    builder1.append(line1);
                }
                String jsonData1 = builder1.toString();
                JSONObject json1 = new JSONObject(jsonData1);
                Boolean error = json1.getBoolean("error");

                if (error)
                    return null;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }


    class VivzAdapter extends ArrayAdapter<String> {
        // Context context;
        String[] tags, date, img_url, name, q_title, q_desc, q_upvotes;
        String[] q_id, answer;

        //Bitmap bitmap;

        VivzAdapter(String[] q_id, String[] tags, String[] img_url, String[] name, String[] q_title, String[] q_desc, String[] q_upvotes, String[] answer) {
            super(getActivity().getApplicationContext(), R.layout.fragment_liability_row, R.id.q_tags_home, tags);
            /*------------Log.d("ENTERED VIVZ ADAPTER = ", name_in[0]);
            amount = amount_in;
            names = name_in;
            date = dates;
            id  = ids;-------*/
            //points = points_in;
            this.tags = tags;
            this.date = date;
            this.img_url = img_url;
            this.name = name;
            this.q_title = q_title;
            this.q_desc = q_desc;
            this.q_upvotes = q_upvotes;
            this.q_id = q_id;
            this.answer = answer;
        }


        @Override
        public void remove(String object) {
            super.remove(object);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.fragment_liability_row, parent, false);

            //ImageView myImage = (ImageView) row.findViewById(R.id.imageStatus);
            TextView tags_tv = (TextView) row.findViewById(R.id.q_tags_home);
            TextView ans_tv = (TextView) row.findViewById(R.id.a_desc_home);
            TextView name_tv = (TextView) row.findViewById(R.id.q_user_name_home);
            TextView title_tv = (TextView) row.findViewById(R.id.q_title_home);
            TextView desc_tv = (TextView) row.findViewById(R.id.q_desc_home);
            TextView upvotes = (TextView) row.findViewById(R.id.q_upvote_no_home);

            Button upvoteB = (Button) row.findViewById(R.id.q_upvote_home);
            Button viewMoreB = (Button) row.findViewById(R.id.viewMoreButtonQ);

            viewMoreB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String q_id_curr = q_id[position];
                    String tag = name[position], picture = img_url[position], nameUser = name[position], titleQ = q_title[position], descQ = q_desc[position], upvotesQ = q_upvotes[position];

                    Toast.makeText(getActivity().getApplicationContext(), "Clicked id:" + q_id_curr, Toast.LENGTH_SHORT).show();
                    comm = (Communicator) getActivity();
                    comm.respond(q_id_curr, tag, picture, nameUser, titleQ, descQ, upvotesQ);
                    /*int x = Integer.parseInt(upvotesQ);
                    x = x + 1;
                    upvotesQ = String.valueOf(x);

                    upvotesPut = "http://192.168.2.82/dost/v1/updateupvotes/" + upvotesQ + "/" + q_id_curr;
                    upVoteIt();*/

                }
            });

            upvoteB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String q_id_curr = q_id[position];
                    String tag = name[position], picture = img_url[position], nameUser = name[position], titleQ = q_title[position], descQ = q_desc[position], upvotesQ = q_upvotes[position];

                    Toast.makeText(getActivity().getApplicationContext(), "Clicked id:" + q_id_curr, Toast.LENGTH_SHORT).show();
                    /*comm = (Communicator) getActivity();
                    comm.respond(q_id_curr, tag, picture, nameUser, titleQ, descQ, upvotesQ);*/
                    int x = Integer.parseInt(upvotesQ);
                    x = x + 1;
                    upvotesQ = String.valueOf(x);

                    upvotesPut = "http://192.168.2.82/dost/v1/updateupvotes/" + upvotesQ + "/" + q_id_curr;
                    q_upvotes[position] = upvotesQ;
                    upVoteIt();

                }
            });


            //myImage.setImageResource(R.drawable.home_grey);
            //myName.setText(names[position]);

            tags_tv.setText(tags[position]);
            ans_tv.setText(answer[position]);
            name_tv.setText(name[position]);
            title_tv.setText(q_title[position]);
            desc_tv.setText(q_desc[position]);
            upvotes.setText(q_upvotes[position]);

            return row;
        }
    }

}
