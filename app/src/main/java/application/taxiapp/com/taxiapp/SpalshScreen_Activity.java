package application.taxiapp.com.taxiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import application.taxiapp.com.taxiapp.constant.MySharedPref;

public class SpalshScreen_Activity extends AppCompatActivity {
    MySharedPref sp;
    String ldata;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spalshscreen_activity);

        // ---------------------------- For Initilize UI -------------------------------------------------------------------------------//
        initUI1();
    }

    private void initUI1() {

        context = this;
        sp=new MySharedPref(this);
        ldata=sp.getData("ldata");

         //Toast.makeText(getApplicationContext(),"Ldatasplash"+ldata,Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

                if (ldata == null || ldata.equals(null) || ldata.equalsIgnoreCase("null") || ldata.equalsIgnoreCase("") || ldata.equals(null)) {

                    Intent ii = new Intent(SpalshScreen_Activity.this, HomePage.class);
                    startActivity(ii);
                    System.out.println("Ldatasplash 1"+ldata);
                }

                else if (ldata!=null && !ldata.equalsIgnoreCase("null") && !ldata.equalsIgnoreCase(""))
                {
                    Intent ii = new Intent(SpalshScreen_Activity.this, MapsActivity.class);
                    startActivity(ii);
                    System.out.println("Ldatasplash 2"+ldata);
                }
                else
                {
                    Intent ii = new Intent(SpalshScreen_Activity.this, HomePage.class);
                    startActivity(ii);
                    System.out.println("Ldatasplash 3"+ldata);
                }
            }
        }, 4000);

      /*  final Thread t = new Thread() {
            @Override
            public void run() {

                try {

                    sleep(4 * 1000);

                    if (ldata.equalsIgnoreCase("null") || ldata.equalsIgnoreCase("")) {

                        Intent ii = new Intent(SpalshScreen_Activity.this, HomePage.class);
                        startActivity(ii);
                        System.out.println("Ldatasplash 1"+ldata);

                    }

                    else if (!ldata.equalsIgnoreCase("null") && !ldata.equalsIgnoreCase(""))
                    {
                        Intent ii = new Intent(SpalshScreen_Activity.this, MapsActivity.class);
                        startActivity(ii);
                        System.out.println("Ldatasplash 2"+ldata);

                    }
                    else
                    {
                         Intent ii = new Intent(SpalshScreen_Activity.this, HomePage.class);
                         startActivity(ii);
                        System.out.println("Ldatasplash 3"+ldata);

                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("$$Exception**=" + e);

                }

            }
        };
        t.start();*/
    }
}
