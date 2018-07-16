package com.example.android.sharethemeal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;

import static com.example.android.sharethemeal.Utility.AbhayaLibre;
import static com.example.android.sharethemeal.Utility.nunito_bold;
import static com.example.android.sharethemeal.Utility.nunito_reg;

public class fill_child_details extends AppCompatActivity {
Intent prev_intent;
    DatabaseReference database;
    FirebaseAuth firebaseAuth;
    EditText name,age,other;
    Button done;
    boolean flag;
    FrameLayout image;

    CheckBox cb_Male, cb_Female;

    TextView upchPhoto;

    ImageView imageView;
    Bitmap global;
    String k;
    String z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_child_details);
        database=FirebaseDatabase.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();

        flag=false;
        name=(EditText)findViewById(R.id.nameofhomeless);
        cb_Male=(CheckBox)findViewById(R.id.cbMale);
        cb_Female=(CheckBox)findViewById(R.id.cbFeMale);
        age=(EditText)findViewById(R.id.ageofhomeless);
        other=(EditText)findViewById(R.id.other);
        done=(Button)findViewById(R.id.done);
        done.setTypeface(nunito_bold);

        image=(FrameLayout) findViewById(R.id.fl_uploadPhoto);
        imageView=(ImageView)findViewById(R.id.imageView);

        prev_intent=getIntent();
        z=prev_intent.getStringExtra("lat_lon_jason");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1067);
                flag=true;
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimage(global);
            }
        });

        cb_Female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) cb_Male.setChecked(false);
                else if(!cb_Male.isChecked()) cb_Female.setChecked(true);
            }
        });

        cb_Male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) cb_Female.setChecked(false);
                else if(!cb_Female.isChecked()) cb_Male.setChecked(true);
            }
        });
        upchPhoto=(TextView)findViewById(R.id.tv_upchPhoto);
        upchPhoto.setTypeface(nunito_reg);

        TextView title = (TextView) findViewById(R.id.child_de_title);
        title.setTypeface(AbhayaLibre);
        TextView appName = (TextView) findViewById(R.id.child_de_appName);
        appName.setTypeface(AbhayaLibre);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1067 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            global=photo;
            imageView.setImageBitmap(photo);
            upchPhoto.setText("Change Photo");
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadimage(Bitmap bitmap)
    {
        if(flag==true){
        FirebaseStorage firebasestorage=FirebaseStorage.getInstance();
        StorageReference storageref=firebasestorage.getReferenceFromUrl("gs://sharethemal.appspot.com/");
        StorageReference mountainImagesRef = storageref.child("images/" + name.getText().toString()+ ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                k =taskSnapshot.getDownloadUrl().toString();
                Homeless current_homeless=new Homeless();
                current_homeless.name=name.getText().toString();
                current_homeless.gender=getGender();
                current_homeless.age= Integer.parseInt(age.getText().toString());
                current_homeless.other=other.getText().toString();
                current_homeless.tagged_by=firebaseAuth.getCurrentUser().getEmail();
                current_homeless.downvotes=0;
                current_homeless.upvotes=0;
                current_homeless.pic_data_url="imagepresent";
                Location_Data locdata=new Gson().fromJson(z,new TypeToken<Location_Data>(){}.getType());
                current_homeless.loc_data=locdata;
                Long s= System.currentTimeMillis() ;
                current_homeless.id=s;


                try {
                    database.child("Homeless").child(s+"").setValue(current_homeless);
                }
                catch(Exception e)
                {

                }
                flag=false;

                Intent intent=new Intent(fill_child_details.this,Main_start.class);
                startActivity(intent);
                finish();

            }
        });}

        else
        {
            Homeless current_homeless=new Homeless();
            current_homeless.name=name.getText().toString().toLowerCase();
            current_homeless.gender=getGender();
            current_homeless.age= Integer.parseInt(age.getText().toString());
            current_homeless.other=other.getText().toString();
            current_homeless.tagged_by=firebaseAuth.getCurrentUser().getEmail();
            current_homeless.downvotes=0;
            current_homeless.upvotes=0;
            current_homeless.pic_data_url="nil";
            Location_Data locdata=new Gson().fromJson(z,new TypeToken<Location_Data>(){}.getType());
            current_homeless.loc_data=locdata;
            Long s= System.currentTimeMillis() ;
            current_homeless.id=s;


            try {
                database.child("Homeless").child(s+"").setValue(current_homeless);
            }
            catch(Exception e)
            {

            }

            Intent intent=new Intent(fill_child_details.this,Main_start.class);
            startActivity(intent);
            finish();

        }

    }

    String getGender(){
        return cb_Female.isChecked() ?"Girl":"Boy";
    }
}
