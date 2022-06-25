package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    DBHelper data;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                startActivity(intent);
                finish();
                //Toast.makeText(this, user.toString(),Toast.LENGTH_SHORT).show();
            }

        });
    }
    String getInput(int id){
        TextInputLayout textInputLayout = findViewById(id);
        return textInputLayout.getEditText().getText().toString();
    }
}