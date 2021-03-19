package com.sergiuivanov.finalprojectm.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.Project;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStaffFragment extends Fragment {
    private Button addBtn;
    private EditText  email, title, description;
    private Project project;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mStaffRef;
    private String staffName;
    private CheckBox checkBox;

    private FirebaseFirestore db;
    private CollectionReference refToStaffProjects;
    private DocumentReference documentReference;

    public AddStaffFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        email = v.findViewById(R.id.student_email_project);
        title = v.findViewById(R.id.add_title);
        description = v.findViewById(R.id.add_description);
        addBtn = v.findViewById(R.id.add_btn);
        mStaffRef = FirebaseDatabase.getInstance().getReference("Users/staff");
        db = FirebaseFirestore.getInstance();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        checkBox = v.findViewById(R.id.checkBox_student_project);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    email.setVisibility(View.VISIBLE);
                } else {
                    email.setVisibility(View.INVISIBLE);
                }
            }
        });
//        if (checkBox.isChecked()) {
//            author.setVisibility(View.VISIBLE);
//            email.setVisibility(View.VISIBLE);
//            Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "CheckBox: " + checkBox.isChecked() , Snackbar.LENGTH_LONG).show();
//
//        }else {
//            Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "CheckBox: " + checkBox.isChecked() , Snackbar.LENGTH_LONG).show();
//
//        }





            Query query = mStaffRef.orderByChild("email").equalTo(mUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBox.isChecked()) {

                    refToStaffProjects = db.collection("Projects/" + staffName + "/PersonalProjects/");
                    documentReference = refToStaffProjects.document();

                    String sEmail = email.getText().toString();
                    String sTitle = title.getText().toString();
                    String sDesc = description.getText().toString();
                    String id = documentReference.getId();
                    addStudentProjectToBooking(documentReference, staffName, sDesc, mUser.getEmail(), id, sTitle, sEmail);

                    DocumentReference refToBooking= db.document("Booking/" + sEmail + "/BookedProject/" + sEmail);
                    addStudentProject(refToBooking, staffName, sDesc, mUser.getEmail(), id, sTitle, sEmail);

                    title.setText("Project Title");
                    description.setText("Content");
                    Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "Project added successfully " , Snackbar.LENGTH_LONG).show();


                }else{
                    refToStaffProjects = db.collection("Projects/" + staffName + "/PersonalProjects/");
                    documentReference = refToStaffProjects.document();

                    String sTitle = title.getText().toString();
                    String sDesc = description.getText().toString();
                    String id = documentReference.getId();
                    addDataToBookingCollection(documentReference, staffName, sDesc, mUser.getEmail(), id, sTitle);

                    title.setText("Project Title");
                    description.setText("Content");
                }
            }
        });
        return v;
    }

    private void addDataToBookingCollection(DocumentReference reference, String mAuthor, String mDescription,
                                            String mEmail, String mId, String mTitle) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("author", mAuthor);
        booking.put("description", mDescription);
        booking.put("email", mEmail);
        booking.put("id", mId);
        booking.put("key", "");
        booking.put("status", "available");
        booking.put("title", mTitle);

        reference.set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "Project added successfully " , Snackbar.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "Project not added " , Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private void addStudentProject(DocumentReference reference, String mAuthor, String mDescription,
                                            String mEmail, String mId, String mTitle, String key) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("author", mAuthor);
        booking.put("description", mDescription);
        booking.put("email", mEmail);
        booking.put("id", mId);
        booking.put("key", key);
        booking.put("status", "booked");
        booking.put("title", mTitle);

        reference.set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "Project added successfully " , Snackbar.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "Project not added " , Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private void addStudentProjectToBooking(DocumentReference reference, String mAuthor, String mDescription,
                                   String mEmail, String mId, String mTitle, String key) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("author", mAuthor);
        booking.put("description", mDescription);
        booking.put("email", mEmail);
        booking.put("id", mId);
        booking.put("key", key);
        booking.put("status", "Booked by: " + key);
        booking.put("title", mTitle);

        reference.set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "Project added successfully " , Snackbar.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make( getActivity().findViewById(R.id.staff_dashboard_layout), "Project not added " , Snackbar.LENGTH_LONG).show();

            }
        });
    }


}
