package com.example.sma;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sma.model.Attendance;
import com.example.sma.model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.example.sma.Util.Utils.*;

public class AttendanceActivity extends AppCompatActivity {


    RecyclerView rvParent;
    List<Student> studentList = new ArrayList<>();
    StudentAdapter studentAdapter;
    TextView titleTv;
    ImageView next;

    String Class, tName;
    int Year, Month, Day;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance2);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTv = (TextView) toolbar.findViewById(R.id.title_bar);
        next = (ImageView) toolbar.findViewById(R.id.next_icon);


        if (getIntent().hasExtra(CLASS)) {
            Class = getIntent().getStringExtra(CLASS);
            tName = getIntent().getStringExtra("TEACHER_NAME");

        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH) + 1;
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        setToolbar(toolbar, "Select Student");
        rvParent = (RecyclerView) findViewById(R.id.all_student_for_attendance_rec);
        rvParent.setLayoutManager(new LinearLayoutManager(this));
        rvParent.setHasFixedSize(true);
        studentAdapter = new StudentAdapter(studentList, this);

        rvParent.setAdapter(studentAdapter);
        loadStudentData();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadStudentData() {
        showAlertDialog(this);
        final String cla = "Class " + Class;
        final String year = String.valueOf(Year);
        String month = String.valueOf(Month);
        String day = String.valueOf(Day);

        firestore.collection(ATTENDANCE)
                .document(year)
                .collection(cla)
                .whereEqualTo(MONTH, Month)
                .whereEqualTo(DAY, Day)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            showStudents();

                        } else {
                            new AlertDialog.Builder(AttendanceActivity.this)
                                    .setMessage("Today's Attendance is completed")
                                    .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            onBackPressed();
                                        }
                                    }).setNegativeButton("No,continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    showStudents();
                                }
                            }).setCancelable(false)
                                    .show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideAlertDialog();
                onBackPressed();
            }
        });


    }

    private void showStudents() {
        final String cla = Class;
        final String year = String.valueOf(Year);
        firestore.collection(STUDENT_QUERY)
                .whereEqualTo(YEAR, year + "")
                .whereEqualTo(_CLASS, cla)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideAlertDialog();
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(AttendanceActivity.this, "Data is empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String name = snapshot.getString(NAME);
                            String image = snapshot.getString(IMAGE);
                            studentList.add(new Student(name, false, image));
                        }
                        studentAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AttendanceActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyStudentViewHolder> {

        List<Student> studentList;
        Context context;

        public StudentAdapter(List<Student> studentList, Context context) {
            this.studentList = studentList;
            this.context = context;
        }

        @NonNull
        @Override
        public MyStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_student_view, parent, false);
            return new MyStudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyStudentViewHolder holder, final int position) {
            final boolean isSelected = studentList.get(position).isPresent();

            holder.checkedTextView.setText(studentList.get(position).getName());

            holder.checkedTextView.setChecked(isSelected);

            if (holder.checkedTextView.isChecked()) {
                holder.checkedTextView.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else
                holder.checkedTextView.setBackgroundColor(Color.WHITE);


            holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = studentList.get(position).getName();
                    String image = studentList.get(position).getImage();
                    if (studentList.get(position).isPresent()) {
                        holder.checkedTextView.setChecked(false);
                        studentList.remove(position);
                        studentList.add(position, new Student(name, false, image));
                        updateCheckedCount();
                    } else {
                        holder.checkedTextView.setChecked(true);
                        studentList.remove(position);
                        studentList.add(position, new Student(name, true, image));
                        updateCheckedCount();
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return studentList.size();
        }

        public class MyStudentViewHolder extends RecyclerView.ViewHolder {
            CheckedTextView checkedTextView;

            public MyStudentViewHolder(@NonNull View itemView) {
                super(itemView);
                checkedTextView = (CheckedTextView) itemView.findViewById(R.id.checkedTextView);
            }
        }
    }


    public void startAbsentProcess(View view) {
        final List<Attendance> attendanceList = new ArrayList<>();
        final List<Student> absentList = new ArrayList<>();
        List<Student> presentList = new ArrayList<>();
        int absent = 0;
        for (Student student : studentList) {
            attendanceList.add(new Attendance(student.getName(),
                    student.isPresent(),
                    student.getImage()));
            if (!student.isPresent()) {
                absent++;
                absentList.add(student);
            } else {
                presentList.add(student);
            }
        }
        String title;
        if (absent == 1)
            title = absent + " Student is absent today";
        else if (absent == 0) {
            title = "Congratulation!!!\nYou have 0 absent today";
            new AlertDialog.Builder(AttendanceActivity.this)
                    .setTitle(title)
                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(AttendanceActivity.this, "Proceeding.....", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        } else
            title = absent + " Students are absent today";

        CharSequence absentStudentNameList[] = new CharSequence[absentList.size()];
        for (int a = 0; a < absentList.size(); a++) {
            absentStudentNameList[a] = absentList.get(a).getName();
        }
        new AlertDialog.Builder(AttendanceActivity.this)
                .setTitle(title)
                .setItems(absentStudentNameList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showStudents();
                        updateAttendance(attendanceList);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void updateAttendance(List<Attendance> attendanceList) {
        showAlertDialog(this);
        Map<String, Object> map = new HashMap<>();
        map.put(ATTENDANCE, attendanceList);
        map.put(MONTH, Month);
        map.put(YEAR, Year);
        map.put(DAY, Day);
        map.put(TIMESTAMP, System.currentTimeMillis());
        map.put(TEACHER_NAME, tName);
        String year = String.valueOf(Year);
        String cla = "Class " + Class;
        firestore.collection(ATTENDANCE)
                .document(year)
                .collection(cla)
                .document(String.valueOf(Day))
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideAlertDialog();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideAlertDialog();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

    }


    private void updateCheckedCount() {
        int counter = 0;
        for (Student student : studentList) {
            if (student.isPresent()) {
                counter++;
            }
        }
        if (counter == 0) {
            next.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }
        titleTv.setText("Selected " + counter);
    }
}
