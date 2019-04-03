package application.taxiapp.com.taxiapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import application.taxiapp.com.taxiapp.ProgressDialog.ProgressBarClass;
import application.taxiapp.com.taxiapp.constant.MySharedPref;
import application.taxiapp.com.taxiapp.other.URls;
import application.taxiapp.com.taxiapp.other.Utils;

public class SignupActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ProgressDialog progressDialog;

    CountryCodePicker ccp;
    Button next;
    AutoCompleteTextView email;
    EditText editText_fname,editText_lname,editText_gender,editText_age,editText_mobile,editText_pass,editText_c_pass,
            editText_city,editText_state,editText_country,editText_reffreal;
    TextInputLayout txt_input_layour1, txt_input_layour2, txt_input_layour3,text_input_gender,text_input_age, txt_input_layour4,
            txt_input_layour5, txt_input_layour6,text_input_city,text_input_state,text_input_country,txt_input_layour7;
    CheckBox checkBox;
    ImageView img_back;
    Boolean iserror;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initialization();
        click();

    }


    //----------------------
    private void initialization()
    {
        progressBar = (ProgressBar) findViewById(R.id.register_progress);
        ccp = (CountryCodePicker)findViewById(R.id.ccp);

        img_back = (ImageView)findViewById(R.id.sign_up_back);

        txt_input_layour1 = (TextInputLayout)findViewById(R.id.email_input);
        txt_input_layour2 = (TextInputLayout)findViewById(R.id.fname_input);
        txt_input_layour3 = (TextInputLayout)findViewById(R.id.lname_input);
        text_input_gender = (TextInputLayout)findViewById(R.id.gender_input);
        text_input_age = (TextInputLayout)findViewById(R.id.age_input);

        txt_input_layour4 = (TextInputLayout)findViewById(R.id.mobile_input);
        txt_input_layour5 = (TextInputLayout)findViewById(R.id.password_input);
        txt_input_layour6 = (TextInputLayout)findViewById(R.id.confirm_password_input);
        text_input_city = (TextInputLayout)findViewById(R.id.city_input);
        text_input_state = (TextInputLayout)findViewById(R.id.state_input);
        text_input_country = (TextInputLayout)findViewById(R.id.country_input);
        txt_input_layour7 = (TextInputLayout)findViewById(R.id.referal_code_input);

        //
        email = (AutoCompleteTextView)findViewById(R.id.email);
        editText_fname = (EditText)findViewById(R.id.fname);
        editText_lname = (EditText)findViewById(R.id.lname);
        editText_gender = (EditText)findViewById(R.id.gender);
        editText_age = (EditText)findViewById(R.id.age);
        editText_mobile = (EditText)findViewById(R.id.mobile);

        // ccp.setNumberAutoFormattingEnabled(true);
        //ccp.registerCarrierNumberEditText(editText_mobile);

        editText_pass = (EditText)findViewById(R.id.password);
        editText_c_pass = (EditText)findViewById(R.id.confirm_password);
        editText_city = (EditText)findViewById(R.id.city);
        editText_state = (EditText)findViewById(R.id.state);
        editText_country = (EditText)findViewById(R.id.country);
        editText_reffreal = (EditText)findViewById(R.id.referal_code);



        next = (Button)findViewById(R.id.log_up_button);

        progressDialog = ProgressBarClass.createProgressDialog(this);
        progressDialog.dismiss();
    }
    //---------------------
    // ------
    private void click() {

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Toast.makeText(getApplicationContext(), ""+ ccp.getSelectedCountryName() + "" + ccp.getSelectedCountryCodeWithPlus(), Toast.LENGTH_SHORT).show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             // progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();
                validation();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    //-------------------------------------------------
    private void validation()
    {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String mail = email.getText().toString().trim();
        String fname = editText_fname.getText().toString().trim();
        String lname = editText_lname.getText().toString().trim();
        String gender = editText_gender.getText().toString().trim();
        String age =   editText_age.getText().toString().trim();
        String mobile = editText_mobile.getText().toString().trim();
        String pass = editText_pass.getText().toString().trim();
        String c_pass = editText_c_pass.getText().toString().trim();
        String city = editText_city.getText().toString().trim();
        String state = editText_state.getText().toString().trim();
        String country = editText_country.getText().toString().trim();

        String refrer = editText_reffreal.getText().toString().trim();

        iserror = false;

        if (mail.equalsIgnoreCase("") && mail.isEmpty())
        {
            iserror = true;
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            txt_input_layour1.setError("Enter your email id");
        }
        else if (!mail.matches(emailPattern))
        {
            iserror = true;
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            txt_input_layour1.setError("Invalid email id");
        }

        if (!mail.equalsIgnoreCase("") && !mail.isEmpty() && mail.matches(emailPattern))
        {
            iserror = false;
            txt_input_layour1.setError(null);
        }

        if (fname.equalsIgnoreCase("") && fname.isEmpty())
        {
            iserror = true;
            txt_input_layour2.setError("Enter your first name");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour2.requestFocus();
        }
        if (!fname.equalsIgnoreCase("") && !fname.isEmpty())
        {
            iserror = false;
            txt_input_layour2.setError(null);
        }

        if (lname.equalsIgnoreCase("") && lname.isEmpty())
        {
            iserror = true;
            txt_input_layour3.setError("Enter your last name");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour3.requestFocus();

        }
        if (!lname.equalsIgnoreCase("") && !lname.isEmpty())
        {
            iserror = false;
            txt_input_layour3.setError(null);
        }

        if (gender.equalsIgnoreCase("") && gender.isEmpty())
        {
            iserror = true;
            text_input_gender.setError("Enter your gender.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour3.requestFocus();
        }
        if (!gender.equalsIgnoreCase("") && !gender.isEmpty())
        {
            iserror = false;
            text_input_gender.setError(null);
        }

        if (age.equalsIgnoreCase("") && age.isEmpty())
        {
            iserror = true;
            text_input_age.setError("Enter your age.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour3.requestFocus();

        }
        if (!age.equalsIgnoreCase("") && !age.isEmpty())
        {
            iserror = false;
            text_input_age.setError(null);
        }


         if (mobile.equalsIgnoreCase("") && mobile.isEmpty())
        {
            iserror = true;
            txt_input_layour4.setError("Enter mobile number");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour4.requestFocus();

        }
        if (!mobile.equalsIgnoreCase("") && !mobile.isEmpty())
        {
            iserror = false;
            txt_input_layour4.setError(null);
        }
        if (pass.equalsIgnoreCase("") && pass.isEmpty())
        {
            iserror = true;
            txt_input_layour5.setError("Choose password");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour5.requestFocus();

        }
        if (!pass.equalsIgnoreCase("") && !pass.isEmpty())
        {
            iserror = false;
            txt_input_layour5.setError(null);
        }

        if (c_pass.equalsIgnoreCase("") && c_pass.isEmpty())
        {
            iserror = true;
            txt_input_layour6.setError("Confirm choose password");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour6.requestFocus();

        }
        if (!c_pass.equalsIgnoreCase("") && !c_pass.isEmpty())
        {
            iserror = false;
            txt_input_layour6.setError(null);
        }

        if (city.equalsIgnoreCase("") && city.isEmpty())
        {
            iserror = true;
            text_input_city.setError("Enter your city.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour6.requestFocus();

        }
        if (!city.equalsIgnoreCase("") && !city.isEmpty())
        {
            iserror = false;
            text_input_city.setError(null);
        }

        if (state.equalsIgnoreCase("") && state.isEmpty())
        {
            iserror = true;
            text_input_state.setError("Enter your state.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour6.requestFocus();

        }
        if (!state.equalsIgnoreCase("") && !state.isEmpty())
        {
            iserror = false;
            text_input_state.setError(null);
        }
        if (country.equalsIgnoreCase("") && country.isEmpty())
        {
            iserror = true;
            text_input_country.setError("Enter your country.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();
            //txt_input_layour6.requestFocus();
        }
        if (!country.equalsIgnoreCase("") && !country.isEmpty())
        {
            iserror = false;
            text_input_country.setError(null);
        }

        if (iserror == false)
        {
            if (Utils.isConnected(this))
            {
                register(mail,fname,lname,gender,age,mobile,pass,c_pass,city,state,country,refrer);
            }
            else
            {
                Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }


    }

    //------------------------
    private void register(final String mail, final String fname, final String lname,final String gender,final String age,
                          final String mobile,
                          final String pass, final String c_pass,final String city,final String state,final String country, String refrer) {
       // startActivity(new Intent(SignupActivity.this,LoginActivity.class));
        //progressBar.setVisibility(View.GONE);
        System.out.println("Print Param -- "+ fname+"==="+lname+"--"+gender+"--"+age+"--"+mail+"--"+mobile+"=="+
                pass+ c_pass+"--"+city+"--"+state+"--"+country);
        //progressBar.setVisibility(View.VISIBLE);
        progressDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URls.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("signUp", "sign up response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    // String status = jObj.getString("status");
                    //String errorMsg = jObj.getString("message");

                    //JSONObject error_object=jObj.getJSONObject("error");

                    JSONObject success=jObj.getJSONObject("success");
                    String token = success.getString("token");

                    if (!token.equalsIgnoreCase("")) {

                        JSONObject result=success.getJSONObject("result");

                        System.out.println("Result register***"+result+"------ " + token);

                        Toast.makeText(SignupActivity.this, "Register Successfully.",Toast.LENGTH_SHORT).show();

                        MySharedPref sp=new MySharedPref(SignupActivity.this);
                        sp.setData("ldata",result+"");

                        Intent ii=new Intent(SignupActivity.this,MapsActivity.class);
                        startActivity(ii);

                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();

                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();

                        Toast.makeText(SignupActivity.this, "Failure",Toast.LENGTH_SHORT).show();
                        //   Toast.makeText(SignupActivity.this,"You are deactivated by Admin.",Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();

                Log.e("register Response", "register  Error: " + error.getMessage());
                try {
                    Toast.makeText(SignupActivity.this, "Something went wrong!",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("X-API-KEY","TEST@123");
                header.put("Authorization", "");
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email",mail);
                params.put("first_name",fname);
                params.put("last_name",lname);
                params.put("gender",gender);
                params.put("age",age);
                params.put("mobile_number",mobile);
                params.put("password",pass);
                params.put("c_password",c_pass);
                params.put("city",city);
                params.put("state",state);
                params.put("country",country);

                //params.put("referral_code","");
                return params;
            }
        };
        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }
//-----------------------------------------------------


}
