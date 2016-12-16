package com.example.boris.rijksapp;

/**
 * Created by Boris on 11-12-2016.
 * MainActivity of this app contains a recyclerviewer containing top artworks of Rijks Museum. From
 * this activity the asynctasks will be called for obtaining more artworks.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    // Firebase
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;

    // Layout of main screen
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CustomAdapter adapter;
    private List<ArtData> data_list;
    static View.OnClickListener myOnClickListener;
    String artInformation;

    // This is for inifinite scrolling
    int pagenumber = 1;
    int pagenumberBefore = 1;

    // Url of topcollections to show in main screen
    String query = "https://www.rijksmuseum.nl/api/nl/collection?&key=ymrZZhuJ&format=json&" +
            "toppieces=True&p=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get google client
        mGoogleApiClient = ((GoogleSignIn) getApplication()).getGoogleApiClient(MainActivity.this, this);

        // Setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting username in the toolbar
        SharedPreferences prefs = getSharedPreferences("Username", Context.MODE_PRIVATE);
        String name = prefs.getString("name", "");
        getSupportActionBar().setTitle("Welkom " + name);

        // Onclicklistener on a cardview
        myOnClickListener = new MyOnClickListener(this);

        // Setting the viewer
        recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        data_list = new ArrayList<>();

        // Load data from url and adding to data_list
        load_data_from_url(query, pagenumber);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CustomAdapter(this, data_list);
        recyclerView.setAdapter(adapter);

        // For obtaining more artworks when scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(linearLayoutManager.findLastVisibleItemPosition() == data_list.size() - 2){
                    load_data_from_url(query, pagenumber + 1);
                    pagenumber += 1;
                }
            }
        });

        // If there are more artworks retrieved -> notify
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

        // Goes to favorite activity
        if (id == R.id.action_favs) {
            Intent favorites = new Intent(MainActivity.this, Favorites.class);
            startActivity(favorites);
        }
        // Logs user off
        else if (id == R.id.action_logOut) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            // Go back to log in screen
                            Intent loginscreen = new Intent(MainActivity.this, LogInScreen.class);
                            // Remove all activities from stack when user logs off
                            loginscreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            // Url with the right page
            String url = query + String.valueOf(page);

            // Get string of the json
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
    // When user want to search for specific artwork
    public void getArtData(View view) {
        // Gets the filled in artwork
        EditText filledArt = (EditText)findViewById(R.id.editTextArtFill);
        String filledArtString = filledArt.getText().toString();
        filledArt.setText("");

        // make sure user enters something
        if (!(filledArtString.length() == 0)) {
            filledArtString = filledArtString.replaceAll(" ", "+");

            query = "https://www.rijksmuseum.nl/api/nl/collection?q=" + filledArtString +
                    "&key=ymrZZhuJ&format=json&p=";

            // Resseting values for endless scrolling, because result of what the user has typed
            // will be in the same recyclerviewer
            pagenumber = 1;
            pagenumberBefore = 1;
            data_list.clear();

            // Retrieves information with url
            load_data_from_url(query, pagenumber);

            adapter.notifyDataSetChanged();
        }
        else {
            Toast toast = Toast.makeText(this, "You did not enter your art!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    // Onclicklistener for cardview item
    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            // Gets position inside recyclerview, use that to find object number in Artdata-object
            int pos = recyclerView.getChildPosition(v);
            ArtData dataOfList = data_list.get(pos);
            String objectNumber = dataOfList.getObjectNumber();

            // With use of objectNumber, new url can be made with additional information
            String url = "https://www.rijksmuseum.nl/api/nl/collection/" + objectNumber +
                    "?key=fpGQTuED&format=json";

            try {
                artInformation = new obtainPaintingsTask().execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // Go to another activity with the extra information of artwork
            Intent showArt = new Intent(MainActivity.this, showArt.class);
            showArt.putExtra("jsonInfo", artInformation);
            startActivity(showArt);
        }
    }
}
