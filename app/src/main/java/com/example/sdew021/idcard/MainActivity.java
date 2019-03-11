package com.example.sdew021.idcard;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseApp;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    int flag=1;
    EditText userID;
    EditText amount;
    EditText pin;
    Button button;
    //private Firebase mRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = findViewById(R.id.userID);
        amount = findViewById(R.id.amount);
        pin = findViewById(R.id.pin);
        button = findViewById(R.id.button);

        Firebase.setAndroidContext(this);
        //mRef = new Firebase("https://idcard-9b3c1.firebaseio.com/Users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                checkDataEntered();
                if(flag==0)
                    AddTransaction();
                if(flag==0)
                    clearForm((ViewGroup) findViewById(R.id.view));
                if(flag==0)
                    showToast();

            }
        });
    }

    public void showToast()
    {
        Toast t = Toast.makeText(this,"Transaction Added",Toast.LENGTH_SHORT);
        t.show();
    }

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

    public void AddTransaction(){
        final String uid = userID.getText().toString();
        final String amt = amount.getText().toString();
        final int cash = Integer.parseInt(amt);
        DatabaseReference current_user_db = mDatabase.child(uid);
        //Map<String,Object> taskMap = new HashMap<>();
        //taskMap.put("Amount", "12");
        //current_user_db.updateChildren(taskMap);
        //current_user_db.child("User ID").setValue(uid);
        //current_user_db.child("Amount").setValue(Integer.toString(cash));

        mDatabase.child(uid).child("Amount").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                String amt2 = (String)dataSnapshot.getValue();
                if(amt2==null)
                    amt2="0";
                int cash2 = Integer.parseInt(amt2);
                int tot = cash+cash2;
                mDatabase.child(uid).child("Amount").setValue(Integer.toString(tot));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    boolean isEmpty(EditText text){
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isKey(EditText text,EditText uid1){
        String key = text.getText().toString();
        String uid = uid1.getText().toString();
        int pin = Integer.parseInt(key);
        if(uid.equals("220122246198150") && key.equals("1212"))
            return true;
        if(uid.equals("15512112153119")&& key.equals("1234"))
            return true;
        if(uid.equals("4377781236")&& key.equals("0101"))
            return true;
        if(uid.equals("7514825110266")&& key.equals("4321"))
            return true;
        if(uid.equals("18272122248124")&& key.equals("9898"))
            return true;
        else
            return false;

    }

    boolean isAmount(EditText text){
        String key = text.getText().toString();
        int pin = Integer.parseInt(key);
        if(pin>0&&pin<1000)
            return true;
        else
            return false;
    }

    boolean isUserID(EditText text){
        String uid1 = text.getText().toString();
        if(uid1.equals("220122246198150")||uid1.equals("15512112153119")||uid1.equals("4377781236")||uid1.equals("7514825110266")||uid1.equals("18272122248124"))
            return true;
        else
            return false;
    }

    void checkDataEntered(){
        flag=0;
        if(isEmpty(userID)){
            flag=1;
            userID.setError("This field cannot be empty");
        }

        if(isEmpty(amount)){
            flag=1;
            amount.setError("This field cannot be empty");
        }
        if(isEmpty(pin)){
            flag=1;
            pin.setError("This field cannot be empty");

        }
        if (!isUserID(userID)) {
            flag = 1;
            userID.setError("Enter a valid user ID");
        }
        if(flag==0) {
            if (!isKey(pin, userID)) {
                flag = 1;
                pin.setError("Wrong pin!!!");
            }

            if (!isAmount(amount)) {
                flag = 1;
                amount.setError("Enter a valid amount");
            }
        }
    }
}
