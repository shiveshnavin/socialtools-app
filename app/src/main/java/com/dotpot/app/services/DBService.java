package com.dotpot.app.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dotpot.app.App;
import com.dotpot.app.BuildConfig;
import com.dotpot.app.ui.messaging.InAppMessage;
import com.dotpot.app.utl;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;

import static com.dotpot.app.Constants.C2C_DELETE;


public class DBService extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Messages.db";
    private static final String TABLE_NAME = "messages";
    private static final String TABLE_NAME_META = "messages_meta";
    private static DBService databaseHelperInst;

    private DBService(Context context) {
        super(context, DATABASE_NAME, null, BuildConfig.VERSION_CODE);
    }

    public static synchronized DBService getInstance(Context context) {
        if(context == null)
            context = App.getAppContext();
        if (databaseHelperInst == null)
            databaseHelperInst = new DBService(context);
        return databaseHelperInst;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String[] fieldsString = {"msgTitle", "targetId", "senderName", "senderId", "message", "icon", "attachmentUrl", "attachmentType", "quotedTextId","count"};//

        String query = "create table IF NOT EXISTS " + TABLE_NAME + " (id TEXT PRIMARY KEY , dateTime INTEGER";

        for (String field : fieldsString) {
            query += "," + field + " TEXT";
        }
        query += ",read INTEGER);";

        utl.e("Chats", "" + query);

        db.execSQL(query);
        db.execSQL(query.replace(TABLE_NAME, TABLE_NAME_META));

    }

    public void addNewColumn(String col, SQLiteDatabase db) {

        try {

            String q = "select " + col + " from " + TABLE_NAME + " WHERE LIMIT 1";
            db.execSQL(q, null);
            utl.e("dbHelper", "Already Exists " + col);

        } catch (Exception e) {
            try {
                db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN " + col + " TEXT DEFAULT 0");
                utl.e("dbHelper", "Added " + col);
            } catch (Exception e1) {
                FirebaseCrashlytics.getInstance().recordException(e1);
            }

        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int currentVersion, int latestVersion) {

        if(currentVersion<231){
            utl.e("Chats","Upgrading databse to "+231);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
            addNewColumn("count",db);
        }
        if(currentVersion<301){

            utl.e("Chats","Upgrading databse to "+301);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_META);
            onCreate(db);
        }
    }

    public boolean insertData(InAppMessage cm) {

        if (cm.getMessage().contains(C2C_DELETE)) {
            String id = cm.getMessage().replace(C2C_DELETE, "");
            utl.e("Chats", "Delete : " + id);
            deleteMessageById(id);
          //  return true;
        }

        if (getData(cm.getId()).getCount() > 0) {
            utl.e("dbHelper", "Message Already Inserted");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", cm.getId());
        contentValues.put("dateTime", cm.getDateTime());
        contentValues.put("msgTitle", cm.getMsgTitle());
        contentValues.put("targetId", cm.getGroupId());
        contentValues.put("senderName", cm.getSenderName());
        contentValues.put("senderId", cm.getSenderId());
        contentValues.put("message", cm.getMessage());
        contentValues.put("icon", cm.getIcon());
        contentValues.put("attachmentUrl", cm.getAttachmentUrl());
        contentValues.put("attachmentType", cm.getAtachmentType());
        contentValues.put("read", cm.getRead());
        contentValues.put("quotedTextId", cm.getQuotedTextId());

        long result = 0;
        try {
            if(cm.getMessage().length()>0 ||  cm.getAttachmentUrl().length()>5)
                result = db.insert(TABLE_NAME, null, contentValues);
            insertMeta(cm, contentValues);
        } catch (Exception e) {

        }
        return result != -1;
    }

    public boolean insertMeta(InAppMessage cm, ContentValues contentValues) {

        SQLiteDatabase db = this.getWritableDatabase();
        contentValues.put("count", cm.getQuotedTextId());
        db.delete(TABLE_NAME_META, "targetId = ?", new String[]{cm.getGroupId()});
        db.insert(TABLE_NAME_META, null, contentValues);

        return true;
    }


    public ArrayList<InAppMessage> convertCursorToArray(Cursor res) {
        ArrayList<InAppMessage> cms = new ArrayList<>();


        while (res.moveToNext()) {
            InAppMessage cm = new InAppMessage(

                    res.getString(res.getColumnIndex("id")),
                    res.getLong(res.getColumnIndex("dateTime")),
                    res.getString(res.getColumnIndex("msgTitle")),
                    res.getString(res.getColumnIndex("targetId")),
                    res.getString(res.getColumnIndex("senderName")),
                    res.getString(res.getColumnIndex("senderId")),
                    res.getString(res.getColumnIndex("message")),
                    res.getString(res.getColumnIndex("icon")),
                    res.getString(res.getColumnIndex("attachmentUrl")),
                    res.getString(res.getColumnIndex("attachmentType")),
                    res.getInt(res.getColumnIndex("read")),
                    res.getString(res.getColumnIndex("quotedTextId"))


            );
            //-- utl.e(DATABASE_NAME,"Chat Latest : "+cm.getMessage());

            cms.add(cm);
        }

        return cms;

    }

    public int getUnreadCountForGroup(String targetId) {

        SQLiteDatabase db = this.getWritableDatabase();
        String q = "select * from " + TABLE_NAME + " WHERE targetId='" + targetId + "' AND read=0 ";

        Cursor res = db.rawQuery(q, null);

        return res.getCount();
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "select * from " + TABLE_NAME + " WHERE id='" + id+"'";

        Cursor res = db.rawQuery(q, null);

        return res;
    }

    public InAppMessage findMessageById(String id) {

        Cursor res = getData(id);
        InAppMessage cm = null;
        while (res.moveToNext()) {
            cm = new InAppMessage(
                    res.getString(res.getColumnIndex("id")),
                    res.getLong(res.getColumnIndex("dateTime")),
                    res.getString(res.getColumnIndex("msgTitle")),
                    res.getString(res.getColumnIndex("targetId")),
                    res.getString(res.getColumnIndex("senderName")),
                    res.getString(res.getColumnIndex("senderId")),
                    res.getString(res.getColumnIndex("message")),
                    res.getString(res.getColumnIndex("icon")),
                    res.getString(res.getColumnIndex("attachmentUrl")),
                    res.getString(res.getColumnIndex("attachmentType")),
                    res.getInt(res.getColumnIndex("read")),
                    res.getString(res.getColumnIndex("quotedTextId"))

            );
            //-- utl.e(DATABASE_NAME,"Chat Latest : "+cm.getMessage());
        }
        return cm;
    }



    public InAppMessage getLatestMessage(String targetId) {
        SQLiteDatabase db = this.getWritableDatabase();
       String q = "select * from " + TABLE_NAME_META + " WHERE targetId='" + targetId + "' ORDER BY dateTime DESC LIMIT 1";

        Cursor res = db.rawQuery(q, null);
        InAppMessage cm = null;

        while (res.moveToNext()) {
            cm = new InAppMessage(
                    res.getString(res.getColumnIndex("id")),
                    res.getLong(res.getColumnIndex("dateTime")),
                    res.getString(res.getColumnIndex("msgTitle")),
                    res.getString(res.getColumnIndex("targetId")),
                    res.getString(res.getColumnIndex("senderName")),
                    res.getString(res.getColumnIndex("senderId")),
                    res.getString(res.getColumnIndex("message")),
                    res.getString(res.getColumnIndex("icon")),
                    res.getString(res.getColumnIndex("attachmentUrl")),
                    res.getString(res.getColumnIndex("attachmentType")),
                    res.getInt(res.getColumnIndex("read")) ,
                    res.getString(res.getColumnIndex("quotedTextId"))

            );
            //-- utl.e(DATABASE_NAME,"Chat Latest : "+cm.getMessage());
        }
        res.close();
        return cm;
    }




    public ArrayList<InAppMessage> getAllMessagesForGroup(String targetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "select * from " + TABLE_NAME + "  ORDER BY dateTime ASC";

        if (targetId.length() > 1)
            q = "select * from " + TABLE_NAME + " WHERE targetId='" + targetId + "' AND message NOT LIKE '%"+ C2C_DELETE+"%' ORDER BY dateTime ASC";

        Cursor res = db.rawQuery(q, null);

        return convertCursorToArray(res);
    }

    public ArrayList<InAppMessage> getAllMessagesForGroupAfter(String targetId, Long dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "select * from " + TABLE_NAME + " ORDER BY dateTime ASC";

        if (targetId.length() > 1)
            q = "select * from " + TABLE_NAME + " WHERE targetId='"
                    + targetId + "'AND dateTime > " + dateTime + " AND message NOT LIKE '%"+ C2C_DELETE+"%' ORDER BY dateTime ASC";

        Cursor res = db.rawQuery(q, null);

        return convertCursorToArray(res);
    }


















    public ArrayList<InAppMessage> getLatestMessagesOfAllGroups() {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "select * from " + TABLE_NAME_META + " GROUP BY targetId ORDER BY dateTime DESC";

        Cursor res = db.rawQuery(q, null);

        return convertCursorToArray(res);
    }

    public ArrayList<InAppMessage> getLatestMessagesOfAllGroups(int limit, int offset) {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "select * from " + TABLE_NAME_META +
                " GROUP BY targetId ORDER BY dateTime DESC LIMIT "+limit+" OFFSET "+offset;

        Cursor res = db.rawQuery(q, null);

        return convertCursorToArray(res);
    }






    public boolean makeSeen(String targetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("read", 1);

        db.update(TABLE_NAME, contentValues, "targetId = ?", new String[]{targetId});
        return true;
    }

    public boolean updateData(InAppMessage cm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", cm.getId());

        db.update(TABLE_NAME, contentValues, "id = ?", new String[]{cm.getId()});
        return true;
    }

    public Integer deleteMessageById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_META, "id = ?", new String[]{id});
        return db.delete(TABLE_NAME, "id = ?", new String[]{id});
    }

    public Integer deleteGroup(String targetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_META, "targetId = ?", new String[]{targetId});
        return db.delete(TABLE_NAME, "targetId = ?", new String[]{targetId});
    }

    public Integer deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_META, "1", null);
        return db.delete(TABLE_NAME, "1", null);
    }
}

