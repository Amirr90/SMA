package com.example.sma;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sma.Student.FeeActivity;
import com.example.sma.Util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sma.Util.Utils.ATTENDANCE;
import static com.example.sma.Util.Utils.ATTENDANCE_HISTORY;
import static com.example.sma.Util.Utils.CLASS;
import static com.example.sma.Util.Utils.CLASS_;
import static com.example.sma.Util.Utils.CLASS_ROUTINE;
import static com.example.sma.Util.Utils.EXAMINATION;
import static com.example.sma.Util.Utils.FEE;
import static com.example.sma.Util.Utils.HOMEWORK;
import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.LOG_OUT;
import static com.example.sma.Util.Utils.NAME;
import static com.example.sma.Util.Utils.NOTICEBOARD;
import static com.example.sma.Util.Utils.ONLINE_EXAM_TEST;
import static com.example.sma.Util.Utils.PROFILE;
import static com.example.sma.Util.Utils.STUDENT;
import static com.example.sma.Util.Utils.SUBJECT;
import static com.example.sma.Util.Utils.SYLLABUS;
import static com.example.sma.Util.Utils.TEACHERS;
import static com.example.sma.Util.Utils.hideAlertDialog;
import static com.example.sma.Util.Utils.logout;
import static com.example.sma.Util.Utils.showAlertDialog;


public class MainActivity extends AppCompatActivity {

    private static final String TEACHER_ID = "yL7AxmO05uTwSBic2VZz";
    private static final int REQUEST_CODE_ATTENDANCE_COMPLETE = 101;
    RecyclerView rvParent;
    List<String> list = new ArrayList<>();
    ParentAdapter parentAdapter;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    String name, Class, subject, image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rvParent = (RecyclerView) findViewById(R.id.rv_parent);
        rvParent.setLayoutManager(new GridLayoutManager(this, 3));
        rvParent.setHasFixedSize(true);
        parentAdapter = new ParentAdapter(this, list);

        rvParent.setAdapter(parentAdapter);
        loadData();


        loadTeacherProfile();

    }

    private void loadTeacherProfile() {
        showAlertDialog(this);
        firestore.collection(TEACHERS)
                .document(TEACHER_ID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hideAlertDialog();
                        if (documentSnapshot.exists()) {

                            TextView tName = (TextView) findViewById(R.id.t_name);
                            TextView tClass = (TextView) findViewById(R.id.t_class);
                            final CircleImageView tProfile = (CircleImageView) findViewById(R.id.t_profile_image);

                            name = documentSnapshot.getString(NAME);
                            Class = documentSnapshot.getString(CLASS);
                            subject = documentSnapshot.getString(SUBJECT);
                            image = documentSnapshot.getString(IMAGE);

                            if (name != null)
                                tName.setText(name);
                            if (Class != null)
                                tClass.setText(CLASS_ + Class);
                            if (image != null)
                                Picasso.with(MainActivity.this).load(image)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.profile)
                                        .into(tProfile, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(tProfile);
                                            }
                                        });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideAlertDialog();
            }
        });
    }

    private void loadData() {

        list.add(ATTENDANCE);
        list.add(EXAMINATION);
        list.add(CLASS_ROUTINE);
        list.add(FEE);
        list.add(SYLLABUS);
        list.add(STUDENT);
        list.add(NOTICEBOARD);
        list.add(HOMEWORK);
        list.add(ONLINE_EXAM_TEST);
        list.add(ATTENDANCE_HISTORY);
        list.add(PROFILE);
        list.add(LOG_OUT);


    }

    public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.MyParentViewHolder> {

        Context context;
        List<String> list;
        int[] iconsList = {R.drawable.attendance,
                R.drawable.examination,
                R.drawable.class_routine,
                R.drawable.fee,
                R.drawable.syllabus,
                R.drawable.student,
                R.drawable.noticeboard,
                R.drawable.homework,
                R.drawable.exam,
                R.drawable.attendance_history,
                R.drawable.t_profile,
                R.drawable.t_profile,
        };

        public ParentAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }


        @NonNull
        @Override
        public MyParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_parent_view, parent, false);
            return new MyParentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyParentViewHolder holder, final int position) {
            holder.textView.setText(list.get(position));
            holder.imageView.setImageResource(iconsList[position]);
            holder.cardHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedText = list.get(position);
                    switch (selectedText) {
                        case ATTENDANCE: {
                            Intent intent = new Intent(context, AttendanceActivity.class);
                            intent.putExtra(CLASS, Class);
                            intent.putExtra("TEACHER_NAME", name);
                            startActivityForResult(intent, REQUEST_CODE_ATTENDANCE_COMPLETE);

                        }
                        break;
                        case ATTENDANCE_HISTORY:
                            context.startActivity(new Intent(context, AttendanceHistory.class)
                                    .putExtra("CLASS", Class));
                            break;


                        case FEE:
                            context.startActivity(new Intent(context, FeeActivity.class)
                                    .putExtra(CLASS, Class));
                            break;
                        case EXAMINATION:
                            context.startActivity(new Intent(context, ExaminationActivity.class)
                                    .putExtra(Utils.TEACHER_ID, TEACHER_ID));
                            break;
                        case STUDENT: {
                            context.startActivity(new Intent(context, StudentsActivity.class).putExtra("Class", Class));

                        }
                        break;

                        case CLASS_ROUTINE: {
                            context.startActivity(new Intent(context, ClassRoutineActivity.class)
                                    .putExtra("Class", Class)
                                    .putExtra("Name", name)
                                    .putExtra(Utils.TEACHER_ID, TEACHER_ID)
                                    .putExtra(IMAGE, image));
                        }
                        break;


                        case PROFILE: {
                            context.startActivity(new Intent(context, ProfileActivity.class)
                                    .putExtra("Class", Class)
                                    .putExtra("Name", name)
                                    .putExtra(Utils.TEACHER_ID, TEACHER_ID)
                                    .putExtra(IMAGE, image));

                        }
                        break;
                        case LOG_OUT: {
                            logout(false, MainActivity.this);

                        }
                        break;
                        default:
                            Snackbar.make(v, "coming soon", Snackbar.LENGTH_SHORT).show();

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void addItems(String item) {
            list.add(item);
            notifyDataSetChanged();
        }

        public class MyParentViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout cardHome;
            TextView textView;
            ImageView imageView;


            public MyParentViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.textView);
                cardHome = (RelativeLayout) itemView.findViewById(R.id.card_Home);
                imageView = (ImageView) itemView.findViewById(R.id.home_rec_imageView);

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ATTENDANCE_COMPLETE) {
            if (resultCode == RESULT_OK) {
                showCompleteAttendanceDialog();
            } else if (resultCode == RESULT_CANCELED) {
                showCancelledDialog();
            }
        }
    }

    private void showCancelledDialog() {
        Toast.makeText(this, "Attendance Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void showCompleteAttendanceDialog() {
        Toast.makeText(this, "Attendance Updated successfully", Toast.LENGTH_SHORT).show();
    }
}
