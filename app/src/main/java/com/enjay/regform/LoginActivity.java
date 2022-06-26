package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    DBHelper data;
    User user;
    CheckBox saveLoginInfo;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editSP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("regForm",MODE_PRIVATE);
        editSP = sharedPreferences.edit();

        saveLoginInfo = findViewById(R.id.saveLoginInfo);

        findViewById(R.id.signup).setOnClickListener(click->{
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        });
        findViewById(R.id.login).setOnClickListener(click->{
            data = new DBHelper(LoginActivity.this);
            user = data.login(getInput(R.id.username).trim(),getInput(R.id.password));
            if(user==null){
                Toast.makeText(this,"Invalid Username or Password",
                        Toast.LENGTH_SHORT).show();
            }
            else {

                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("USERNAME", user.username);
                intent.putExtra("PASSWORD", user.password);

                if(saveLoginInfo.isChecked()){

                    String username = getInput(R.id.username);
                    String password = getInput(R.id.password);

                    editSP.putString("USERNAME", username);
                    editSP.putString("PASSWORD", password);
                }

                editSP.commit();

                startActivity(intent);
                finish();
                //Toast.makeText(this, user.toString(),Toast.LENGTH_SHORT).show();
            }

        });
        saveLoginInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editSP.putBoolean("userCredentials",true);
                    return;
                }
                else {
                    editSP.putBoolean("userCredentials",false);
                    return;
                }
            }
        });
    }

    String getInput(int id){
        TextInputLayout textInputLayout = findViewById(id);
        return textInputLayout.getEditText().getText().toString();
    }
}