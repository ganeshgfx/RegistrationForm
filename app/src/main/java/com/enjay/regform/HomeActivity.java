package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.enjay.regform.fragment.ProfileFragment;
import com.enjay.regform.fragment.profile.MyListAdapter;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

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

        Log.d("TAG", "onCreate: "+  getIntent().getStringExtra("USERNAME"));
        Log.d("TAG", "onCreate: "+  getIntent().getStringExtra("PASSWORD"));

        user = data.login(
                getIntent().getStringExtra("USERNAME"),
                getIntent().getStringExtra("PASSWORD")
        );
        if(user!=null){
            profile.setImageBitmap(user.getImg());
            name.setText(user.fullName);
        }


        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_viewProfile, ProfileFragment.class, null)
                .commit();

        profileCard.setOnLongClickListener(view -> {
            logOut();
            return false;
        });

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.listView);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(
                layout);
        recyclerView.setAdapter(new MyListAdapter(data.getAllData()));
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