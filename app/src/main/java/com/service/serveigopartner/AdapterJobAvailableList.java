package com.service.serveigopartner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterJobAvailableList extends RecyclerView.Adapter<AdapterJobAvailableList.PostViewHolder> {

    private ArrayList<ClassBooking> items;
    private Context context;
    private String state;
    private String city;
    private String category;
    private String subCategory;
    private String vendorID;

    public AdapterJobAvailableList(ArrayList<ClassBooking> items, Context context, String state, String city, String category, String subCategory, String vendorID) {
        this.items = items;
        this.context = context;
        this.state = state;
        this.city = city;
        this.category = category;
        this.subCategory = subCategory;
        this.vendorID = vendorID;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cardview_availablejobs,parent,false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        final ClassBooking item =items.get(position);
        String[] nameList=item.getUserName().split(" ");
        String name=nameList[0];
        holder.textView1.setText(name);
        holder.textView2.setText(item.getDate());
        /*if(item.getInstant().equals("true")){
            holder.textView2.setText("Instant");
        }else {
            holder.textView2.setText(item.getDate());
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getStatus().equals("Open")) {
                    Intent intent = new Intent(context, JobAcceptanceActivity.class);
                    intent.putExtra("jobID", item.getJobId());
                    intent.putExtra("state",state);
                    intent.putExtra("city",city);
                    intent.putExtra("category",category);
                    intent.putExtra("subCategory",subCategory);
                    intent.putExtra("vendorID",vendorID);
                    context.startActivity(intent);
                }
            }
        });
        holder.buttonEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DataSnapshot dataSnapshot;
                //String matchId = dataSnapshot.getChildren().iterator().next().getKey().toString();
                //Toast.makeText(context, item.getJobId(), Toast.LENGTH_SHORT).show();
                if(item.getStatus().equals("Open")) {
                    Intent intent = new Intent(context, JobAcceptanceActivity.class);
                    intent.putExtra("jobID", item.getJobId());
                    intent.putExtra("state",state);
                    intent.putExtra("city",city);
                    intent.putExtra("category",category);
                    intent.putExtra("subCategory",subCategory);
                    intent.putExtra("vendorID",vendorID);
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
        public Button buttonEye;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_customerName);
            textView2=itemView.findViewById(R.id.textView_date);
            buttonEye=itemView.findViewById(R.id.button_eye);

        }
    }
}
