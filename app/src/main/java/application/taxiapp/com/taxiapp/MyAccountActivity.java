package application.taxiapp.com.taxiapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import application.taxiapp.com.taxiapp.constant.MySharedPref;

public class MyAccountActivity extends AppCompatActivity {

    //shared prefrence
    MySharedPref pref;
    String ldata,userfName,userlName,userEmail,usermobile,userGender,userAge;

    ImageView img_back;
    TextView textView_fname,textView_lname,textView_email,textView_phone,textView_gender,textView_age;
    Button btn_chnage_pass;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        getBundleData();
        initialize();
        clickEvents();

    }

    //----------------------------------------------

    private void getBundleData()
    {
        pref = new MySharedPref(this);
        ldata=pref.getData("ldata");

        //-------------
        try {
            System.out.println(ldata);
            JSONObject jsonObject = new JSONObject(ldata);

            userfName = jsonObject.getString("first_name");
            userlName = jsonObject.getString("last_name");
            userEmail = jsonObject.getString("email");
            usermobile = jsonObject.getString("mobile_number");
            userGender = jsonObject.getString("gender");
            userAge = jsonObject.getString("age");


            System.out.println(userfName);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void initialize()
    {
        context = this;
        img_back = (ImageView)findViewById(R.id.setting_back);

        //textViewss
        textView_fname = (TextView)findViewById(R.id.add_fname_text);
        textView_lname = (TextView)findViewById(R.id.add_lname_text);
        textView_email = (TextView)findViewById(R.id.add_email_text);
        textView_phone = (TextView)findViewById(R.id.add_mobile_text);

        textView_gender = (TextView)findViewById(R.id.user_gender);
        textView_age = (TextView)findViewById(R.id.user_age);

        textView_fname.setText(userfName);
        textView_lname.setText(userlName);
        textView_email.setText(userEmail);
        textView_phone.setText(usermobile);

        textView_gender.setText(userGender);
        textView_age.setText(userAge);

        //buttons
        btn_chnage_pass = (Button)findViewById(R.id.chnge_password);

    }
    private void clickEvents()
    {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_chnage_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(context,ChangePasswordActivity.class));
            }
        });

    }
}
