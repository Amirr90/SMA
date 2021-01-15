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
import java.util.Map;

import static com.example.sma.Util.Utils.BUS_FEE;
import static com.example.sma.Util.Utils.BUS_NUMBER;
import static com.example.sma.Util.Utils.BUS_ROUTE;
import static com.example.sma.Util.Utils.DRIVER_NAME;
import static com.example.sma.Util.Utils.PARENT_DATA;
import static com.example.sma.Util.Utils.TRANSPORT_DATA;

public class TeacherTransportFragment extends Fragment {

    String TeacherId;
    private RecyclerView recyclerView;
    private RecyclerRefreshLayout swipe_refresh;
    List<PersonalDetailModel> profileData = new ArrayList<>();
    ProfileAdapter profileAdapter;


    DocumentSnapshot snapshot;

    public TeacherTransportFragment(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_transport, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_transport_parent_rec);

        swipe_refresh = (RecyclerRefreshLayout) view.findViewById(R.id.fragment_transport_refresh_layout);
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
            profileData.add(new PersonalDetailModel(getString(R.string.bus_number), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.bus_route), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.bus_fee), ""));
            profileData.add(new PersonalDetailModel(getString(R.string.driver_name), ""));
            profileAdapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
            return;
        }

        try {
            Map<String, Object> personal = (Map<String, Object>) snapshot.get(TRANSPORT_DATA);
            profileData.add(new PersonalDetailModel(getString(R.string.bus_number), (String) personal.get(BUS_NUMBER)));
            profileData.add(new PersonalDetailModel(getString(R.string.bus_route), (String) personal.get(BUS_ROUTE)));
            profileData.add(new PersonalDetailModel(getString(R.string.bus_fee), (String) personal.get(BUS_FEE) + " Yearly"));
            profileData.add(new PersonalDetailModel(getString(R.string.driver_name), (String) personal.get(DRIVER_NAME)));
            profileAdapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
            profileAdapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
        }

    }
}