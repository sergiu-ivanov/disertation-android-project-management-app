package com.sergiuivanov.finalprojectm.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.User;
import com.sergiuivanov.finalprojectm.activities.ProjectListActivity;
import com.sergiuivanov.finalprojectm.adapters.StaffListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class StaffListFragment extends Fragment implements StaffListAdapter.OnStaffListener {

    private RecyclerView recyclerView;
    private List<User> mUserList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View view;

    private final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference(
            "Users/staff");

    public StaffListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRef.keepSynced(true);
        mUserList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view_list_fragment);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new StaffListAdapter( mUserList, this );
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        updateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_staff, container, false);
        return view;
    }


    private void updateList(){
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mUserList.add(dataSnapshot.getValue(User.class));
                // update + animation
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged( DataSnapshot dataSnapshot,  String s) {
                User user = dataSnapshot.getValue(User.class);

                int index = getIndex(user);
                mUserList.set(index, user);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved( DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                int index = getIndex(user);
                mUserList.remove(index);
                adapter.notifyItemRemoved(index);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //remove the project
    // find the index of the project
    private int getIndex(User user){
        int index = -1;
        for (int i = 0; i< mUserList.size(); i++){
            if (mUserList.get(i).getEmail().equals(user.getEmail())){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onStaffClick(int position) {
        Intent intent = new Intent(this.getActivity(), ProjectListActivity.class);
        intent.putExtra("currentUser", mUserList.get(position));
        startActivity(intent);



    }


}


