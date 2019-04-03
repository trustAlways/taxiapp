package application.taxiapp.com.taxiapp;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class YourTripsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<String> mCategoryTitle;
    ImageView img_back;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_trips);

        setUpPager();
        clickEvents();
    }

    private void setUpPager() {
        mCategoryTitle = new ArrayList<>();
        mCategoryTitle.add("PAST");
        mCategoryTitle.add("UPCOMING");

        context = this;
        img_back = (ImageView)findViewById(R.id.trips_back);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),mCategoryTitle);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void clickEvents()
    {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    //------------------------------------------------------------------------------
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        // List<Fragment> mFragments = new ArrayList<>();
        List<String> mFragmentsTitle;

        public ViewPagerAdapter(FragmentManager fm, List<String> mCategoryTitle) {
            super(fm);
            this.mFragmentsTitle = mCategoryTitle;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("mylog", mFragmentsTitle.get(position));
            return mFragmentsTitle.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return ProductDetail.getInstance(position,mFragmentsTitle.get(position));
        }

        @Override
        public int getCount() {
            return mFragmentsTitle.size();
        }
    }
}
