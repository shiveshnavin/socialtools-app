package com.dotpot.app.models;


import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.utl;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by shivesh on 29/6/17.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenricUser {

    private String name;
    private String alias;
    private String password ;
    private String id;
    private String email;
    private String image;
    private String avatar;
    private String phone;
    private String gender ="male";
    private String type;
    private String dateofbirthLong;
    private String about;

    private String rank;

    private String webIdToken;
    private boolean isAnonymous=true;

    private String fcmToken;
    private String status;
    private String verifdoc;
    private Float weeklyAward;

    public String getName() {
        return ""+name;
    }

    public String getAliasAndVerif() {
        String status=this.getStatus();
        if(status.startsWith("VERIFIED"))
            return alias+ Constants.V2V_VERIFIED;
        return alias;
    }
    public void setGender(String gender) {
        this.gender = gender.toLowerCase();
    }

    @Nullable
    public int getAge() {

        if(dateofbirthLong ==null || dateofbirthLong.length()<5)
            return 0;

        Date d=new Date(Long.parseLong(dateofbirthLong));
        int age= Calendar.getInstance().getTime().getYear()-
                d.getYear();

        return  age;

    }


    @Nullable
    public int getAgeInt() {

        if(dateofbirthLong ==null )
            return 0;

        Date d=new Date(Long.parseLong(dateofbirthLong));
        int age= Calendar.getInstance().getTime().getYear()-
                d.getYear();

        return  age;

    }

    public String  getDateofbirthString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        if(getDateofbirthLong()==null)
        return null;
        Date date = new Date(Long.parseLong(getDateofbirthLong()));
        return formatter.format(date);
    }

    public String getDateofbirthLong() {
        return dateofbirthLong;
    }

    public void setDateofbirthLong(String dateofbirthLong) {
        this.dateofbirthLong = dateofbirthLong;
    }


    public boolean canPost(){
        return getStatus()!=null && (isAdmin() ||getStatus().equals(Constants.userStatuses[2]) );
    }
    public boolean isAdmin(){
        return getStatus()!=null && getStatus().equals(Constants.userStatuses[3]);
    }

    public String getStatus() {
        if(status==null) status="";
        return status;
    }
    public static void renderImage(String userImUrl,ImageView img){

        if(userImUrl!=null && userImUrl.startsWith("http")){
            Picasso.get().load(userImUrl)
                    .error(R.drawable.ic_users).into(img);

        }
    }

    public String hisHer() {
        return getGender().replace("female","her").replace("male","his");
    }


    public boolean validate(){
        return !utl.isEmpty(phone) &&
                !utl.isEmpty(dateofbirthLong) &&
                !utl.isEmpty(email) &&
                !utl.isEmpty(name) &&
                !utl.isEmpty(password) ;
    }

}
