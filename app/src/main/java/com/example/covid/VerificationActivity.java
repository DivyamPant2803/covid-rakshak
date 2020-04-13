package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covid.MainActivity;
import com.example.covid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    private EditText otp;
    private Button submit;
    private TextView resend;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference ref;
    Bundle bundle;

    //Variables
    private String phone,id;//, name, address, contact, city, state, country, aadhaar, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        bundle = getIntent().getExtras();

        otp=(EditText)findViewById(R.id.otp);
        submit=(Button)findViewById(R.id.submitButton);
        resend=findViewById(R.id.resend);
        mAuth = FirebaseAuth.getInstance();
        phone = getIntent().getStringExtra("phone");

        //database = FirebaseDatabase.getInstance();
        //uid = firebaseUser.getUid();
        //ref = database.getReference().child("Users").child("Non Suspects");

        sendVerificationCode();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(otp.getText().toString())){
                    Toast.makeText(VerificationActivity.this, "Enter Otp", Toast.LENGTH_SHORT).show();
                }
                else if(otp.getText().toString().replace(" ","").length()!=6){
                    Toast.makeText(VerificationActivity.this, "Enter right otp", Toast.LENGTH_SHORT).show();
                }
                else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString().replace(" ",""));
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

    }
    private void sendVerificationCode() {

        new CountDownTimer(60000,1000){
            @Override
            public void onTick(long l) {
                resend.setText(""+l/1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText(" Resend");
                resend.setEnabled(true);
            }
        }.start();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationActivity.this.id = id;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerificationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Log.e("ERROR", e.getMessage());
                    }
                });        // OnVerificationStateChangedCallbacks
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            /*if(bundle != null){
                                name = bundle.getString("name");
                                contact = bundle.getString("contact");
                                address = bundle.getString("address");
                                city = bundle.getString("city");
                                state = bundle.getString("state");
                                country = bundle.getString("country");
                                aadhaar = bundle.getString("aadhaar");
                                uid = bundle.getString("userId");

                                NonSuspectUsers users = new NonSuspectUsers();
                                users.setUserName(name);
                                users.setContact(contact);
                                users.setAddress(address);
                                users.setCity(city);
                                users.setState(state);
                                users.setCountry(country);
                                users.setAadhaar(aadhaar);

                                DatabaseReference myRef = ref.child(uid);

                                myRef.setValue(users);

                            }*/
                            Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            //startActivity(new Intent(VerificationActivity.this, MainActivity.class));
                            finish();
                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            Toast.makeText(VerificationActivity.this, "Verification Filed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
