package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    DBHelper data;
    ImageView profile;
    TextView name;
    TextView details;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        details = findViewById(R.id.details);

        data = new DBHelper(HomeActivity.this);

        user = data.login(
                getIntent().getStringExtra("USERNAME"),
                getIntent().getStringExtra("PASSWORD")
        );

        profile.setImageBitmap(user.getImg());
        details.setText(user.toString());
        name.setText(user.fullName);

        //Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
    }
}