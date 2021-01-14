package com.dotpot.app.models;

import com.dotpot.app.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shivesh on 18/6/19.
 */

public class InAppMessage {


    String id;
    Long dateTime;
    String groupName;// question in qna context
    String groupId;
    String senderName;// useralias in qna context
    String senderId; // asker id in qna context
    String message;
    String icon;
    String attachmentUrl;
    String quotedTextId;// tags in qna context
    String atachmentType= Constants.attachmentTypes[0];
    Boolean read=false;


    String tags;
    public String recieverId;
    String recieverName;

    public String senderStatus;

    public InAppMessage(String groupName, String groupId,
                        String senderName, String senderId,
                        String message, String icon, String attachmentUrl,
                        String atachmentType) {
        dateTime= System.currentTimeMillis();
        id =""+ dateTime;
        this.quotedTextId = null;
        this.groupName = groupName;
        this.groupId = groupId;
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.icon = icon;
        this.attachmentUrl = attachmentUrl;
        this.atachmentType = atachmentType;
    }


    public InAppMessage(String groupName, String groupId, String senderName,
                        String senderId, String message, String icon,
                        String attachmentUrl, String atachmentType,
                        String quotedTextId) {
        dateTime= System.currentTimeMillis();
        id =""+ dateTime;
        this.groupName = groupName;
        this.groupId = groupId;
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.icon = icon;
        this.attachmentUrl = attachmentUrl;
        this.atachmentType = atachmentType;
        this.quotedTextId = quotedTextId;

    }

    public InAppMessage(String id, Long dateTime, String groupName, String groupId, String senderName, String senderId, String message, String icon, String attachmentUrl, String atachmentType, Boolean read, String quotedTextId) {
        this.id = id;
        this.dateTime = dateTime;
        this.groupName = groupName;
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

    public InAppMessage( ) {

    }

    public String getTags() {
        if(tags==null)
            tags="";
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    public String getQuotedTextId() {
        return quotedTextId;
    }

    public void setQuotedTextId(String quotedTextId) {
        this.quotedTextId = quotedTextId;
    }

    public String getGroupName() {
        return groupName!=null ? groupName : "";
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getSenderName() {
        return senderName;
    }
    public String senderNameOnly() {
        return senderName.replace(Constants.V2V_VERIFIED,"");
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
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
    public void setMessage(String message) {
        this.message = message;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAttachmentUrl() {
        return ""+attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAtachmentType() {
        return atachmentType;
    }

    public void setAtachmentType(String atachmentType) {
        this.atachmentType = atachmentType;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
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


    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public String getSenderStatus() {
        return senderStatus;
    }

    public void setSenderStatus(String senderStatus) {
        this.senderStatus = senderStatus;
    }


}
