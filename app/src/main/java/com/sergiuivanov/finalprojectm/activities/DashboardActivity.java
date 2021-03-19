package com.sergiuivanov.finalprojectm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sergiuivanov.finalprojectm.fragments.HomeStudentFragment;
import com.sergiuivanov.finalprojectm.fragments.QueueFragment;
import com.sergiuivanov.finalprojectm.fragments.ProfileStudentsFragment;
import com.sergiuivanov.finalprojectm.fragments.StaffListFragment;
import com.sergiuivanov.finalprojectm.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        actionBar = getSupportActionBar();
        firebaseAuth = FirebaseAuth.getInstance();
        BottomNavigationView navigationView = findViewById(R.id.nav_bar);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        // default home transaction
        actionBar.setTitle("My selected project");
        HomeStudentFragment homeStudentFragment1 = new HomeStudentFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_activity_dashboard, homeStudentFragment1, "");
        fragmentTransaction.commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //handle item clicks
                    switch (menuItem.getItemId()){
                        case R.id.nav_queue:
                            //home fragment transaction
                            actionBar.setTitle("Projects in the queue");
                            QueueFragment fragmentQueue = new QueueFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content_activity_dashboard, fragmentQueue, "");
                            ft1.commit();
                            return true;
                        case R.id.profile:
                            //profile fragment transaction
                            actionBar.setTitle("My profile");
                            ProfileStudentsFragment fragmentProfile = new ProfileStudentsFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content_activity_dashboard, fragmentProfile, "");
                            ft2.commit();
                            return true;
                        case R.id.projects:
                            //projects fragment transaction
                            actionBar.setTitle("All projects");
                            StaffListFragment fragmentProject = new StaffListFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content_activity_dashboard, fragmentProject, "");
                            ft3.commit();
                            return true;
                        case R.id.nav_home:
                            actionBar.setTitle("My selected project");
                            HomeStudentFragment homeStudentFragment = new HomeStudentFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content_activity_dashboard, homeStudentFragment, "");
                            ft4.commit();
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
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//    }
//
//    @Override
////    public void onRestart()
////    {
////        super.onRestart();
////        finish();
////        startActivity(getIntent());
////    }


    // very important  for UI update
    @Override
    public void onBackPressed() {
        refreshActivity();
        super.onBackPressed();
    }

    public void refreshActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

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

    public void setProjectText(String title, String description){
        TextView mTitle = findViewById(R.id.home_title_field);
        TextView mDescription= findViewById(R.id.home_description);

        mTitle.setText(title);
        mDescription.setText(description);
    }




}