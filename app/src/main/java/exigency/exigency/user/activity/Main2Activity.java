package exigency.exigency.user.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import exigency.exigency.user.R;
import exigency.exigency.user.app.AppConfig;
import exigency.exigency.user.app.AppController;
import exigency.exigency.user.helper.SQLiteHandler;
import exigency.exigency.user.helper.SessionManager;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText iage;
    private EditText igender,icontact1,icontact2;
    private EditText icontact3;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        iage = (EditText) findViewById(R.id.age);
        igender = (EditText) findViewById(R.id.gender);
        icontact1 = (EditText) findViewById(R.id.contact1);
        icontact2 = (EditText) findViewById(R.id.contact2);
        icontact3 = (EditText) findViewById(R.id.contact3);
    btnRegister = (Button) findViewById(R.id.btnRegister);
    btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

    // Progress dialog
    pDialog = new ProgressDialog(this);
    pDialog.setCancelable(false);

    // Session manager
    session = new SessionManager(getApplicationContext());

    // SQLite database handler
    db = new SQLiteHandler(getApplicationContext());

    // Check if user is already logged in or not
    if (session.isLoggedIn()) {
        // User is already logged in. Take him to main activity
        Intent intent = new Intent(Main2Activity.this,
                MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Register Button Click event
    btnRegister.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
            String age = iage.getText().toString().trim();
            String gender = igender.getText().toString().trim();
            String contact1 = icontact1.getText().toString().trim();
            String contact2 = icontact2.getText().toString().trim();
            String contact3 = icontact3.getText().toString().trim();
            Bundle bundle = getIntent().getExtras();
            String name = bundle.getString("name");
            String email = bundle.getString("email");
            String password = bundle.getString("password");
            String phonenumber = bundle.getString("phonenumber");
            String bloodgroup = bundle.getString("bloodgroup");
            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()&& !phonenumber.isEmpty()&& !bloodgroup.isEmpty()
                    && !age.isEmpty()&& !gender.isEmpty()&& !contact1.isEmpty()&& !contact2.isEmpty()
                    && !contact3.isEmpty()) {
                registerUser(name, email, password,phonenumber,bloodgroup,age,gender,contact1,contact2,contact3);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please enter your details!", Toast.LENGTH_LONG)
                        .show();
            }
        }
    });

    // Link to Login Screen
    btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

        public void onClick(View view) {
            Intent i = new Intent(getApplicationContext(),
                    LoginActivity.class);
            startActivity(i);
            finish();
        }
    });

}

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email,
                              final String password,final String phonenumber,final String bloodgroup,final String age,
                              final String gender,final String contact1,final String contact2,final String contact3) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");
                        String phonenumber = user.getString("phonenumber");
                        String bloodgroup = user.getString("bloodgroup");
                        String age = user.getString("age");
                        String gender = user.getString("gender");
                        String contact1 = user.getString("contact1");
                        String contact2 = user.getString("contact2");
                        String contact3 = user.getString("contact3");
                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at,phonenumber,bloodgroup,age,gender,contact1,contact2,contact3);
// Create object of SharedPreferences.
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this);
                        //now get Editor
                        SharedPreferences.Editor editor = sharedPref.edit();
                        //put your value
                        editor.putString("contact1",contact1);
                        editor.putString("contact2",contact2);
                        editor.putString("contact3",contact3);
                        editor.putString("uid",uid);
                        //commits your edits
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                        // Launch login activity
                        Intent i = new Intent(
                                Main2Activity.this,
                                LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("phonenumber", phonenumber);
                params.put("bloodgroup", bloodgroup);
                params.put("age", age);
                params.put("gender", gender);
                params.put("contact1", contact1);
                params.put("contact2", contact2);
                params.put("contact3", contact3);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}


