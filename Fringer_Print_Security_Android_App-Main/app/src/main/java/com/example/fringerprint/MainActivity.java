package com.example.fringerprint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Button btn_fp,btn_fppin;
    TextView Create,Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btn_fp = findViewById(R.id.btn_fp);
        btn_fppin = findViewById(R.id.btn_fppin);

        CheckBioSoppert();

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this,"Auth error : "+errString,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this,"Auth succeeded ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this,"Auth failed",Toast.LENGTH_SHORT).show();
            }
        });

        btn_fp.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo.Builder promptInt = dialogMetric();
            promptInt.setNegativeButtonText("Cancel");
            biometricPrompt.authenticate(promptInt.build());
        });

        btn_fppin.setOnClickListener(view ->{
            BiometricPrompt.PromptInfo.Builder promptInt = dialogMetric();
            promptInt.setDeviceCredentialAllowed(true);
            biometricPrompt.authenticate(promptInt.build());
        });
    }

    BiometricPrompt.PromptInfo.Builder dialogMetric(){
        return new BiometricPrompt.PromptInfo.Builder().setTitle("Biometric login").setSubtitle("Login using your biometric credential");
    }

    private void CheckBioSoppert() {

        String info;

        BiometricManager Manager = BiometricManager.from(this);

        switch (Manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.BIOMETRIC_STRONG)){

            case BiometricManager.BIOMETRIC_SUCCESS:

                info = "App can authenticate Using Biometrics";
                enableButton(true);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "No biometric features available on";
                enableButton(false);
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Biometric features are currently unavailable";
                enableButton(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                info = "Need register at least one fingerprint";
                enableButton(false,true);
                break;
            default:
                info = "UnKnown case";
                break;
        }

        TextView tx_info = findViewById(R.id.tx_info);
        tx_info.setText(info);
    }

    void enableButton(boolean enable){
        btn_fp.setEnabled(enable);
        btn_fppin.setEnabled(true);
    }

    void enableButton(boolean enable , boolean enroll){
        enableButton(enable);
        if (!enable) return;

        Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                    | BiometricManager.Authenticators.BIOMETRIC_WEAK);

        startActivity(enrollIntent);
    }
}