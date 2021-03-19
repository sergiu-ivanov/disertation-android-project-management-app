
package com.sergiuivanov.finalprojectm.activities;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Intent;
        import android.os.Bundle;

        import com.sergiuivanov.finalprojectm.R;
        import com.sergiuivanov.finalprojectm.adapters.ProjectFirestoreListAdapter;
        import com.sergiuivanov.finalprojectm.models.Project;
        import com.sergiuivanov.finalprojectm.models.User;
        import com.firebase.ui.firestore.FirestoreRecyclerOptions;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.CollectionReference;
        import com.google.firebase.firestore.DocumentSnapshot;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.Query;

public class ProjectListActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private User user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refToProjectsNode;
    private ProjectFirestoreListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (getIntent().hasExtra("currentUser")) {
            user = getIntent().getParcelableExtra("currentUser");

            setupRecyclerView(user);
        } else {
            System.out.println("Failure getting intent from Projectlist Activity*************");
        }
    }


    private void setupRecyclerView(User user) {

        refToProjectsNode = db.collection("Projects/"+user.getName() +"/PersonalProjects");

        Query query = refToProjectsNode;

        FirestoreRecyclerOptions<Project> options = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class).build();

        adapter = new ProjectFirestoreListAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_list_projects);
//        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        adapter.setOnProjectListClickListener(new ProjectFirestoreListAdapter.OnListClickListener() {
            @Override
            public void OnProjectListClick(DocumentSnapshot documentSnapshot, int position) {
                Project project = documentSnapshot.toObject(Project.class);
                String projectID = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Intent intent = new Intent(ProjectListActivity.this, ProjectSelectionActivity2.class);
                intent.putExtra("pathToProject", path);
                intent.putExtra("author", project.getAuthor());
                intent.putExtra("title", project.getTitle());
                intent.putExtra("description", project.getDescription());
                intent.putExtra("email", project.getEmail());
                intent.putExtra("status", project.getStatus());
                intent.putExtra("id", project.getId());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}


