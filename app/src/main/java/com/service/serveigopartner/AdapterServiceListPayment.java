package com.service.serveigopartner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterServiceListPayment extends RecyclerView.Adapter<AdapterServiceListPayment.PostViewHolder>{
    private ArrayList<String> items;
    private Context context;

    public AdapterServiceListPayment(ArrayList<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterServiceListPayment.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cardview_service,parent,false);
        return new AdapterServiceListPayment.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AdapterServiceListPayment.PostViewHolder holder, int position) {

        String item =items.get(position);
        holder.textView1.setText(item);
        // holder.textView2.setText(item.getVendorAddress());
        //   holder.textView3.setText(item.getTime());
//        Picasso.get().load(item.getImageUrlbgr()).into(holder.imageView);*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DataSnapshot dataSnapshot;
                //String matchId = dataSnapshot.getChildren().iterator().next().getKey().toString();
                // Toast.makeText(context, matchId, Toast.LENGTH_SHORT).show();
                // Intent intent=new Intent(context,JobAcceptanceActivity.class);

                //    intent.putExtra("Match",tId);
                // context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (items==null) ? 0:items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
        public TextView textView2;
        public TextView textView3;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.text_id);
            // textView2=itemView.findViewById(R.id.textView_vendorAddress);
            //textView3=itemView.findViewById(R.id.textView_time);


        }
    }
}
