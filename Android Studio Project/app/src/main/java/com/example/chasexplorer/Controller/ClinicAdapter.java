package com.example.chasexplorer.Controller;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chasexplorer.Boundary.MapsActivity;
import com.example.chasexplorer.Entity.Clinic;
import com.example.chasexplorer.Entity.Review;
import com.example.chasexplorer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ClinicAdapter extends AppCompatActivity {

    private static ArrayList<Clinic> FIREBASEDATA;
    private Handler mHandler;
    private Runnable mRunnable;
    private ProgressBar mProgressBar;
    private ObjectAnimator progressAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 98);
        startAnimation();
        // Connection to Clinic
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(database == null)
            database.setPersistenceEnabled(true);
        final DatabaseReference myRef = database.getReference();
        myRef.keepSynced(true);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long Rows = dataSnapshot.getChildrenCount();
                Log.d(TAG,"No Of Data Rows: " + Rows);
                ArrayList<Clinic> t = new ArrayList<Clinic>();
                List<Review> reviewArrayList = new ArrayList<Review>();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Clinic chasClinic = postSnapshot.getValue(Clinic.class);
                    for(DataSnapshot reviews: postSnapshot.child("reviewAl").getChildren()) {
                        Log.d(TAG, "LOOPING THROUGH REVIEWAL CHILDREN");
                        Review clinicReview = reviews.getValue(Review.class);
                        reviewArrayList.add(clinicReview);
                    }
                    t.add(chasClinic);
                }
                ReviewAdapter.setReviewAl((ArrayList) reviewArrayList);
                FIREBASEDATA = (ArrayList<Clinic>) t.clone();
                Log.d(TAG,"Pulled Data:  " + Rows);
                progressAnimator.setIntValues(100);
                progressAnimator.start();
                Intent i = new Intent(ClinicAdapter.this, MapsActivity.class);
                ClinicAdapter.this.startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed To Read From Clinic...", error.toException());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler != null && mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
    }

    private void startAnimation(){
        progressAnimator.setDuration(2500);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
    }

    public static ArrayList<Clinic> passMeAllData (){
        return FIREBASEDATA;
    }

}