package com.enjay.regform;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
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
    boolean profileCheck;

    DBHelper data;

    ImageView editProfileIcon;
    ImageView profile;
    AlertDialog alertDialog;
    MaterialCardView imageCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new DBHelper(MainActivity.this);

        profile = findViewById(R.id.profile);
        editProfileIcon = findViewById(R.id.editProfileIcon);

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

            if( usernameCheck&& fullNameCheck && emailCheck&& numberCheck&& newPasswordCheck&& confirmPasswordCheck&& genderCheck&& hobbiesCheck && checkProfile()){
                String result = data.insertUser(username,fullName,email,number,gender,
                        hobbies.toString(),
                        newPassword,bitmapToByte(img));
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
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

        imageCard = findViewById(R.id.imageCard);
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
            imageCard.setStrokeWidth(1);
            editProfileIcon.setColorFilter(ContextCompat.getColor(this,R.color.onBase));
        });

        findViewById(R.id.login).setOnClickListener(click->{
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        });
    }

    private boolean checkProfile() {
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

            ScrollView scrollView = findViewById(R.id.mainScroll);
            scrollView.scrollTo(0,0);
            Toast.makeText(this, "Please add profile", Toast.LENGTH_SHORT).show();
        }
        return profileCheck;
    }

    private void captureImage() {
        if(checkCamPermision()){
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
    private boolean checkCamPermision()
    {
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
                    } catch (FileNotFoundException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, outputStream);
        return outputStream.toByteArray();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//
//        switch(requestCode) {
//            case 100:
//                if(resultCode == RESULT_OK){
//                    try {
//                        final Uri imageUri = imageReturnedIntent.getData();
//                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//
//                        profile.setImageBitmap(selectedImage);
//
//                        alertDialog.dismiss();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                break;
//        }
//    }
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
                //textInputLayout.setError(null);
                //Toast.makeText(MainActivity.this, str.charAt(str.length()-1)+"",Toast.LENGTH_SHORT).show();
                if(str.length()==0){
                    textInputLayout.setError("Cannot be empty");
                    usernameCheck = false;
                }
                else if(checkPattern(str, USERNAME_PATTERN)){
                    textInputLayout.setError(null);
                    usernameCheck = true;
                }else if(Character.isDigit(str.charAt(0))){
                    textInputLayout.setError("Number not allowed at start.");
                    usernameCheck = false;
                }
                else if(str.length()>20){
                    textInputLayout.setError("Max length reached");
                    usernameCheck = false;
                }
                else{
                    textInputLayout.setError("Invalid Input only letters and numbers allowed");
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
                if(str.length()==0){
                    textInputLayout.setError("Cannot be empty");
                    usernameCheck = false;
                }
                else if(checkPattern(str, FULL_NAME_PATTERN)){
                    textInputLayout.setError(null);
                    fullNameCheck = true;
                }else{
                    textInputLayout.setError("Invalid Input, only letters");
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
                if(str.length()==0){
                    textInputLayout.setError("Cannot be empty");
                    usernameCheck = false;
                }
                else if(checkPattern(str, EMAIL_PATTERN)){
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
                if(str.length()==0){
                    textInputLayout.setError("Cannot be empty");
                    usernameCheck = false;
                }
                else if(str.length()<10 || str.length()>15){
                    textInputLayout.setError("Minimum 10, maximum 14 numbers");
                    numberCheck = false;
                }
                else if(checkPattern(str, NUMBER_PATTERN)){
                    textInputLayout.setError(null);
                    numberCheck = true;
                }else{
                    textInputLayout.setError("Only number allowed");
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
                if(str.length()==0){
                    textInputLayout.setError("Cannot be empty");
                    usernameCheck = false;
                }
                else if(checkPattern(str, PASSWORD_PATTERN)){
                    textInputLayout.setError(null);
                    newPasswordCheck = true;
                }else{
                    textInputLayout.setError("Must contain (1 uppercase, 1 special character,3 " +
                            "digits and minimum 10 length)");
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