package com.example.sma;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.sma.Student.StudentProfile;
import com.example.sma.Util.Utils;
import com.example.sma.model.Attendance;
import com.example.sma.model.FeeModel;
import com.example.sma.model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sma.Util.Utils.*;

public class StudentsActivity extends AppCompatActivity {


    private RecyclerView studentRec;
    private RecyclerRefreshLayout swipe_refresh;
    private StudentAdapter studentAdapter;
    List<DocumentSnapshot> allStudentList = new ArrayList<>();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    int Year, Month, Day;
    String Class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        Toolbar toolbar = (Toolbar) findViewById(R.id.stu_toolbar);
        setToolbar(toolbar, "All Students");


        Class = getIntent().getStringExtra("Class");


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH) + 1;
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        swipe_refresh = (RecyclerRefreshLayout) findViewById(R.id.stu_refresh_layout);
        studentRec = (RecyclerView) findViewById(R.id.stu_rec);
        studentRec.setHasFixedSize(true);
        studentRec.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(allStudentList, this);
        studentRec.setAdapter(studentAdapter);
        loadAllStudentData();


        swipe_refresh.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //setData();
                loadAllStudentData();
            }
        });

    }

    private void loadAllStudentData() {

        showAlertDialog(this);
        firestore.collection(STUDENT_QUERY)
                .whereEqualTo(YEAR, Year + "")
                .whereEqualTo(_CLASS, Class)
                /*.document("" + Year)
                .collection(Class)*/
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        allStudentList.clear();
                        if (swipe_refresh != null)
                            swipe_refresh.setRefreshing(false);

                        hideAlertDialog();

                        if (queryDocumentSnapshots.isEmpty()) {
                            return;
                        }
                        allStudentList.addAll(queryDocumentSnapshots.getDocuments());
                        studentAdapter.notifyDataSetChanged();
                        if (!allStudentList.isEmpty())
                            getSupportActionBar().setTitle("Students(" + allStudentList.size() + ")");
                    }
                });
    }

    private void setData() {
        Student student = new Student();


        for (int Class = 1; Class <= 10; Class++) {
            for (int stu = 1; stu <= 10; stu++) {
                long roll_no = System.currentTimeMillis();
                student.setRoll_no(roll_no);
                student.set_class(Class + "");
                student.setYear(Year + "");
                student.setSon_of("");
                student.setTeacherName("");
                student.setImage("");
                firestore.collection(STUDENT_QUERY)
                        .document(roll_no + "")
                        .set(student);
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Students(0)");

    }

    private class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyHolder> {
        List<DocumentSnapshot> list;
        Context context;
        Dialog dialog;

        public StudentAdapter(List<DocumentSnapshot> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public StudentAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_student_view2, parent, false);
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.profile_dialog_view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final StudentAdapter.MyHolder holder, int position) {

            final DocumentSnapshot snapshot = list.get(position);
            holder.name.setText(snapshot.getString(NAME));
            final String image = snapshot.getString(IMAGE);
            if (image != null && !image.equalsIgnoreCase(""))
                Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(image).into(holder.userImage);
                    }
                });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfileDialog(snapshot);
                    addFee(snapshot);
                }
            });
        }

        private void showProfileDialog(final DocumentSnapshot snapshot) {
            final TextView mStuName = dialog.findViewById(R.id.s_name);
            TextView mStuRollNumber = dialog.findViewById(R.id.s_roll_number);
            TextView mStuFatherName = dialog.findViewById(R.id.s_father_name);
            final CircleImageView mStuProfile = dialog.findViewById(R.id.s_profile);
            final Button mViewProfile = dialog.findViewById(R.id.view_stdent_profile_btn);
            Button dismissBtn = dialog.findViewById(R.id.dismiss_btn);

            if (snapshot.contains(PERSONAL_DATA)) {
                Map<String, Object> map = (Map<String, Object>) snapshot.get(PERSONAL_DATA);
                mStuFatherName.setText("S/O " + (String) map.get(FATHER_NAME));
            } else mStuFatherName.setText("S/O");
            mStuName.setText(snapshot.getString(NAME));

            mStuRollNumber.setText(getResources().getString(R.string.roll_number) + ":" + snapshot.getLong(ROLL_NUMBER));
            final String image = snapshot.getString(IMAGE);
            if (image != null && !image.equals(""))
                Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(mStuProfile, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(image).placeholder(R.drawable.profile).into(mStuProfile);
                    }
                });

            else
                mStuProfile.setImageResource(R.drawable.profile);

            mViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivity(new Intent(StudentsActivity.this, StudentProfile.class)
                            .putExtra(STU_ID, snapshot.getId()));
                }
            });

            dismissBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private CircleImageView userImage;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.all_stu_name);
                userImage = (CircleImageView) itemView.findViewById(R.id.all_stu_profile);
            }
        }
    }

    private void addFee(DocumentSnapshot snapshot) {
        Map<String, Object> map = new HashMap<>();
        map.put(CLASS, snapshot.getString(_CLASS));
        map.put(ROLL_NUMBER, snapshot.getLong(ROLL_NUMBER) + "");
        map.put(FEE_STATUS, getFeeStatus());
        map.put("image_reference", snapshot.getReference());
        map.put("reference2", snapshot.getDocumentReference(TEACHERS));
        map.put(NAME, snapshot.getString(NAME));
        firestore.collection(FEE_COLLECTION_QUERY).document(snapshot.getLong(ROLL_NUMBER) + "")
                .set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(StudentsActivity.this, "Added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Object getFeeStatus() {
        List<FeeModel> integers = new ArrayList<>();
        for (int a = 0; a < 12; a++) {
            integers.add(new FeeModel(1500, Month, Year, System.currentTimeMillis(), 1200, 2));
        }
        return integers;
    }

    private void createFeeList(String mStuId) {
        firestore.collection("Students")
                .document(String.valueOf(Year))
                .collection(Class)
                .document(mStuId)
                .collection(FEE)
                .add(getFeeMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(StudentsActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentsActivity.this, "Failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
