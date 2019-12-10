package com.example.jsonclientlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements AsyncNetConn.SendTaskListener{

    private Integer tabnum = 0;
    private Button submitButton;
    private TextView errorMessageView;
    private String sEmail = "";
    private String sPassword = "";
    private ProgressDialog spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TabLayout tl = ((TabLayout)findViewById(R.id.tabLayoutBottom));
        submitButton = ((Button)findViewById((R.id.buttonLogin)));
        errorMessageView = ((TextView)findViewById((R.id.textViewMessage)));

        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    tabnum = 0; submitButton.setText(getResources().getString(R.string.button_login));
                } else {
                    tabnum = 1; submitButton.setText(getResources().getString(R.string.button_create));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        spinner = new ProgressDialog(this);
        spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //spinner.setTitle("");
        //spinner.setMessage("");
        spinner.setIndeterminate(true);
        spinner.setCanceledOnTouchOutside(false);
    }

    public void clickButton(View view) {
        String jsonToSend = null;
        sEmail = ((EditText)findViewById((R.id.editTextEmail))).getText().toString();
        sPassword = ((EditText)findViewById((R.id.editTextPassword))).getText().toString();

        if(areUsernameAndPasswordValid()) {
            if(tabnum == 0) {
                jsonToSend = JsonRequests.loginAccount(sEmail, CryptoFuncs.getSHA256Hash(sPassword));
            } else {
                jsonToSend = JsonRequests.createAccount(sEmail, CryptoFuncs.getSHA256Hash(sPassword));
            }

            spinner.show();

            AsyncNetConn anc = new AsyncNetConn();
            anc.mListener = this;
            anc.execute(jsonToSend);
        }
    }

    @Override
    public void onSendFinish(String s) {
        try {
            JSONObject reader = new JSONObject(s);
            String action = reader.getString("action");
            if (action.equals("chklogin")) {
                loginchkResponse(reader.getString("result"));
            } else if (action.equals("newaccnt")) {
                newaccntResponse(reader.getString("result"));
            } else {
                errorMessageView.setText(getResources().getString(R.string.message_error));
                spinner.dismiss();
            }
        } catch (JSONException e) {
            errorMessageView.setText(getResources().getString(R.string.message_error));
            spinner.dismiss();
        }
    }

    private boolean isEmailValid(CharSequence email) {
        // check if email is valid email format
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(CharSequence password) {
        // code to check password meets your requirements
        return true;
    }

    private boolean areUsernameAndPasswordValid() {
        return (isEmailValid(sEmail) && isPasswordValid(sPassword));
    }

    private void loginchkResponse(String result) {
        if (result.equals("success")) {
            // login success
            saveCreds();
            spinner.dismiss();
            goMainActivity();
        } else if (result.equals("invalid")) {
            // password bad but email exists
            errorMessageView.setText(getResources().getString(R.string.message_password));
            spinner.dismiss();
            // send reset password?
        } else if (result.equals("noexists")) {
            // cant find account by email
            errorMessageView.setText(getResources().getString(R.string.message_noexists));
            spinner.dismiss();
        }
    }

    private void newaccntResponse(String result) {
        if(result.equals("success")) {
            // account creation success
            saveCreds();
            spinner.dismiss();
            goMainActivity();
        } else if (result.equals("exists")) {
            // error creating account
            errorMessageView.setText(getResources().getString(R.string.message_exists));
            spinner.dismiss();
        }
    }

    private void saveCreds() {
        SharedPreferences sharedprefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefseditor = sharedprefs.edit();
        prefseditor.putString("email", sEmail);
        prefseditor.putString("password", sPassword);
        prefseditor.apply();
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
