package com.sergiuivanov.finalprojectm.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.Project;
import com.sergiuivanov.finalprojectm.adapters.ProjectStaffAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeStaffFragment extends Fragment {

    private View view;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String staffName;


    private FirebaseFirestore db;
    private CollectionReference refToStaffProjects;
    private ProjectStaffAdapter adapter;

    public HomeStaffFragment() {
        // Required empty public constructor
    }
    public HomeStaffFragment(String staffName) {
        this.staffName = staffName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_staff, container, false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        refToStaffProjects = db.collection("Projects/" + staffName + "/PersonalProjects");

        seeProjectList();
        return view;
    }

    private void seeProjectList() {
        Query query = refToStaffProjects;
        FirestoreRecyclerOptions<Project> options = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class).build();
        adapter = new ProjectStaffAdapter(options);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_projects_staff);
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

}
