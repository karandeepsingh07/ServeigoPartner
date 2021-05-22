package com.service.serveigopartner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterNotificationList extends RecyclerView.Adapter<AdapterNotificationList.PostViewHolder>{
    private ArrayList<ClassNotification> items;
    private Context context;
    public String state;
    public String city;
    public String vendorID;
    public String category;
    public String subCategory;

    public AdapterNotificationList(ArrayList<ClassNotification> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterNotificationList.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cardview_notification,parent,false);
        return new AdapterNotificationList.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AdapterNotificationList.PostViewHolder holder, int position) {

        final ClassNotification item =items.get(position);

        holder.textView1.setText(item.getTitle());
        holder.textView2.setText(item.getBody());
        //holder.textView3.setText(item.getDate());
        Picasso.get().load(item.getImageUrl())
                .placeholder(R.color.colorPrimaryDark)
                .error(R.color.colorPrimaryDark).into(holder.imageView);


        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public ImageView imageView;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_head);
            textView2=itemView.findViewById(R.id.textView_body);
            textView3=itemView.findViewById(R.id.textView_date);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
