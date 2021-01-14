package com.dotpot.app.models;


import com.dotpot.app.utl;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenericItem {

    public String id;
    public String title;
    public String text="";
    public String type;
    public Long dateTime;

    public String getTags() {
        if(tags==null)
            tags="";
        return tags;
    }

    public String tags;
    public int status;

    public String objJson;


    public String printDate(){

        SimpleDateFormat format=new SimpleDateFormat("dd MMM yyyy");
        try {
            return format.format(new Date(dateTime));
        } catch (Exception e) {
            return format.format(new Date(System.currentTimeMillis()));
        }

    }

    public String elapsed(){
        return printDifference(new Date(dateTime),new Date(System.currentTimeMillis()));
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


    public <T> Object toObject(Class<T> type) {

        return utl.js.fromJson(objJson,type);
    }



    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }
}
