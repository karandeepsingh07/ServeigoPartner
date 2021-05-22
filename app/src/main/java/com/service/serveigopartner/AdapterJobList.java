package com.service.serveigopartner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterJobList extends RecyclerView.Adapter<AdapterJobList.PostViewHolder> {

    private ArrayList<ClassBooking> items;
    private Context context;
    public String state;
    public String city;
    public String vendorID;
    public String category;
    public String subCategory;

    public AdapterJobList(ArrayList<ClassBooking> items, Context context, String state, String city, String vendorID, String category, String subCategory) {
        this.items = items;
        this.context = context;
        this.state = state;
        this.city = city;
        this.vendorID = vendorID;
        this.category = category;
        this.subCategory = subCategory;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cardview_myjob,parent,false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        final ClassBooking item =items.get(position);
        String[] nameList=item.getUserName().split(" ");
        String name=nameList[0];
        holder.textView1.setText(name);
        holder.textView2.setText(item.getDate());
        holder.textView3.setText(item.getStatus());
        if(item.getStatus().equals("DuePayment")){
            holder.textView3.setTextColor(Color.parseColor("#EF7F1A"));
            holder.textView3.setText("Pending");
        }else if(item.getStatus().equals("Closed") && item.isPayment()){
            holder.textView3.setTextColor(Color.parseColor("#009846"));
            holder.textView3.setText("Closed");
        }else if(item.getStatus().equals("Closed")){
            holder.textView3.setTextColor(Color.parseColor("#EF7F1A"));
            holder.textView3.setText("Due Payment");
        }else if(item.getStatus().equals("Rejected")){
            holder.textView3.setTextColor(Color.parseColor("#E31E24"));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!item.getStatus().equals("Rejected"))
                {
                    Intent intent = new Intent(context, PaymentActivity.class);
                    intent.putExtra("jobID",item.getJobId());
                    intent.putExtra("state",state);
                    intent.putExtra("city",city);
                    intent.putExtra("category",category);
                    intent.putExtra("subCategory",subCategory);
                    intent.putExtra("vendorID",vendorID);
                    intent.putExtra("status",item.getStatus());
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
        public TextView textView2;
        public TextView textView3;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_customerName);
            textView2=itemView.findViewById(R.id.textView_date);
            textView3=itemView.findViewById(R.id.textView_status);
        }
    }
}
