package com.example.android.sharethemeal;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.android.sharethemeal.Utility.AbhayaLibre;

public class Food_Donation_details extends AppCompatActivity {
    EditText name, address, quantity, phonenumber;
    Button okay;
    DatabaseReference database;
    FirebaseAuth firebaseAuth;
    Intent prev_intent;
    CheckBox cb_veg, cb_nveg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_donation_details);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        prev_intent = getIntent();
        String z = prev_intent.getStringExtra("lat_lon_jason");

        final Location_Data locdata = new Gson().fromJson(z, new TypeToken<Location_Data>() {
        }.getType());

        name = (EditText) findViewById(R.id.nameoforg);
        address = (EditText) findViewById(R.id.address);
        quantity = (EditText) findViewById(R.id.quantity);
        phonenumber = (EditText) findViewById(R.id.phone);
        okay = (Button) findViewById(R.id.done_food);

        cb_nveg = (CheckBox) findViewById(R.id.cb_nveg);
        cb_veg = (CheckBox) findViewById(R.id.cb_veg);

        cb_nveg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) cb_veg.setChecked(false);
                else if (!cb_veg.isChecked()) cb_nveg.setChecked(true);
            }
        });

        cb_veg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) cb_nveg.setChecked(false);
                else if (!cb_nveg.isChecked()) cb_veg.setChecked(true);
            }
        });

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodDonation food_details = new FoodDonation();
                food_details.address = capitalize(address.getText().toString());
                food_details.name_of_provider = capitalize(name.getText().toString());
                food_details.phone_number = phonenumber.getText().toString();
                food_details.quantity = Integer.parseInt(quantity.getText().toString());

                food_details.veg_nonveg = getFoodType();

                food_details.loc_data = locdata;

                upload_details_to_firebase(food_details);
                createNotification();

            }
        });
        TextView title = (TextView) findViewById(R.id.food_donor_title);
        title.setTypeface(AbhayaLibre);
        TextView appName = (TextView) findViewById(R.id.food_donor_appName);
        appName.setTypeface(AbhayaLibre);
    }

    public void upload_details_to_firebase(FoodDonation food) {
        try {
            database.child("FoodDonate").child(System.currentTimeMillis() + "").setValue(food);
        } catch (Exception e) {

        }
        Intent myintent = new Intent(Food_Donation_details.this, Main_start.class);
        startActivity(myintent);
        finish();
    }

    int getFoodType() {
        return cb_veg.isChecked() ? 0 : 1;
    }

    public String capitalize(String s) {
        char first = s.charAt(0);
        if ((int) first >= 97) {
            s = (char) (s.charAt(0) - 32) + s.substring(1);
        }
        return s;

    }


    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, FoodDonorListItem.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 3); // adds one hour


        DateFormat df = new SimpleDateFormat("h:mm a");
        String date = df.format(cal.getTime());
        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Thanks for your contribution")
                .setContentText("Food pick up is scheduled on "+ date).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent).setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }
}



