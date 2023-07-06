package com.sworks.otpverify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.truecaller.android.sdk.common.TrueException;
import com.truecaller.android.sdk.common.VerificationCallback;
import com.truecaller.android.sdk.common.VerificationDataBundle;
import com.truecaller.android.sdk.common.models.TrueProfile;
import com.truecaller.android.sdk.oAuth.CodeVerifierUtil;
import com.truecaller.android.sdk.oAuth.TcOAuthCallback;
import com.truecaller.android.sdk.oAuth.TcOAuthData;
import com.truecaller.android.sdk.oAuth.TcOAuthError;
import com.truecaller.android.sdk.oAuth.TcSdk;
import com.truecaller.android.sdk.oAuth.TcSdkOptions;

import java.math.BigInteger;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private Button getstart, getOtp;
    private EditText phone;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getstart = findViewById(R.id.start);
        phone = findViewById(R.id.txtEPhone);
        getOtp = findViewById(R.id.OTPButton);

//        initTrueCallerSdkOptions();
//        verify();

        //Scenarios https://docs.truecaller.com/truecaller-sdk/android/user-flows-for-verification-truecaller-+-non-truecaller-users
        getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
//                initTrueCallerSdkOptions();
//                verify();
            }
        });

        TcSdkOptions tcSdkOptions = new TcSdkOptions.Builder(this, tcOAuthCallback)
                .sdkOptions(TcSdkOptions.OPTION_VERIFY_ALL_USERS)
                .build();
        TcSdk.init(tcSdkOptions);

    }

        VerificationCallback verificationCallback = new VerificationCallback() {
            @Override
            public void onRequestSuccess(int calltype, @Nullable VerificationDataBundle verificationDataBundle) {
                switch(calltype){
                    case VerificationCallback.TYPE_OTP_INITIATED:
                        // Missed call initiated
                        if (verificationDataBundle != null) {
                            String ttl = verificationDataBundle.getString(VerificationDataBundle.KEY_TTL);
                            String requestNonce = verificationDataBundle.getString(VerificationDataBundle.KEY_REQUEST_NONCE);
                            // Handle ttl and requestNonce
                        }
                        break;

                    case VerificationCallback.TYPE_OTP_RECEIVED:
                        // Missed call received
//                        TrueProfile profile = new TrueProfile.Builder("Roop Sagar", "Teella").build();
//                        TcSdk.getInstance().verifyOtp(profile, verificationCallback);
                        break;
                    case VerificationCallback.TYPE_VERIFICATION_COMPLETE:
                        // Verification complete
                        break;
                    case VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE:
                        // User already verified
                        break;
                }
            }

            @Override
            public void onRequestFailure(int i, @NonNull TrueException e) {
                Toast.makeText(MainActivity.this, "Non_Failure", Toast.LENGTH_SHORT).show();
            }
        };

    TcOAuthCallback tcOAuthCallback = new TcOAuthCallback() {
        @Override
        public void onSuccess(@NonNull TcOAuthData tcOAuthData) {
            Toast.makeText(MainActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
            String stateRequested = new BigInteger(130, new Random()).toString(32);
            TcSdk.getInstance().setOAuthState(stateRequested);
        }
        @Override
        public void onFailure(@NonNull TcOAuthError tcOAuthError) {
            Log.d("loginFailure", tcOAuthError.toString());
        }

        @Override
        public void onVerificationRequired(@Nullable TcOAuthError tcOAuthError) {
            Log.d("current status" , "verification call initiated");
            try {
                TcSdk.getInstance().requestVerification("IN", "6301237943", verificationCallback , MainActivity.this);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "error" + e, Toast.LENGTH_SHORT).show();
            }
        }
    };


    public void verify() {
        if (TcSdk.getInstance().isOAuthFlowUsable()) {
            //This is to set language refer here for : https://docs.truecaller.com/truecaller-sdk/android/integrating-with-your-app/customisation-1
            Locale locale = new Locale("en");
            TcSdk.getInstance().setLocale(locale);

            String codeVerifier = CodeVerifierUtil.Companion.generateRandomCodeVerifier();
            String codeChallenge = CodeVerifierUtil.Companion.getCodeChallenge(codeVerifier);

            if (codeChallenge != null) {
                TcSdk.getInstance().setCodeChallenge(codeChallenge);
            }
            TcSdk.getInstance().getAuthorizationCode(this);
            //For reference https://docs.truecaller.com/truecaller-sdk/android-oauth-sdk-early-access/integration-steps/setting-up-oauth-parameters
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TcSdk.SHARE_PROFILE_REQUEST_CODE) {
            TcSdk.getInstance().onActivityResultObtained(this, requestCode, resultCode, data);
        }
    }
}