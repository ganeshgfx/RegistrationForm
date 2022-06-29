package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.enjay.regform.fragment.ProfileFragment;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    DBHelper data;
    ImageView profile;
    MaterialCardView profileCard;
    TextView name;

    User user;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        profileCard = findViewById(R.id.myProfileCard);

        data = new DBHelper(HomeActivity.this);

        user = data.login(
                getIntent().getStringExtra("USERNAME"),
                getIntent().getStringExtra("PASSWORD")
        );

        profile.setImageBitmap(user.getImg());

        name.setText(user.fullName);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_viewProfile, ProfileFragment.class, null)
                .commit();

        profileCard.setOnLongClickListener(view -> {
            logOut();
            return false;
        });

    }

    private void logOut() {
        sharedPreferences = getSharedPreferences("regForm",MODE_PRIVATE);
        editSP = sharedPreferences.edit();
        editSP.putBoolean("userCredentials",false);
        editSP.putBoolean("directLogin",false);
        editSP.putString("USERNAME", "");
        editSP.putString("PASSWORD", "");
        editSP.commit();
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
        finish();
    }

}