package com.example.sma;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.sma.Util.Utils.EXAMINATION;
import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.TITLE;

public class ExaminationActivity extends AppCompatActivity {

    RecyclerView rec;
    List<DocumentSnapshot> examinationList = new ArrayList<>();
    ExaminationAdapter adapter;
    ProgressBar progressBar;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private RecyclerRefreshLayout swipe_refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);


        Toolbar toolbar = (Toolbar) findViewById(R.id.ExaminationActivity_toolbar);
        setToolbar(toolbar, "");
        swipe_refresh = findViewById(R.id.examinationActivity_refresh_layout);
        setRec();

        swipe_refresh.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                examinationList.clear();
                loadExaminationData();
            }
        });
    }

    private void setRec() {
        progressBar = findViewById(R.id.progressBar);
        rec = findViewById(R.id.examination_rec);
        rec.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ExaminationAdapter(examinationList);
        rec.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        loadExaminationData();

    }

    private void loadExaminationData() {

        firestore.collection(EXAMINATION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        if (null == queryDocumentSnapshots && queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(ExaminationActivity.this, "No data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        examinationList.addAll(queryDocumentSnapshots.getDocuments());
                        adapter.notifyDataSetChanged();
                        swipe_refresh.setRefreshing(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ExaminationActivity.this, "try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(EXAMINATION);

    }

    private class ExaminationAdapter extends RecyclerView.Adapter<ExaminationAdapter.ViewHolder> {
        List<DocumentSnapshot> snapshots;

        public ExaminationAdapter(List<DocumentSnapshot> snapshots) {
            this.snapshots = snapshots;
        }

        @NonNull
        @Override
        public ExaminationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.examination_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExaminationAdapter.ViewHolder holder, final int position) {

            final DocumentSnapshot snapshot = snapshots.get(position);
            holder.textView.setText(snapshot.getString(TITLE));
            String image = snapshot.getString(IMAGE);
            if (null != image && !image.equalsIgnoreCase(""))
                Picasso.with(ExaminationActivity.this)
                        .load(image).placeholder(R.drawable.profile)
                        .into(holder.circleImageView);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = snapshot.getId();
                    if (position == 1)
                        startActivity(new Intent(ExaminationActivity.this, ScheduleActivity.class)
                                .putExtra("id", id));
                    else
                        startActivity(new Intent(ExaminationActivity.this, ExamResultsActivity.class)
                                .putExtra("id", id));
                }
            });
        }

        @Override
        public int getItemCount() {
            return snapshots.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView circleImageView;
            RelativeLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.exa_textView);
                circleImageView = itemView.findViewById(R.id.exa_imageView);
                layout = itemView.findViewById(R.id.card_Home2);
            }
        }
    }
}