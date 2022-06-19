package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String USERNAME_PATTERN = "[A-Za-z]([A-Za-z0-9]){2,20}";
    public static final String FULL_NAME_PATTERN = "[A-Za-z]{1,20} [A-Za-z]{1,20}";
    public static final String EMAIL_PATTERN = "(([A-Za-z][A-Za-z0-9_.]{1,64})@[A-Za-z]{2,}(([.]{1})([A-Za-z]{2,255}))*)";
    public static final String NUMBER_PATTERN = "([0-9]{10,14})";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d{3,})(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$";
    String username;
    String fullName;
    String email;
    String number;
    String newPassword;
    String confirmPassword;
    String gender;
    List<String> hobbies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUsername();
        checkFullName();
        checkEmail();
        checkNumber();
        checkPassword();

        Button signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(click->{

            username = getInput(R.id.username);
            fullName = getInput(R.id.fullName);
            email = getInput(R.id.email);
            number = getInput(R.id.number);
            newPassword = getInput(R.id.newPassword);
            confirmPassword = getInput(R.id.confirmPassword);
            gender = getGender();
            hobbies = getHobbies();

        });
    }
//For getting inputs
    String getInput(int id){
        TextInputLayout textInputLayout = findViewById(id);
        return textInputLayout.getEditText().getText().toString();
    }
    String getGender(){
        RadioButton male =  (RadioButton)findViewById(R.id.male);
        RadioButton female =  (RadioButton)findViewById(R.id.female);
        RadioButton others =  (RadioButton)findViewById(R.id.others);

        if(male.isChecked()) return "Male";
        else if(female.isChecked()) return "Female";
        else if(others.isChecked()) return "Other";
        else findViewById(R.id.genderError).setVisibility(View.VISIBLE);
        return "";
    }
    List <String> getHobbies(){
        List <String> hobbies = new ArrayList<>();

        CheckBox sports = (CheckBox) findViewById(R.id.sports);
        CheckBox music = (CheckBox) findViewById(R.id.music);
        CheckBox photo = (CheckBox) findViewById(R.id.photo);
        CheckBox reading = (CheckBox) findViewById(R.id.reading);

        boolean isAnySelected = false;

        if(sports.isChecked()) {
            isAnySelected = true;
            hobbies.add("Sports");
        };
        if(music.isChecked()) {
            isAnySelected = true;
            hobbies.add("Music");
        };
        if(photo.isChecked()) {
            isAnySelected = true;
            hobbies.add("Photography");
        };
        if(reading.isChecked()) {
            isAnySelected = true;
            hobbies.add("Reading");
        };
        if(!isAnySelected) findViewById(R.id.hobbiError).setVisibility(View.VISIBLE);
        else findViewById(R.id.hobbiError).setVisibility(View.VISIBLE);
        return hobbies;
    }
    //for validations
    void checkUsername(){
        TextInputLayout textInputLayout = findViewById(R.id.username);
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {

                if(checkPattern(str, USERNAME_PATTERN)){
                    textInputLayout.setError(null);
                }else{
                    textInputLayout.setError("Invalid Input");
                }
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }
    void checkFullName(){
        TextInputLayout textInputLayout = findViewById(R.id.fullName);
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {

                if(checkPattern(str, FULL_NAME_PATTERN)){
                    textInputLayout.setError(null);
                }else{
                    textInputLayout.setError("Invalid Input");
                }
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }
    void checkEmail(){
        TextInputLayout textInputLayout = findViewById(R.id.email);
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {

                if(checkPattern(str, EMAIL_PATTERN)){
                    textInputLayout.setError(null);
                }else{
                    textInputLayout.setError("Invalid Email");
                }
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }
    void checkNumber(){
        TextInputLayout textInputLayout = findViewById(R.id.number);
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {

                if(checkPattern(str, NUMBER_PATTERN)){
                    textInputLayout.setError(null);
                }else{
                    textInputLayout.setError("Invalid Number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }
    void checkPassword(){
        TextInputLayout textInputLayout = findViewById(R.id.newPassword);
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {

                if(checkPattern(str, PASSWORD_PATTERN)){
                    textInputLayout.setError(null);
                }else{
                    textInputLayout.setError("Weak Password");
                }
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }
    boolean checkPattern(CharSequence str,String pattern){
        Pattern sPattern = Pattern.compile(pattern);
        return sPattern.matcher(str).matches();
    }

}