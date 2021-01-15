package com.example.sma;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import static com.example.sma.Util.Utils.getTimeAgo;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView rec;
    List<DocumentSnapshot> examinationList = new ArrayList<>();
    ScheduleAdapter adapter;
    ProgressBar progressBar;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String doc_id;
    private RecyclerRefreshLayout swipe_refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        doc_id = getIntent().getStringExtra("id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.ScheduleActivity_toolbar);
        swipe_refresh = findViewById(R.id.ScheduleActivity_refresh_layout);

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
        progressBar = findViewById(R.id.ScheduleActivity_progressBar);
        rec = findViewById(R.id.ScheduleActivity_rec);
        rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(examinationList);
        rec.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        loadExaminationData();

    }

    private void loadExaminationData() {

        firestore.collection(EXAMINATION)
                .document(doc_id)
                .collection(SCHEDULE)
                .orderBy(LAST_UPDATE, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        if (null == queryDocumentSnapshots && queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(ScheduleActivity.this, "No data", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ScheduleActivity.this, "try again", Toast.LENGTH_SHORT).show();
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
        getSupportActionBar().setTitle("Exam " + SCHEDULE+" 2019-2020");

    }

    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
        List<DocumentSnapshot> snapshots;

        public ScheduleAdapter(List<DocumentSnapshot> snapshots) {
            this.snapshots = snapshots;
        }

        @NonNull
        @Override
        public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_view, parent, false);
            return new ScheduleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, final int position) {
            final DocumentSnapshot snapshot = snapshots.get(position);
            try {

                holder.textView.setText(snapshot.getString(CLASS));
                holder.board.setText(snapshot.getString(BOARD));
                holder.last_update.setText(getTimeAgo(snapshot.getLong(LAST_UPDATE)));
                holder.is_newTv.setVisibility(snapshot.getBoolean(IS_NEW) ? View.VISIBLE : View.GONE);
            } catch (Exception e) {
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // addData(doc_id,snapshot.getId(), ScheduleActivity.this);
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