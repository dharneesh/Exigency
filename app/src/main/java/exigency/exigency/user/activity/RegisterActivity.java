/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package exigency.exigency.user.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import exigency.exigency.user.R;
import exigency.exigency.user.helper.SQLiteHandler;
import exigency.exigency.user.helper.SessionManager;

public class RegisterActivity extends Main2Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnNextpage;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail,Bloodgroup,Phone;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnNextpage = (Button) findViewById(R.id.btnNext);
        Phone = (EditText) findViewById(R.id.phonenumber);
        Bloodgroup = (EditText) findViewById(R.id.bloodgroup);
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
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnNextpage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String phonenumber = Phone.getText().toString().trim();
                String bloodgroup = Bloodgroup.getText().toString().trim();
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()&& !phonenumber.isEmpty()&& !bloodgroup.isEmpty()) {
                    Intent intent = new Intent(view.getContext(),Main2Activity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("email",email);
                    intent.putExtra("password",password);
                    intent.putExtra("phonenumber",phonenumber);
                    intent.putExtra("bloodgroup",bloodgroup);
                    startActivity(intent);
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



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
