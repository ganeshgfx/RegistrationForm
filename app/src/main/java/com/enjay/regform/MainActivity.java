package com.enjay.regform;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String USERNAME_PATTERN = "[A-Za-z]([A-Za-z0-9]){2,20}";
    public static final String FULL_NAME_PATTERN = "[A-Za-z ]*";
    public static final String EMAIL_PATTERN = "(([A-Za-z][A-Za-z0-9_.]{1,64})@[A-Za-z]{2,}([.]{1})([A-Za-z]{2,255}))*";
    public static final String NUMBER_PATTERN = "([0-9]{10,14})";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d{3,})(?=" +
            ".*[@$!%*?&])[A-Za-z\\d@$!%*?& .\\\\/|(){}^#_+=₹\"'`:;,~`|•√π÷×¶∆£€$¢°©®™✓-]{10,}$";

    String username;
    String fullName;
    String email;
    String number;
    String newPassword;
    String confirmPassword;
    String gender;
    List<String> hobbies;
    Bitmap img;

    boolean usernameCheck;
    boolean fullNameCheck;
    boolean emailCheck;
    boolean numberCheck;
    boolean newPasswordCheck;
    boolean confirmPasswordCheck;
    boolean genderCheck;
    boolean hobbiesCheck;
    boolean profileCheck = false;

    DBHelper data;

    ImageView editProfileIcon;
    ImageView profile;
    AlertDialog alertDialog;
    MaterialCardView imageCard;
    TextInputLayout passwordInputLayout;
    TextInputLayout confirmPasswordTL ;
    TextInputLayout numberInputLayout;
    TextInputLayout emailInputLayout;
    TextInputLayout fullNameInputLayout;
    TextInputLayout usernameInputLayout;
    Button signup;
    Button login;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startActivity(new Intent(MainActivity.this,LoginActivity.class));
//        finish();

        sharedPreferences = getSharedPreferences("regForm",MODE_PRIVATE);
        editSP = sharedPreferences.edit();

        data = new DBHelper(MainActivity.this);
        checkOldLogin();

        profile = findViewById(R.id.profile);
        editProfileIcon = findViewById(R.id.editProfileIcon);
        imageCard = findViewById(R.id.imageCard);
        passwordInputLayout = findViewById(R.id.newPassword);
        confirmPasswordTL = findViewById(R.id.confirmPassword);
        numberInputLayout = findViewById(R.id.number);
        emailInputLayout = findViewById(R.id.email);
        fullNameInputLayout = findViewById(R.id.fullName);
        usernameInputLayout = findViewById(R.id.username);
        signup = (Button) findViewById(R.id.signup);
        login =  findViewById(R.id.login);

        checkUsername();
        checkFullName();
        checkEmail();
        checkNumber();
        checkPassword();

        signup.setOnClickListener(click->{

            username = getInput(R.id.username);
            fullName = getInput(R.id.fullName);
            email = getInput(R.id.email);
            number = getInput(R.id.number);
            newPassword = getInput(R.id.newPassword);
            confirmPassword = getInput(R.id.confirmPassword);

            if( validateUsername(username) && validateFullName(fullName) && validateEmail(email) && validateNumber(number) && validatePassword(newPassword) && checkConfirmPassword() && isGenderCheck() && validateHobbies() && checkProfile()){
                boolean result = data.insertUser(username,fullName,email,number,gender,
                        hobbies.toString(),
                        newPassword,bitmapToByte(img));
                if(result) {
                 if(getIntent().getBooleanExtra("fromHome",false)){
                     finish();
                 }
                 else {
                     Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                 }
                }
                else {
                    usernameInputLayout.setError("Username not available");
                    scrollTop();
                }
                //Toast.makeText(this, result+"", Toast.LENGTH_SHORT).show();
            }

        });
        signup.setOnLongClickListener(view -> {

            TextView output = findViewById(R.id.output);
            Log.d("TAG", "onLongClick: rows : "+data.numberOfRows());
            try{
                output.setText(data.getData(getInput(R.id.username)));
                profile.setImageBitmap(data.getProfile(getInput(R.id.username)));
            }catch (Exception e){
                output.setText("Error : "+e.getMessage());
            }
            return false;
        });
        imageCard.setOnClickListener(v->{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.profile_dialog, viewGroup,
                    false);
            dialogView.findViewById(R.id.capture).setOnClickListener(click->{
                captureImage();
            });
            dialogView.findViewById(R.id.select).setOnClickListener(click->{
                selectImage();
            });
            builder.setView(dialogView);
            builder.setTitle("Add profile picture");
            builder.setNegativeButton("Cancel", (dialog, i) -> {
                dialog.dismiss();
            });
            alertDialog = builder.create();
            alertDialog.show();
            imageCard.setStrokeColor(ContextCompat.getColor(this,R.color.onBase));
            imageCard.setStrokeWidth(3);
            editProfileIcon.setColorFilter(ContextCompat.getColor(this,R.color.onBase));
        });
        login.setOnClickListener(click->{
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        });

    }

    private void checkOldLogin() {

        if(sharedPreferences.getBoolean("ADD_NEW",false)){
            editSP.putBoolean("ADD_NEW", false);
            editSP.commit();
        } else if(sharedPreferences.getBoolean("directLogin",false)){
            String username = sharedPreferences.getString("USERNAME", "");
            String password = sharedPreferences.getString("PASSWORD", "");

            if(data.login(username,password)==null){
                return;
            }
            else {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("PASSWORD", password);

                startActivity(intent);
                finish();
            }
        }else {

        }

    }

    private boolean validateHobbies() {
        gender = getGender();
        return hobbiesCheck;
    }

    private boolean isGenderCheck() {
        hobbies = getHobbies();
        return genderCheck;
    }

    private boolean checkConfirmPassword() {
        if(!newPassword.equals(confirmPassword)){
            confirmPasswordTL.setError("Password didn't match.");
            return false;
        }else {
            confirmPasswordTL.setError(null);
             return true;
        }
    }

    private boolean checkProfile() {
        //Toast.makeText(this, ""+profileCheck, Toast.LENGTH_SHORT).show();
        if(!profileCheck){
            imageCard.setStrokeColor(ContextCompat.getColor(this,R.color.red));

            ValueAnimator animation = ValueAnimator.ofInt(0, 7);
            animation.setInterpolator(new AnticipateOvershootInterpolator());
            animation.setDuration(500);
            animation.start();
            animation.addUpdateListener(updatedAnimation -> {
                int animatedValue = (int)updatedAnimation.getAnimatedValue();
                imageCard.setStrokeWidth(animatedValue);
            });

            editProfileIcon.setColorFilter(ContextCompat.getColor(this,R.color.red));

            scrollTop();
            Toast.makeText(this, "Please add profile", Toast.LENGTH_SHORT).show();
        }
        return profileCheck;
    }

    private void scrollTop() {
        ScrollView scrollView = findViewById(R.id.mainScroll);
        scrollView.scrollTo(0,0);
    }

    private void captureImage() {
        if(checkCamPermission()){
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(i,reqCode);
            try {
                takeImage.launch(i);
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Please Allow Camera permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}
                    , 100);
        }
    }

    private void selectImage() {
        //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //photoPickerIntent.setType("image/*");
        //startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        selectImage.launch("image/*");
    }

    private boolean checkCamPermission() {
        String permission = android.Manifest.permission.CAMERA;
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    ActivityResultLauncher<String> selectImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri imageUri) {
                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profile.setImageBitmap(selectedImage);
                        profileCheck = true;
                        alertDialog.dismiss();
                        img = selectedImage;
                        //bitmapToByte(img);
                    } catch (Exception e) {
                       // Toast.makeText(MainActivity.this,e.getLocalizedMessage()+":"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

    ActivityResultLauncher<Intent> takeImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK && result.getData()!=null){
                Bundle bundle = result.getData().getExtras();
                Bitmap image = (Bitmap) bundle.get("data");
                profile.setImageBitmap(image);
                profileCheck = true;
                alertDialog.dismiss();
                img = image;
            }
        }
    });

    byte[] bitmapToByte(Bitmap bitmap){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        float ratio = Math.min(
                (float) 500 / bitmap.getWidth(),
                (float) 500 / bitmap.getHeight());
        int width = Math.round((float) ratio * bitmap.getWidth());
        int height = Math.round((float) ratio * bitmap.getHeight());

        //Toast.makeText(this, height+":"+width, Toast.LENGTH_LONG).show();
        bitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
        //bitmap = Bitmap.createScaledBitmap(bitmap,500,500,true);
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);

        return (outputStream.toByteArray());
    }

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

        if(validateHobbies()) findViewById(R.id.hobbiError).setVisibility(View.GONE);
        else findViewById(R.id.hobbiError).setVisibility(View.VISIBLE);
        return hobbies;
    }
    //for validations
    void checkUsername(){
        usernameInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {
                validateUsername(str);
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }

    private boolean validateUsername(CharSequence str) {
        if(str.length()==0){
            usernameInputLayout.setError("Cannot be empty");
            scrollTop();
            return false;
        }
        else if(checkPattern(str, USERNAME_PATTERN)){
            usernameInputLayout.setError(null);
            return true;
        }else if(Character.isDigit(str.charAt(0))){
            usernameInputLayout.setError("Number not allowed at start.");
            scrollTop();
            return false;
        }
        else if(str.length()>20){
            usernameInputLayout.setError("Max length reached");
            scrollTop();
            return false;
        }
        else{
            usernameInputLayout.setError("Invalid Input only letters and numbers allowed");
            scrollTop();
            return false;
        }
    }

    void checkFullName(){

        fullNameInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {
                validateFullName(str);
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }

    private boolean validateFullName(CharSequence str) {
        if(str.length()==0){
            fullNameInputLayout.setError("Cannot be empty");
            scrollTop();
            return false;
        }
        else if(checkPattern(str, FULL_NAME_PATTERN)){
            fullNameInputLayout.setError(null);
            return true;
        }else{
            fullNameInputLayout.setError("Invalid Input, only letters");
            scrollTop();
            return false;
        }
    }

    void checkEmail(){
        emailInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {
                validateEmail(str);
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }

    private boolean validateEmail(CharSequence str) {
        if(str.length()==0){
            emailInputLayout.setError("Cannot be empty");
            return false;
        }
        else if(checkPattern(str, EMAIL_PATTERN)){
            emailInputLayout.setError(null);
            return true;
        }else{
            emailInputLayout.setError("Invalid Email");
            return false;
        }
    }

    void checkNumber(){
                numberInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {
                validateNumber(str);
            }

            @Override
            public void afterTextChanged(Editable editable){}
        });
    }

    private boolean validateNumber(CharSequence str) {
        if(str.length()==0){
            numberInputLayout.setError("Cannot be empty");
            return false;
        }
        else if(str.length()<10 || str.length()>15){
            numberInputLayout.setError("Minimum 10, maximum 14 numbers");
            return false;
        }
        else if(checkPattern(str, NUMBER_PATTERN)){
            numberInputLayout.setError(null);
            return true;
        }else{
            numberInputLayout.setError("Only number allowed");
            return false;
        }
    }

    void checkPassword(){

        passwordInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence str, int i, int i1, int i2) {
                validatePassword(str);
            }
            @Override
            public void afterTextChanged(Editable editable){}
        });
    }

    private boolean validatePassword(CharSequence str) {
        if(str.length()==0){
            passwordInputLayout.setError("Cannot be empty");
            return false;
        }
        else if(checkPattern(str, PASSWORD_PATTERN)){
            passwordInputLayout.setError(null);
            return true;
        }else{
            passwordInputLayout.setError("Must contain (1 uppercase, 1 special character,3 " +
                    "digits and minimum 10 length)");
            return false;
        }
    }

    boolean checkPattern(CharSequence str,String pattern){
        Pattern sPattern = Pattern.compile(pattern);
        return sPattern.matcher(str).matches();
    }

    // TODO: For testing
    void fillData(){
//         passwordInputLayout.getEditText().setText("Ganesh@1234");
//         numberInputLayout;
//         emailInputLayout;
//         fullNameInputLayout;
//         usernameInputLayout;
    }

}