package com.sergiuivanov.finalprojectm.adapters;

import android.view.ContextMenu;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProjectFirestoreAdapter extends FirestoreRecyclerAdapter<Project, ProjectFirestoreAdapter.ProjectFirestoreHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference refToProjects ;
    private OnProjectClickListener listener;

    public ProjectFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Project> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ProjectFirestoreHolder holder, int i, @NonNull Project project) {
        holder.author.setText(project.getAuthor());
        holder.description.setText(project.getDescription());
        holder.title.setText(project.getTitle());
        holder.email.setText(project.getEmail());
        holder.status.setText(project.getStatus());
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 1, 0, "Refuse");
            }
        });


    }

    @NonNull
    @Override
    public ProjectFirestoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item_home, parent, false);
        return new ProjectFirestoreHolder(v);
    }

    public void updateProjectStatus(int position){
        String id = getSnapshots().getSnapshot(position).get("id").toString();
        String author = getSnapshots().getSnapshot(position).get("author").toString();

        try{
            refToProjects = db.document("Projects/"+ author +"/PersonalProjects/"+ id);
            refToProjects.update("status", "available");
            refToProjects.update("key", "");
        }catch (Exception e){

        }



    }

    class ProjectFirestoreHolder extends RecyclerView.ViewHolder {

        TextView author , description, title, email, status ;


        public ProjectFirestoreHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.home_author);
            description = itemView.findViewById(R.id.home_description);
            title = itemView.findViewById(R.id.home_title_field);
            email = itemView.findViewById(R.id.home_email);
            status = itemView.findViewById(R.id.home_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onProjectClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface OnProjectClickListener {
        void onProjectClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnProjectClickListener(OnProjectClickListener listener){
        this.listener = listener;
    }

}
