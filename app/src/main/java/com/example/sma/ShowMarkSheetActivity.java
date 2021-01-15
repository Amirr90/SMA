package com.example.sma;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.sma.model.MarksModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sma.Util.Utils.ART;
import static com.example.sma.Util.Utils.COMPUTER;
import static com.example.sma.Util.Utils.ENGLISH;
import static com.example.sma.Util.Utils.EXAMS_RESULTS;
import static com.example.sma.Util.Utils.FAIL;
import static com.example.sma.Util.Utils.FULL_MARKS;
import static com.example.sma.Util.Utils.HINDI;
import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.MATH;
import static com.example.sma.Util.Utils.NAME;
import static com.example.sma.Util.Utils.PASS;
import static com.example.sma.Util.Utils.PASSING_MARKS;
import static com.example.sma.Util.Utils.RESULT;
import static com.example.sma.Util.Utils.ROLL_NUMBER;
import static com.example.sma.Util.Utils.SCI;
import static com.example.sma.Util.Utils.SON_OF;
import static com.example.sma.Util.Utils.STUDENT_QUERY;
import static com.example.sma.Util.Utils._CLASS;
import static com.example.sma.Util.Utils.logout;

public class ShowMarkSheetActivity extends AppCompatActivity {

    RecyclerView rec;
    List<MarksModel> marksList = new ArrayList<>();
    List<DocumentSnapshot> usersSnaps = new ArrayList<>();
    ShowMarkSheetActivityAdapter adapter;

