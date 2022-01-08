package com.sameer.covidtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sameer.covidtracker.R;
import com.sameer.covidtracker.dto.Covid_Info;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Covid_Info> list;
    int m;

    public RecyclerAdapter(Context context, ArrayList<Covid_Info> list, int m) {
        this.context = context;
        this.list = list;
        this.m = m;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_short_info,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {

        holder.country.setText(list.get(position).getCounty());

        if(m==0){
            holder.num.setText(String.valueOf(list.get(position).getActive()));
        }else if(m==1){
            holder.num.setText(String.valueOf(list.get(position).getRecovered()));
        }else if(m==3){
            holder.num.setText(String.valueOf(list.get(position).getDeaths()));
        }else {
            holder.num.setText(String.valueOf(list.get(position).getCases()));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView country,num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            country = itemView.findViewById(R.id.id_country);
            num = itemView.findViewById(R.id.id_num);
        }
    }
}
