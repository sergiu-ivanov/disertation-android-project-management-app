package com.sergiuivanov.finalprojectm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.Project;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProjectStaffAdapter extends FirestoreRecyclerAdapter<Project, ProjectStaffAdapter.ProjectStaffHolder> {


    public ProjectStaffAdapter(@NonNull FirestoreRecyclerOptions<Project> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProjectStaffHolder holder, int i, @NonNull Project project) {
        holder.description.setText(project.getDescription());
        holder.title.setText(project.getTitle());
        holder.status.setText(project.getStatus());

    }

    @NonNull
    @Override
    public ProjectStaffHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item_staff, parent, false);
        return new ProjectStaffHolder(v);
    }

    class ProjectStaffHolder extends RecyclerView.ViewHolder{

        TextView title, description, status;

        public ProjectStaffHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_field_staff_f);
            description = itemView.findViewById(R.id.description_staff_f);
            status = itemView.findViewById(R.id.status_staff_f);
        }
    }
}
