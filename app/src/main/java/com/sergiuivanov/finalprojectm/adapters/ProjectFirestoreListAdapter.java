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
import com.google.firebase.firestore.DocumentSnapshot;

public class ProjectFirestoreListAdapter extends FirestoreRecyclerAdapter<Project, ProjectFirestoreListAdapter.ProjectFirestoreListHolder>  {

    private OnListClickListener listener;

    public ProjectFirestoreListAdapter(@NonNull FirestoreRecyclerOptions<Project> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProjectFirestoreListHolder holder, int i, @NonNull Project project) {
        holder.author.setText(project.getAuthor());
        holder.description.setText(project.getDescription());
        holder.title.setText(project.getTitle());
        holder.email.setText(project.getEmail());
        holder.status.setText(project.getStatus());
    }

    @NonNull
    @Override
    public ProjectFirestoreListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        return new ProjectFirestoreListHolder(v);
    }

    class ProjectFirestoreListHolder extends RecyclerView.ViewHolder{

        TextView author , description, title, email, status ;

        public ProjectFirestoreListHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title_field);
            email = itemView.findViewById(R.id.email);
            status = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.OnProjectListClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnListClickListener{
        void OnProjectListClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnProjectListClickListener(OnListClickListener listener){
        this.listener = listener;
    }
}
