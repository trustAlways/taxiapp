package application.taxiapp.com.taxiapp.constant;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPref
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public MySharedPref(Context context) {
       sharedPreferences = context.getSharedPreferences("TaxiDriverApp",Context.MODE_PRIVATE);
       editor = sharedPreferences.edit();
    }

    public String getData(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setData(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

}
