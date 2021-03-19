package com.sergiuivanov.finalprojectm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.User;

import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.StaffListViewHolder> {

    private List<User> userList;
    private OnStaffListener mOnStaffListener;

    public interface OnStaffListener{
        void onStaffClick(int position);
    }

    public StaffListAdapter(List<User> userList) {
        this.userList = userList;
    }
    public StaffListAdapter( List<User> userList, OnStaffListener onStaffListener) {
        this.userList = userList;
        this.mOnStaffListener = onStaffListener;
    }

    @NonNull
    @Override
    public StaffListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StaffListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_item, parent, false), mOnStaffListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffListViewHolder holder, final int position) {

        User user = userList.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class StaffListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name , email;
        OnStaffListener onStaffListener;

        public StaffListViewHolder(View itemView, OnStaffListener onStaffListener ){
            super(itemView);
            this.onStaffListener = onStaffListener;
            name = itemView.findViewById(R.id.staff_author);
            email = itemView.findViewById(R.id.staff_email);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStaffListener.onStaffClick(getAdapterPosition());
        }
    }

}
