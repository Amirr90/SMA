package com.example.sma.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sma.R;
import com.example.sma.model.PersonalDetailModel;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {

    List<PersonalDetailModel> detailModels;
    Context context;

    public ProfileAdapter(List<PersonalDetailModel> detailModels, Context context) {
        this.detailModels = detailModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_personal_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.MyViewHolder holder, int position) {

        PersonalDetailModel personalDetailModel = detailModels.get(position);
        if (personalDetailModel == null)
            return;
        holder.title.setText(personalDetailModel.getTitle());
        holder.description.setText(personalDetailModel.getDescription());

    }

    @Override
    public int getItemCount() {
        return detailModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView11);
            description = (TextView) itemView.findViewById(R.id.textView12);
        }
    }
}
