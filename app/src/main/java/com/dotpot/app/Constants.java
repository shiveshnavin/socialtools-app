package com.dotpot.app;

/**
 * Created by shivesh on 28/6/17.
 */

public class Constants {



    public static String u(String endpoint){
        return HOST+endpoint;
    }

    public static String HOST="https://host.com";

    public static final String API_UPLOAD_IMAGE = "/api/image";



    public static String[] userCategories = {"User", "Moderator", "Admin"};
    public static String[] attachmentTypes = {"Message", "Image","Delete","Exited"};
    public static String[] userStatuses = {"ACTIVATED_USER","VERIFIED_STUDENT","VERIFIED_DOCTOR","ACTIVATED_ADMIN"};
    public static String O2O="o2o_chat___";
    public static String C2C_DELETE=O2O+"_DELETE_";
    public static String C2C_EXIT=O2O+"_EXITED_";
    public static String V2V_VERIFIED=O2O+"_VERIFIED_";

    public final static String ACTION_HOME = "home";
    public static String ACTION_LOGIN = "ACTION_LOGIN";
    public static String ACTION_SIGNUP = "ACTION_SIGNUP";
    public static String ACTION_CHANGE_PASSWORD = "ACTION_CHANGE_PASSWORD";
    public static String ACTION_VERIFY_PHONE = "ACTION_VERIFY_PHONE";

    public final static String ATTACHMENT_TYPE_IMAGE = "Image";
    public final static String ATTACHMENT_TYPE_DOC = "Doc";
    public final static String ATTACHMENT_TYPE_DEFAULT = "Text";
    public final static String ATTACHMENT_TYPE_LOCATION = "Location";
    public final static String ATTACHMENT_TYPE_FILE = "File";
    public final static String ATTACHMENT_TYPE_ENTITY_DISEASE = "Text";

    public static int TRANSITION_VERTICAL = 1;
    public static int TRANSITION_HORIZONTAL = -1;
    public static int TRANSITION_NONE = 0;


}
