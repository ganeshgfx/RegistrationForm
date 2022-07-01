package com.enjay.regform.fragment.profile;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.enjay.regform.HomeActivity;
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

    Context context;

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.profile_list_item, parent, false);
        RecycleViewHolder viewHolder = new RecycleViewHolder(listItem);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        User user = list.get(position);
        //Log.d("TAG", "onBindViewHolder: "+user.getFullName());
        holder.name.setText(user.getFullName());
        holder.imageView.setImageBitmap(user.getImg());
        holder.card.setOnClickListener(click->{

        });
        holder.card.setOnFocusChangeListener((view, b) -> {
            if(b){

                Fragment profileFragment = new ProfileFragment(list.get(position));
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_viewProfile, profileFragment);
                fragmentTransaction.commit();

                holder.card.setStrokeColor(ContextCompat.getColor(context, R.color.purple_200));
                holder.card.setCardElevation(7);

                ValueAnimator animation = ValueAnimator.ofInt(0, 15
                );
                animation.setInterpolator(new AnticipateOvershootInterpolator());
                animation.setDuration(500);
                animation.start();
                animation.addUpdateListener(updatedAnimation -> {
                    int animatedValue = (int)updatedAnimation.getAnimatedValue();
                    holder.card.setStrokeWidth(animatedValue);
                });

                //api info 2 type
                //whether api
                //firebase api

//                ValueAnimator anim = ValueAnimator.ofFloat(0, -150
//                );
//                anim.setInterpolator(new AnticipateOvershootInterpolator());
//                anim.setDuration(500);
//                anim.start();
//                anim.addUpdateListener(updatedAnimation -> {
//                    float animatedValue = (float)updatedAnimation.getAnimatedValue();
//                    holder.card.setTranslationY(animatedValue);
//                });

            }else{
                holder.card.setStrokeColor(ContextCompat.getColor(context, R.color.onBase));
                holder.card.setCardElevation(1);

                ValueAnimator animation = ValueAnimator.ofInt(15, 3);
                animation.setInterpolator(new AnticipateOvershootInterpolator());
                animation.setDuration(500);
                animation.start();
                animation.addUpdateListener(updatedAnimation -> {
                    int animatedValue = (int)updatedAnimation.getAnimatedValue();
                    holder.card.setStrokeWidth(animatedValue);
                });

//                ValueAnimator anim = ValueAnimator.ofFloat( -150, 0
//                );
//                anim.setInterpolator(new AnticipateOvershootInterpolator());
//                anim.setDuration(500);
//                anim.start();
//                anim.addUpdateListener(updatedAnimation -> {
//                    float animatedValue = (float)updatedAnimation.getAnimatedValue();
//                    holder.card.setTranslationY(animatedValue);
//                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addNew(User user){
        this.list.add(user);
        notifyDataSetChanged();
    }
    public void update(List<User> userList){
        this.list.clear();
        this.list = userList;
        notifyDataSetChanged();
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
