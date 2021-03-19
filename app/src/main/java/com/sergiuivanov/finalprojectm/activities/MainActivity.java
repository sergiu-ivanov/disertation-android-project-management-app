package com.sergiuivanov.finalprojectm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.network.RegisterUsers;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MainActivity
 *
 * @author Sergiu Ivanov.
 * @version 2.1 01-10-2019.
 */
public class MainActivity extends AppCompatActivity {

//    android:launchMode="singleTop"

    Button btnRegister, btnSignIn;
    TextView recoverPass;
    private String userId;

    private FirebaseAuth mAuth; // authentification
    private FirebaseDatabase db;   // connect to db
    private DatabaseReference projects;// work with the tables inside db

    // registration path for students

    private DatabaseReference users;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegister = findViewById(R.id.btn_register);
        btnSignIn = findViewById(R.id.btn_sign_in);
        recoverPass = findViewById(R.id.forgot_pass);
        root = findViewById(R.id.root_layout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference();

        // register
        final RegisterUsers registerUsers = new RegisterUsers(mAuth, root, this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUsers.showRegisterWindow();
            }
        });

        //sign in
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInWindow();
            }
        });


        // recover password
        recoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetEmail = new EditText(v.getContext());
                final AlertDialog.Builder passwordReset = new AlertDialog.Builder(v.getContext());
                passwordReset.setTitle("Reset Password");
                passwordReset.setMessage("Enter your email to get the reset link");
                passwordReset.setView(resetEmail);

                passwordReset.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetEmail.getText().toString();
                        if (mail.equals(null) || mail.equals("")) {
                            Snackbar.make(root, "Please type in a valid email", Snackbar.LENGTH_LONG).show();
                        } else {
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(root, "Reset link sent successfully. You should receive it shortly ", Snackbar.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(root, "Failed to send reset email " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                });
                passwordReset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                passwordReset.create().show();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            System.out.println("The user is signed in ********");
        } else {
            System.out.println("The user is not signed in***********");
        }
    }

    // sign in method
    private void showSignInWindow() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Sign in");
        dialog.setMessage("Please complete all fields");

        LayoutInflater inflater = LayoutInflater.from(this); // get needed layout
        //put this layout in view variable
        View sign_in_window = inflater.inflate(R.layout.sign_in_mini_window, null);
        dialog.setView(sign_in_window);

        final MaterialEditText email = sign_in_window.findViewById(R.id.email_field);
        final MaterialEditText pass = sign_in_window.findViewById(R.id.pass_field);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // hide the pop up window
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                // if no text in email field is entered
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Please enter a valid email address", Snackbar.LENGTH_LONG).show();
                    return;// if there is error, exit
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    Snackbar.make(root, "Please enter a password between 9 and 16 characters", Snackbar.LENGTH_LONG).show();
                    return;
                } else if ((pass.getText().toString().length() < 9) || (pass.getText().toString().length() > 16)) {
                    Snackbar.make(root, "Please enter a password between 9 and 16 characters", Snackbar.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    if (!user.isEmailVerified()) {
                                        verifyEmailDialog(user);

                                    } else {
                                        if (stringContainsNumber(user.getEmail())) {
                                            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                                        } else {
                                            startActivity(new Intent(MainActivity.this, StaffDashboardActivity.class));
                                        }
                                        finish();
                                    }
                                } else {
                                    Snackbar.make(root, "Unauthorised User ", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Sign in error  " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
        // show the pop up window
        dialog.show();
    }

    /***
     *
     * @param s
     * @return
     */
    public boolean stringContainsNumber(String s) {
        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(s);
        return m.find();
    }

    public void verifyEmailDialog(final FirebaseUser user){
        final AlertDialog.Builder verifyEmail = new AlertDialog.Builder(MainActivity.this);
        verifyEmail.setTitle("Email not verified!");
        verifyEmail.setMessage("Please check your inbox to verify your email address");

        verifyEmail.setPositiveButton("Resend code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(root, "Verification email sent successfully", Snackbar.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, "Failed to send verification email" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
        verifyEmail.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        verifyEmail.create().show();
    }
}
