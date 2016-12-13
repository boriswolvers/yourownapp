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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Favorites extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "Favorites";
    GoogleApiClient mGoogleApiClient;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private LinearLayoutManager linearLayoutManager;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mGoogleApiClient = ((GoogleSignIn) getApplication()).getGoogleApiClient(Favorites.this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favorieten");

        mRecyclerView = (RecyclerView)findViewById(R.id.recycleViewFavs);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

        //FirebaseRecyclerAdapter<String, MessageViewHolder> adapter = new Firebase
        FirebaseRecyclerAdapter<ArtData, CustomAdapterFirebase> adapter = new FirebaseRecyclerAdapter<ArtData, CustomAdapterFirebase>(
                ArtData.class,
                R.layout.card_item_favs,
                CustomAdapterFirebase.class,
                mConditionRef
        ) {
            @Override
            protected void populateViewHolder(CustomAdapterFirebase viewHolder, ArtData model, int position) {
                viewHolder.onBindViewHolder(model);
            }

        };
        mRecyclerView.setAdapter(adapter);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favs) {
            // Nothing, because this is already the right intent
        }
        else if (id == R.id.action_logOut) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent loginscreen = new Intent(Favorites.this, LogInScreen.class);
                            startActivity(loginscreen);
                            finish();

                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteFavorites(View view) {
        if (mConditionRef == null) {
            return;
        }
        SharedPreferences prefs = getSharedPreferences("ARTS", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();

        mConditionRef.setValue(null);
    }
}
