package com.sergiuivanov.finalprojectm.adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sergiuivanov.finalprojectm.R;
import com.sergiuivanov.finalprojectm.models.IndividualQueueProject;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class IndividualQueueAdapter extends FirestoreRecyclerAdapter<IndividualQueueProject, IndividualQueueAdapter.IndividualQueueHolder> {


    public IndividualQueueAdapter(@NonNull FirestoreRecyclerOptions<IndividualQueueProject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final IndividualQueueHolder holder, int i, @NonNull IndividualQueueProject individualQueueProject) {

        holder.author.setText(individualQueueProject.getAuthor());
        holder.title.setText(individualQueueProject.getTitle());
        holder.description.setText(individualQueueProject.getDescription());
        holder.email.setText(individualQueueProject.getEmail());
        holder.queuePosition.setText(individualQueueProject.getQueuePosition());
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), 1, 0, "Refuse");
            }
        });

    }

    @NonNull
    @Override
    public IndividualQueueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        return new IndividualQueueHolder(v);
    }

    class IndividualQueueHolder extends RecyclerView.ViewHolder {

        TextView author , description, title, email, queuePosition ;

        public IndividualQueueHolder(@NonNull View itemView) {
            super(itemView);

            author = itemView.findViewById(R.id.queue_author);
            description = itemView.findViewById(R.id.queue_description);
            title = itemView.findViewById(R.id.queue_title_field);
            email = itemView.findViewById(R.id.queue_email);
            queuePosition = itemView.findViewById(R.id.queue_position);

        }
    }

}
