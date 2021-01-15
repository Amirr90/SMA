package com.example.sma.TeacherFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.sma.Adapter.ProfileAdapter;
import com.example.sma.R;
import com.example.sma.model.PersonalDetailModel;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeacherOtherFragment extends Fragment {

    String TeacherId;
    private RecyclerView recyclerView;
    private RecyclerRefreshLayout swipe_refresh;
    List<PersonalDetailModel> profileData = new ArrayList<>();
    ProfileAdapter profileAdapter;

    DocumentSnapshot snapshot;

    public TeacherOtherFragment(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_other, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_other_rec);


        swipe_refresh = (RecyclerRefreshLayout) view.findViewById(R.id.fragment_other_refresh_layout);
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
        profileData.add(new PersonalDetailModel("Other Title ", "Description "));
        profileAdapter.notifyDataSetChanged();

        swipe_refresh.setRefreshing(false);
    }

}