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

/**
 * Created by shivesh on 29/6/17.
 */

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

    private String webIdToken;
    private boolean isAnonymous=true;

    private String fcmToken;
    private String status;
    private String verifdoc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVerifdoc() {
        return verifdoc;
    }

    public void setVerifdoc(String verifdoc) {
        this.verifdoc = verifdoc;
    }

    public String getName() {
        return ""+name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAlias() {
        return alias;
    }

    public String getAliasAndVerif() {
        String status=this.getStatus();
        if(status.startsWith("VERIFIED"))
            return alias+ Constants.V2V_VERIFIED;
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return id;
    }

    public void setUid(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender.toLowerCase();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public GenricUser()
    {

    }


    public boolean canPost(){
        return getStatus()!=null && (isAdmin() ||getStatus().equals(Constants.userStatuses[2]) );
    }
    public boolean isAdmin(){
        return getStatus()!=null && getStatus().equals(Constants.userStatuses[3]);
    }
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getStatus() {
        if(status==null) status="";
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
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


    public String getWebIdToken() {
        return webIdToken;
    }

    public void setWebIdToken(String webIdToken) {
        this.webIdToken = webIdToken;
    }


    public boolean validate(){
        return !utl.isEmpty(phone) &&
                !utl.isEmpty(dateofbirthLong) &&
                !utl.isEmpty(email) &&
                !utl.isEmpty(name) &&
                !utl.isEmpty(password) ;
    }

}
