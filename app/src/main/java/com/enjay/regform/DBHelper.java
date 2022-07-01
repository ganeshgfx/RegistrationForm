package com.enjay.regform;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE = "myapp.db";
    public static final String TABLE_NAME = "userdata";

    public static final String USERNAME = "username";
    public static final String FULL_NAME = "fullname";
    public static final String EMAIL = "email";
    public static final String NUMBER = "number";
    public static final String GENDER = "gender";
    public static final String HOBBIES = "hobbies";
    public static final String PASSWORD = "password";
    public static final String PROFILE = "profile";

    public DBHelper(Context context) {
        super(context, DATABASE , null, 17);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(

                "create table "+TABLE_NAME+"("+USERNAME+" text PRIMARY KEY,fullname text,"+EMAIL+" text," +
                        "number " +
                        "text,gender text,hobbies text,password text,"+PROFILE+" blob);"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertUser (String username, String fullname, String email, String number,
                              String gender,String hobbies,String password,byte[] img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, username);
        contentValues.put(FULL_NAME, fullname);
        contentValues.put(EMAIL, email);
        contentValues.put(NUMBER, number);
        contentValues.put(GENDER, gender);
        contentValues.put(HOBBIES, hobbies);
        contentValues.put(GENDER, gender);
        contentValues.put(PASSWORD, password);
        contentValues.put(PROFILE,img);
        try {
            db.insertOrThrow(TABLE_NAME, null, contentValues);
        }catch (SQLiteConstraintException error){
            //return "Error : "+error.getMessage();
            return false;
        }catch (Exception error){
            //return "Some Error : "+error.getMessage();
            return false;
        }
        //return "Inserted";
        return true;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }
    @SuppressLint("Range")
    public String getData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where "+USERNAME+" = '"+username+"';"
                , null );
        res.moveToFirst();
        return ""+USERNAME+" : "+res.getString(res.getColumnIndex(USERNAME))+
                "\n"+FULL_NAME+" : "+res.getString(res.getColumnIndex(FULL_NAME))+
                "\n"+EMAIL+" : "+res.getString(res.getColumnIndex(EMAIL))+
                "\n"+NUMBER+" : "+res.getString(res.getColumnIndex(NUMBER))+
                "\n"+GENDER+" : "+res.getString(res.getColumnIndex(GENDER))+
                "\n"+HOBBIES+" : "+res.getString(res.getColumnIndex(HOBBIES))+
                "\n"+PASSWORD+" : "+res.getString(res.getColumnIndex(PASSWORD));
    }
    public Bitmap getProfile(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur =
                db.rawQuery( "select "+PROFILE+" from "+TABLE_NAME+" where "+USERNAME+" = '"+username+
                                "';"
                        , null );

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(0);
            cur.close();
            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }

        return null;
    }
    @SuppressLint("Range")
    public User login(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =
                db.rawQuery("select * from " + TABLE_NAME + " where " + USERNAME + " = '" + username + "' " +
                                "AND " + PASSWORD + " = '" + password + "';"
                        , null);
        res.moveToFirst();

        if(res.getCount()==1)

            return new User(
                    res.getString(res.getColumnIndex(USERNAME)),
                    res.getString(res.getColumnIndex(FULL_NAME)),
                    res.getString(res.getColumnIndex(EMAIL)),
                    res.getString(res.getColumnIndex(NUMBER)),
                    res.getString(res.getColumnIndex(PASSWORD)),
                    res.getString(res.getColumnIndex(GENDER)),
                    res.getString(res.getColumnIndex(HOBBIES)),
                    getProfile(username)
            );
        else return null;
    }
    @SuppressLint("Range")
    public List<User> getAllData() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =
                db.rawQuery("select * from " + TABLE_NAME + ";", null);
        res.moveToFirst();
        List<User> users = new ArrayList<>();
       if (res.moveToFirst()){
           while (!res.isAfterLast()) {

//            byte[] blob = res.getBlob(res.getColumnIndex(PROFILE));
//            Bitmap img = BitmapFactory.decodeByteArray(blob, 0, blob.length);

               users.add(new User(
                       res.getString(res.getColumnIndex(USERNAME)),
                       res.getString(res.getColumnIndex(FULL_NAME)),
                       res.getString(res.getColumnIndex(EMAIL)),
                       res.getString(res.getColumnIndex(NUMBER)),
                       res.getString(res.getColumnIndex(PASSWORD)),
                       res.getString(res.getColumnIndex(GENDER)),
                       res.getString(res.getColumnIndex(HOBBIES)),
                       getProfile( res.getString(res.getColumnIndex(USERNAME)))
               ));
              // Log.d("TAG", "getAllData: "+ res.getString(res.getColumnIndex(USERNAME)));
               res.moveToNext();
           }
       }
        return users;
    }

    //for future use
    public String md5(String password,String salt) {
        String s =  password+salt;
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
