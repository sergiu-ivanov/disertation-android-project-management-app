package com.sergiuivanov.finalprojectm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.ProjectQueue;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueueActivity extends AppCompatActivity {
    private TextView author , description, title, email , queuePosition;
    private String mAuthor, mDescription, mTitle, mEmail, mQueuePosition, mId;

    private Button btnRefuse;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ScrollView scrollView;
    private Map<String, String> projectIdMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        projectIdMap = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        author = findViewById(R.id.author_student_queue);
        description = findViewById(R.id.description_student_queue);
        title = findViewById(R.id.title_field_student_queue);
        email = findViewById(R.id.email_student_queue);
        queuePosition = findViewById(R.id.queue_student_position);
        btnRefuse = findViewById(R.id.btn_refuse_queue);

        mAuthor = getIntent().getStringExtra("author");
        mDescription = getIntent().getStringExtra("description");
        mTitle = getIntent().getStringExtra("title");
        mEmail = getIntent().getStringExtra("email");
        mQueuePosition = getIntent().getStringExtra("queuePosition");
        mId = getIntent().getStringExtra("id");

        author.setText(mAuthor);
        description.setText(mDescription);
        title.setText(mTitle);
        email.setText(mEmail);
        queuePosition.setText(mQueuePosition);
        scrollView = findViewById(R.id.scroll_view_layout_queue);

        loadProjectsFromGlobalQueue();

        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refuseQueueProject();
            }
        });
    }

    private void refuseQueueProject(){

        String refToIndividual = "Queue/individualQueue/"+ mUser.getUid() + "/" + mId;
        DatabaseReference refToIndividualQueue  = FirebaseDatabase.getInstance().getReference(
                refToIndividual );

        refToIndividualQueue.removeValue();
        emptyQueueSystem(mId);
        Intent intent = new Intent(this.getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
//        Snackbar.make(scrollView, "Project refused successfully" , Snackbar.LENGTH_LONG).show();
    }

    private void loadProjectsFromGlobalQueue() {
        String reference = "Queue/globalQueue/";
        DatabaseReference refToGlobalQueue = FirebaseDatabase.getInstance().getReference(reference);
        refToGlobalQueue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        if (ds.child("queueList").getValue().toString().contains(mUser.getUid())) {
                            String key= ds.child("projectID").getValue().toString();
                            String value = ds.child("queueList").getValue().toString();
                            projectIdMap.put(key, value);
                        }
                    }
                }else {
                    System.out.println("No data in Global Queue ++++++++++++++++++++++++++++");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void emptyQueueSystem(String currentProject) {

        String reference = "Queue/globalQueue/";
        if (projectIdMap.containsKey(currentProject)){
            String finalValue = extractComma(projectIdMap.get(currentProject));

            DatabaseReference refToGlobalQueue = FirebaseDatabase.getInstance().getReference(reference + currentProject);
            ProjectQueue projectQueue = new ProjectQueue(finalValue, currentProject);
            refToGlobalQueue.setValue(projectQueue);
        }
        projectIdMap.remove(currentProject);

    }

    private String extractComma(String finalValueInput){

        ArrayList finalValueList = Lists.newArrayList(Splitter.on(",").split(finalValueInput));
        finalValueList.remove(mUser.getUid());
        String temp = "";
        for (int i = 0; i < finalValueList.size(); i++){
            temp += finalValueList.get(i);
            if (i < finalValueList.size() -1 ){
                temp += ",";
            }
        }
        finalValueList.clear();
        return temp;
    }


}
