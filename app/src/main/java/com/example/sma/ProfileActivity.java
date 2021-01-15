package com.example.sma;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.sma.Adapter.PagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.OTHERS;
import static com.example.sma.Util.Utils.PARENTS;
import static com.example.sma.Util.Utils.PERSONAL;
import static com.example.sma.Util.Utils.TEACHER_ID;
import static com.example.sma.Util.Utils.TRANSPORT;

public class ProfileActivity extends AppCompatActivity {

    String TeacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.teacher_profile_toolbar);
        setToolbar(toolbar, "Teacher Profile");

        setFragments();
        setProfileInfo();
    }

    private void setProfileInfo() {

        final String image, name, Class, RollNo;
        final CircleImageView imageView = (CircleImageView) findViewById(R.id.t_profile);
        TextView tName = (TextView) findViewById(R.id.textView7);
        TextView tClass = (TextView) findViewById(R.id.textView8);
        TextView tRollNo = (TextView) findViewById(R.id.textView9);

        if (getIntent().hasExtra(IMAGE)) {

            image = getIntent().getStringExtra(IMAGE);
            name = getIntent().getStringExtra("Name");
            Class = getIntent().getStringExtra("Class");
            TeacherId = getIntent().getStringExtra(TEACHER_ID);

            if (image != null && !image.equals("")) {
                Picasso.with(this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this)
                                        .load(image).placeholder(R.drawable.profile)
                                        .into(imageView);
                            }
                        });
            }

            tName.setText(name);
            tClass.setText("Class: " + Class + " | Section A");
            tRollNo.setText("Roll No: 123654789");
        }
    }

    private void setFragments() {

       /* TabLayout tabLayout = (TabLayout) findViewById(R.id.item_tabs);
        tabLayout.addTab(tabLayout.newTab().setText(PERSONAL));
        tabLayout.addTab(tabLayout.newTab().setText(PARENTS));
        tabLayout.addTab(tabLayout.newTab().setText(TRANSPORT));
        tabLayout.addTab(tabLayout.newTab().setText(OTHERS));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), TeacherId,);
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
        });*/
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