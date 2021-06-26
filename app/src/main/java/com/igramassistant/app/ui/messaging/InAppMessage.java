package com.igramassistant.app.ui.messaging;

import android.content.Intent;

import com.igramassistant.app.App;
import com.igramassistant.app.Constants;
import com.igramassistant.app.ui.activities.SplashActivity;
import com.igramassistant.app.utl;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by shivesh on 18/6/19.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InAppMessage {


    String id;
    Long dateTime;
    String msgTitle;// question in qna context
    String groupId;
    String senderName;// useralias in qna context
    String senderId; // asker id in qna context
    String message;
    String icon;
    String attachmentUrl;
    String quotedTextId;// tags in qna context
    String atachmentType = attachmentTypes[0];
    int read=0;


    String tags;
    public String recieverId;
    String recieverName;

    public String senderStatus;

    volatile public static String[] attachmentTypes = {"Message", "Image","Delete","Exited"};




    public InAppMessage(String id, Long dateTime, String msgTitle, String groupId, String senderName, String senderId, String message, String icon, String attachmentUrl, String atachmentType, int read, String quotedTextId) {
        this.id = id;
        this.dateTime = dateTime;
        this.msgTitle = msgTitle;
        this.groupId = groupId;
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.icon = icon;
        this.attachmentUrl = attachmentUrl;
        this.atachmentType = atachmentType;
        this.read = read;
        this.quotedTextId = quotedTextId;

    }


    public String getTags() {
        if(tags==null)
            tags="";
        return tags;
    }

    public String getMsgTitle() {
        return msgTitle !=null ? msgTitle : "";
    }

    public String senderNameOnly() {
        return (""+senderName).replace(Constants.V2V_VERIFIED,"");
    }

    public String getRefinedMessage()
    {
        if(message==null || message.contains(Constants.C2C_DELETE)){
            return "";
        }
        return (""+message)
                .replace(Constants.C2C_EXIT,"")
                .replace(Constants.C2C_DELETE,"")
                .replace(Constants.O2O,"");
    }
    public String getAttachmentUrl() {
        return ""+attachmentUrl;
    }

    public String elapsedText()
    {

        return printDifference(new Date(dateTime), Calendar.getInstance().getTime());

    }


    public String  printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        if(different<60000)
        {
            return "just now";
        }
/*
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);*/

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if(elapsedDays>=7)
        {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            return format.format(startDate);
        }
        else if(elapsedDays>0) {

            return "" + (elapsedDays + " d")  ;

        }
        else if(elapsedHours>0)
        {
            return ( elapsedHours+" h" ) ;
        }
        else
            return  elapsedMinutes+" mins"   ;
    }





    public String timeFormatted() {

        Date startDate=new Date(dateTime);
        Date endDate =Calendar.getInstance().getTime();



        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        if(different<60000)
        {
            return "just now";
        }
/*
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);*/

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");


        if(elapsedDays>7)
        {

        }
        else if(elapsedDays>0) {

            format=new SimpleDateFormat("dd MMMM hh:mm a");

        }
        else
        {
            format=new SimpleDateFormat("hh:mm a");
        }

        return format.format(startDate);
    }


    public Intent getIntent(){
        Intent it = new Intent(App.getAppContext(),SplashActivity.class);
        it.putExtra("message", utl.js.toJson(this));
        it.putExtra("action","home");
        return it;
    }


    public String getDefaultImageUrl() {
        if(atachmentType!=null && atachmentType.equals(Constants.ATTACHMENT_TYPE_IMAGE)
        && !utl.isEmpty(getAttachmentUrl())){
            return getAttachmentUrl();
        }
        return FirebaseRemoteConfig.getInstance().getString("notif_img_url");
    }

    @Override
    public boolean equals(Object o){
        InAppMessage e = (InAppMessage) o;
        return this.id.equals(e.id);
    }

    public JSONObject toJson() {
        try {
            return new JSONObject(new Gson().toJson(this));
        } catch (JSONException e) {
            return null;
        }
    }
}
