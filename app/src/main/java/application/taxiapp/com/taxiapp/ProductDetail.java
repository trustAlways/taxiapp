package application.taxiapp.com.taxiapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//import tabview.nested.demo.com.demoapp.modelfile.Company;

public class ProductDetail extends Fragment {
    View v;
    int position;
    String cat_name;
    GridView gridView;
    RecyclerView recyclerView;
    ArrayList<String> company;

    public  static Fragment getInstance(int position, String category_name){
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        bundle.putString("cat_name",category_name);
        ProductDetail productDetail = new ProductDetail();
        productDetail.setArguments(bundle);
        return productDetail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("pos");
        cat_name = getArguments().getString("cat_name");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.product_recycler_view, container, false);
        gridView = (GridView) v.findViewById(R.id.grid);
        recyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(layoutManager);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        //MySimpleAdapter adapter = new MySimpleAdapter(getActivity(),company);
        //recyclerView.setAdapter(adapter);
    }



    //---------------------------------------------------------------
   /* public class MySimpleAdapter extends BaseAdapter {
        View v;
        ImageView imgcompany;
        TextView txtname,txtdetail;
        Activity context;
        ArrayList<Company> companyArrayList;

        public MySimpleAdapter(FragmentActivity activity, ArrayList<Company> company) {
          this.context = activity;
          this.companyArrayList = company;
        }

        @Override
        public int getCount() {
            return companyArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return companyArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            v = LayoutInflater.from(getActivity()).inflate(R.layout.single_company, viewGroup, false);
            txtname = (TextView)v.findViewById(R.id.lbl_name);
            txtdetail = (TextView)v.findViewById(R.id.lbl_networth);
            imgcompany = (ImageView)v.findViewById(R.id.img_company);

            txtname.setText(companyArrayList.get(i).getCompany_name());
            txtdetail.setText(companyArrayList.get(i).getCompany_networth());
            imgcompany.setImageResource(company.get(i).getCompany_photo());

            return v;
        }

    }*/

//recycler view adapter class for show datd

/*public class MySimpleAdapter extends RecyclerView.Adapter<MySimpleAdapter.ViewHolder>
{
    View v;
    ImageView imgcompany,video_play_icon;
    TextView txtname,txtdetail;
    Activity context;
    ArrayList<Company> companyArrayList;

    public MySimpleAdapter(FragmentActivity activity, ArrayList<Company> company) {
        this.context = activity;
        this.companyArrayList = company;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.single_company, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        if (companyArrayList.get(i).getComapny_id()=="2")
        {
            video_play_icon.setVisibility(View.VISIBLE);
            txtname.setText(companyArrayList.get(i).getCompany_name());
            txtdetail.setText(companyArrayList.get(i).getCompany_networth());
            imgcompany.setImageResource(company.get(i).getCompany_photo());
        }
        else
        {

            video_play_icon.setVisibility(View.GONE);
            txtname.setText(companyArrayList.get(i).getCompany_name());
            txtdetail.setText(companyArrayList.get(i).getCompany_networth());
            imgcompany.setImageResource(company.get(i).getCompany_photo());
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (companyArrayList.get(i).getComapny_id()=="2")
                {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, NewsVideoPlay.class);
                    context.startActivity(intent);
                }
                else
                {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_POSITION, i);
                    context.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return companyArrayList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtname = (TextView)itemView.findViewById(R.id.lbl_name);
            txtdetail = (TextView)itemView.findViewById(R.id.lbl_networth);
            imgcompany = (ImageView)itemView.findViewById(R.id.img_company);
            video_play_icon = (ImageView)itemView.findViewById(R.id.video_play_icon);
        }
    }
}*/


}
