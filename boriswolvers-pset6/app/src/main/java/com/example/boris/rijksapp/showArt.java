package com.example.boris.rijksapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class showArt extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "showArt";
    GoogleApiClient mGoogleApiClient;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private String showArtInfo;
    private String title;
    private String imageURL;
    private String objectNumber;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_art);

        mGoogleApiClient = ((GoogleSignIn) getApplication()).getGoogleApiClient(showArt.this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Rijks Collectie");

        Bundle extras = getIntent().getExtras();
        showArtInfo = extras.getString("jsonInfo");

        setArtInfo(showArtInfo);
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFiled:" + connectionResult);
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
            Intent favorites = new Intent(showArt.this, Favorites.class);
            startActivity(favorites);
        }
        else if (id == R.id.action_logOut) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent loginscreen = new Intent(showArt.this, LogInScreen.class);
                            startActivity(loginscreen);
                            finish();

                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setArtInfo(String stringInfo) {

        try {
            // this is the main json object, obtained by parameter stringInfo
            JSONObject jsonObj = new JSONObject(stringInfo);

            // jsonArray inside the main json object
            JSONObject jsonObjArt = jsonObj.getJSONObject("artObject");

            // Get title of art object
            JSONArray titles = jsonObjArt.getJSONArray("titles");
            title = titles.getString(0);

            // Get objectnumber of art
            objectNumber = jsonObjArt.getString("objectNumber");

            // Get image of art object
            JSONObject imageJson= jsonObjArt.getJSONObject("webImage");
            imageURL = imageJson.getString("url");

            // Get maker of art object
            JSONArray makers = jsonObjArt.getJSONArray("principalMakers");
            JSONObject makerOb = makers.getJSONObject(0);
            String nameMaker = makerOb.getString("name");

            // Get description of art
            String desc = jsonObjArt.getString("description");

            // Get materials of art
            JSONArray material = jsonObjArt.getJSONArray("materials");
            String materials = "";
            for (int i = 0; i < material.length(); i++) {
                String materialList = material.getString(i);
                materials += materialList + ", ";
                Log.d("item", materialList);
            }

            // Get techniques of art
            JSONArray technique = jsonObjArt.getJSONArray("techniques");
            String techniques = "";
            for (int i =0; i < technique.length(); i++) {
                String techList = technique.getString(i);
                techniques += techList + ", ";
            }

            // Get dimensions of art
            String dimensions = jsonObjArt.getString("subTitle");

            // Get credits of art
            JSONObject creditObj = jsonObjArt.getJSONObject("acquisition");
            String credit = creditObj.getString("creditLine");

            // Finding all textviews
            TextView titleTextView = (TextView)findViewById(R.id.textViewTitle);
            TextView makerTv = (TextView)findViewById(R.id.textviewMaker);
            TextView descTv = (TextView)findViewById(R.id.textviewDescription);
            TextView materialTv = (TextView)findViewById(R.id.textviewMaterials);
            TextView techTv = (TextView)findViewById(R.id.textviewtechniques);
            TextView dimensionsTv = (TextView)findViewById(R.id.textviewSizes);
            TextView creditTv = (TextView)findViewById(R.id.textviewCredit);

            // Setting all of the textviews
            titleTextView.setText(title);
            makerTv.setText(Html.fromHtml("<b> Maker: </b> <br>" + nameMaker));
            descTv.setText(Html.fromHtml("<b> Description: </b> <br>" + desc));
            materialTv.setText(Html.fromHtml("<b> Materials: </b> <br>" + materials));
            techTv.setText(Html.fromHtml("<b> Techniques: </b> <br>" + techniques));
            dimensionsTv.setText(Html.fromHtml("<b> Dimensions: </b> <br>" + dimensions));
            creditTv.setText(Html.fromHtml("<b> Credit: </b> <br>" + credit + "<br>" +
                    "<br><b> Objectnumber: </b> <br>" + objectNumber));

            // Setting the image
            new showPictureTask((ImageView) findViewById(R.id.imageViewArt)).execute(imageURL);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addFavorite(View view) {

        prefs = getSharedPreferences("ARTS", Context.MODE_PRIVATE);
        Map<String, ?> keys = prefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getValue().equals(title)) {
                Toast toast = Toast.makeText(this, "Already in your favorites!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("title", title);
        editor.commit();

        Toast toast = Toast.makeText(this, "Added to your favorites!", Toast.LENGTH_SHORT);
        toast.show();

        ArtData mydata = new ArtData(title, imageURL, objectNumber);
        mConditionRef.push().setValue(mydata);
    }
}
