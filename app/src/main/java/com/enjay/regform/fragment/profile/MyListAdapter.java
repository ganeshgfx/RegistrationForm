package com.enjay.regform.fragment.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.enjay.regform.R;
import com.enjay.regform.User;
import com.enjay.regform.fragment.ProfileFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.RecycleViewHolder> {

    List<User> list;
    FragmentManager fm;
    public MyListAdapter(List<User> list, FragmentManager fm){
        this.list = list;
        this.fm = fm;
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
        holder.name.setText(user.getFullName());
        holder.imageView.setImageBitmap(user.getImg());
        holder.card.setOnClickListener(click->{
            //Log.d("TAG", "onBindViewHolder: here");
            Fragment profileFragment = new ProfileFragment(list.get(position));
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_viewProfile, profileFragment);
            fragmentTransaction.commit();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecycleViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView name;
        public MaterialCardView card;
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            card  = itemView.findViewById(R.id.profileCardListItem);
        }
    }
}
