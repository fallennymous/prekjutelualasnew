package com.example.fallennymous.prekjutelulas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fallennymous.prekjutelulas.Common.Common;
import com.example.fallennymous.prekjutelulas.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignIn extends AppCompatActivity {
    @BindView(R.id.edtPhone) MaterialEditText edtPhone;
    @BindView(R.id.edtPassword) MaterialEditText edtPassword;
    @BindView(R.id.btnSignIn) Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        // Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        final DatabaseReference table_user = database.getReference("User");

        // listener when user presses Sign in button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // show diaglog
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Tunggu Sebentar");
                mDialog.show();

                // Get User information
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Check if user exists in database
                        if (dataSnapshot.child(edtPhone.getText().toString().trim()).exists()) {
                            // dismiss dialog
                            mDialog.dismiss();
                            // get user
                            User user = dataSnapshot.child(edtPhone.getText().toString().trim()).getValue(User.class);
                            // set phone for user
                            user.setPhone(edtPhone.getText().toString().trim());

                            // Check password
                            if (user.getPassword().equalsIgnoreCase(edtPassword.getText().toString())) {
                                Intent homeIntent = new Intent(SignIn.this, Home.class);
                                Common.currenUser = user;
                                startActivity(homeIntent);
                                finish();
                            } else {
                                Toast.makeText(SignIn.this, "Password Salah", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "User Tidak di Temukan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
