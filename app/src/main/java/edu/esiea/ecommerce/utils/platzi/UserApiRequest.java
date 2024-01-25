package edu.esiea.ecommerce.utils.platzi;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.esiea.ecommerce.models.User;

public class UserApiRequest {

    private static final String URL_AUTH = "https://api.escuelajs.co/api/v1/auth";
    private static final String URL_USERS = "https://api.escuelajs.co/api/v1/users";

    private static final String PREF_NAME = "user_session";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final RequestQueue requestQueue;
    private final Context context;

    public UserApiRequest(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
    }

    public void putUser(Integer userId, JSONObject jsonObject, UserApiRequest.UserApiCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                "https://api.escuelajs.co/api/v1/users/" + userId,
                jsonObject,
                response -> {
                    Log.i(TAG, "User Modifié");
                    User user = parseUser(jsonObject);
                    callback.onSuccess(user);
                },
                error -> {
                    if (error.getMessage() != null) {
                        Log.e(TAG, error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    public interface UserApiCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface EmailCheckCallback{
        void onSuccess(boolean available);
        void onError(String errorMessage);
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public void loginUser(String email, String password, final AuthCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Error creating login request");
            return;
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_AUTH + "/login",
                requestBody,
                response -> {
                    try {
                        Log.i("USER_API_REQUEST", "success!!");

                        String accessToken = response.getString("access_token");
                        String refreshToken = response.getString("refresh_token");
                        saveTokens(accessToken, refreshToken);

                        Log.i("USER_API_REQUEST", "success: " + accessToken + " " + refreshToken);

                        callback.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Error parsing login response");
                    }
                },
                error -> {
                    callback.onError("Login failed: " + error.toString());
                }
        );

        requestQueue.add(loginRequest);
    }

    public String getAccessToken() {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(ACCESS_TOKEN_KEY, null);
    }

    private void saveTokens(String accessToken, String refreshToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.putString(REFRESH_TOKEN_KEY, refreshToken);
        editor.apply();
    }

    public void createUser(final String name, final String email, final String password, final String avatar, final UserApiCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", name);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("avatar", avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_USERS, requestBody,
                response -> {
                    User newUser = parseUser(response);
                    if (callback != null) {
                        callback.onSuccess(newUser);
                    }
                },
                error -> {
                    if (callback != null) {
                        callback.onError("Failed to create user");
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void getUserInfo(final UserInfoCallback callback) {
        String url = "https://api.escuelajs.co/api/v1/auth/profile";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Parse user information from the response
                        int id = response.getInt("id");
                        String email = response.getString("email");
                        String name = response.getString("name");
                        String role = response.getString("role");
                        String avatar = response.getString("avatar");

                        // Callback to handle user information
                        if (callback != null) {
                            callback.onSuccess(id, email, name, role, avatar);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onError("Error parsing JSON response");
                        }
                    }
                },
                error -> {
                    Log.e("Volley", "Error during getUserInfo request", error);
                    if (callback != null) {
                        callback.onError("Error during getUserInfo request");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getAccessToken());
                return headers;
            }
        };

        // Add the request to the queue
        requestQueue.add(request);
    }

    // Callback interface to handle user info responses
    public interface UserInfoCallback {
        void onSuccess(int id, String email, String name, String role, String avatar);
        void onError(String errorMessage);
    }

    public void isEmailAvailable(final String email, final EmailCheckCallback callback) {
        String url = URL_USERS + "/is-available";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        boolean isAvailable = response.getBoolean("isAvailable");
                        if (callback != null) {
                            callback.onSuccess(isAvailable);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onError("Failed to parse response");
                        }
                    }
                },
                error -> {
                    if (callback != null) {
                        callback.onError("Failed to check email availability");
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private User parseUser(JSONObject response) {
        return new Gson().fromJson(response.toString(), User.class);
    }
}
