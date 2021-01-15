package com.example.sma.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sma.Adapter.PagerAdapter;
import com.example.sma.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sma.Util.Utils.CLASS;
import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.NAME;
import static com.example.sma.Util.Utils.OTHERS;
import static com.example.sma.Util.Utils.PARENTS;
import static com.example.sma.Util.Utils.PERSONAL;
import static com.example.sma.Util.Utils.ROLL_NUMBER;
import static com.example.sma.Util.Utils.STUDENT_QUERY;
import static com.example.sma.Util.Utils.STU_ID;
import static com.example.sma.Util.Utils.TRANSPORT;
import static com.example.sma.Util.Utils.hideAlertDialog;
import static com.example.sma.Util.Utils.showAlertDialog;

public class StudentProfile extends AppCompatActivity {

    String StudentId;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.StudentProfile_profile_toolbar);
        setToolbar(toolbar, "Student Profile");

        StudentId = getIntent().getStringExtra(STU_ID);
        loadStudentData();


    }

    private void loadStudentData() {
        showAlertDialog(StudentProfile.this);
        firestore.collection(STUDENT_QUERY).document(StudentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        hideAlertDialog();
                        if (snapshot.exists()) {
                            setProfileInfo(snapshot);
                            setFragments(snapshot);
                        } else {
                            Toast.makeText(StudentProfile.this, "Student not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideAlertDialog();
                Toast.makeText(StudentProfile.this, getString(R.string.failed_to_get), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setProfileInfo(DocumentSnapshot snapshot) {

        final String image, name, Class, RollNo;
        final CircleImageView imageView = findViewById(R.id.StudentProfile_profile);
        TextView tName = findViewById(R.id.textView7);
        TextView tClass = findViewById(R.id.textView8);
        TextView tRollNo = findViewById(R.id.textView9);

        image = snapshot.getString(IMAGE);
        name = snapshot.getString(NAME);
        Class = snapshot.getString(CLASS);


        if (image != null && !image.equals("")) {
            Picasso.with(this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(StudentProfile.this)
                                    .load(image).placeholder(R.drawable.profile)
                                    .into(imageView);
                        }
                    });
        }

        tName.setText(name);
        tClass.setText("Class: " + Class + " | Section A");
        tRollNo.setText(getString(R.string.roll_number) + ":" + snapshot.getLong(ROLL_NUMBER));

    }

    private void setFragments(DocumentSnapshot snapshot) {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.StudentProfile_item_tabs);
        tabLayout.addTab(tabLayout.newTab().setText(PERSONAL));
        tabLayout.addTab(tabLayout.newTab().setText(PARENTS));
        tabLayout.addTab(tabLayout.newTab().setText(TRANSPORT));
        tabLayout.addTab(tabLayout.newTab().setText(OTHERS));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.StudentProfile_pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), StudentId, snapshot);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
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
}