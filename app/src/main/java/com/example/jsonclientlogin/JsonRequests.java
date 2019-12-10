package com.example.jsonclientlogin;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonRequests {

    // functions that assemble a json string for sending to server

    public static String createAccount(String sEmail, String sLoginHash) {
        String jsonResult = null;
        try {
            JSONObject postData = new JSONObject();
            postData.put("action", "newaccnt");
            postData.put("email", sEmail);
            postData.put("loginHash", sLoginHash);
            jsonResult = postData.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static String loginAccount(String sEmail, String sLoginHash) {
        String jsonResult = null;
        try {
            JSONObject postData = new JSONObject();
            postData.put("action", "chklogin");
            postData.put("email", sEmail);
            postData.put("loginHash", sLoginHash);
            jsonResult = postData.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    // add other requests here for use in main application
}
