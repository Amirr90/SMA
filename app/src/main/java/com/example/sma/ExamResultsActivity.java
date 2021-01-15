package com.example.sma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.sma.Util.Utils.BOARD;
import static com.example.sma.Util.Utils.CLASS;
import static com.example.sma.Util.Utils.EXAMINATION;
import static com.example.sma.Util.Utils.IS_NEW;
import static com.example.sma.Util.Utils.LAST_UPDATE;
import static com.example.sma.Util.Utils.SCHEDULE;
import static com.example.sma.Util.Utils.YEAR;
import static com.example.sma.Util.Utils.addData;
import static com.example.sma.Util.Utils.getTimeAgo;

public class ExamResultsActivity extends AppCompatActivity {

    RecyclerView rec;
    List<DocumentSnapshot> examinationList = new ArrayList<>();
    ExamResultsAdapter adapter;
    ProgressBar progressBar;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String doc_id;
    private RecyclerRefreshLayout swipe_refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examresults);

        doc_id = getIntent().getStringExtra("id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.ExamResultsActivity_toolbar);
        swipe_refresh = findViewById(R.id.ExamResultsActivity_refresh_layout);

        setToolbar(toolbar, "");
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
        progressBar = findViewById(R.id.ExamResultsActivity_progressBar);
        rec = findViewById(R.id.ExamResultsActivity_rec);
        rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExamResultsAdapter(examinationList);
        rec.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        loadExaminationData();
        //addData(doc_id, ExamResultsActivity.this);

    }

    private void loadExaminationData() {

        firestore.collection(EXAMINATION)
                .document(doc_id)
                .collection(CLASS)
                .orderBy(LAST_UPDATE, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        if (null == queryDocumentSnapshots && queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(ExamResultsActivity.this, "No data", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ExamResultsActivity.this, "try again", Toast.LENGTH_SHORT).show();
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
        getSupportActionBar().setTitle("Exam Result");

    }

    private class ExamResultsAdapter extends RecyclerView.Adapter<ExamResultsAdapter.ViewHolder> {
        List<DocumentSnapshot> snapshots;

        public ExamResultsAdapter(List<DocumentSnapshot> snapshots) {
            this.snapshots = snapshots;
        }

        @NonNull
        @Override
        public ExamResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_view, parent, false);
            return new ExamResultsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ExamResultsAdapter.ViewHolder holder, final int position) {
            final DocumentSnapshot snapshot = snapshots.get(position);
            try {

                holder.textView.setText(snapshot.getString(CLASS));
                holder.board.setText(snapshot.getString(BOARD) + "(" + snapshot.getString(YEAR) + ")");
                holder.last_update.setText("Last update: " + getTimeAgo(snapshot.getLong(LAST_UPDATE)));
                holder.is_newTv.setVisibility(snapshot.getBoolean(IS_NEW) ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
                Toast.makeText(ExamResultsActivity.this, "error "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String board_year = holder.board.getText().toString();
                    String class_id = holder.textView.getText().toString();
                    startActivity(new Intent(ExamResultsActivity.this, ResultListActivity.class)
                            .putExtra("board_year", board_year)
                            .putExtra("class_id", class_id));
                }
            });

        }

        @Override
        public int getItemCount() {
            return snapshots.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView, board, last_update, is_newTv;
            RelativeLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.sch_textView);
                board = itemView.findViewById(R.id.sch_textView2);
                last_update = itemView.findViewById(R.id.sch_textView3);
                is_newTv = itemView.findViewById(R.id.sch_textView4);
                layout = itemView.findViewById(R.id.card_Home3);

                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(200); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                is_newTv.startAnimation(anim);
            }
        }
    }

}