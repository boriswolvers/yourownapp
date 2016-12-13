package com.example.boris.rijksapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MainActivity";

    GoogleApiClient mGoogleApiClient;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomAdapter adapter;
    private List<ArtData> data_list;
    static View.OnClickListener myOnClickListener;
    String artInformation;
    int pagenumber = 1;
    int pagenumberBefore = 1;

    String query = "https://www.rijksmuseum.nl/api/nl/collection?&key=ymrZZhuJ&format=json&" +
            "toppieces=True&p=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = ((GoogleSignIn) getApplication()).getGoogleApiClient(MainActivity.this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences prefs = getSharedPreferences("Username", Context.MODE_PRIVATE);
        String name = prefs.getString("name", "");
        getSupportActionBar().setTitle("Welkom " + name);

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        data_list = new ArrayList<>();
        load_data_from_url(query, pagenumber);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CustomAdapter(this, data_list);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(linearLayoutManager.findLastVisibleItemPosition() == data_list.size() - 2){
                    load_data_from_url(query, pagenumber + 1);
                    pagenumber += 1;
                }
            }
        });

        if (pagenumber != pagenumberBefore) {
            adapter.notifyDataSetChanged();
            pagenumberBefore += 1;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFiled:" + connectionResult);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favs) {
            Intent favorites = new Intent(MainActivity.this, Favorites.class);
            startActivity(favorites);
        }
        else if (id == R.id.action_logOut) {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent loginscreen = new Intent(MainActivity.this, LogInScreen.class);
                            startActivity(loginscreen);
                            finish();
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void load_data_from_url(String query, int page) {
        try {

            String url = query + String.valueOf(page);

            artInformation = new obtainPaintingsTask().execute(url).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // add appropriate information to data_list
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(artInformation);

            // jsonArray inside the main json object
            JSONArray jsonArray = jsonObj.getJSONArray("artObjects");

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String objectNumber = jsonObject.getString("objectNumber");

                JSONObject imageJson= jsonObject.getJSONObject("webImage");
                String imageURL = imageJson.getString("url");

                ArtData data = new ArtData(title, imageURL, objectNumber);

                data_list.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getArtData(View view) {

        EditText filledArt = (EditText)findViewById(R.id.editTextArtFill);
        String filledArtString = filledArt.getText().toString();
        filledArt.setText("");

        // make sure user enters a movie
        if (!(filledArtString.length() == 0)) {
            filledArtString = filledArtString.replaceAll(" ", "+");

            query = "https://www.rijksmuseum.nl/api/nl/collection?q=" + filledArtString +
                    "&key=ymrZZhuJ&format=json&p=";

            pagenumber = 1;
            pagenumberBefore = 1;
            data_list.clear();
            load_data_from_url(query, pagenumber);

            adapter.notifyDataSetChanged();
        }
        else {
            Toast toast = Toast.makeText(this, "You did not enter your art!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int pos = recyclerView.getChildPosition(v);
            ArtData dataOfList = data_list.get(pos);
            String objectNumber = dataOfList.getObjectNumber();

            String url = "https://www.rijksmuseum.nl/api/nl/collection/" + objectNumber +
                    "?key=fpGQTuED&format=json";

            try {
                artInformation = new obtainPaintingsTask().execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Intent showArt = new Intent(MainActivity.this, showArt.class);
            showArt.putExtra("jsonInfo", artInformation);
            startActivity(showArt);
        }
    }
}
