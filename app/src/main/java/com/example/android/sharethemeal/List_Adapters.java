package com.example.android.sharethemeal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static com.example.android.sharethemeal.Utility.nunito_Extrabold;
import static com.example.android.sharethemeal.Utility.nunito_bold;
import static com.example.android.sharethemeal.Utility.nunito_reg;


class Child_list_Item extends BaseAdapter {
    List<Homeless> home_list;
    LayoutInflater inflater;
    Context context;

    Child_list_Item(Context con, List<Homeless> list)
    {
        home_list=list;
        context=con;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return home_list.size();
    }

    @Override
    public Object getItem(int i) {
        return home_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class Holder
    {
        TextView name,age,others;
        ImageView homelessimage;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View myview = inflater.inflate(R.layout.card_child,null);

        final Homeless homeless =  home_list.get(i);

        TextView nameView =(TextView)myview.findViewById(R.id.homeless_name);
        String sname=homeless.name;
        nameView.setText(sname.substring(0, 1).toUpperCase() + sname.substring(1).toLowerCase());
        nameView.setTypeface(nunito_Extrabold);


        TextView ageView =(TextView)myview.findViewById(R.id.homeless_age);
        ageView.setText(homeless.age+" years");
        ageView.setTypeface(nunito_reg);


        TextView genderView = (TextView)myview.findViewById(R.id.homeless_Gender);
        String gender;
        genderView.setText(homeless.gender);
        genderView.setTypeface(nunito_reg);



        TextView descView = (TextView)myview.findViewById(R.id.homeless_desc);
        descView.setText(homeless.other);
        descView.setTypeface(nunito_reg);


        TextView taggedView =(TextView)myview.findViewById(R.id.homeless_taggedBy);
        taggedView.setText(homeless.tagged_by);
        taggedView.setTypeface(nunito_reg);

        ImageButton thumbsup=(ImageButton)myview.findViewById(R.id.homeless_thumbsUp);
        ImageButton thumbsdown=(ImageButton)myview.findViewById(R.id.homeless_thumbsDown);
        final TextView no_up=(TextView)myview.findViewById(R.id.homeless_no_thumbsUp);
        final TextView no_down=(TextView)myview.findViewById(R.id.homeless_no_thumbsDown);
        no_up.setText(home_list.get(i).upvotes+"");
        no_down.setText(home_list.get(i).downvotes+"");

        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();


        thumbsup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home_list.get(i).upvotes=home_list.get(i).upvotes+1;
              databaseReference.child("Homeless").child(home_list.get(i).id+"").setValue(home_list.get(i));
                Log.d("id",home_list.get(i).id+"");
                no_up.setText(home_list.get(i).upvotes+"");
            }
        });

        thumbsdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Homeless").child(home_list.get(i).id+"").child("downvotes").setValue(home_list.get(i).downvotes+1);
                home_list.get(i).downvotes=home_list.get(i).downvotes+1;
                no_down.setText(home_list.get(i).downvotes+"");
            }
        });


        StorageReference storage= FirebaseStorage.getInstance().getReference();
        StorageReference s1=storage.child("images/"+nameView.getText().toString().toLowerCase()+".jpg");
        ImageView homelessimage=(ImageView)myview.findViewById(R.id.homeless_pic);
        if(homeless.pic_data_url==null || homeless.pic_data_url.equals("nil")==false) {

            Glide.with(context).using(new FirebaseImageLoader()).load(s1).into(homelessimage);
        }
        else
        {

        }
        homelessimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ChildViewOnMap.class);
                intent.putExtra("index",i);
                context.startActivity(intent);
            }
        });


        return myview;
    }

    public void notify(List<Homeless> newlist)
    {
        home_list=newlist;
        notifyDataSetChanged();
    }
}


class Food_Donor_list extends BaseAdapter
{
    List<FoodDonation> foodlist;
    LayoutInflater layoutInflater;
    Context context;

    Food_Donor_list(List<FoodDonation> food_dis, Context con)
    {
        foodlist=food_dis;
        context=con;
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return foodlist.size();
    }

    @Override
    public Object getItem(int i) {
        return foodlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class Holder
    {
        TextView name;
        TextView quantity;
        TextView phone;
        TextView address;
        TextView type;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
       View myview= layoutInflater.inflate(R.layout.donor_listview,null);
        Holder holder=new Holder();
        holder.name= (TextView) myview.findViewById(R.id.nameofhomeless);
        holder.address=(TextView)myview.findViewById(R.id.address);
        holder.phone=(TextView)myview.findViewById(R.id.ph_no);
        holder.quantity=(TextView)myview.findViewById(R.id.quantity);
        holder.type=(TextView)myview.findViewById(R.id.mytype);

        holder.name.setTypeface(nunito_bold);
        holder.address.setTypeface(nunito_reg);
        holder.phone.setTypeface(nunito_reg);
        holder.quantity.setTypeface(nunito_reg);
        holder.type.setTypeface(nunito_reg);

        holder.name.setText(foodlist.get(i).name_of_provider);
        holder.quantity.setText(foodlist.get(i).quantity+" Kg");
        holder.address.setText(foodlist.get(i).address);
        holder.phone.setText(foodlist.get(i).phone_number);
        holder.type.setText(getFoodType(foodlist.get(i).veg_nonveg));

        myview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,FoodDonorListItem.class );
                intent.putExtra("index",i);
                context.startActivity(intent);
            }
        });

        return myview;
    }

    public void notify_myfunc(List<FoodDonation> flist)
    {
        foodlist=flist;
        notifyDataSetChanged();
    }

    private String getFoodType(int vegnvegCode){
        return vegnvegCode==0 ? "Veg" : "Non Veg";
    }
}
