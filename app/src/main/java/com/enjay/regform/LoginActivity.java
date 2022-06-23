package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    DBHelper data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.signup).setOnClickListener(click->{
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        });
        findViewById(R.id.login).setOnClickListener(click->{
            data = new DBHelper(LoginActivity.this);
            Toast.makeText(this, data.login(getInput(R.id.username),getInput(R.id.password)),
                    Toast.LENGTH_SHORT).show();
        });
    }
    String getInput(int id){
        TextInputLayout textInputLayout = findViewById(id);
        return textInputLayout.getEditText().getText().toString();
    }
}