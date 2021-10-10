package com.example.fooddeilvery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;

public class Register extends AppCompatActivity {
    String[] Rangsit = {"Rangsit","Phatomtani"};

    TextInputLayout Fname,Lname,Email,Pass,cpass,mobileno,houseno,area,pincode;
    Spinner Statespin,Cityspin;
    Button signup;
    CountryCodePicker Cpp;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String fname,lname,emailid,password,confpassword,mobile,house,Area,Pincode,role="Customer",statee,cityy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Fname = (TextInputLayout)findViewById(R.id.Firstname);
        Lname = (TextInputLayout)findViewById(R.id.Lastname);
        Pass = (TextInputLayout)findViewById(R.id.Pwd);
        cpass = (TextInputLayout)findViewById(R.id.CPwd);
        mobileno = (TextInputLayout)findViewById(R.id.Mobileno);
        houseno = (TextInputLayout)findViewById(R.id.houseNo);
        area = (TextInputLayout)findViewById(R.id.Area);
        pincode = (TextInputLayout)findViewById(R.id.Pincode);
        Statespin= (Spinner) findViewById(R.id.Statee);
        Cityspin = (Spinner)findViewById(R.id.Citys);

        signup = (Button)findViewById(R.id.btn_signup);

        Cpp = (CountryCodePicker)findViewById(R.id.CountryCode);

        Statespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Object value = adapterView.getItemIdAtPosition(i);
                statee = value.toString().trim();
                if (statee.equals("Rangsit")){
                    ArrayList<String> list = new ArrayList<>();
                    for (String cities : Rangsit){
                        list.add(cities);
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Register.this,android.R.layout.simple_spinner_item,list);
                    Cityspin.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Cityspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Object value = adapterView.getItemIdAtPosition(i);
                cityy = value.toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        databaseReference = firebaseDatabase.getInstance().getReference("Customer");
        FAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = Fname.getEditText().getText().toString().trim();
                lname = Lname.getEditText().getText().toString().trim();
                emailid = Email.getEditText().getText().toString().trim();
                password = Pass.getEditText().getText().toString().trim();
                confpassword = cpass.getEditText().getText().toString().trim();
                Area = area.getEditText().getText().toString().trim();
                house = houseno.getEditText().getText().toString().trim();
                Pincode = pincode.getEditText().getText().toString().trim();
            }
        });

        if (isValid()){
            final ProgressDialog mDialog = new ProgressDialog(Register.this);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage("กำลังลงทะเบียน โปรดรอสักครู");
            mDialog.show();

            FAuth.createUserWithEmailAndPassword(emailpattern,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(useridd);
                        final HashMap<String , String> hashMap = new HashMap<>();
                        hashMap.put("Role",role);
                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                HashMap<String , String> hashMap1 = new HashMap<>();
                                hashMap1.put("หมายเลขโทรศัพท์",mobile);
                                hashMap1.put("ชื่อ",fname);
                                hashMap1.put("นามสกุล์",lname);
                                hashMap1.put("อีเมล์",emailid);
                                hashMap1.put("อำเภอ",cityy);
                                hashMap1.put("เขต",Area);
                                hashMap1.put("รหัสผ่าน",password);
                                hashMap1.put("ยืนยันรหัสผ่าน",confpassword);
                                hashMap1.put("ถนน",statee);
                                hashMap1.put("บ้านเลขที่",house);
                                hashMap1.put("รหัสไปรษณีย์",Pincode);

                                firebaseDatabase.getInstance().getReference("Customer")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDialog.dismiss();

                                        FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                                    builder.setMessage("อีเมล์นี้ของคุณเคยลงทะเบียนไปแล้ว โปรดเช็คอีกครั้ง");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    AlertDialog Alert = builder.create();
                                                    Alert.show();
                                                }else{
                                                    mDialog.dismiss();
                                                    ReusableCodeFoeAll.ShowAlert(Register.this,"Error",task.getException().getMessage());
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    String emailpattern = "[a-z A-Z0-9._-]+@[a-z]+\\+[a-z]+";
    public  boolean isValid(){
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Lname.setErrorEnabled(false);
        Lname.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        mobileno.setErrorEnabled(false);
        mobileno.setError("");
        cpass.setErrorEnabled(false);
        cpass.setError("");
        area.setErrorEnabled(false);
        area.setError("");
        houseno.setErrorEnabled(false);
        houseno.setError("");
        pincode.setErrorEnabled(false);
        pincode.setError("");

        boolean isValid=false,isValidlname=false,isValidhouseno=false,isValidname=false,isValdemail=false,isValdpassword=false,isValdconfpassword=false,isValdmobilenum=false,isValdarea=false,isValdpincode=false;
        if (TextUtils.isEmpty(fname)){
            Fname.setErrorEnabled(true);
            Fname.setError("โปรดกรอกชื่อ");
        }else{
            isValidname = true;
        }
        if (TextUtils.isEmpty(lname)){
            Lname.setErrorEnabled(true);
            Lname.setError("โปรดกรอกนามสกุล");
        }else{
            isValidlname = true;
        }
        if (TextUtils.isEmpty(emailid)){
            Email.setErrorEnabled(true);
            Email.setError("โปรดใช้อีเมล์จริง");
        }else{
            if(emailid.matches(emailpattern)){
                isValdemail = true;
            }else{
                Email.setErrorEnabled(true);
                Email.setError("ป้อนรหัสอีเมลที่ถูกต้อง");
            }
        }
        if (TextUtils.isEmpty(password)){
            Pass.setErrorEnabled(true);
            Pass.setError("ใส่รหัสผ่าน");
        }else{
            if (password.length()<8) {
                Pass.setErrorEnabled(true);
                Pass.setError("รหัสผ่านง่ายไป");
            }else {
                isValdpassword = true;
            }
        }

        if (TextUtils.isEmpty(confpassword)){
            cpass.setErrorEnabled(true);
            cpass.setError("รหัสผ่านใหม่อีกครั้ง");
        }else{
            if (!password.equals(confpassword)){
                cpass.setErrorEnabled(true);
                cpass.setError("รหัสผ่านไม่ตรงกัน");
            }else {
                isValdconfpassword = true;
            }
        }
        if (TextUtils.isEmpty(mobile)){
            mobileno.setErrorEnabled(true);
            mobileno.setError("กรอกหมายเลขโทรศัพท์อีกครั้ง");
        }else{
            if (mobile.length()<10){
                mobileno.setErrorEnabled(true);
                mobileno.setError("หมายเลขโทรศัพท์มือถือไม่ถูกต้อง");
            }else{
                isValdmobilenum = true;
            }
        }
        if (TextUtils.isEmpty(Area)){
            area.setErrorEnabled(true);
            area.setError("จำเป็นต้องระบุพื้นที่");
        }else{
            isValdarea = true;
        }
        if (TextUtils.isEmpty(Pincode)){
            pincode.setErrorEnabled(true);
            pincode.setError("จำเป็นต้องระบุรหัสไปรษณีย์");
        }else{
            if (Pincode.length()<5){
                pincode.setErrorEnabled(true);
                pincode.setError("รหัสไปรษณีย์ไม่ถูกต้อง");
            }else{
                isValdpincode = true;
            }
        }
        if (TextUtils.isEmpty(house)){
            houseno.setErrorEnabled(true);
            houseno.setError("จำเป็นต้องใส่บ้านเลขที่");
        }else{
            isValidhouseno = true;
        }

        isValid = (isValdarea && isValdpassword && isValdconfpassword && isValdpincode && isValdemail && isValdmobilenum && isValidname && isValidlname && isValidhouseno) ? true : false;
        return isValid;
    }
}