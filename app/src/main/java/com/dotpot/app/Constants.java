package com.dotpot.app;

/**
 * Created by shivesh on 28/6/17.
 */

public class Constants {


    public static final String KEY_PROVIDERTOKEN = "providertoken";


    public static String HOST="https://dotpot.herokuapp.com";
    public static String u(String endpoint){
        return HOST+endpoint;
    }



    public static String API_USERS = "/api/users";
    public static String API_CHECK_PHONE = "/api/users/checkphone";
    public static String API_RESET_PASSWORD = "/api/users/resetpassword";



    public static final String API_CREATE_TXN = "/pay/api/createTxn";
    public static final String API_CHECK_TXN = "/pay/api/status";
    public static String API_WALLET(String userId) {
        return u("/api/users/"+userId+"/wallet");
    }
    public static String API_TRANSACTIONS(String userId,String txnType) {
        return u("/api/transactions/user/"+userId+"?debitOrCredit="+txnType);
    }


    public static final String API_UPLOAD_IMAGE = "/api/image";



    public static String[] userCategories = {"user", "moderator", "admin"};
    public static String[] attachmentTypes = {"Message", "Image","Delete","Exited"};
    public static String[] userStatuses = {"ACTIVATED_USER","VERIFIED_STUDENT","VERIFIED_DOCTOR","ACTIVATED_ADMIN"};
    public static String O2O="o2o_chat___";
    public static String C2C_DELETE=O2O+"_DELETE_";
    public static String C2C_EXIT=O2O+"_EXITED_";
    public static String V2V_VERIFIED=O2O+"_VERIFIED_";

    public static String ACTION_HOME = "home";
    public static String ACTION_LOGIN = "ACTION_LOGIN";
    public static String ACTION_SIGNUP = "ACTION_SIGNUP";
    public static String ACTION_ACCOUNT = "ACTION_ACCOUNT";
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

    public static final String TXN_SUCCESS = "TXN_SUCCESS";
    public static final String TXN_FAILURE = "TXN_FAILURE";


}
