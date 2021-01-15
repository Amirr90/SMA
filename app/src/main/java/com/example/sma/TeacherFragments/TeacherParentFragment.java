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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.sma.Util.Utils.FATHER_MOBILE;
import static com.example.sma.Util.Utils.FATHER_OCCUPATION;
import static com.example.sma.Util.Utils.MOTHER_MOBILE;
import static com.example.sma.Util.Utils.MOTHER_OCCUPATION;
import static com.example.sma.Util.Utils.PARENT_DATA;
import static com.example.sma.Util.Utils.PERSONAL_DATA;

public class TeacherParentFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerRefreshLayout swipe_refresh;
    List<PersonalDetailModel> profileData = new ArrayList<>();
    ProfileAdapter profileAdapter;

    DocumentSnapshot snapshot;

    public TeacherParentFragment(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_parent, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_parent_rec);

        swipe_refresh = (RecyclerRefreshLayout) view.findViewById(R.id.fragment_parent_refresh_layout);
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
        if (!snapshot.contains(PARENT_DATA)) {
            profileData.add(new PersonalDetailModel(getString(R.string.father_mobile), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.mother_mobile), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.father_occupation), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.mother_occupation), ""));
            profileAdapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
            return;
        }

      try {
          Map<String, Object> personal = (Map<String, Object>) snapshot.get(PARENT_DATA);
          profileData.add(new PersonalDetailModel(getString(R.string.father_mobile), (String) personal.get(FATHER_MOBILE)));
          profileData.add(new PersonalDetailModel(getString(R.string.mother_mobile), (String) personal.get(MOTHER_MOBILE)));
          profileData.add(new PersonalDetailModel(getString(R.string.father_occupation), (String) personal.get(FATHER_OCCUPATION)));
          profileData.add(new PersonalDetailModel(getString(R.string.mother_occupation), (String) personal.get(MOTHER_OCCUPATION)));
          profileAdapter.notifyDataSetChanged();
          swipe_refresh.setRefreshing(false);
      } catch (Exception e) {
          e.printStackTrace();
          profileAdapter.notifyDataSetChanged();
          swipe_refresh.setRefreshing(false);
      }
    }
}