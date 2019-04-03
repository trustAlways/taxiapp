package application.taxiapp.com.taxiapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

import application.taxiapp.com.taxiapp.ProgressDialog.ProgressBarClass;

public class ChangePasswordActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ProgressDialog progressDialog;

    Button update_pass;
    EditText editText_old_pass,editText_new_pass,editText_confirm_pass;
    TextInputLayout txt_input_layour1, txt_input_layour2, txt_input_layour3;
    ImageView img_back;
    Boolean iserror;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();
        click();
    }
//--------------------------------------------------------------------------------------------------------
    private void init() {

        progressBar = (ProgressBar) findViewById(R.id.register_progress);
        progressDialog = ProgressBarClass.createProgressDialog(this);
        progressDialog.dismiss();
        progressDialog.setCancelable(true);

        img_back = (ImageView)findViewById(R.id.change_pass_back);

        txt_input_layour1 = (TextInputLayout)findViewById(R.id.old_pass_input);
        txt_input_layour2 = (TextInputLayout)findViewById(R.id.new_pass_input);
        txt_input_layour3 = (TextInputLayout)findViewById(R.id.confirm_pass_input);


        editText_old_pass = (EditText)findViewById(R.id.edt_old_pass);
        editText_new_pass = (EditText)findViewById(R.id.edt_new_pass);
        editText_confirm_pass = (EditText)findViewById(R.id.edt_confirm_pass);

        update_pass = (Button)findViewById(R.id.update_password);
    }

    private void click()
    {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this,MyAccountActivity.class));
            }
        });

        update_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();
                validation();
            }
        });
    }

    private void validation() {
        String old = editText_old_pass.getText().toString().trim();
        String neww = editText_new_pass.getText().toString().trim();
        String confirm = editText_confirm_pass.getText().toString().trim();

        iserror = false;

        if (old.equalsIgnoreCase("") && old.isEmpty())
        {
            iserror = true;
            txt_input_layour1.setError("Enter your old password.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour2.requestFocus();
        }
        if (!old.equalsIgnoreCase("") && !old.isEmpty())
        {
            iserror = false;
            txt_input_layour1.setError(null);
        }

        if (neww.equalsIgnoreCase("") && neww.isEmpty())
        {
            iserror = true;
            txt_input_layour2.setError("Enter your new password.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour3.requestFocus();

        }
        if (!neww.equalsIgnoreCase("") && !neww.isEmpty())
        {
            iserror = false;
            txt_input_layour2.setError(null);
        }

        if (confirm.equalsIgnoreCase("") && confirm.isEmpty())
        {
            iserror = true;
            txt_input_layour3.setError("Enter your confirm password.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour3.requestFocus();
        }
         else if (!confirm.equalsIgnoreCase(neww) && !confirm.equals(neww))
        {
            iserror = true;
            txt_input_layour3.setError("Confirm password not matching with new password.");
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();

            //txt_input_layour3.requestFocus();
        }
        if (!confirm.equalsIgnoreCase("") && confirm.equals(neww))
        {
            iserror = false;
            txt_input_layour3.setError(null);
        }

    }
}
