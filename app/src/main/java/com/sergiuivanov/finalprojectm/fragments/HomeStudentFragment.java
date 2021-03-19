package com.sergiuivanov.finalprojectm.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.Project;
import com.sergiuivanov.finalprojectm.adapters.ProjectFirestoreAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeStudentFragment extends Fragment {

    private View view;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private CollectionReference refToBookingNode ;

    private ProjectFirestoreAdapter adapter;
    private FrameLayout frameLayout;

    public HomeStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_student, container, false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        frameLayout = view.findViewById(R.id.home_student_fragment);
        refToBookingNode = db.collection("Booking/" + mUser.getEmail() + "/BookedProject");
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {

        Query query = refToBookingNode;
        FirestoreRecyclerOptions<Project> options = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class).build();

        adapter = new ProjectFirestoreAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_home_student_firestore);
//        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                adapter.updateProjectStatus(item.getGroupId());
                refuseProject(db.collection("Booking"),mUser.getEmail());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void refuseProject(CollectionReference reference, String email) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("author", "");
        booking.put("description", "Currently you don't have any project selected for your dissertation");
        booking.put("email", "");
        booking.put("id", "");
        booking.put("key", "");
        booking.put("status", "");
        booking.put("title", "");

        reference.document(email).collection("/BookedProject/").document(email).set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(frameLayout, "Project refused successfully" , Snackbar.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(frameLayout, "Error when refusing the project" , Snackbar.LENGTH_LONG).show();

            }
        });
    }


}
