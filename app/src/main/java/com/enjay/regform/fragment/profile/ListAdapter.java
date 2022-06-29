package com.enjay.regform.fragment.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enjay.regform.R;
import com.enjay.regform.User;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.RecycleAdapter> {

    List<User> list;
    public ListAdapter(List<User> list){
        this.list = list;
    }

    @NonNull
    @Override
    public RecycleAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.profile_list_item, parent, false);
        RecycleAdapter viewHolder = new RecycleAdapter(listItem);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RecycleAdapter extends RecyclerView.ViewHolder{

        public RecycleAdapter(@NonNull View itemView) {
            super(itemView);
        }
    }
}
