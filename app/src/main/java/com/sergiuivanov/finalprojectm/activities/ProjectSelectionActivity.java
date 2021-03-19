package com.sergiuivanov.finalprojectm.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.Project;
import com.sergiuivanov.finalprojectm.models.ProjectBooked;
import com.sergiuivanov.finalprojectm.models.ProjectQueue;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ProjectSelectionActivity extends AppCompatActivity {

    private TextView author , description, title, email , status;
    private Button btnSelect;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference refToStatus, refToKey, refToNode, refToBookingTepm, refToBookingId;
    private ScrollView scrollView;
    private String mAuthor, mDescription, mTitle, mEmail, mRefToStatus, mRefToKey, mRefToNode, mId;
    private String currentProject = "initial value";
    private static int countProjects = 0;
    private ArrayList<String> projectKeys = new ArrayList<>();
    private Map<String, String> projectIdMap = new HashMap<>();

    private LinkedList<String> queueList;
    private String finalList= "";
    private boolean isInQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_full);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        author = findViewById(R.id.author_student);
        description = findViewById(R.id.description_student);
        title = findViewById(R.id.title_field_student);
        email = findViewById(R.id.email_student);
        btnSelect = findViewById(R.id.btn_select);

        scrollView = findViewById(R.id.scroll_view_layout);
        mAuthor = getIntent().getStringExtra("author");
        mDescription = getIntent().getStringExtra("description");
        mTitle = getIntent().getStringExtra("title");
        mEmail = getIntent().getStringExtra("email");
        mId = getIntent().getStringExtra("id");

        mRefToNode = getIntent().getStringExtra("mRefToNode");
        mRefToStatus = getIntent().getStringExtra("mRefToStatus");
        mRefToKey = getIntent().getStringExtra("mRefToKey");

        author.setText(mAuthor);
        description.setText(mDescription);
        title.setText(mTitle);
        email.setText(mEmail);

        refToNode = FirebaseDatabase.getInstance().getReference(mRefToNode);
        refToStatus = FirebaseDatabase.getInstance().getReference(mRefToStatus);
        refToKey = FirebaseDatabase.getInstance().getReference(mRefToKey);
        refToBookingTepm = FirebaseDatabase.getInstance().getReference("Booking/" + mUser.getUid()+ "/selectedProject2" );
        refToBookingId =  FirebaseDatabase.getInstance().getReference("Booking/" + mUser.getUid()+ "/selectedProject" );

        refToStatus.keepSynced(true);
        refToKey.keepSynced(true);
        refToBookingId.keepSynced(true);

        checkIfCurrentProject(mId);
        addProjectsToList();
        checkQueueLimit();
        addGlobalQueueToList();
        checkProjectIsInQueue();
        loadProjectsFromGlobalQueue();


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectProject();
            }
        });
    }

    private void selectProject(){
        refToNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                if ((currentProject.equals("Same Project")) &&
                        (ds.child("key").getValue().toString().equals(mUser.getEmail()))){
                    Snackbar.make(scrollView, "This project is already selected by you " , Snackbar.LENGTH_LONG).show();
                }
                else if (currentProject.equals("Dummy Project")){
                    if(ds.child("status").getValue().toString().equals("available")){
                        btnSelect.setVisibility(View.INVISIBLE);
                        bookProject();
                        emptyIndividualQueue();
                        emptyQueueSystem();
                        Snackbar.make(scrollView, "Booking complete! " , Snackbar.LENGTH_LONG).show();
                    }
                    else if (ds.child("status").getValue().toString().equals("booked")){
                        // implement queue system
                        implementQueueSystem();

                    }else {
                        Snackbar.make(scrollView, "Bug in selection " , Snackbar.LENGTH_LONG).show();
                    }
                }
                else if (currentProject.equals("Another Project")){
                    Snackbar.make(scrollView, "Please refuse your selected project first" , Snackbar.LENGTH_LONG).show();

                }else {
                    Snackbar.make(scrollView, "Bug in project selection" , Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void queueTransaction(DatabaseReference ref){
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return null;
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }




// ------------------------------------- Queue System ----------------------------------------------
    private void implementQueueSystem() {

        if (!isInQueue){
            if ( countProjects<= 2){
                btnSelect.setVisibility(View.INVISIBLE);
                // add to the personal queue, queue fragment
                addToIndividualQueue();// done
                // add to queue every project to keep track of who is in the queue
                System.out.println("+++++++++++++++FinalList is :" + finalList + "+++++++++++++++++++++++++++");
                addToGlobalQueue(finalList);
                Snackbar.make(scrollView, "Added to the queue " , Snackbar.LENGTH_LONG).show();
            }else {
                Snackbar.make(scrollView, "Queue limit reached, please refuse projects from the queue list" ,
                        Snackbar.LENGTH_LONG).show();
            }
        }else {
            Snackbar.make(scrollView, "This project is already in the queue" ,
                    Snackbar.LENGTH_LONG).show();
        }


    }
    private void addProjectsToList() {
        String reference = "Queue/individualQueue/" + mUser.getUid();
        DatabaseReference refToIndividualQueue= FirebaseDatabase.getInstance().getReference(reference);
        refToIndividualQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                        projectKeys.add(ds.getKey());
                    }
                    //printList();
                }else {
                    System.out.println("****************** No projects in the queue : " + countProjects + "************************************");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addGlobalQueueToList(){

        String reference = "Queue/globalQueue/" + mId;
        DatabaseReference refToGlobalQueue = FirebaseDatabase.getInstance().getReference(reference);
        refToGlobalQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()){
                    if (ds.child("queueList").getValue().toString().equals("")){
                        queueList = new LinkedList<>();
                        finalList = mUser.getUid();
                    }else {
                        String queueChild = ds.child("queueList").getValue().toString();
                        queueList = Lists.newLinkedList(Splitter.on(",").split(queueChild));
                        queueList.add(mUser.getUid());
                        finalList = "";
                        for (int i = 0; i < queueList.size(); i++){
                            finalList += queueList.get(i);
                            if (i < queueList.size() -1 ){
                                finalList += ",";
                            }
                        }
                    }
                }else {
                    queueList = new LinkedList<>();
                    finalList = mUser.getUid();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void checkProjectIsInQueue(){
        String reference = "Queue/individualQueue/" + mUser.getUid() + "/" + mId;
        DatabaseReference refToIndividualQueue = FirebaseDatabase.getInstance().getReference(reference);
        refToIndividualQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()){
                    isInQueue = true;
                    System.out.println("This project IS  in the queue " + mId + " &&&&&&&&&&&&&&&&&");

                }else{
                    System.out.println("This project is not in the queue " + mId + " &&&&&&&&&&&&&&&&&");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    private void addToGlobalQueue(String finalList) {
        String reference = "Queue/globalQueue/" + mId;
        DatabaseReference refToGlobalQueue = FirebaseDatabase.getInstance().getReference(reference);
        ProjectQueue projectQueue = new ProjectQueue(finalList, mId); // final string + ","
        refToGlobalQueue.setValue(projectQueue);
        System.out.println("QueueValue is : " + projectQueue.getQueueList() + "_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_");
    }



    private void addToIndividualQueue() {
        String reference = "Queue/individualQueue/" + mUser.getUid() + "/" + mId;
        DatabaseReference refToIndividualQueue = FirebaseDatabase.getInstance().getReference(reference);

        ProjectBooked project = new ProjectBooked(mUser.getEmail(),mAuthor,
                mDescription, mTitle, mEmail, "booked", mId, "Queue position: " + "3"); /// + position
        refToIndividualQueue.setValue(project);
        projectKeys.add(mId);
    }




    private void emptyIndividualQueue() {
        String reference = "Queue/individualQueue/" + mUser.getUid();
        for (String key: projectKeys){
            DatabaseReference refToIndividualQueue = FirebaseDatabase.getInstance().getReference(reference + "/" + key);
            refToIndividualQueue.removeValue();
        }
        projectKeys.clear();
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

    private void emptyQueueSystem() {

        String reference = "Queue/globalQueue/";
        for (Map.Entry<String, String> entry : projectIdMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String finalValue = extractComma(value);

            DatabaseReference refToGlobalQueue = FirebaseDatabase.getInstance().getReference(reference + key);
            ProjectQueue projectQueue = new ProjectQueue(finalValue, key); // final string + ","
            refToGlobalQueue.setValue(projectQueue);
        }
        projectIdMap.clear();
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


    private void checkQueueLimit() {
        String reference = "Queue/individualQueue/" + mUser.getUid();
        DatabaseReference refToIndividualQueue= FirebaseDatabase.getInstance().getReference(reference);
        refToIndividualQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    countProjects = (int) dataSnapshot.getChildrenCount();
                }else {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    //----------------------------------------------------------------------------------------------



// -------------------------------- Booking System -------------------------------------------------
    private void bookProject() {
        refToStatus.setValue("booked");
        refToKey.setValue(mUser.getEmail());
        addProjectToBookingTemp();
        refToBookingId.removeValue();
        addProjectToBooking();
        refToBookingTepm.removeValue();
    }



    public void checkIfCurrentProject(final String id){
        refToBookingId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if ((ds.child("id").getValue()).toString().equals(id)) {
                    currentProject = "Same Project";
                }else if ((ds.child("id").getValue()).toString().equals("")){
                    currentProject = "Dummy Project";
                }else {
                    currentProject = "Another Project";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addProjectToBookingTemp() {
        Project project = new Project("","",
                "", "", "", "", "");
        refToBookingTepm.setValue(project);
    }
    public void addProjectToBooking(){
        Project project = new Project(mUser.getEmail(),mAuthor,
                mDescription, mTitle, mEmail, "booked", mId);
        refToBookingId.setValue(project);
    }


    // ---------------------------------------------------------------------------------------------


/**
 * Returns the top of the deck but doesn't remove it
 */
//    public String seeTop(){
//        return queueList.peek();
//    }

    /**
     * GetTop returns the top of the deck and remove it
     */
//    public String getTop() {
//        return queueList.poll();
//    }

    /**
     * addToQueue adds a uuid to the queue
     *
     *@param uuid means the face value
     */
//    public void addToTheQueue(String uuid){
//        queueList.add(uuid);
//    }

//    public void readQueueList(DatabaseReference reference, final String uuid){
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot ds) {
//
//                if (ds.exists()){
//                    if (ds.hasChild("queueChild") && (!ds.child("queueChild").getValue().toString().equals(""))){
//                        Queue<String> queueList;
//                        queueChild = ds.child("queueChild").getValue().toString();
//                        queueList = Lists.newLinkedList(Splitter.on(",").split(queueChild));
//                        System.out.println("Linked List: " + queueList + "????????????????????????????????????");
//                        queueList.add(uuid);
//                        System.out.println("Linked List: " + queueList + "????????????????????????????????????");
//
//                        //String temp= "";
//
//                        for (String member: queueList){
//                            queueChild += member + ",";
//                            System.out.println("QueueChild in for loop :" + queueChild + "------------------------------");
//                        }
//
//                        // Databasereference to individual queue , asign queue number
//                    }else if(ds.hasChild("queueChild") && (ds.child("queueChild").getValue().toString().equals(""))){
//                        queueChild = uuid;
//                        System.out.println("first in the queue inside , queList:" + queueChild + "?????????????????????????????");
//                    }
//                }else{
//                    queueChild = uuid;
//                    System.out.println("first in the queue , queList:" + queueChild + "?????????????????????????????");
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }


}
