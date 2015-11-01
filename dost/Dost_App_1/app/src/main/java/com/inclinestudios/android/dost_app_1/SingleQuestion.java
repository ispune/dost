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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
 * Use the {@linkSingleQuestionnewInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleQuestion extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    LinearLayout answersListViewll;
    VivzAdapter adapter;
    Integer[] q_id_ar;
    String[] tags_ar, date_ar, img_url_ar, name_ar, q_title_ar, q_desc_ar, q_upvotes_ar;
    String feedUrl = "http://"+ R.string.ip_string +"/dost/v1/getAnsOfQuestion/";
    String q_id, tag, picture, nameUser, titleQ, descQ, upvotesQ;
    int incomeTot;
    int expenseTot;
    int balanceTot;
    ImageButton FAB;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @paramparam1 Parameter 1.
     * @paramparam2 Parameter 2.
     * @return A new instance of fragment SingleQuestion.
     */
    // TODO: Rename and change types and number of parameters


    public SingleQuestion(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        // Required empty public constructor
        q_id = str;
        tag = str2;
        picture = str3;
        nameUser = str4;
        titleQ = str5;
        descQ = str6;
        upvotesQ = str7;
        feedUrl = feedUrl + q_id;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onBackPressed()
    {
        //Handle any cleanup you don't always want done in the normal lifecycle
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_single_question, container, false);
        Log.d("TAG = ", tag);
        Log.d("Picture = ", picture);
        Log.d("NAME USER = ", nameUser);
        Log.d("TitleQuest = ", titleQ);Log.d("DescriptionQuest = ", descQ);

        Toast.makeText(getActivity().getApplicationContext(), feedUrl, Toast.LENGTH_SHORT).show();
        TextView tags = (TextView) view.findViewById(R.id.q_tags_sq);
        ImageView userpic = (ImageView) view.findViewById(R.id.q_user_pic_sq);
        TextView username = (TextView) view.findViewById(R.id.q_user_name_sq);
        TextView title = (TextView) view.findViewById(R.id.q_title_sq);
        TextView description = (TextView) view.findViewById(R.id.q_desc_sq);
        //TextView tags = (TextView) getActivity().findViewById(R.id.q_tags_sq);
        tags.setText(tag);
        username.setText(nameUser);
        title.setText(titleQ);
        description.setText(descQ);




        VideoListTask loaderTask  = new VideoListTask();
        loaderTask.execute();
        // userpic.setImageDrawable(R.drawable.user_dp);
        return  view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                final JSONArray results = json.getJSONArray("tasks");
                //Log.d(results.length() + "RESULTS = ", results.getString(0));
                final String[] ans_id = new String[results.length()];
                final String[] answer = new String[results.length()];
                //final String[] description = new String[results.length()];
                final String[] ans_upvotes = new String[results.length()];
                final String[] name_user = new String[results.length()];
                final String[] user_url = new String[results.length()];




                for(int i = 0; i < results.length(); i++){
                    j = i;
                    k = i;
                    l = i;
                    JSONObject videos = results.getJSONObject(i);
                    //String title = videos.getString("title");
                    //videoArrayList.add(videos.getString("image"));
                    ans_id[i] = videos.getString("a_id");
                    HttpClient client2 = new DefaultHttpClient();
                    String feedUrl2 = "http://"+ R.string.ip_string +"/dost/v1/getUserOfAnswer/" + ans_id[i];
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
                    user_url[k] = videos2.getString("pic_url");
                    Log.d(k+"Name_USEr", name_user[k]);
                    Log.d(k+"PIC_URL", user_url[k]);
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
                    //Log.e( i + "imageURLs =", imageURLs[i]);
                    answer[i] = videos.getString("ans");
                    //[i] = videos.getString("description");
                    ans_upvotes[i] = videos.getString("ans_upvotes");



                }

                feedUrl = "http://"+ R.string.ip_string +"/dost/v1/getAnsOfQuestion/";
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), answer[0], Toast.LENGTH_LONG).show();

                        //adapter = new VivzAdapter(ans_id, answer, ans_upvotes, name_user, user_url);
                        for(int i = 0; i < results.length(); i++) {
                            final String str = "Row no: "+i;
                            answersListViewll = (LinearLayout) getActivity().findViewById(R.id.listview_answers_ll);
                            LayoutInflater inflater1 = null;
                            inflater1 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View mListView = inflater1.inflate(R.layout.row_list_ll, null);

                            TextView dateLltv = (TextView) mListView.findViewById(R.id.a_date_ll);
                            TextView nameLltv = (TextView) mListView.findViewById(R.id.a_user_name_ll);
                            TextView descLltv = (TextView) mListView.findViewById(R.id.a_desc_ll);

                            ImageView userPicLltv = (ImageView) mListView.findViewById(R.id.a_user_pic_ll);

                            //dateLltv.setText();
                            nameLltv.setText(name_user[i]);
                            descLltv.setText(answer[i]);
                            userPicLltv.setImageResource(R.drawable.user_dp);


                            answersListViewll.addView(mListView);
                            mListView.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(getActivity().getApplicationContext(), "Clicked item;" + str,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                        //mListView.setAdapter(adapter);

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
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        FAB = (ImageButton) getActivity().findViewById(R.id.imageButton2);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity().getApplicationContext(), AnswerActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().getApplicationContext().startActivity(myIntent);
            }
        });

        /*for(int i = 0; i < 100; i++) {
            final String str = "Row no: "+i;
            answersListViewll = (LinearLayout) getActivity().findViewById(R.id.listview_answers_ll);
            LayoutInflater inflater1 = null;
            inflater1 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View mListView = inflater1.inflate(R.layout.row_list_ll, null);

            TextView dateLltv = (TextView) mListView.findViewById(R.id.a_date_ll);
            TextView nameLltv = (TextView) mListView.findViewById(R.id.a_user_name_ll);
            TextView descLltv = (TextView) mListView.findViewById(R.id.a_desc_ll);

            ImageView userPicLltv = (ImageView) mListView.findViewById(R.id.a_user_pic_ll);

            dateLltv.setText("5th July::" + i);
            nameLltv.setText("Kaustubh Khare::" + i);
            descLltv.setText("Or is DVD:BluRay::BluRay:4K/8K?#SAT, amirite?Hello, kaustubh, kjslkfls lksdfjlsfs" +
                    "dvdskvbkjsdbvjsnjv sbvhsbd vjks k kdhu sdfg ysgfkskhs  dsvsv::" + i);
            userPicLltv.setImageResource(R.drawable.user_dp);


            answersListViewll.addView(mListView);
            mListView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getActivity().getApplicationContext(), "Clicked item;" + str,
                            Toast.LENGTH_SHORT).show();
                }
            });


        }*/

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
        this.onDestroy();
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

    class VivzAdapter extends ArrayAdapter<String> {
        // Context context;
        String[] a_id, a_desc, a_upvotes, a_username, a_userpic;


        //Bitmap bitmap;

        VivzAdapter(String[] ans_id_in, String[] answer_in, String[] ans_upvotes_in, String[] name_in, String[] userpic_in) {
            super(getActivity().getApplicationContext(), R.layout.row_list_ll, R.id.a_desc_ll, ans_id_in);
            /*------------Log.d("ENTERED VIVZ ADAPTER = ", name_in[0]);
            amount = amount_in;
            names = name_in;
            date = dates;
            id  = ids;-------*/
            //points = points_in;

            this.a_desc = answer_in;
            this.a_upvotes = ans_upvotes_in;
            this.a_id = ans_id_in;
            this.a_username = name_in;
            this.a_userpic = userpic_in;
        }


        @Override
        public void remove(String object) {
            super.remove(object);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.row_list_ll, parent, false);

            //ImageView myImage = (ImageView) row.findViewById(R.id.imageStatus);
            TextView username_tv = (TextView) row.findViewById(R.id.a_user_name_ll);
            //TextView date_tv = (TextView) row.findViewById(R.id.q_date_sq);
            //TextView name_tv = (TextView) row.findViewById(R.id.q_user_name_sq);
            //TextView title_tv = (TextView) row.findViewById(R.id.q_title_sq);
            TextView desc_tv = (TextView) row.findViewById(R.id.a_desc_ll);

            //myImage.setImageResource(R.drawable.SingleQuestion_grey);
            //myName.setText(names[position]);
            //tags_tv.setText(tags[position]);
            //date_tv.setText(date[position]);
            username_tv.setText(a_username[position]);
            //title_tv.setText(q_title[position]);
            desc_tv.setText(a_desc[position]);

            return row;
        }
    }

}