    ProgressBar progressBar;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String roll_no;
    private RecyclerRefreshLayout swipe_refresh;
    public long TOTAL_MARKS = 0;
    public long MARKS_OBTAINED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_marsheet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ShowMarkSheetActivity_toolbar);
        swipe_refresh = findViewById(R.id.ShowMarkSheetActivity_refresh_layout);

        roll_no = getIntent().getStringExtra(ROLL_NUMBER);
        setToolbar(toolbar, "");

        setRec();

        swipe_refresh.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(false);
                setProfileDetails();
            }
        });
        setProfileDetails();


    }

    private void setMarksData() {
        firestore.collection(EXAMS_RESULTS)
                .document("1596396470064")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            if (documentSnapshot.exists()) {
                                marksList.clear();
                                MARKS_OBTAINED = 0;
                                TOTAL_MARKS = 0;
                                HashMap<String, Object> marks = (HashMap<String, Object>) documentSnapshot.get("marks");
                                if (marks == null)
                                    marks = new HashMap<>();
                                for (int a = 0; a < marks.size(); a++) {
                                    MarksModel marksModel = new MarksModel();
                                    marksModel.setFull_marks(documentSnapshot.getString(FULL_MARKS));
                                    marksModel.setSubject(getSubKey(a));
                                    marksModel.setPass_marks(documentSnapshot.getString(PASSING_MARKS));
                                    marksModel.setMarks((Long) marks.get(getSubKey(a)));
                                    marksModel.setResult(documentSnapshot.getString(RESULT));
                                    marksList.add(marksModel);

                                    MARKS_OBTAINED = MARKS_OBTAINED + (Long) marks.get(getSubKey(a));
                                }
                                long full_marks = Long.parseLong(documentSnapshot.getString(FULL_MARKS));
                                TOTAL_MARKS = marks.size() * full_marks;
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(ShowMarkSheetActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ShowMarkSheetActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowMarkSheetActivity.this, "failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSubKey(int a) {
        List<String> subjects = new ArrayList<>();
        subjects.add(ENGLISH);
        subjects.add(HINDI);
        subjects.add(MATH);
        subjects.add(SCI);
        subjects.add(COMPUTER);
        subjects.add(ART);

        Collections.sort(subjects);
        return subjects.get(a);
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
        getSupportActionBar().setTitle("Result");

    }

    private void setRec() {
        progressBar = findViewById(R.id.ShowMarkSheetActivity_progressBar);
        rec = findViewById(R.id.ShowMarkSheetActivity_rec);
        rec.setLayoutManager(new LinearLayoutManager(ShowMarkSheetActivity.this));
        progressBar.setVisibility(View.VISIBLE);
        adapter = new ShowMarkSheetActivityAdapter(usersSnaps, marksList);
        rec.setAdapter(adapter);
        setProfileDetails();
    }

    public class ShowMarkSheetActivityAdapter extends RecyclerView.Adapter<ShowMarkSheetActivityAdapter.ViewHolder> {
        List<DocumentSnapshot> snapshots;
        List<MarksModel> marksModelList;

        public ShowMarkSheetActivityAdapter(List<DocumentSnapshot> snapshots, List<MarksModel> marksModelList) {
            this.snapshots = snapshots;
            this.marksModelList = marksModelList;
        }


        @NonNull
        @Override
        public ShowMarkSheetActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_mark_sheet_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ShowMarkSheetActivityAdapter.ViewHolder holder, int position) {
            DocumentSnapshot documentSnapshot = snapshots.get(position);
            holder.name.setText(documentSnapshot.getString(NAME));
            holder.fatherName.setText(documentSnapshot.getString(SON_OF));
            holder.Class.setText("Class " + documentSnapshot.getString(_CLASS));

           /* holder.total_marks.setText(TOTAL_MARKS + "");
            holder.marks_obtained_tv.setText(MARKS_OBTAINED + "");
            double percentage = (MARKS_OBTAINED * 100) / TOTAL_MARKS;
            holder.percentage_tv.setText(percentage + "%");
            holder.total_marks.setText(percentage >= 45 ? PASS : FAIL);
            if (percentage < 45) {
                holder.percentage_tv.setTextColor(Color.RED);
                holder.result_tv.setTextColor(Color.RED);
            } else {
                holder.percentage_tv.setTextColor(Color.GREEN);
                holder.result_tv.setTextColor(Color.GREEN);
            }*/

            final String image = documentSnapshot.getString(IMAGE);
            if (null != image && !image.equalsIgnoreCase(""))
                Picasso.with(ShowMarkSheetActivity.this).load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                        .into(holder.image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ShowMarkSheetActivity.this).load(image)
                                        .into(holder.image);
                            }
                        });


            Toast.makeText(ShowMarkSheetActivity.this, ""+marksModelList.size(), Toast.LENGTH_SHORT).show();
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(ShowMarkSheetActivity.this));
            holder.adapter2 = new ShowMarkSheetActivityAdapter2(marksModelList);
            holder.recyclerView.setAdapter(holder.adapter2);
            holder.adapter2.notifyDataSetChanged();




        }

        @Override
        public int getItemCount() {
            return snapshots.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerView recyclerView;
            TextView name, fatherName, Class, total_marks, marks_obtained_tv, result_tv, percentage_tv;
            CircleImageView image;
            ShowMarkSheetActivityAdapter2 adapter2;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                recyclerView = itemView.findViewById(R.id.show_mark_sheet_rec_2);
                name = itemView.findViewById(R.id.textView13);
                fatherName = itemView.findViewById(R.id.textView14);
                result_tv = itemView.findViewById(R.id.result_tv);
                total_marks = itemView.findViewById(R.id.textView18);
                percentage_tv = itemView.findViewById(R.id.percentage_tv);
                marks_obtained_tv = itemView.findViewById(R.id.marks_obtained_tv);
                Class = itemView.findViewById(R.id.textView15);
                image = itemView.findViewById(R.id.ShowMarkSheetActivity_imageView);

            }
        }

        public void addData(DocumentSnapshot snapshot) {
            snapshots.add(snapshot);
            notifyDataSetChanged();
        }
    }

    private void setProfileDetails() {

        firestore.collection(STUDENT_QUERY)
                .document(roll_no)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Toast.makeText(ShowMarkSheetActivity.this, "user not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        usersSnaps.clear();
                        usersSnaps.add(documentSnapshot);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        setMarksData();


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowMarkSheetActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private class ShowMarkSheetActivityAdapter2 extends RecyclerView.Adapter<ShowMarkSheetActivityAdapter2.ViewHolder> {
        List<MarksModel> marksModelList;


        public ShowMarkSheetActivityAdapter2(List<MarksModel> marksModelList) {
            this.marksModelList = marksModelList;
        }

        @NonNull
        @Override
        public ShowMarkSheetActivityAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_mark_sheet_view2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowMarkSheetActivityAdapter2.ViewHolder holder, int position) {

            MarksModel marksModel = marksModelList.get(position);

            try {
                String subject = marksModel.getSubject();
                holder.sub.setText(subject);

                String max_marks = marksModel.getFull_marks();
                holder.max_marks.setText(max_marks);

                long marks_obtained = marksModel.getMarks();
                holder.marks_obtained.setText(marks_obtained + "");


                String passingMarks = marksModel.getPass_marks();
                holder.passing_marks.setText(passingMarks);

                if (marks_obtained <= Long.parseLong(passingMarks)) {
                    holder.result.setText(FAIL);
                    holder.result.setTextColor(getResources().getColor(R.color.red));
                    holder.marks_obtained.setTextColor(getResources().getColor(R.color.red));
                } else {
                    holder.result.setText(PASS);
                    holder.result.setTextColor(getResources().getColor(R.color.green2));
                    holder.marks_obtained.setTextColor(getResources().getColor(R.color.green2));
                }

            } catch (Exception e) {

                e.printStackTrace();
                Toast.makeText(ShowMarkSheetActivity.this, "error in adapter 2" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        private void setResult(MarksModel marksModel, ViewHolder holder) {

        }

        @Override
        public int getItemCount() {
            return marksModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView sub, max_marks, marks_obtained, result, passing_marks;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                sub = itemView.findViewById(R.id.textView17);
                max_marks = itemView.findViewById(R.id.max_marks);
                marks_obtained = itemView.findViewById(R.id.marks_obtained);
                result = itemView.findViewById(R.id.result);
                passing_marks = itemView.findViewById(R.id.passing_marks);
            }
        }
    }

}