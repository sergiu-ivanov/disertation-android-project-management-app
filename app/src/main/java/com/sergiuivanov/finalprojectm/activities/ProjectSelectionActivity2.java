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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class ProjectSelectionActivity2 extends AppCompatActivity {

    private TextView author , description, title, email;
    private String mAuthor, mDescription, mTitle, mEmail, mStatus, mId, mPathToProject;
    private Button btnSelect;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ScrollView scrollView;
    private String toast = "First attempt";
    private String isMyProject;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refToBooking = db.collection("Booking");
    private CollectionReference refToProjects = db.collection("Projects");
    private CollectionReference refToGlobalQueue = db.collection("GlobalQueue");
    private CollectionReference refToIndividualQueue = db.collection("IndividualQueue");
    private DocumentReference pathToProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_selection2);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        author = findViewById(R.id.author_student_firestore);
        description = findViewById(R.id.description_student_firestore);
        title = findViewById(R.id.title_field_student_firestore);
        email = findViewById(R.id.email_student_firestore);
        btnSelect = findViewById(R.id.btn_select_firestore);
        scrollView = findViewById(R.id.scroll_view_layout_firestore);

        if (getIntent().hasExtra("pathToProject")) {
            mPathToProject = getIntent().getStringExtra("pathToProject");
            mAuthor = getIntent().getStringExtra("author");
            mDescription = getIntent().getStringExtra("description");
            mTitle = getIntent().getStringExtra("title");
            mEmail = getIntent().getStringExtra("email");
            mId = getIntent().getStringExtra("id");

            author.setText(mAuthor);
            description.setText(mDescription);
            title.setText(mTitle);
            email.setText(mEmail);
            pathToProject = db.document(mPathToProject);


            extractProjectIdFromBooking();
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    bookProjectTransaction(refToProjects, refToBooking, mAuthor, mId);
                }
            });
        } else {
            System.out.println("Failure getting intent from Projectlist Activity*************");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    // -------------------------------- Booking System -------------------------------------------------

    public void bookProjectTransaction(final CollectionReference reference, final CollectionReference docRefToBooking,
                                       final String author, final String mId){

        db.runTransaction(new Transaction.Function<String>() {

            @Nullable
            @Override
            public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentReference docRef = reference.document(author ).collection("PersonalProjects").document(mId);
                DocumentSnapshot documentSnapshot = transaction.get(docRef);
//                Task task = docRef.get();
                String status = documentSnapshot.get("status").toString();
                String key = documentSnapshot.get("key").toString();

                if (status.equals("available") && (isMyProject.equals(""))){
                    transaction.update(docRef, "status", "Booked by: "+ mUser.getEmail());
                    transaction.update(docRef, "key", mUser.getEmail());
                    toast = "Booked Successfully";
                } else if(mId.equals(isMyProject)){
                    toast = "This project is already booked by you";
                }else if (status.equals("available") && (!isMyProject.equals(""))){
                    toast = "Please refuse your existing project first";
                }
                else{
                    // implement queue system
                    toast = "Project is already booked";
//                    DocumentReference docRef2 = reference.document(author ).collection("PersonalProjects").document(mId);
//                    DocumentSnapshot documentSnapshot2 = transaction.get(docRef2);
//                    addProjectToIndividualQueue();
                }

                return toast;
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                btnSelect.setVisibility(View.INVISIBLE);
                // book project
                addDataToBookingCollection(docRefToBooking);
                Snackbar.make(scrollView, toast , Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(scrollView, toast , Snackbar.LENGTH_LONG).show();
            }
        });
    }


    private void extractProjectIdFromBooking(){

        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        DocumentReference refToPersonalProjects = db2.document("Booking/" + mUser.getEmail() + "/BookedProject/" + mUser.getEmail());
        refToPersonalProjects.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    isMyProject = documentSnapshot.get("id").toString();
                }else {
                    toast = "Project does not exist in Booking";
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void addDataToBookingCollection(CollectionReference reference) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("author", mAuthor);
        booking.put("description", mDescription);
        booking.put("email", mEmail);
        booking.put("id", mId);
        booking.put("key", mUser.getEmail());
        booking.put("status", "booked");
        booking.put("title", mTitle);

        reference.document( mUser.getEmail() + "/BookedProject/" + mUser.getEmail()).set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    // ---------------------------------------------------------------------------------------------





    // ----------------------------------- QUEUE SYSTEM --------------------------------------------



    private void addProjectToIndividualQueue(){
        FirebaseFirestore db2 = FirebaseFirestore.getInstance();
        CollectionReference reference = db2.collection("IndividualQueue/" + mUser.getEmail() +
                "/PersonalQueue/");
        DocumentReference refToPersonalProjects = reference.document();

        addProjectToIndividualQueue(refToPersonalProjects, mAuthor,mDescription, mTitle, mEmail, mId, "1");

    }

    private void addProjectToIndividualQueue(DocumentReference reference, String author, String description,
                                             String title, String email,  String mId, String queuePosition) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("author", author);
        booking.put("description", description);
        booking.put("email", email);
        booking.put("id", mId);
        booking.put("key", "");
        booking.put("status", "");
        booking.put("title", title);
        booking.put("queuePosition", queuePosition);
        reference.set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(scrollView, "Failed adding project to individual queue" + e.getMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }


    // ---------------------------------------------------------------------------------------------







}