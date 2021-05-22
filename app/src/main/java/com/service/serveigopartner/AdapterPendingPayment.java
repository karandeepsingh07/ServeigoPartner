package com.service.serveigopartner;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPendingPayment extends RecyclerView.Adapter<AdapterPendingPayment.PostViewHolder> {

    private ArrayList<ClassBooking> items;
    private Context context;
    public String state;
    public String city;
    public String vendorID;
    public String category;
    public String subCategory;

    public AdapterPendingPayment(ArrayList<ClassBooking> items, Context context, String state, String city, String vendorID, String category, String subCategory) {
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
    public AdapterPendingPayment.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cardview_pending_payment,parent,false);
        return new AdapterPendingPayment.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final AdapterPendingPayment.PostViewHolder holder, int position) {

        final ClassBooking item =items.get(position);
        String[] nameList=item.getUserName().split(" ");
        String name=nameList[0];
        Log.d("xyz",name);
        holder.textView1.setText(name);
        holder.textView2.setText(item.getDate());
        holder.textView3.setText("Due Payment");
        holder.textView3.setTextColor(Color.parseColor("#EF7F1A"));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Intent intent = new Intent("custom-message");
                    intent.putExtra("item", item.getJobId());
                    intent.putExtra("amount", item.getAmount());
                    intent.putExtra("task","add");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }else{
                    Intent intent = new Intent("custom-message");
                    intent.putExtra("item", item.getJobId());
                    intent.putExtra("amount", item.getAmount());
                    intent.putExtra("task","remove");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                    /*Intent intent = new Intent("custom-message");
                    intent.putExtra("item", item.getJobId());
                    intent.putExtra("amount", item.getAmount());
                    intent.putExtra("task","add");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
                }else{
                    holder.checkBox.setChecked(false);
                    /*Intent intent = new Intent("custom-message");
                    intent.putExtra("item", item.getJobId());
                    intent.putExtra("amount", item.getAmount());
                    intent.putExtra("task","remove");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
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
        public CheckBox checkBox;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_customerName);
            textView2=itemView.findViewById(R.id.textView_date);
            textView3=itemView.findViewById(R.id.textView_status);
            checkBox=itemView.findViewById(R.id.checkbox);
        }
    }
}

