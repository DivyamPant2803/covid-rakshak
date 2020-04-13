package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserDetails extends AppCompatActivity {

    FirebaseAuth mAuth,mAuth2;
    //FirebaseAuth.AuthStateListener mauthListener;
    Button signup;
    DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =1 ;

    //Widgets
    private EditText name, contact, address, city, state, country, aadhaar;
    private Button submit;
    private CountryCodePicker countryCodePicker;
    private TextInputLayout nameTemp, cityTemp, stateTemp, countryTemp;

    //Variables
    private String userName, userContact, userAddress, userState, userCity, userCountry, userAadhaar, userId;
    private int flag=0;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onStart () {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
        //finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        name = findViewById(R.id.Name);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        country = findViewById(R.id.country);
        aadhaar = findViewById(R.id.aadhaar);
        submit = findViewById(R.id.submit);
        nameTemp = findViewById(R.id.nameTemp);
        cityTemp = findViewById(R.id.cityTemp);
        stateTemp = findViewById(R.id.stateTemp);
        countryTemp = findViewById(R.id.countryTemp);
        mAuth=FirebaseAuth.getInstance();

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.Name, "[A-Za-z]+[\\sA-Za-z]*$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.city, "^[A-Za-z\\\\s]{1,}[\\\\.]{0,1}[A-Za-z\\\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.state, "^[A-Za-z\\\\s]{1,}[\\\\.]{0,1}[A-Za-z\\\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.country, "^[A-Za-z\\\\s]{1,}[\\\\.]{0,1}[A-Za-z\\\\s]{0,}$", R.string.nameerror);



        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(contact);

        if(Build.VERSION.SDK_INT >=23){
            if(checkSelfPermission(Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(UserDetails.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = name.getText().toString().trim();
                userContact = contact.getText().toString().trim();
                userAddress = address.getText().toString().trim();
                userCity = city.getText().toString().trim();
                userState = state.getText().toString().trim();
                userCountry = country.getText().toString().trim();
                userAadhaar = aadhaar.getText().toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                userId = sdf.format(new Date());
                if(awesomeValidation.validate())
                    Toast.makeText(UserDetails.this,"Validation successful", Toast.LENGTH_SHORT).show();
                else
                    return;




                if(TextUtils.isEmpty(userName))
                {
                    Toast.makeText(UserDetails.this, "Enter the Name", Toast.LENGTH_SHORT).show();
                    //name.setError("Incorrect Name Format");
                    flag++;
                    return;
                }
                if(TextUtils.isEmpty(userContact))
                {
                    Toast.makeText(UserDetails.this, "Enter the Contact Number", Toast.LENGTH_SHORT).show();
                    flag++;
                    return;
                }
                if(TextUtils.isEmpty(userAddress))
                {
                    Toast.makeText(UserDetails.this, "Enter the Address", Toast.LENGTH_SHORT).show();
                    flag++;
                    return;
                }
                if(TextUtils.isEmpty(userCity))
                {
                    Toast.makeText(UserDetails.this, "Enter the City", Toast.LENGTH_SHORT).show();
                    flag++;
                    return;
                }
                if(TextUtils.isEmpty(userState))
                {
                    Toast.makeText(UserDetails.this, "Enter the State", Toast.LENGTH_SHORT).show();
                    flag++;
                    return;
                }
                if(TextUtils.isEmpty(userCountry))
                {
                    Toast.makeText(UserDetails.this, "Enter the Country", Toast.LENGTH_SHORT).show();
                    flag++;
                    return;
                }
                if(TextUtils.isEmpty(userAadhaar))
                {
                    Toast.makeText(UserDetails.this, "Enter the Aadhaar Number", Toast.LENGTH_SHORT).show();
                    flag++;
                    return;
                }
                if(flag==0) {
                    if (TextUtils.isEmpty(contact.getText().toString())) {
                        Toast.makeText(UserDetails.this, "Enter Contact No ....", Toast.LENGTH_SHORT).show();
                    } else if (contact.getText().toString().replace(" ", "").length() != 10) {
                        Toast.makeText(UserDetails.this, "Enter Correct No ...", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("name",userName);
                        bundle.putString("contact",userContact);
                        bundle.putString("address",userAddress);
                        bundle.putString("state",userState);
                        bundle.putString("city",userCity);
                        bundle.putString("country",userCountry);
                        bundle.putString("aadhaar",userAadhaar);
                        bundle.putString("userId",userId);
                        Intent intent = new Intent(UserDetails.this, VerificationActivity.class);
                        intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus().replace(" ", ""));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }

            }

        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(UserDetails.this,MainActivity.class));
                }
            }
        };
    }
    private static boolean isStringOnlyAlpha(String str){
        return ((str != null)
                && (!str.equals("")) && (str.matches("^[a-zA-Z]*$]")));
    }
}
