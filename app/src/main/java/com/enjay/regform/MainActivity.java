package com.enjay.regform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
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

    boolean usernameCheck;
    boolean fullNameCheck;
    boolean emailCheck;
    boolean numberCheck;
    boolean newPasswordCheck;
    boolean confirmPasswordCheck;
    boolean genderCheck;
    boolean hobbiesCheck;

    DBHelper data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new DBHelper(MainActivity.this);
//
//        data.insertUser("ganesh","g g","g@g.com","123","m","photo","123");

        //Toast.makeText(this, data.getData("ganesh")+"", Toast.LENGTH_SHORT).show();

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
            gender = getGender();
            hobbies = getHobbies();
            newPassword = getInput(R.id.newPassword);
            confirmPassword = getInput(R.id.confirmPassword);

            TextInputLayout cp = findViewById(R.id.confirmPassword);
            if(!newPassword.equals(confirmPassword)){
                cp.setError("Password didn't match.");
                confirmPasswordCheck = false;
            }else {
                cp.setError(null);
                confirmPasswordCheck = true;
            }

            if( usernameCheck&& fullNameCheck && emailCheck&& numberCheck&& newPasswordCheck&& confirmPasswordCheck&& genderCheck&& hobbiesCheck){
               String result = data.insertUser(username,fullName,email,number,gender,
                        hobbies.toString(),
                        newPassword);
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }

        });
        signup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TextView output = findViewById(R.id.output);
                Log.d("TAG", "onLongClick: rows : "+data.numberOfRows());
                try{
                    output.setText(data.getData(getInput(R.id.username)));
                }catch (Exception e){
                    output.setText("Error : "+e.getMessage());
                }
                return false;
            }
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

        male.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) setTextError(R.id.genderError,!b);
        });
        female.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) setTextError(R.id.genderError,!b);
        });
        others.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) setTextError(R.id.genderError,!b);
        });

        genderCheck = true;

        if(male.isChecked()) return "Male";
        else if(female.isChecked()) return "Female";
        else if(others.isChecked()) return "Other";
        else {
            setTextError(R.id.genderError,true);
            genderCheck = false;
        };
        return null;
    }

    private void setTextError(int id, boolean set) {
        if(set)
        findViewById(id).setVisibility(View.VISIBLE);
        else findViewById(id).setVisibility(View.GONE);
    }

    List <String> getHobbies(){
        List <String> hobbies = new ArrayList<>();

        CheckBox sports = (CheckBox) findViewById(R.id.sports);
        CheckBox music = (CheckBox) findViewById(R.id.music);
        CheckBox photo = (CheckBox) findViewById(R.id.photo);
        CheckBox reading = (CheckBox) findViewById(R.id.reading);

        sports.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) setTextError(R.id.hobbiError,!b);
        });
        music.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) setTextError(R.id.hobbiError,!b);
        });
        photo.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)  setTextError(R.id.hobbiError,!b);
        });
        reading.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) setTextError(R.id.hobbiError,!b);
        });

        hobbiesCheck = false;

        if(sports.isChecked()) {
            hobbiesCheck = true;
            hobbies.add("Sports");
        };
        if(music.isChecked()) {
            hobbiesCheck = true;
            hobbies.add("Music");
        };
        if(photo.isChecked()) {
            hobbiesCheck = true;
            hobbies.add("Photography");
        };
        if(reading.isChecked()) {
            hobbiesCheck = true;
            hobbies.add("Reading");
        };

        if(hobbiesCheck) findViewById(R.id.hobbiError).setVisibility(View.GONE);
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
                    usernameCheck = true;
                }else{
                    textInputLayout.setError("Invalid Input");
                    usernameCheck = false;
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
                    fullNameCheck = true;
                }else{
                    textInputLayout.setError("Invalid Input");
                    fullNameCheck = false;
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
                    emailCheck = true;
                }else{
                    textInputLayout.setError("Invalid Email");
                    emailCheck = false;
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
                    numberCheck = true;
                }else{
                    textInputLayout.setError("Invalid Number");
                    numberCheck = false;
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
                    newPasswordCheck = true;
                }else{
                    textInputLayout.setError("Weak Password");
                    newPasswordCheck = false;
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