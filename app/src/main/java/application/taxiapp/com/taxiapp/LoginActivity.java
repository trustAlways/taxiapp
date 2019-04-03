package application.taxiapp.com.taxiapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import application.taxiapp.com.taxiapp.ProgressDialog.ProgressBarClass;
import application.taxiapp.com.taxiapp.constant.MySharedPref;
import application.taxiapp.com.taxiapp.other.URls;
import application.taxiapp.com.taxiapp.other.Utils;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    Button next;
    AutoCompleteTextView email;
    EditText editText_pass;
    TextInputLayout txt_input_layout1, txt_input_layout2;
    CheckBox checkBox;
    Boolean iserror;
    TextView txt_creat_accoint;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization();

        click();

    }
    //----------------------
    private void initialization()
    {
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        progressBar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        txt_input_layout1 = (TextInputLayout)findViewById(R.id.email_input);
        txt_input_layout2 = (TextInputLayout)findViewById(R.id.password_input);

        img_back = (ImageView)findViewById(R.id.sign_in_back);
        //
        email = (AutoCompleteTextView)findViewById(R.id.email);
        editText_pass = (EditText)findViewById(R.id.password);

        //tetxtview
        txt_creat_accoint = (TextView)findViewById(R.id.txt_creat_accoint);

        next = (Button)findViewById(R.id.log_in_button);

        progressDialog = ProgressBarClass.createProgressDialog(this);
        progressDialog.dismiss();
    }
    //---------------------
    // ------
    private void click() {

        txt_creat_accoint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
                progressBar.setVisibility(View.GONE);
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();
                validation();
            }
        });

        img_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(LoginActivity.this,HomePage.class));
            }
        });

    }

    private void validation()
    {
        String mail = email.getText().toString().trim();
        String pass = editText_pass.getText().toString().trim();

        iserror = false;

        if (mail.equalsIgnoreCase("") && mail.isEmpty())
        {
            iserror = true;
            progressBar.setVisibility(View.GONE);
            txt_input_layout1.setError("Invalid email id");
        }
        if (!mail.equalsIgnoreCase("") && !mail.isEmpty())
        {
            iserror = false;
            txt_input_layout1.setError(null);
        }

        if (pass.equalsIgnoreCase("") && pass.isEmpty())
        {
            iserror = true;
            txt_input_layout2.setError("Choose password");
            progressBar.setVisibility(View.GONE);
        }
        if (!pass.equalsIgnoreCase("") && !pass.isEmpty())
        {
            iserror = false;
            txt_input_layout2.setError(null);
        }

        if (iserror == false)
        {
            if (Utils.isConnected(this))
            {
                Log_in(mail,pass);
            }
            else
            {
                Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }


    }

    //------------------------
    private void Log_in(final String email, final String pass) {

        //startActivity(new Intent(LoginActivity.this,MapsActivity.class));
        //progressBar.setVisibility(View.VISIBLE);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URls.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("LogIn", "Log In response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                   // String status = jObj.getString("status");
                    //String errorMsg = jObj.getString("message");
                    JSONObject success=jObj.getJSONObject("success");
                    String token = success.getString("token");

                    if (!token.equalsIgnoreCase("")) {

                        JSONObject result=success.getJSONObject("result");

                        System.out.println("Result Login***"+result+"------ " + token);

                        Toast.makeText(LoginActivity.this, "Login Successfully.",Toast.LENGTH_SHORT).show();

                        MySharedPref sp=new MySharedPref(LoginActivity.this);
                        sp.setData("ldata",result+"");

                        Intent ii=new Intent(LoginActivity.this,MapsActivity.class);
                        startActivity(ii);

                        progressBar.setVisibility(View.GONE);
                        progressDialog.hide();

                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        progressDialog.hide();

                        Toast.makeText(LoginActivity.this, "Failure",Toast.LENGTH_SHORT).show();
                        //   Toast.makeText(LoginActivity.this,"You are deactivated by Admin.",Toast.LENGTH_SHORT).show();

                        Log.e("errorMsg", "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                progressDialog.hide();
                Log.e("LogIn Response", "LogIn  Error: " + error.getMessage());
                try {
                    Toast.makeText(LoginActivity.this, "Something went wrong!",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {


            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-API-KEY","TEST@123");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile_number",email);
                params.put("password",pass);
                return params;
            }
        };
        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
///-----------------------------------------------------------------------


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(LoginActivity.this,HomePage.class));
    }

}

