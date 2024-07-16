package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Pay_log_RV_Adapter extends RecyclerView.Adapter<Pay_log_RV_Adapter.ViewHolder> {

    Context context;
    ArrayList<pay_log_struct> payLogStructs;
    Pay_log_RV_Adapter(Context context, ArrayList<pay_log_struct> payLogStructs){
        this.context=context;
        this.payLogStructs=payLogStructs;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.pay_row,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.add_tv.setText(payLogStructs.get(position).Address);
        holder.amt_tv.setText(payLogStructs.get(position).Amount);
        holder.dat_tv.setText(payLogStructs.get(position).Date);
        Log.e("TAG", "View binded at position: " + position);


    }

    @Override
    public int getItemCount() {
        return payLogStructs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView add_tv, amt_tv, dat_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            add_tv = itemView.findViewById(R.id.add_txt);
            amt_tv = itemView.findViewById(R.id.amt_txt);
            dat_tv = itemView.findViewById(R.id.dat_txt);
        }
    }


}
