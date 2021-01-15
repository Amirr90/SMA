package com.example.sma.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.sma.MainActivity;
import com.example.sma.R;
import com.example.sma.ScheduleActivity;
import com.example.sma.SplashScreen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.tt.whorlviewlibrary.WhorlView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Utils {
    public static final String MyPREFERENCES = "My_Prefs";
    public static final String SCHEDULE = "Schedule";
    public static final String BOARD = "board";
    public static final String LAST_UPDATE = "last_update";
    public static final String CLASS = "class";
    public static final String CLASS_ = "class_";
    public static final String IS_LOGIN = "Is_login";
    public static final String ATTENDANCE = "Attendance";
    public static final String EXAMINATION = "Examination";
    public static final String SYLLABUS = "Syllabus";
    public static final String FEE = "Fee";
    public static final String NOTICEBOARD = "Noticeboard";
    public static final String PROFILE = "Profile";
    public static final String LOG_OUT = "logout";
    public static final String FULL_MARKS = "full_marks";
    public static final String PASSING_MARKS = "passing_marks";
    public static final String ONLINE_EXAM_TEST = "Online\nExam/Test";
    public static final String HOMEWORK = "HomeWork";
    public static final String STUDENT = "Student";
    public static final String CLASS_ROUTINE = "Class Routine";
    public static final String ATTENDANCE_HISTORY = "Attendance History";
    public static final String STUDENT_QUERY = "Students";
    public static final String SON_OF = "son_of";
    public static final String YEAR_BOARD = "year_board";
    public static final String _CLASS = "_class";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String CLASS_1 = "Class 1";
    public static final String TEACHERS = "Teachers";
    public static final String SUBJECT = "subject";
    public static final String TIMESTAMP = "timestamp";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String DAY = "day";
    public static final String TEACHER_NAME = "TeacherName";
    public static final String PRESENT = "present";
    public static final String PERSONAL = "PERSONAL";
    public static final String PARENTS = "PARENTS";
    public static final String TRANSPORT = "TRANSPORT";
    public static final String OTHERS = "OTHERS";
    public static final String TEACHER_ID = "Teacher_Id";
    public static final String MONTHLY_FEE = "monthly_fee";
    public static final String IS_PAID = "is_paid";
    public static final String PAID = "paid";
    public static final String FEE_COLLECTION_QUERY = "Fee_Collection";

    public static final String CREATED_AT = "created_at";
    public static final String USERNAME = "username";
    public static final String BANK_NAME = "Bank_name";
    public static final String BRANCH_NAME = "Branch_name";
    public static final String TITLE = "title";
    public static final String FROM = "from";

    public static final String LOGIN_CREDENTIALS = "Login_credentials";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String EXAMS_RESULTS = "Exams_Results";

    public static final String ENGLISH = "eng";
    public static final String HINDI = "hindi";
    public static final String MATH = "math";
    public static final String SCI = "sci";
    public static final String ART = "art";
    public static final String COMPUTER = "computer";


    public static final String ROLL_NUMBER = "roll_no";
    public static final String STU_ID = "stu_id";
    public static final String MARKS = "marks";
    public static final String RESULT = "result";
    public static final String PASS = "pass";
    public static final String FAIL = "fail";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final String IS_NEW = "is_new";

    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String RELIGION = "religion";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String PRESENT_ADDRESS = "present_address";
    public static final String PERMANENT_ADDRESS = "permanent_address";
    public static final String FATHER_NAME = "father_name";
    public static final String MOTHER_NAME = "mother_name";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String PERSONAL_DATA = "personal";
    public static final String PARENT_DATA = "parent";
    public static final String FATHER_MOBILE = "father_mobile";
    public static final String MOTHER_MOBILE = "mother_mobile";
    public static final String FATHER_OCCUPATION = "father_occupation";
    public static final String MOTHER_OCCUPATION = "mother_occupation";
    public static final String BUS_NUMBER = "bus_number";
    public static final String BUS_ROUTE = "bus_route";
    public static final String BUS_FEE = "bus_fee";
    public static final String DRIVER_NAME = "driver_name";
    public static final String TRANSPORT_DATA = "transport";

    public static final String FEE_STATUS = "fee_status";
    public static final String STATUS = "status";
    public static final String PARTIAL_PAID = "Partial Paid";

    public static final String CLASS_ROUTINE_QUERY = "Class_Routine";

    static WhorlView whorlView;

    public static AlertDialog alertDialog;


    public static Map<String, Object> getFeeMap() {
        Map<String, Object> feeMap = new HashMap<>();
        feeMap.put(FEE, 0);
        feeMap.put(IS_PAID, false);
        feeMap.put(MONTHLY_FEE, 0);
        feeMap.put(MONTH, Calendar.getInstance().get(Calendar.MONTH) + 1);
        feeMap.put(YEAR, Calendar.getInstance().get(Calendar.YEAR));
        feeMap.put(TIMESTAMP, System.currentTimeMillis());
        feeMap.put(PAID, 0);
        return feeMap;
    }

    public static void saveLoginSession(Boolean value, Context context, String email, String username) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(IS_LOGIN, value);
        editor.putString(EMAIL, email);
        editor.putString(USERNAME, username);
        editor.commit();
    }

    public static Map<String, Object> getSignUpMap(String email, String password, String username) {
        Map<String, Object> map = new HashMap<>();
        map.put(EMAIL, email);
        map.put(PASSWORD, password);
        map.put(USERNAME, username);
        map.put(CREATED_AT, System.currentTimeMillis());
        return map;

    }

    public static void logout(final Boolean value, final Activity context) {
        new AlertDialog.Builder(context).setMessage("Want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(IS_LOGIN, value);
                        editor.putString(EMAIL, "");
                        editor.putString(USERNAME, "");
                        editor.putString(BANK_NAME, "");
                        editor.putString(BRANCH_NAME, "");
                        editor.commit();
                        context.finish();
                        context.startActivity(new Intent(context, SplashScreen.class));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }


    public static void showAlertDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_lay, null));

        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(android.R.color.transparent)));
        alertDialog.show();

    }


    public static void hideAlertDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }


    public static List<String> getWeekDaysList() {
        List<String> list = new ArrayList<>();
        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        list.add("Thursday");
        list.add("Friday");
        list.add("Saturday");
        return list;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setTeacherProfile(CircleImageView mCircleImageView, Activity activity) {

    }


    public static String getCurrencyFormat(long num) {
        String COUNTRY = "IN";
        String LANGUAGE = "en";
        String str = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(num);

        return str;
    }

    public static void setTeacherProfile(Activity activity, CircleImageView mCircleImageView, String image) {

        if (image != null && !image.equalsIgnoreCase(""))
            Picasso.with(activity).load(image).placeholder(R.drawable.profile).into(mCircleImageView);
    }


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static void addData(String doc_id1, final Activity activity) {

        Map<String, Object> map = new HashMap<>();
        map.put(LAST_UPDATE, System.currentTimeMillis());
        map.put(BOARD, "CBSE");
        map.put(IS_NEW, true);
        map.put(YEAR, "2019-2020");
        map.put(CLASS, "class 2");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(EXAMINATION)
                .document(doc_id1)
                .collection(CLASS)
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        Toast.makeText(activity, "added", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static List<String> getTimingList() {
        List<String> list = new ArrayList<>();

        for (int a = 0; a < 5; a++) {
            list.add("10:00 am to 10:40 am");
        }
        return list;
    }

    public static List<String> getSubjectList() {
        List<String> list = new ArrayList<>();

        for (int a = 0; a < 5; a++) {
            list.add("Maths");
        }
        return list;
    }

    public static List<String> getRoomList() {
        List<String> list = new ArrayList<>();

        for (int a = 0; a < 5; a++) {
            list.add("101");
        }
        return list;
    }

    public static String getDay(int pos) {
        List<String> list = new ArrayList<>();

        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        list.add("Thursday");
        list.add("Friday");
        list.add("Saturday");
        for (int a = 0; a < 6; a++) {
            if (a == pos)
                return list.get(pos);
        }
        return "";
    }

    public static List<String> getDayList() {
        List<String> list = new ArrayList<>();

        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        list.add("Thursday");
        list.add("Friday");
        list.add("Saturday");
        return list;
    }


}
