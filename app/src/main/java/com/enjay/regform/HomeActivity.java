package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    List<User> userList;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editSP;

    RecyclerView recyclerView;
    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        sharedPreferences = getSharedPreferences("regForm",MODE_PRIVATE);
        editSP = sharedPreferences.edit();

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        profileCard = findViewById(R.id.myProfileCard);

        data = new DBHelper(HomeActivity.this);

        //Log.d("TAG", "onCreate: "+  getIntent().getStringExtra("USERNAME"));
        //Log.d("TAG", "onCreate: "+  getIntent().getStringExtra("PASSWORD"));

        user = data.login(
                getIntent().getStringExtra("USERNAME"),
                getIntent().getStringExtra("PASSWORD")
        );
        if(user!=null){
            profile.setImageBitmap(user.getImg());
            name.setText(user.fullName);
        }

        Bundle args = new Bundle();
        args.putCharSequence("name","User");

//        getSupportFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .add(R.id.fragment_viewProfile, ProfileFragment.class, null)
//                .commit();



        profileCard.setOnLongClickListener(view -> {
            logOut();
            return false;
        });

        recyclerView = (RecyclerView)findViewById(R.id.listView);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(RecyclerView.HORIZONTAL);
//        layout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layout);
        userList = data.getAllData();
        adapter = new MyListAdapter(
                userList,
                getSupportFragmentManager()
        );
        recyclerView.setAdapter(
                adapter
        );

        LinearLayout mainLinearLayout =findViewById(R.id.mainLinearLayout);
        FragmentContainerView fragmentContainer = findViewById(R.id.fragment_viewProfile);

        findViewById(R.id.addNewUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("fromHome",true);
                startActivity(intent);
                editSP.putBoolean("ADD_NEW", true);
                editSP.commit();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(data.getAllData().size()>userList.size()){
            adapter.update(data.getAllData());
        }
        Log.d("TAG", data.getAllData().size()+"<-N : O->"+userList.size());
    }

    private void logOut() {

        editSP.putBoolean("userCredentials",false);
        editSP.putBoolean("directLogin",false);
        editSP.putString("USERNAME", "");
        editSP.putString("PASSWORD", "");
        editSP.commit();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}