package com.sergiuivanov.finalprojectm.network;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.Project;
import com.sergiuivanov.finalprojectm.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUsers {

    private FirebaseAuth auth;
    private DatabaseReference users; // work with the tables inside db
    private RelativeLayout root;
    private Context context;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refToBooking = db.collection("Booking");
    private final DatabaseReference userStudent = FirebaseDatabase.getInstance().getReference("Users/students");
    private final DatabaseReference userStaff = FirebaseDatabase.getInstance().getReference("Users/staff");
    private final DatabaseReference userAdmin = FirebaseDatabase.getInstance().getReference("Users/admin");

    public RegisterUsers(FirebaseAuth auth, RelativeLayout root, Context context) {
        this.auth = auth;
        this.root = root;
        this.context = context;
    }

    /***
     *
     */
    public void showRegisterWindow() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Registration");

        LayoutInflater inflater = LayoutInflater.from(context); // get needed layout
        //put this layout in view variable
        View register_window = inflater.inflate(R.layout.register_mini_window, null);
        dialog.setView(register_window);

        final MaterialEditText email = register_window.findViewById(R.id.email_field);
        final MaterialEditText pass = register_window.findViewById(R.id.pass_field);
        final MaterialEditText name = register_window.findViewById(R.id.name_field);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // hide the pop up window
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                // check the inputs are valid
                if (checkInfo(email, pass, name)){
                    if (stringContainsNumber(email.getText().toString())) {
                        users = userStudent;
                    }else {
                        users = userStaff;
                    }
                    registerUser( email, pass, name);
                }
            }
        });
        // show the pop up window
        dialog.show();
    }

    /***
     * checks if the inputs are valid
     * @param email
     * @param pass
     * @param name
     * @return
     */
    private boolean checkInfo(final MaterialEditText email, final MaterialEditText pass,
                           final MaterialEditText name) {
        if (TextUtils.isEmpty(name.getText().toString())) {
            Snackbar.make(root, "Please enter your name ", Snackbar.LENGTH_LONG).show();
            return false;// if there is error, exit
        }
        // if no text in email field is entered
        if (TextUtils.isEmpty(email.getText().toString())) {
            Snackbar.make(root, "Please enter your email address", Snackbar.LENGTH_LONG).show();
            return false;// if there is error, exit
        }else if (!email.getText().toString().contains("@exeter.ac.uk")){
            Snackbar.make(root, "Please use your university email address", Snackbar.LENGTH_LONG).show();
            return false;// if there is error, exit
        }

        if (TextUtils.isEmpty(pass.getText().toString())) {
            Snackbar.make(root, "Please enter a password between 9 and 16 characters", Snackbar.LENGTH_LONG).show();
            return false;// if there is error, exit
        }else if ((pass.getText().toString().length() < 9) || (pass.getText().toString().length() > 16)){
            Snackbar.make(root, "Please enter a password between 9 and 16 characters", Snackbar.LENGTH_LONG).show();
            return false;// if there is error, exit
        }
        return true;
    }

    // register users
    private void registerUser(final MaterialEditText email, final MaterialEditText pass,
                              final MaterialEditText name) {
        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser mUser = auth.getCurrentUser();
                        mUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Snackbar.make(root, "Verification email sent successfully", Snackbar.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(root, "Failed to send verification email "+ e.getMessage(), Snackbar.LENGTH_LONG).show();

                            }
                        });
                        User user = new User();

                        user.setEmail(email.getText().toString());
                        user.setName(name.getText().toString());
                        user.setPass(pass.getText().toString());
                        user.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        //setUserType(User user);

                        if (users.toString().equals(userStudent.toString())){
                            user.setType("student");
                        }else if(users.toString().equals(userStaff.toString()) ){
                            user.setType("staff");
                        }else if(users.toString().equals(userAdmin.toString())){
                            user.setType("admin");
                        }else {
                            user.setType("unkown");
                        }
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                createEmptyProject(refToBooking, firebaseUser.getUid(), firebaseUser.getEmail());

                                Snackbar.make(root, "Registration successful", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(root, "FAIL "+ e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public boolean stringContainsNumber( String s )
    {
        Pattern p = Pattern.compile( "[0-9]" );
        Matcher m = p.matcher( s );
        return m.find();
    }



    private void createEmptyProject(CollectionReference reference, String uuid, String email) {

        Map<String, Object> booking = new HashMap<>();
        booking.put("author", "");
        booking.put("description", "Currently you don't have any project selected for your dissertation");
        booking.put("email", "");
        booking.put("id", "");
        booking.put("key", "");
        booking.put("status", "");
        booking.put("title", "");

        reference.document(email ).collection("BookedProject").document(email).set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }




}
