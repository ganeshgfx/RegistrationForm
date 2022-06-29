package com.enjay.regform.fragment.profile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enjay.regform.R;
import com.enjay.regform.User;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.RecycleViewHolder> {

    List<User> list;
    public MyListAdapter(List<User> list){
        this.list = list;
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.profile_list_item, parent, false);
        RecycleViewHolder viewHolder = new RecycleViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        User user = list.get(position);
        //Log.d("TAG", "onBindViewHolder: "+user.getFullName());
        holder.textView.setText(user.getFullName());
        holder.imageView.setImageBitmap(user.getImg());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            textView = itemView.findViewById(R.id.name);
        }
    }
}
