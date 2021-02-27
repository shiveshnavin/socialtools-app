package com.dotpot.app.services;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.dotpot.app.App;
import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.interfaces.NetworkRequestCallback;
import com.dotpot.app.interfaces.NetworkService;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.activities.AccountActivity;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.dotpot.app.Constants.API_USERS;

public class LoginService {

    public static int RC_SIGN_UP = 1001;
    public static int RC_SIGN_IN = 1002;
    private BaseActivity ctx;
    private InAppNavService inAppNavService;
    private GoogleSignInClient mGoogleSignInClient;
    private GenricUser tempGenricUser;
    private FirebaseAuth firebaseAuth;
    private NetworkService networkService;
    private GoogleSignInOptions gso;

    public LoginService(BaseActivity ctx) {
        this.ctx = ctx;
        inAppNavService = ctx.inAppNavService;
        firebaseAuth = ctx.mAuth;
        networkService = ctx.netService;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(ctx.mFirebaseRemoteConfig.getString("google_web_client_id"))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(App.getAppContext(), gso);

    }

    public static boolean isValidPhone(String phone) {
        return phone != null && (phone.length() == 10 || phone.length() == 13 && phone.startsWith("+"));
    }

    private GoogleSignInAccount getSignedInAccount() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ctx);
        return account;
    }

    public GenricUser getTempGenricUser() {
        if (tempGenricUser == null) {
            tempGenricUser = utl.readUserData();
        }
        return tempGenricUser;
    }

    public void setTempGenricUser(GenricUser tempGenricUser) {
        this.tempGenricUser = tempGenricUser;
    }

    public void googleLogin(int code) {

        GoogleSignInAccount account = getSignedInAccount();

        if (account != null) {
            utl.logout();
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ctx.startActivityForResult(signInIntent, code);
    }

    public void emailPhoneLogin(String emailPhone,
                                String paswd,
                                GenricObjectCallback<GenricUser> cb) {


        JSONObject jop = new JSONObject();
        try {
            if (emailPhone.isEmpty() || paswd.isEmpty()) {
                cb.onError(ctx.getString(R.string.invalidemailorpaswd));
                return;
            }
            jop.put("password", paswd);

            if (emailPhone.contains("@")) {
                jop.put("email", emailPhone);
            } else if (utl.isValidMobile(emailPhone)) {
                String ccode = ResourceUtils.getString(R.string.ccode);
                if (!emailPhone.contains(ccode))
                    emailPhone = ccode + emailPhone;
                jop.put("phone", emailPhone);
            } else {
                cb.onError(ctx.getString(R.string.invalidemailorpaswd));
                return;
            }

            String fcmToken = utl.getKey("fcm_token",ctx);
            if(fcmToken!=null){
                jop.put("fcmToken",fcmToken);
            }

        } catch (Exception e) {
        }

        networkService.callPost((Constants.u(API_USERS))
                , jop, false, new NetworkRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        GenricUser genricUser = utl.js.fromJson(response.toString(), GenricUser.class);
                        cb.onEntity(genricUser);
                        GenericUserViewModel.getInstance().updateLocalAndNotify(ctx.getApplicationContext(), genricUser);
                    }

                    @Override
                    public void onFail(ANError job) {
                        cb.onError(job.getMessage());
                    }
                });

    }

    public void firebaseId(String providertoken, GenricObjectCallback<GenricUser> cb) {

        utl.setKey(Constants.KEY_PROVIDERTOKEN, providertoken, ctx);
        networkService.updateTokens(providertoken, providertoken);

        JSONObject jop=new JSONObject();
        try{

            String fcmToken = utl.getKey("fcm_token",ctx);
            if(fcmToken!=null){
                jop.put("fcmToken",fcmToken);
            }

        }catch(Exception e)
        {}

        networkService.callPost(Constants.u(API_USERS), jop , false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                GenricUser genricUser = utl.js.fromJson(response.toString(), GenricUser.class);
                cb.onEntity(genricUser);
                GenericUserViewModel.getInstance().updateLocalAndNotify(ctx, genricUser);
            }

            @Override
            public void onFail(ANError job) {
                cb.onError(job.getMessage());
            }
        });


    }

    public void refreshProviderToken(GenricDataCallback cb) {

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            cb.onStart(null, -1);
            return;
        }
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        cb.onStart(idToken, 1);
                    } else {
                        cb.onStart(null, -1);
                    }
                });

    }

    public void getLoggedInUser(GenricObjectCallback<GenricUser> cb) {


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            refreshProviderToken((token, status) -> {
                if (status == 1) {

                    utl.setKey(Constants.KEY_PROVIDERTOKEN, token, ctx);
                    networkService.updateTokens(token, token);
                    firebaseId(token, new GenricObjectCallback<GenricUser>() {
                        @Override
                        public void onEntity(GenricUser data) {
                            GenericUserViewModel.getInstance().updateLocalAndNotify(ctx,data);
                            cb.onEntity(data);
                        }

                        @Override
                        public void onError(String message) {
                            cb.onError(message);
                        }
                    });

                } else {
                    GenricUser nonFirebaseLoginUser = utl.readUserData();
                    if (nonFirebaseLoginUser != null) {
                        emailPhoneLogin(nonFirebaseLoginUser.getEmail(), nonFirebaseLoginUser.getPassword(), new GenricObjectCallback<GenricUser>() {
                            @Override
                            public void onEntity(GenricUser data) {
                                if(data!=null)
                                {
                                    GenericUserViewModel.getInstance().updateLocalAndNotify(ctx,data);
                                    cb.onEntity(data);
                                    utl.writeUserData(data,ctx);
                                }
                                else {
                                    cb.onError("Invalid Credentials ! Please login again .");
                                }
                            }

                            @Override
                            public void onError(String message) {
                                cb.onError("An unexpected error occurred . "+message);
                            }
                        });
                    } else
                        cb.onError("No token found");
                }
            });


        });
    }

    public void verifyPhoneNumberWithCode(AccountActivity act, String verificationId, String code, GenricObjectCallback<Task<AuthResult>> onVerificatoinComplete
    ) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(act, credential, onVerificatoinComplete);
    }

    public void resendVerificationCode(String phoneNumber,
                                       long timeout,
                                       PhoneAuthProvider.ForceResendingToken token
            , PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(timeout, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(ctx)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signInWithPhoneAuthCredential(AccountActivity act, PhoneAuthCredential credential, GenricObjectCallback<Task<AuthResult>> onVerificatoinComplete) {
        FirebaseUser gloginUser = firebaseAuth.getCurrentUser();
        if(gloginUser!=null){
            gloginUser.linkWithCredential(credential).addOnCompleteListener(ctx, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        onVerificatoinComplete.onEntity(task);
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            onVerificatoinComplete.onError(ResourceUtils.getString(R.string.invalidotp));
                        } else {
                            onVerificatoinComplete.onError(task.getException().getMessage());
                        }
                    }
                }
            });
        }
        else
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onVerificatoinComplete.onEntity(task);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                onVerificatoinComplete.onError(ResourceUtils.getString(R.string.invalidotp));
                            } else {
                                onVerificatoinComplete.onError(task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    public void sendOTP(String phoneNumber, long timeout, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeout, TimeUnit.SECONDS)
                        .setActivity(ctx)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void commitTemporaryUserToServer(GenricObjectCallback<GenricUser> cb) {

        String fcmToken = utl.getKey("fcm_token",ctx);
        if(fcmToken!=null){
            getTempGenricUser().setFcmToken(fcmToken);
        }

        networkService.callPost(Constants.u(API_USERS), getTempGenricUser(), false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                GenricUser genricUser = utl.js.fromJson(response.toString(), GenricUser.class);
                cb.onEntity(genricUser);
                GenericUserViewModel.getInstance().updateLocalAndNotify(ctx, genricUser);
            }

            @Override
            public void onFail(ANError job) {
                cb.onError(job.getMessage());
            }
        });
    }

    public void commitPasswordAndPhone(String oldPaswd, String newPaswd,
                                       String phone,
                                       GenricObjectCallback<GenricUser> cb) {

        JSONObject jop = new JSONObject();
        try {


            if (oldPaswd != null && newPaswd != null) {
                jop.put("password", oldPaswd);
                jop.put("newpassword", newPaswd);
            } else if (phone != null) {
                jop.put("newphone", phone);
            } else {
                cb.onError(ctx.getString(R.string.invalidinput));
            }
            String fcmToken = utl.getKey("fcm_token",ctx);
            if(fcmToken!=null){
                jop.put("fcmToken",fcmToken);
            }
        } catch (Exception e) {
        }

        networkService.callPost(Constants.u(API_USERS) + "/" +
                        getTempGenricUser().getId()
                , jop, false, new NetworkRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        GenricUser genricUser = utl.js.fromJson(response.toString(), GenricUser.class);
                        cb.onEntity(genricUser);
                        GenericUserViewModel.getInstance().updateLocalAndNotify(ctx.getApplicationContext(), genricUser);
                    }

                    @Override
                    public void onFail(ANError job) {
                        cb.onError(job.getMessage());
                    }
                });
    }

    public void checkPhoneExists(String phoneNumber, GenricDataCallback genricDataCallback) {


        networkService.callGet(Constants.u(Constants.API_CHECK_PHONE) + "?phone=" + URLEncoder.encode(phoneNumber)
                , false, new NetworkRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        if (response.optBoolean("success")) {
                            genricDataCallback.onStart(response.optString("message"), 1);
                        } else {
                            genricDataCallback.onStart(response.optString("message"), -1);

                        }

                    }

                    @Override
                    public void onFail(ANError job) {
                        genricDataCallback.onStart(null, -1);
                    }
                });


    }

    public void sendPasswordResetMail(String emailPhone,GenricDataCallback cb) {

        String suffix = "";
        if (emailPhone.contains("@")) {
            suffix = "email="+URLEncoder.encode(emailPhone);
        } else if (utl.isValidMobile(emailPhone)) {
            String ccode = ResourceUtils.getString(R.string.ccode);
            if (!emailPhone.contains(ccode))
                emailPhone = ccode + emailPhone;
            suffix = "phone="+URLEncoder.encode(emailPhone);
        }
        networkService.callGet(Constants.u(Constants.API_RESET_PASSWORD) + "?"+suffix
                , false, new NetworkRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        if (response.optBoolean("success")) {
                            cb.onStart(response.optString("message"), 1);
                        } else {
                            cb.onStart(response.optString("message"), -1);

                        }

                    }

                    @Override
                    public void onFail(ANError job) {
                        cb.onStart(null, -1);
                    }
                });


    }
}
