package com.sergiuivanov.finalprojectm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.fragments.AddStaffFragment;
import com.sergiuivanov.finalprojectm.fragments.HomeStaffFragment;
import com.sergiuivanov.finalprojectm.fragments.ProfileStaffFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StaffDashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    ActionBar actionBar;
    private DatabaseReference mStaffRef;
    private String staffName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        actionBar = getSupportActionBar();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mStaffRef = FirebaseDatabase.getInstance().getReference("Users/staff");
        setPath();
        BottomNavigationView navigationView = findViewById(R.id.nav_bar_staff);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        // default home transaction
        actionBar.setTitle("Add Project");
        AddStaffFragment addStaffFragment1 = new AddStaffFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_activity_dashboard_staff, addStaffFragment1, "");
        fragmentTransaction.commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.projects_staff:
                            //home fragment transaction
                            actionBar.setTitle("My Projects");
                            HomeStaffFragment homeStaffFragment = new HomeStaffFragment(staffName);
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content_activity_dashboard_staff, homeStaffFragment, "");
                            ft1.commit();
                            return true;
                        case R.id.profile:
                            //profile fragment transaction
                            actionBar.setTitle("Profile");
                            ProfileStaffFragment fragmentProfile = new ProfileStaffFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content_activity_dashboard_staff, fragmentProfile, "");
                            ft2.commit();
                            return true;
                        case R.id.nav_staff_add:
                            //projects fragment transaction
                            actionBar.setTitle("Add Project");
                            AddStaffFragment addStaffFragment = new AddStaffFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content_activity_dashboard_staff, addStaffFragment, "");
                            ft3.commit();
                            return true;
                    }
                    return false;
                }
            };

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //if user is signed in
        if (user!= null){
//            userEmail.setText(user.getEmail());

        }else {
            startActivity(new Intent(StaffDashboardActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    /*
    inflate menu options
    **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate current menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle menu clicks


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.log_out){
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setPath(){

        Query query = mStaffRef.orderByChild("email").equalTo(user.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    staffName = "" + ds.child("name").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
