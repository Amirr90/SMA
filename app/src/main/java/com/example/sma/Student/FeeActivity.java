package com.example.sma.Student;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.sma.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sma.Util.Utils.CLASS;
import static com.example.sma.Util.Utils.FEE_COLLECTION_QUERY;
import static com.example.sma.Util.Utils.FEE_STATUS;
import static com.example.sma.Util.Utils.IMAGE;
import static com.example.sma.Util.Utils.MONTHLY_FEE;
import static com.example.sma.Util.Utils.NAME;
import static com.example.sma.Util.Utils.PAID;
import static com.example.sma.Util.Utils.ROLL_NUMBER;
import static com.example.sma.Util.Utils.STATUS;
import static com.example.sma.Util.Utils.STU_ID;
import static com.example.sma.Util.Utils.getCurrencyFormat;
import static com.example.sma.Util.Utils.hideAlertDialog;
import static com.example.sma.Util.Utils.showAlertDialog;

public class FeeActivity extends AppCompatActivity {

    List<DocumentSnapshot> feeList = new ArrayList<>();
    FeeAdapter feeAdapter;
    RecyclerView recyclerView;
    String Class;
    int Year, Month, Day;
    int paid, unpaid, partial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee);

        Toolbar toolbar = (Toolbar) findViewById(R.id.fee_toolbar);
        setToolbar(toolbar, "All Students");


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);

        Class = getIntent().getStringExtra(CLASS);
        setRec();
    }

    private void setRec() {

        recyclerView = findViewById(R.id.fee_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        feeAdapter = new FeeAdapter(feeList);
        recyclerView.setAdapter(feeAdapter);
        loadFeeData();

    }

    private void loadFeeData() {
        paid = partial = unpaid = 0;
        showAlertDialog(this);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(FEE_COLLECTION_QUERY)
                .whereEqualTo(CLASS, Class)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hideAlertDialog();

                        if (!queryDocumentSnapshots.isEmpty()) {
                            feeList.clear();
                            feeList.addAll(queryDocumentSnapshots.getDocuments());
                            feeAdapter.notifyDataSetChanged();
                        }

                        //Setting Status Data
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            List<Map<String, Object>> feePaidStatusList = (List<Map<String, Object>>) snapshot.get(FEE_STATUS);
                            if ((Long) feePaidStatusList.get(Month).get(STATUS) == 0)
                                unpaid++;
                            else if ((Long) feePaidStatusList.get(Month).get(STATUS) == 1)
                                paid++;
                            else
                                partial++;
                        }

                        TextView tvpartial = findViewById(R.id.textView23);
                        TextView tvpaid = findViewById(R.id.textView21);
                        TextView tvunpaid = findViewById(R.id.textView22);

                        tvpaid.setText("Paid: " + paid);
                        tvpartial.setText("Partial: " + partial);
                        tvunpaid.setText("Unpaid: " + unpaid);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideAlertDialog();
                Toast.makeText(FeeActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Fee");

    }

    private class FeeAdapter extends RecyclerView.Adapter<FeeAdapter.ViewHolder> {
        List<DocumentSnapshot> feeList;
        Dialog dialog;
        String Image;

        public FeeAdapter(List<DocumentSnapshot> feeList) {
            this.feeList = feeList;
        }

        @NonNull
        @Override
        public FeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fee_view, parent, false);
            dialog = new Dialog(FeeActivity.this);
            dialog.setContentView(R.layout.fee_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final FeeAdapter.ViewHolder holder, int position) {
            final DocumentSnapshot snapshot = feeList.get(position);

            try {
                holder.all_stu_name.setText(snapshot.getString(NAME));
                holder.roll_number.setText(getString(R.string.roll_number) + ": " + snapshot.getString(ROLL_NUMBER));
                DocumentReference image_ref = snapshot.getDocumentReference("image_reference");
                image_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot.exists()) {
                            final String image = snapshot.getString(IMAGE);
                            if (image != null && !image.equalsIgnoreCase(""))
                                Picasso.with(FeeActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.circleImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(FeeActivity.this).load(image).into(holder.circleImageView);
                                    }
                                });

                        }
                    }
                });

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFeeStatusDialog(snapshot,
                                dialog, holder.feeStatus.getText().toString()
                        );
                    }
                });


                List<Map<String, Object>> feePaidStatusList = (List<Map<String, Object>>) snapshot.get(FEE_STATUS);
                for (int a = 0; a < feePaidStatusList.size(); a++) {
                    if ((Long) feePaidStatusList.get(Month).get(STATUS) == 0) {
                        holder.feeStatus.setText(getString(R.string.unpaid));
                        holder.feeStatus.setTextColor(getResources().getColor(R.color.red));
                        return;
                    } else if ((Long) feePaidStatusList.get(Month).get(STATUS) == 1) {
                        holder.feeStatus.setText(getString(R.string.paid));
                        holder.feeStatus.setTextColor(getResources().getColor(R.color.green2));
                        return;
                    } else if ((Long) feePaidStatusList.get(Month).get(STATUS) == 2) {
                        holder.feeStatus.setText(getString(R.string.partial_paid));
                        holder.feeStatus.setTextColor(getResources().getColor(R.color.yellow));
                        return;
                    }


                }


            } catch (Exception e) {
                Toast.makeText(FeeActivity.this, "error " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return feeList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView feeStatus, all_stu_name, roll_number;
            CircleImageView circleImageView;
            RelativeLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                feeStatus = itemView.findViewById(R.id.textView20);
                all_stu_name = itemView.findViewById(R.id.all_stu_name);

                roll_number = itemView.findViewById(R.id.all_user_message);

                circleImageView = itemView.findViewById(R.id.all_stu_profile);

                layout = itemView.findViewById(R.id.main_fee_lay);
            }
        }
    }

    private void openFeeStatusDialog(final DocumentSnapshot snapshot, final Dialog dialog, String feeStatus) {
        List<Map<String, Object>> feePaidStatusList = (List<Map<String, Object>>) snapshot.get(FEE_STATUS);

        final TextView mStuName = dialog.findViewById(R.id.s_name);
        final TextView tvBalance = dialog.findViewById(R.id.textView16);
        TextView mStuRollNumber = dialog.findViewById(R.id.s_roll_number);
        TextView mStuFatherName = dialog.findViewById(R.id.s_father_name);
        LottieAnimationView mStuProfile = dialog.findViewById(R.id.animation_view);
        final Button mViewProfile = dialog.findViewById(R.id.view_stdent_profile_btn);
        Button dismissBtn = dialog.findViewById(R.id.dismiss_btn);

        mStuName.setText(snapshot.getString(NAME));
        mStuRollNumber.setText(getResources().getString(R.string.roll_number) + ": " + snapshot.getString(ROLL_NUMBER));
        if (feeStatus.equalsIgnoreCase(getString(R.string.paid))) {
            mStuFatherName.setText("Paid " + getCurrencyFormat((Long) feePaidStatusList.get(Month).get(PAID)));
            mStuFatherName.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
            mStuProfile.setAnimation(R.raw.check_ok);
            tvBalance.setVisibility(View.GONE);

        } else if (feeStatus.equalsIgnoreCase(getString(R.string.partial_paid))) {
            mStuFatherName.setText("Paid " + getCurrencyFormat((Long) feePaidStatusList.get(Month).get(PAID)));
            mStuFatherName.setTextColor(getResources().getColor(R.color.yellow));
            mStuProfile.setAnimation(R.raw.unpaid_json);
            long balance = (Long) feePaidStatusList.get(Month).get(MONTHLY_FEE) - (Long) feePaidStatusList.get(Month).get(PAID);
            tvBalance.setText("Balance " + getCurrencyFormat(balance));
            tvBalance.setVisibility(View.VISIBLE);
        } else {
            mStuFatherName.setText("Balance " + getCurrencyFormat((Long) feePaidStatusList.get(Month).get(MONTHLY_FEE)));
            mStuFatherName.setTextColor(getResources().getColor(R.color.red));
            mStuProfile.setAnimation(R.raw.not_paid_json);
            tvBalance.setVisibility(View.GONE);

        }


        mViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(FeeActivity.this, StudentProfile.class)
                        .putExtra(STU_ID, snapshot.getId()));
            }
        });

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}