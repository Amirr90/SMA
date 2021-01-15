package com.example.sma;

import android.app.AlertDialog;
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
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.sma.Student.StudentProfile;
import com.example.sma.model.Attendance;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

import static com.example.sma.Util.Utils.ATTENDANCE;
import static com.example.sma.Util.Utils.DAY;
import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.MONTH;
import static com.example.sma.Util.Utils.NAME;
import static com.example.sma.Util.Utils.PRESENT;
import static com.example.sma.Util.Utils.STU_ID;
import static com.example.sma.Util.Utils.hideAlertDialog;

public class AttendanceHistory extends AppCompatActivity {

    CalendarView calendarView;
    RelativeLayout calenderLay, RecLay;

    int mMonth, mDay, mYear, present = 0, absent = 0;

    RecyclerView recyclerView;
    List<Attendance> attendanceList = new ArrayList<>();
    AttendanceHistoryAdapter adapter;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id._at_hi_toolbar);
        setToolbar(toolbar, "Select Date");


        calendarView = findViewById(R.id.calendarView);
        calenderLay = (RelativeLayout) findViewById(R.id.calender_lay);
        RecLay = (RelativeLayout) findViewById(R.id.rec_lay);

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                mMonth = month + 1;
                mYear = year;
                mDay = dayOfMonth;
                String date = "" + mDay + "/" + mMonth + "/" + year;
                Toast.makeText(AttendanceHistory.this, date, Toast.LENGTH_SHORT).show();
            }
        });

        setRowCalender();

        recyclerView = (RecyclerView) findViewById(R.id.a_h_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new AttendanceHistoryAdapter(attendanceList, this);
        recyclerView.setAdapter(adapter);

    }

    private void setRowCalender() {

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.row_calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                //do something
                int mYear = date.get(Calendar.YEAR);
                int mMonth = date.get(Calendar.MONTH) + 1;
                int mDay = date.get(Calendar.DAY_OF_MONTH);


              /*  String datee = "" + mDay + "/" + mMonth + "/" + mYear;
                Toast.makeText(AttendanceHistory.this, datee, Toast.LENGTH_SHORT).show();*/
                hideAlertDialog();
                loadData(mYear, mMonth, mDay);

            }
        });
        horizontalCalendar.centerCalendarToPosition(3);
        horizontalCalendar.positionOfDate(startDate);
    }

    public void showAttendanceActivity(View v) {
        calenderLay.setVisibility(View.GONE);
        hideAlertDialog();
        loadData(mYear, mMonth, mDay);

    }

    private void loadData(int mYear, int mMonth, int mDay) {
        present = absent = 0;
        final String date = mDay + "-" + mMonth + "-" + mYear;
        String cla = "Class " + getIntent().getStringExtra("CLASS");
        firestore.collection(ATTENDANCE)
                .document(String.valueOf(mYear))
                .collection(cla)
                .whereEqualTo(MONTH, mMonth)
                .whereEqualTo(DAY, mDay)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideAlertDialog();
                        if (queryDocumentSnapshots.getDocuments().size() == 0) {
                            Toast.makeText(AttendanceHistory.this, "No Data Found", Toast.LENGTH_SHORT).show();
                            calenderLay.setVisibility(View.VISIBLE);
                            RecLay.setVisibility(View.GONE);
                            getSupportActionBar().setTitle("Select Date");
                            return;
                        }

                        attendanceList.clear();
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        List<Map<String, Object>> mapList = (List<Map<String, Object>>) snapshot.get(ATTENDANCE);
                        for (int a = 0; a < mapList.size(); a++) {
                            String name = mapList.get(a).get(NAME).toString();
                            Boolean status = (Boolean) mapList.get(a).get(PRESENT);
                            String image = (String) mapList.get(a).get(IMAGE);
                            attendanceList.add(new Attendance(name, status, image));
                            if (status != null) {
                                if (status)
                                    present++;
                                else
                                    absent++;
                            }

                        }

                        TextView mPresent = (TextView) findViewById(R.id.present_tv);
                        TextView mAbsent = (TextView) findViewById(R.id.absent_tv);
                        TextView mTotal = (TextView) findViewById(R.id.total_tv);

                        mPresent.setText(present + " Presents");
                        mAbsent.setText(absent + " Absent");
                        mTotal.setText(attendanceList.size() + " Total");

                        RecLay.setVisibility(View.VISIBLE);
                        getSupportActionBar().setTitle("Date: " + date);
                        adapter.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AttendanceHistory.this, "No data found,try again", Toast.LENGTH_SHORT).show();
                calenderLay.setVisibility(View.VISIBLE);
                RecLay.setVisibility(View.GONE);
                getSupportActionBar().setTitle("Select Date");
            }
        });
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
        getSupportActionBar().setTitle(id);
    }

    private class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistory.MyHolder> {
        List<Attendance> list;
        Context context;
        Dialog dialog;

        public AttendanceHistoryAdapter(List<Attendance> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public AttendanceHistory.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_student_view3, parent, false);
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.profile_dialog_view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final AttendanceHistory.MyHolder holder, final int position) {

            holder.textView.setText(list.get(position).getName());
            Boolean isPresent = list.get(position).getPresent();
            if (isPresent)
                holder.imageView.setBorderColor(context.getResources().getColor(R.color.colorPrimaryDark));
            else
                holder.imageView.setBorderColor(Color.RED);

            final String image = list.get(position).getImage();
            if (image != null && !image.equalsIgnoreCase(""))
                Picasso.with(context).load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.profile)
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(context).load(image).placeholder(R.drawable.profile).into(holder.imageView);
                            }
                        });


            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfileDialog(list.get(position));
                }
            });
        }

        private void showProfileDialog(Attendance attendance) {
            TextView mStuName = (TextView) dialog.findViewById(R.id.s_name);
            TextView mStuRollNumber = (TextView) dialog.findViewById(R.id.s_roll_number);
            TextView mStuFatherName = (TextView) dialog.findViewById(R.id.s_father_name);
            final CircleImageView mStuProfile = (CircleImageView) dialog.findViewById(R.id.s_profile);
            final Button mViewProfile = (Button) dialog.findViewById(R.id.view_stdent_profile_btn);
            Button dismissBtn = (Button) dialog.findViewById(R.id.dismiss_btn);

            mStuFatherName.setVisibility(View.GONE);
            mStuName.setText(attendance.getName());
            final String image = attendance.getImage();
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
                    /*startActivity(new Intent(AttendanceHistory.this, StudentProfile.class)
                            .putExtra(STU_ID, snapshot.getId()));*/
                }
            });

            mViewProfile.setOnClickListener(new View.OnClickListener() {
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
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CircleImageView imageView;
        private RelativeLayout layout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.card_Home);
            textView = (TextView) itemView.findViewById(R.id.textView3);
            imageView = (CircleImageView) itemView.findViewById(R.id.home_rec_imageView3);
        }
    }
}
