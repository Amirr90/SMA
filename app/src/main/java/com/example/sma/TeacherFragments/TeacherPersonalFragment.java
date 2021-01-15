package com.example.sma.TeacherFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.sma.Adapter.ProfileAdapter;
import com.example.sma.R;
import com.example.sma.StudentsActivity;
import com.example.sma.model.PersonalDetailModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.sma.Util.Utils.BLOOD_GROUP;
import static com.example.sma.Util.Utils.DATE_OF_BIRTH;
import static com.example.sma.Util.Utils.EMAIL;
import static com.example.sma.Util.Utils.FATHER_NAME;
import static com.example.sma.Util.Utils.MOTHER_NAME;
import static com.example.sma.Util.Utils.PERMANENT_ADDRESS;
import static com.example.sma.Util.Utils.PERSONAL_DATA;
import static com.example.sma.Util.Utils.PHONE_NUMBER;
import static com.example.sma.Util.Utils.PRESENT_ADDRESS;
import static com.example.sma.Util.Utils.RELIGION;

public class TeacherPersonalFragment extends Fragment {

    String TeacherId;
    private RecyclerView recyclerView;
    private RecyclerRefreshLayout swipe_refresh;
    List<PersonalDetailModel> profileData = new ArrayList<>();
    ProfileAdapter profileAdapter;

    DocumentSnapshot snapshot;


    public TeacherPersonalFragment(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_teacher, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_personal_rec);


        swipe_refresh = (RecyclerRefreshLayout) view.findViewById(R.id.fragment_personal_refresh_layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        profileAdapter = new ProfileAdapter(profileData, getActivity());
        recyclerView.setAdapter(profileAdapter);
        loadData();


        swipe_refresh.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //setData();
                loadData();
            }
        });


        return view;
    }

    private void loadData() {

        profileData.clear();

        if (!snapshot.contains(PERSONAL_DATA)) {
            profileData.add(new PersonalDetailModel(getString(R.string.date_of_birth), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.religion), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.phone_number), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.email_address), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.present_address), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.permanent_address), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.father_name), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.mother_name), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.blood_group), ""));
            profileAdapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
            return;
        }
        try {
            Map<String, Object> personal = (Map<String, Object>) snapshot.get(PERSONAL_DATA);

            profileData.add(new PersonalDetailModel(getString(R.string.date_of_birth), (String) personal.get(DATE_OF_BIRTH)));
            profileData.add(new PersonalDetailModel(getString(R.string.religion), (String) personal.get(RELIGION)));
            profileData.add(new PersonalDetailModel(getString(R.string.phone_number), (String) personal.get(PHONE_NUMBER)));
            profileData.add(new PersonalDetailModel(getString(R.string.email_address), (String) personal.get(EMAIL)));
            profileData.add(new PersonalDetailModel(getString(R.string.present_address), (String) personal.get(PRESENT_ADDRESS)));
            profileData.add(new PersonalDetailModel(getString(R.string.permanent_address), (String) personal.get(PERMANENT_ADDRESS)));
            profileData.add(new PersonalDetailModel(getString(R.string.father_name), (String) personal.get(FATHER_NAME)));
            profileData.add(new PersonalDetailModel(getString(R.string.mother_name), (String) personal.get(MOTHER_NAME)));
            profileData.add(new PersonalDetailModel(getString(R.string.blood_group), (String) personal.get(BLOOD_GROUP)));
            profileAdapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            profileAdapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
        }
    }

}