package com.example.android.sharethemeal;

import java.util.ArrayList;
import java.util.List;


public class Data_holder {
    public static List<Homeless> Homeless_list=new ArrayList<>();
    public static List<FoodDonation> Food_distribution=new ArrayList<>();
}

class Login_credentials
{
    String email_id;
    String password;
}

class Location_Data
{
    public double latitude;
    public double longitude;
}

class Homeless
{
    public String name;
    public String gender;
    public int age;
    public String other;
    public Location_Data loc_data;
    public String tagged_by;
    public int downvotes;
    public int upvotes;
    public String pic_data_url;
    public long id;
}

class FoodDonation
{
    public String name_of_provider;
    public int quantity;
    public String phone_number;
    public String address;
    public int veg_nonveg;
    public Location_Data loc_data;

}