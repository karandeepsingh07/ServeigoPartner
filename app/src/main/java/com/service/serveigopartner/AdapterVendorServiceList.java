package com.service.serveigopartner;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterVendorServiceList extends RecyclerView.Adapter<AdapterVendorServiceList.PostViewHolder> {

    private List<ClassService> items;
    private Context context;

    public AdapterVendorServiceList(List<ClassService> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.servicelist_cardview,parent,false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        final ClassService item =items.get(position);
        holder.textView1.setText(item.getHead());
        holder.textView2.setText(""+item.getPrice());
        holder.textView3.setText(""+item.getCommission());
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(item.isChecked());
        // holder.textView2.setText(item.getVendorAddress());
        //   holder.textView3.setText(item.getTime());
//        Picasso.get().load(item.getImageUrlbgr()).into(holder.imageView);*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    item.setChecked(false);
                }else{
                    holder.checkBox.setChecked(true);
                    item.setChecked(true);
                }
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                holder.checkBox.setChecked(b);
                item.setChecked(b);
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
        public CheckBox checkBox;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_service);
            textView2=itemView.findViewById(R.id.text_price);
            textView3=itemView.findViewById(R.id.text_commission);
            checkBox=itemView.findViewById(R.id.checkbox);
        }
    }
}

