package com.sergiuivanov.finalprojectm.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sergiuivanov.finalprojectm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileStaffFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    //    ImageView mProfilePic;
    TextView mName, mEmail;
    Button sendEmailBtn;

    public ProfileStaffFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/staff");

//        mProfilePic = view.findViewById(R.id.profile_pic);
        mName = view.findViewById(R.id.profile_name);
        mEmail = view.findViewById(R.id.profile_email);
        sendEmailBtn = view.findViewById(R.id.send_email_btn);
        sendEmailBtn.setText("Email a student");

        // order by child searches for all nodes

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    mName.setText(name);
                    mEmail.setText(email);
//                    try {
//                        if (stringContainsNumber(user.getEmail())){
//                            Picasso.get().load(R.drawable.serj).into(mProfilePic);
//                        }else {
//                            Picasso.get().load(R.drawable.fabrizio).into(mProfilePic);
//                        }
//                    }catch (Exception e){
//                        Picasso.get().load(R.drawable.ic_add_image).into(mProfilePic);
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String[] to_email = {"sergiuivanov90@gmail.com", "aa269@exeter.ac.uk"};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
//                intent.putExtra(Intent.EXTRA_EMAIL, to_email);
//                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                intent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(intent, "Choose the suitable app"));
            }
        });

        return view;
    }

    public boolean stringContainsNumber(String s) {
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(s);
        return m.find();
    }

}
