package com.example.baikiemtrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baikiemtrack.R;
import com.example.baikiemtrack.model.Thongtin;

import java.util.ArrayList;

public class ThongtinAdapter extends RecyclerView.Adapter<ThongtinAdapter.ViewHolder> {
    private ArrayList<Thongtin> userList;

    public ThongtinAdapter(ArrayList<Thongtin> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Thongtin thongtin = userList.get(position);
        holder.name.setText(thongtin.getName());
        holder.msv.setText(thongtin.getMsv());
        holder.lop.setText(thongtin.getLop());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, msv, lop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_name);
            msv = itemView.findViewById(R.id.txt_msv);
            lop = itemView.findViewById(R.id.txt_lop);
        }
    }
}
