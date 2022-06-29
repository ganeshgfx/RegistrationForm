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
    CheckBox directLogin;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("regForm",MODE_PRIVATE);
        editSP = sharedPreferences.edit();

        saveLoginInfo = findViewById(R.id.saveLoginInfo);
        directLogin = findViewById(R.id.loginDirect);

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

                if(saveLoginInfo.isChecked() || directLogin.isChecked()){
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
        saveLoginInfo.setOnCheckedChangeListener((compoundButton, b) -> {
            editSP.putBoolean("userCredentials",b);
            if (b){
                directLogin.setChecked(false);
            }
            return;
        });
        directLogin.setOnCheckedChangeListener((compoundButton, b) -> {
            editSP.putBoolean("directLogin",b);
            if (b){
                saveLoginInfo.setChecked(false);
            }
            return;
        });
        autofill();
    }

    String getInput(int id){
        TextInputLayout textInputLayout = findViewById(id);
        return textInputLayout.getEditText().getText().toString();
    }
    void setInput(int id,String msg){
        TextInputLayout textInputLayout = findViewById(id);
        textInputLayout.getEditText().setText(msg);
    }

    private void autofill() {
        if(sharedPreferences.getBoolean("userCredentials",false)){
            String username = sharedPreferences.getString("USERNAME", "");
            String password = sharedPreferences.getString("PASSWORD", "");
            setInput(R.id.username,username);
            setInput(R.id.password,password);
        }

    }
}