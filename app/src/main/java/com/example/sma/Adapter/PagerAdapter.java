package com.example.sma.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.sma.TeacherFragments.TeacherPersonalFragment;
import com.example.sma.TeacherFragments.TeacherOtherFragment;
import com.example.sma.TeacherFragments.TeacherParentFragment;
import com.example.sma.TeacherFragments.TeacherTransportFragment;
import com.google.firebase.firestore.DocumentSnapshot;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String TeacherId;
    DocumentSnapshot snapshot;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String TeacherId, DocumentSnapshot snapshot) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.TeacherId = TeacherId;
        this.snapshot = snapshot;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TeacherPersonalFragment tab1 = new TeacherPersonalFragment(snapshot);
                return tab1;
            case 1:
                TeacherParentFragment tab2 = new TeacherParentFragment(snapshot);
                return tab2;
            case 2:
                TeacherTransportFragment tab3 = new TeacherTransportFragment(snapshot);
                return tab3;
            case 3:
                TeacherOtherFragment tab4 = new TeacherOtherFragment(snapshot);
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
