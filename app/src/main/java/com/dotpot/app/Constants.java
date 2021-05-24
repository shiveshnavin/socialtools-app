package com.dotpot.app;

/**
 * Created by shivesh on 28/6/17.
 */

public class Constants {


    public static String HOST="https://dotpot.herokuapp.com";
    public static String u(String endpoint){
        return HOST+endpoint;
    }



    public static String API_USERS = "/api/users";
    public static String API_CHECK_PHONE = "/api/users/checkphone";
    public static String API_RESET_PASSWORD = "/api/users/resetpassword";
    public static String API_REDEEM_REFERRAL = "/api/users/refer";


    public static final String API_GET_PAY_AMOUNTS = "/api/transactions/amounts";
    public static final String API_CREATE_TXN = "/pay/api/createTxn";
    public static final String API_CHECK_TXN = "/pay/api/status";
    public static final String API_WALLET = "/api/users/${userId}/wallet";
    public static String API_WALLET(String userId) {
        return u(API_WALLET.replace("${userId}",userId));
    }
    public static final String API_TRANSACTIONS = "/api/transactions/user/${userId}?debitOrCredit=${txnType}";
    public static String API_TRANSACTIONS(String userId,String txnType) {
        return u(API_TRANSACTIONS.replace("${userId}",userId).replace("${txnType}",txnType == null ?"":txnType));
    }
    public static final String API_GET_USER_WITHDRAW = "/api/users/${userId}/withdraw";
    public static String API_GET_USER_WITHDRAW(String userId) {
        return u(API_GET_USER_WITHDRAW.replace("${userId}",userId));
    }
    public static final String API_GET_GAME_AMOUNTS = "/api/games/amounts";
    public static final String API_CREATE_GAME = "/api/games/create";
    public static String API_GET_USER_GAMES = "/api/games/user/";
    public static String API_GET_USER_GAMES(String userId) {
        return API_GET_USER_GAMES+userId;
    }
    public static final String API_FINISH_GAME = "/api/games/finish";

    public static String API_GET_PRODUCTS = "/api/products/all";
    public static String API_GET_USER_PRODUCTS = "/api/products/user/";
    public static String API_GET_USER_PRODUCTS(String userId) {
        return API_GET_USER_PRODUCTS+userId;
    }
    public static String API_BUY_PRODUCT = "/api/products/buy/";
    public static String API_BUY_PRODUCT(String productId) {
        return Constants.u(API_BUY_PRODUCT+productId);
    }

    public static String API_GET_LEADERBOARD= "/api/analytics/weeklyLeaderboard";


    public static final String API_UPLOAD_IMAGE = "/api/image";



    public static String[] userCategories = {"user", "moderator", "admin"};
    public static String[] userStatuses = {"ACTIVATED_USER","VERIFIED_USER","VERIFIED_MODERATOR","ACTIVATED_ADMIN"};
    public static String O2O="o2o_chat___";
    public static String C2C_DELETE=O2O+"_DELETE_";
    public static String C2C_EXIT=O2O+"_EXITED_";
    public static String V2V_VERIFIED=O2O+"_VERIFIED_";

    public static final String ACTION_HOME = "home";
    public static final String ACTION_LOGIN = "ACTION_LOGIN";
    public static final String ACTION_SIGNUP = "ACTION_SIGNUP";
    public static final String ACTION_ACCOUNT = "ACTION_ACCOUNT";
    public static final String ACTION_CHANGE_PASSWORD = "ACTION_CHANGE_PASSWORD";
    public static final String ACTION_VERIFY_PHONE = "ACTION_VERIFY_PHONE";
    public static final String ACTION_ADD_CREDITS = "ACTION_ADD_CREDITS";
    public static final String ACTION_HOW_TO_PLAY = "ACTION_HOW_TO_PLAY";
    public static final String ACTION_REDEEM_OPTIONS = "ACTION_REDEEM_OPTIONS";
    public static final String ACTION_EARN_MONEY = "ACTION_EARN_MONEY";
    public static final String ACTION_PLAY_GAME = "ACTION_PLAY_GAME";
    public static final String ACTION_WITHDRAW = "ACTION_WITHDRAW";
    public static final String ACTION_SHOP = "ACTION_SHOP";
    public static final String ACTION_TOC = "ACTION_TOC";

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
    public static final String TXN_TYPE_CREDIT = "wallet_credit";
    public static final String TXN_TYPE_DEBIT = "wallet_debit";


    public static final String KEY_PROVIDERTOKEN = "providertoken";

}
