package com.dotpot.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

import com.dotpot.app.R;
import com.dotpot.app.utl;

import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * Created by shivesh on 23/11/17.
 */

public class DateTimePicker {


    public static interface DateTimeCallback{

        public void picked(String dateTime);

    }


    public static interface MiliisCallback{

        public void picked(Long dateTime);

    }

    public Context ctx;
    public Activity act;
    public static int DATE_ONLY =101, TIME_ONLY =105,DATE_TIME=106;

    public int PICK_TYPE=DATE_TIME;

    public String format="dd MMMM yyyy - hh:mm aa";


    public DateTimeCallback cb;
    public MiliisCallback cbT;

    public DateTimePicker(Activity at,int pick,DateTimeCallback callback){


        act=at;
        ctx=act;
        cb=callback;
        PICK_TYPE=pick;
    }

    public DateTimePicker(Activity at,int pick,MiliisCallback callback){


        act=at;
        ctx=act;
        cbT=callback;
        PICK_TYPE=pick;
    }


    public DateTimePicker(Activity at,int pick,String format,DateTimeCallback callback){


        act=at;
        ctx=act;
        cb=callback;
        this.format =format;
        PICK_TYPE=pick;


    }



    View v=null;

    public Dialog getDig() {
        return dig;
    }

    private Dialog dig;
    public void pick( boolean datePicked){

        AlertDialog.Builder di;
        if(!datePicked||v==null){

            if(dig!=null)
            {
                if(dig.isShowing())
                    dig.dismiss();
            }
            v=act.getLayoutInflater().inflate(R.layout.utl_date_time_picker,null);
            di = new AlertDialog.Builder(ctx);
            di.setView(v);
            dig=di.create();
            dig.show();

        }


        if(v!=null) {
            final DatePicker date = (DatePicker) v.findViewById(R.id.datePicker1);
            final TimePicker time = (TimePicker) v.findViewById(R.id.timePicker1);
            final Button next = (Button) v.findViewById(R.id.login);


            if(PICK_TYPE== TIME_ONLY)
            {
                date.setVisibility(View.GONE);
                time.setVisibility(View.VISIBLE);
                next.setText("DONE");
                format="hh:mm aa";



            }
            else if(PICK_TYPE== DATE_ONLY)
            {
                time.setVisibility(View.GONE);
                date.setVisibility(View.VISIBLE);
                next.setText("DONE");
                format="dd MMMM yyyy";

            }
            else
            {
                if(datePicked)
                {
                    date.setVisibility(View.GONE);
                    time.setVisibility(View.VISIBLE);
                }else{
                    time.setVisibility(View.GONE);
                    date.setVisibility(View.VISIBLE);

                }

            }


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //dd MM yyyy - HH:ii p
                    Calendar cal=Calendar.getInstance();
                    SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                    int monthnum=date.getMonth();
                    cal.set(Calendar.MONTH,monthnum);
                    String month_name = month_date.format(cal.getTime());

                    //   utl.snack(act,"min "+time.getCurrentMinute());
                    Integer selMin;
                    if(time.getCurrentMinute()>=30)
                    {
                        selMin=30;
                    }
                    else
                    {
                        selMin=0;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        time.setMinute(selMin);
                    }
                    else
                        time.setCurrentMinute(selMin);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                            time.getCurrentHour(),selMin);

                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    String output = formatter.format(calendar.getTime()); //eg: "Tue May"


                    utl.e("DateTimePicker","Selected :  "+" -> "+output);



                    if(next.getText().toString().contains("NEXT")&&PICK_TYPE==DATE_TIME)
                    {
                        next.setText("DONE");
                        pick(true);

                    }
                    else {

                        dig.dismiss();;
                        if(cb!=null)
                            cb.picked(output);
                        if(cbT!=null)
                            cbT.picked(calendar.getTime().getTime());

                        lastPickedString = output;
                        lastPickedMilis = calendar.getTime().getTime();
                     }



                }
            });

        }

    }

    long lastPickedMilis = 0l;
    String lastPickedString = "";

    public String getLastPickedString() {
        return lastPickedString;
    }


    public long getPickedMilis(){
       return lastPickedMilis;
    }






}
