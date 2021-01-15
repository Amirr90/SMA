package com.example.sma;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sma.Util.Utils.CLASS;
import static com.example.sma.Util.Utils.CLASS_ROUTINE_QUERY;
import static com.example.sma.Util.Utils.DAY;
import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.TEACHER_ID;
import static com.example.sma.Util.Utils.getDay;
import static com.example.sma.Util.Utils.getRoomList;
import static com.example.sma.Util.Utils.getSubjectList;
import static com.example.sma.Util.Utils.getTimingList;
import static com.example.sma.Util.Utils.getWeekDaysList;
import static com.example.sma.Util.Utils.setTeacherProfile;
import static com.example.sma.Util.Utils.showAlertDialog;
import static com.example.sma.Util.Utils.hideAlertDialog;

public class ClassRoutineActivity extends AppCompatActivity {

    private static final String TAG = "ClassRoutineActivity";
    RecyclerView rec;
    List<String> weekdays;
    ClassRoutineAdapter1 adapter1;
    ClassRoutineAdapter2 adapter2;
    public static CircleImageView mCircleImageView;
    String image, name, Class, TeacherId;
    TextView mTitle_tv, mNoDataTv;
    RecyclerRefreshLayout refreshLayout;
    ArrayList<ArrayList<ArrayList<String>>> timingListA = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_routine);

        Toolbar toolbar = (Toolbar) findViewById(R.id.class_routine_toolbar);
        mCircleImageView = toolbar.findViewById(R.id.t_profile2);
        mNoDataTv = findViewById(R.id.textView19);
        mTitle_tv = toolbar.findViewById(R.id.textView10);


        image = getIntent().getStringExtra(IMAGE);
        name = getIntent().getStringExtra("Name");
        Class = getIntent().getStringExtra("Class");
        TeacherId = getIntent().getStringExtra(TEACHER_ID);


        refreshLayout = findViewById(R.id.ClassRoutineActivity_refresh_lay);
        mTitle_tv.setText(name);
        setToolbar(toolbar, name);
        setTeacherProfile(this, mCircleImageView, image);
        weekdays = getWeekDaysList();


        setRec1();


        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //setClassRoutineData();
                loadClassRoutineData();
            }
        });


    }

    private void setClassRoutineData() {

        for (int a = 0; a < 6; a++) {
            Map<String, Object> map = new HashMap<>();
            map.put(CLASS, Class);
            map.put(DAY, getDay(a));
            map.put("timings", getTimingList());
            map.put("subjects", getSubjectList());
            map.put("rooms", getRoomList());
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(CLASS_ROUTINE_QUERY).document().set(map).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });

            if (a == 5) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ClassRoutineActivity.this, "Class Routine Data Added Successfully", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private void setRec1() {
        rec = findViewById(R.id.client_rec_view1);
        rec.setLayoutManager(new LinearLayoutManager(this));
        rec.setHasFixedSize(true);
        adapter1 = new ClassRoutineAdapter1(timingListA);
        rec.setAdapter(adapter1);
        loadClassRoutineData();
    }

    private void loadClassRoutineData() {

        showAlertDialog(this);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(CLASS_ROUTINE_QUERY)
                .whereEqualTo(CLASS, Class)
                .orderBy(DAY, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideAlertDialog();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            mNoDataTv.setVisibility(View.GONE);
                            rec.setVisibility(View.VISIBLE);


                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                ArrayList<ArrayList<String>> classRoutineList = new ArrayList<>();
                                ArrayList<String> timingList = ((ArrayList<String>) snapshot.get("timings"));
                                ArrayList<String> subjectsList = (ArrayList<String>) snapshot.get("subjects");
                                ArrayList<String> roomList = (ArrayList<String>) snapshot.get("rooms");
                                classRoutineList.add(timingList);
                                classRoutineList.add(subjectsList);
                                classRoutineList.add(roomList);
                                timingListA.add(classRoutineList);

                            }

                            adapter1.notifyDataSetChanged();
                        } else {
                            mNoDataTv.setVisibility(View.VISIBLE);
                            rec.setVisibility(View.GONE);
                            hideAlertDialog();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideAlertDialog();
                mNoDataTv.setVisibility(View.VISIBLE);
                rec.setVisibility(View.GONE);
                Log.d(TAG, "error " + e.getLocalizedMessage());
            }
        });

        // adapter1.notifyDataSetChanged();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);

    }

    private class ClassRoutineAdapter1 extends RecyclerView.Adapter<ClassRoutineAdapter1.ViewHolder> {

        ArrayList<ArrayList<ArrayList<String>>> Timing;
        ArrayList<String> timings;
        ArrayList<String> subjects;
        ArrayList<String> rooms;

        public ClassRoutineAdapter1(ArrayList<ArrayList<ArrayList<String>>> Timing) {
            this.Timing = Timing;

        }

        @NonNull
        @Override
        public ClassRoutineAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_routine_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassRoutineAdapter1.ViewHolder holder, int position) {

            try {
                holder.textView.setText(weekdays.get(position));
                timings = Timing.get(position).get(0);
                subjects = Timing.get(position).get(1);
                rooms = Timing.get(position).get(2);
                holder.recyclerView.setAdapter(new ClassRoutineAdapter2(timings, subjects, rooms));
                //loadAdapter2();
            } catch (Exception e) {
            }
        }

        @Override
        public int getItemCount() {
            return 6;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerView recyclerView;
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                recyclerView = itemView.findViewById(R.id.class_rec_2);
                textView = itemView.findViewById(R.id.textView5);
                recyclerView.setLayoutManager(new LinearLayoutManager(ClassRoutineActivity.this));
                recyclerView.setHasFixedSize(true);

            }
        }
    }

    private void loadAdapter2() {
        adapter2.notifyDataSetChanged();
    }

    private class ClassRoutineAdapter2 extends RecyclerView.Adapter<ClassRoutineAdapter2.ViewHolder> {

        ArrayList<String> timingList;
        ArrayList<String> subjectsList;
        ArrayList<String> roomsList;

        public ClassRoutineAdapter2(ArrayList<String> timingList, ArrayList<String> subjectsList, ArrayList<String> roomsList) {
            this.timingList = timingList;
            this.subjectsList = subjectsList;
            this.roomsList = roomsList;
        }

        @NonNull
        @Override
        public ClassRoutineAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_routine_view2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassRoutineAdapter2.ViewHolder holder, int position) {

            holder.timing.setText(timingList.get(position));
            holder.subjects.setText(subjectsList.get(position));
            holder.rooms.setText(roomsList.get(position));
        }

        @Override
        public int getItemCount() {
            return timingList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView timing, subjects, rooms;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                timing = itemView.findViewById(R.id.timingTv);
                subjects = itemView.findViewById(R.id.subjectsTv);
                rooms = itemView.findViewById(R.id.roomsTv);
            }
        }
    }
}