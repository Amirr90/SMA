package com.example.sma;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.sma.Util.Utils.ART;
import static com.example.sma.Util.Utils.COMPUTER;
import static com.example.sma.Util.Utils.ENGLISH;
import static com.example.sma.Util.Utils.EXAMS_RESULTS;
import static com.example.sma.Util.Utils.HINDI;
import static com.example.sma.Util.Utils.MARKS;
import static com.example.sma.Util.Utils.MATH;
import static com.example.sma.Util.Utils.RESULT;
import static com.example.sma.Util.Utils.ROLL_NUMBER;
import static com.example.sma.Util.Utils.SCI;
import static com.example.sma.Util.Utils.STU_ID;
import static com.example.sma.Util.Utils.TIMESTAMP;
import static com.example.sma.Util.Utils.YEAR_BOARD;
import static com.example.sma.Util.Utils._CLASS;
import static com.example.sma.Util.Utils.getTimeAgo;

public class ResultListActivity extends AppCompatActivity {

    private static final String TAG2 = "ResultListActivity";
    RecyclerView rec;
    List<DocumentSnapshot> examinationList = new ArrayList<>();
    ResultListActivityAdapter adapter;
    ProgressBar progressBar;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String doc_id_year_board, coll_id_class;
    private RecyclerRefreshLayout swipe_refresh;
    TextInputEditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);
        doc_id_year_board = getIntent().getStringExtra("board_year");
        coll_id_class = getIntent().getStringExtra("class_id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.ResultListActivity_toolbar);
        swipe_refresh = findViewById(R.id.ResultListActivity_refresh_layout);
        searchEditText = findViewById(R.id.search_stu_for_result);

        setToolbar(toolbar, "");
        setRec();

        swipe_refresh.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                examinationList.clear();
                loadExaminationData();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = s.toString();
                loadStuData(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void loadStuData(String text) {


        long roll_no;
        try {
            roll_no = Long.parseLong(text);
            Query query = firestore.collection(EXAMS_RESULTS)
                    .orderBy(ROLL_NUMBER)
                    .startAt(roll_no)
                    .endAt(roll_no + "\uf8ff")
                    .limit(3);


            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    progressBar.setVisibility(View.GONE);
                    if (e != null && null != queryDocumentSnapshots && queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(ResultListActivity.this, "failed to get User " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        examinationList.clear();
                        examinationList.addAll(queryDocumentSnapshots.getDocuments());
                    }
                    adapter.notifyDataSetChanged();

                }
            });
        } catch (Exception e) {
            Log.d(TAG2, "error " + e.getLocalizedMessage());
            Toast.makeText(this, "error " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setRec() {
        progressBar = findViewById(R.id.ResultListActivity_progressBar);
        rec = findViewById(R.id.ResultListActivity_rec);
        rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResultListActivityAdapter(examinationList);
        rec.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);
        loadExaminationData();
        // addData();

    }

    private void loadExaminationData() {

        firestore.collection(EXAMS_RESULTS)
                .whereEqualTo(YEAR_BOARD, doc_id_year_board)
                .whereEqualTo(_CLASS, coll_id_class)
                //.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        if (null == queryDocumentSnapshots && queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(ResultListActivity.this, "No data", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ResultListActivity.this, "try again " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private class ResultListActivityAdapter extends RecyclerView.Adapter<ResultListActivityAdapter.ViewHolder> {
        List<DocumentSnapshot> snapshots;

        public ResultListActivityAdapter(List<DocumentSnapshot> snapshots) {
            this.snapshots = snapshots;
        }

        @NonNull
        @Override
        public ResultListActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_view, parent, false);
            return new ResultListActivityAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultListActivityAdapter.ViewHolder holder, final int position) {
            final DocumentSnapshot snapshot = snapshots.get(position);
            try {

                holder.textView.setText("Roll No " + snapshot.getLong(ROLL_NUMBER));
                holder.last_update.setText("Last update: " + getTimeAgo(snapshot.getLong(TIMESTAMP)));
            } catch (Exception e) {
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ResultListActivity.this, ShowMarkSheetActivity.class)
                            .putExtra(ROLL_NUMBER, snapshot.getLong(ROLL_NUMBER) + ""));
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

                /*Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(200); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                is_newTv.startAnimation(anim);*/
                is_newTv.setVisibility(View.GONE);
                board.setVisibility(View.GONE);
            }
        }
    }

    private void addData() {
        Random r = new Random();
        String stu_id = System.currentTimeMillis() + "";
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> marks_map = new HashMap<>();
        marks_map.put(ENGLISH, r.nextInt(80 - 20) + 20);
        marks_map.put(HINDI, r.nextInt(80 - 20) + 20);
        marks_map.put(MATH, r.nextInt(80 - 20) + 20);
        marks_map.put(SCI, r.nextInt(80 - 20) + 20);
        marks_map.put(ART, r.nextInt(80 - 20) + 20);
        marks_map.put(COMPUTER, r.nextInt(80 - 20) + 20);

        map.put(ROLL_NUMBER, System.currentTimeMillis());
        map.put(RESULT, "Pass");
        map.put(STU_ID, stu_id);
        map.put(_CLASS, coll_id_class);
        map.put(YEAR_BOARD, doc_id_year_board);
        map.put(MARKS, marks_map);
        map.put(TIMESTAMP, System.currentTimeMillis());


        firestore.collection(EXAMS_RESULTS)
                .document(stu_id)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ResultListActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}