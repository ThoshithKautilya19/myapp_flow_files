package com.example.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SMS_PERMISSION = 1;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String LAST_PROCESSED_TIME = "LastProcessedTime";
    private static final String FIRST_RUN = "FirstRun";
    private static final String LAST_PROCESSED_SUM = "LastProcessedSum";



    TextView prog_disp, tv3;
    Button calc_but;
    ImageButton hom_but, chang_but, log_but,go_prev_month,go_next_month;
    BarChart barChart;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        tv3 = findViewById(R.id.tot_amt_disp);
        prog_disp = findViewById(R.id.prog);
        calc_but = findViewById(R.id.calculate_but);
        hom_but = findViewById(R.id.home_but);
        chang_but = findViewById(R.id.change_but);
        log_but = findViewById(R.id.log_but);
        barChart = findViewById(R.id.chart_space);

        go_prev_month=findViewById(R.id.prev_month_but);
        go_next_month=findViewById(R.id.next_month_but);

        prog_disp.setText("Click on Calculate");

        final Integer[] curr_month_int = {0};
        final Integer[] curr_year_int = {0};



        List<String> months_list = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        List<Integer> int_list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

// Initialising dicts for reference to prev and next months
        Dictionary<Integer, String> dict_int_to_mon = new Hashtable<>();
        for (Integer i : int_list) {
            dict_int_to_mon.put(i, months_list.get(i));
        }

        Dictionary<String, Integer> dict_mon_to_int = new Hashtable<>();
        for (int i = 0; i < months_list.size(); i++) {
            dict_mon_to_int.put(months_list.get(i), i);
        }




        // Request SMS permissions
        requestSmsPermission();

        // Fetch the SMS addresses from the intent extras
        Bundle bundle = getIntent().getExtras();
        ArrayList<String> sms_add = new ArrayList<>();
        if (bundle != null && bundle.size() != 0) {
            Log.e("TAG", " Nigga");
            sms_add = (ArrayList<String>) bundle.get("addr");
            if(sms_add==null){
                sms_add=(ArrayList<String>) bundle.get("sms_adds_back");
            }
        }

        //Fetch the month
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        ArrayList<String> CDL = new ArrayList<>(Arrays.asList(formattedDate.split("-")));
        CDL.set(2, CDL.get(2).substring(2));

        // Start the FetchSmsTask in a new thread
        curr_month_int[0]=dict_mon_to_int.get(CDL.get(1));
        curr_year_int[0]=Integer.parseInt(CDL.get(2));

        Log.e("TAG",String.valueOf(curr_month_int[0]));

        new Thread(new FetchSmsTask(sms_add,CDL.get(1),CDL.get(2),false)).start();

        // Set click listeners for buttons
        hom_but.setOnClickListener(v -> {
            Intent main_page = new Intent(MainActivity.this, MainActivity.class);
            startActivity(main_page);
        });

        chang_but.setOnClickListener(v -> {
            Intent addr_page = new Intent(MainActivity.this, Graph.class);
            startActivity(addr_page);
        });

        ArrayList<String> finalSms_add = sms_add;
        log_but.setOnClickListener(v -> {
            Intent log_page = new Intent(MainActivity.this, payment_log_logic.class);
            log_page.putExtra("list_sms", get_req_SMS(finalSms_add));
            log_page.putExtra("sms_adds",finalSms_add);
            startActivity(log_page);
        });

        ArrayList<String> finalSms_add1 = sms_add;
        go_prev_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prev_month_int=curr_month_int[0]-1;
                curr_month_int[0] = curr_month_int[0] -1;
                if(prev_month_int<0){
                    prev_month_int=11;
                    curr_month_int[0]=11;
                    String prev_month=dict_int_to_mon.get(prev_month_int);

                    curr_year_int[0]=curr_year_int[0]-1;
                    new Thread(new FetchSmsTask(finalSms_add1,prev_month,String.valueOf(curr_year_int[0]),true)).start();
                    Log.e("TAG",String.valueOf(curr_month_int[0]));
                }
                else{
                    String prev_month=dict_int_to_mon.get(prev_month_int);
                    new Thread(new FetchSmsTask(finalSms_add1,prev_month,String.valueOf(curr_year_int[0]),true)).start();
                    Log.e("TAG",String.valueOf(curr_month_int[0]));
                }

            }
        });

        go_next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int next_month_int= curr_month_int[0]+1;
                curr_month_int[0] = curr_month_int[0] +1;
                if(next_month_int>11){
                    next_month_int=0;
                    curr_month_int[0]=0;
                    String prev_month=dict_int_to_mon.get(next_month_int);

                    curr_year_int[0]=curr_year_int[0]+1;
                    new Thread(new FetchSmsTask(finalSms_add1,prev_month,String.valueOf(curr_year_int[0]),true)).start();
                    Log.e("TAG",String.valueOf(curr_month_int[0]));
                }
                else{
                    String next_month=dict_int_to_mon.get(next_month_int);
                    new Thread(new FetchSmsTask(finalSms_add1,next_month,String.valueOf(curr_year_int[0]),true)).start();
                    Log.e("TAG",String.valueOf(curr_month_int[0]));
                }

            }
        });
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE_SMS_PERMISSION);
        } else {
            Log.d(TAG, "SMS permission already granted.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "SMS permission granted.");
            } else {
                Log.e(TAG, "SMS permission denied.");
                Toast.makeText(this, "Permission denied to read SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchSmsTask implements Runnable {
        private final ArrayList<String> smsAdd;
        private final String month;
        private final String year;
        private final boolean change_month;

        public FetchSmsTask(ArrayList<String> smsAdd, String month, String year, boolean change_month) {
            this.smsAdd = smsAdd;
            this.month = month;
            this.year = year;
            this.change_month = change_month;
        }
        @Override
        public void run() {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                List<Sms> smsList = getAllSms();
                if (!smsList.isEmpty()) {
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "payment_db")
                            .fallbackToDestructiveMigration()
                            .build();

                    UserDao dao = db.userDao();
                    List<BarEntry> barEntries;
                    float sum;
//                    if (change_month) {
////                        barEntries = getTemporaryBarEntries(month + "_" + year);
////                        sum = getTemporarySum(month + "_" + year);
//                        barEntries= new ArrayList<>();
//                        if (barEntries == null || barEntries.isEmpty()) {
//                            barEntries = new ArrayList<>();
//                            for (int i = 1; i <= 30; i++) {
//                                barEntries.add(new BarEntry(i, 0));
//                            }
//                        }
//
//
//                    } else {
//                        barEntries= new ArrayList<>();
//                        if (barEntries == null || barEntries.isEmpty()) {
//                            barEntries = new ArrayList<>();
//                            for (int i = 1; i <= 30; i++) {
//                                barEntries.add(new BarEntry(i, 0));
//                            }
//                        }
//                    }

                    barEntries= new ArrayList<>();
                        if (barEntries == null || barEntries.isEmpty()) {
                            barEntries = new ArrayList<>();
                            for (int i = 1; i <= 30; i++) {
                                barEntries.add(new BarEntry(i, 0));
                            }
                        }

                    System.out.println(barEntries);

                    uiHandler.post(() -> prog_disp.setText("Calculating"));

                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    boolean isFirstRun = sharedPreferences.getBoolean(FIRST_RUN, true);
                    Log.e("TAG",String.valueOf("isFirstRun hit"));

                    long lastProcessedTime = getLastProcessedTime();
                    long maxTime = lastProcessedTime;
                    ArrayList<String> reqAdds = new ArrayList<>();
                    ArrayList<Integer> getTimes = new ArrayList<>();

                    for (Sms sms : smsList) {
//                        Log.e("TAG",String.valueOf(change_month));
//                        Log.e("TAG",String.valueOf(isFirstRun));
//                        Log.e("TAG",String.valueOf(sms.getTime()>lastProcessedTime));
//                          Log.e("TAG",String.valueOf(smsAdd.contains(sms.getAddress())));
//                          Log.e("TAG",smsAdd.get(0) );
//                          Log.e("TAG",smsAdd.get(1) );

//                        (change_month || isFirstRun || sms.getTime() > lastProcessedTime) &&
                        if ( smsAdd.contains(sms.getAddress()) && sms.getMsg().contains("debited")) {
                            reqAdds.add(sms.getMsg());
                            getTimes.add(sms.getTime());
                            Log.d(TAG, "Got one");
                        }
                    }

                    Log.e("TAG",String.valueOf(!reqAdds.isEmpty()));
                    if (!reqAdds.isEmpty()) {
                        final double[] sumArray = {0.00};
                        for (int k = 0; k < reqAdds.size(); k++) {
                            String msg = reqAdds.get(k);
                            int msgTime = getTimes.get(k);
                            String[] msgArray = msg.split(" ");
                            for (int i = 1; i < msgArray.length; i++) {
                                double amount = 0;
                                if ("on".equals(msgArray[i])) {
                                    String wordBeforeOn = msgArray[i - 1];
                                    String wordAfterOn = msgArray[i + 1].substring(0, msgArray[i + 1].length() - 1);
                                    ArrayList<String> date = new ArrayList<>(Arrays.asList(wordAfterOn.split("-")));

                                    if (year.equals(date.get(2)) && month.equals(date.get(1))) {
                                        int day;
                                        try {
                                            day = Integer.parseInt(date.get(0));
                                        } catch (NumberFormatException |
                                                 IndexOutOfBoundsException e) {
                                            Log.e(TAG, "Invalid date format in SMS: " + wordAfterOn);
                                            continue;
                                        }

                                        try {
                                            amount = Double.parseDouble(wordBeforeOn);
                                        } catch (NumberFormatException e) {
                                            ArrayList<String> four_dig_list = new ArrayList<>(Arrays.asList(wordBeforeOn.split(",")));
                                            String four_dig = String.join("", four_dig_list);
                                            try {
                                                amount = Double.parseDouble(four_dig);
                                            } catch (NumberFormatException ex) {
                                                Log.e(TAG, "Invalid amount format in SMS: " + wordBeforeOn);
                                                continue;
                                            }
                                        }

                                        if (day > 0 && day <= barEntries.size()) {
                                            BarEntry barEntry = barEntries.get(day - 1);
                                            if (barEntry != null) {
                                                if (barEntry.getY() != 0) {
                                                    float old_val = barEntry.getY();
                                                    float new_val = (float) amount + old_val;
                                                    barEntries.set(day - 1, new BarEntry(day, new_val));
                                                    Log.d("TAG","Same day another payment");
                                                    System.out.println(old_val);
                                                    System.out.println(new_val);
                                                    System.out.println(barEntry);
                                                } else {
                                                    float val = (float) amount;
                                                    barEntries.set(day - 1, new BarEntry(day, val));
                                                    Log.d("TAG","Unique day");
                                                    System.out.println(val);
                                                    System.out.println(barEntry);
                                                }

                                                if (!change_month) {
                                                    User user = new User(0, wordBeforeOn, date.get(0), date.get(1), date.get(2), msgTime);
                                                    dao.insertrecord(user);
                                                    maxTime = Math.max(maxTime, msgTime);
                                                }
                                            } else {
                                                Log.e(TAG, "BarEntry is null for day: " + day);
                                            }
                                        } else {
                                            Log.e(TAG, "Invalid day index: " + day);
                                        }
                                    }
                                }
                                sumArray[0] = sumArray[0] + amount;
//                                Log.d("TAG","One sms completed");
//                                System.out.println(barEntries);

                            }


                        }


                        if (change_month) {
                            saveTemporaryMonthData(barEntries, (float) sumArray[0], month + "_" + year);
                            uiHandler.post(() -> tv3.setText(String.valueOf(sumArray[0])));
                            System.out.println(barEntries);



                        } else {
                            saveBarEntries(barEntries);
                            setLastProcessedTime(maxTime);
                            setLastProcessedSum((float) sumArray[0]);
                            uiHandler.post(() -> tv3.setText(String.valueOf(sumArray[0])));
                        }

                        uiHandler.post(() -> prog_disp.setText("Done"));
                        BarDataSet barDataSet = new BarDataSet(barEntries, "Amount Spent Per Day");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.WHITE);
                        barDataSet.setValueTextSize(5f);

                        BarData barData = new BarData(barDataSet);
                        barChart.getAxisLeft().setTextColor(Color.WHITE);
                        barChart.getAxisRight().setTextColor(Color.WHITE);
                        barChart.getXAxis().setTextColor(Color.WHITE);
                        barChart.getLegend().setTextColor(Color.WHITE);

                        uiHandler.post(() -> {
                            barChart.setFitBars(true);
                            barChart.setData(barData);
                            barChart.animateY(2000, Easing.EaseInOutCubic);
                        });
                    } else {
                        Log.e(TAG, "Already done");
                        float old_sum = getLastProcessedSum();

                        uiHandler.post(() -> tv3.setText(String.valueOf(old_sum)));

                        ArrayList<BarEntry> old_bar_entries = new ArrayList<>(getBarEntries());

                        System.out.println(old_bar_entries);

                        BarDataSet barDataSet = new BarDataSet(old_bar_entries, "Payments this month");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.WHITE);
                        barDataSet.setValueTextSize(0.05F);
                        BarData barData = new BarData(barDataSet);

                        barChart.getAxisLeft().setTextColor(Color.WHITE);
                        barChart.getAxisRight().setTextColor(Color.WHITE);
                        barChart.getXAxis().setTextColor(Color.WHITE);
                        barChart.getLegend().setTextColor(Color.WHITE);

                        runOnUiThread(() -> barChart.animateY(5000, Easing.EaseInOutQuad));

                        barChart.setData(barData);
                        barChart.invalidate();

                        uiHandler.post(() -> prog_disp.setText("Done"));

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(FIRST_RUN, false);
                        editor.apply();
                    }
                }
            }
        }
    }



    public List<Sms> getAllSms() {
        List<Sms> smsList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/"), null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Sms sms = new Sms();
                    sms.setId(cursor.getString(cursor.getColumnIndex("_id")));
                    sms.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                    sms.setMsg(cursor.getString(cursor.getColumnIndex("body")));
                    sms.setReadState(cursor.getString(cursor.getColumnIndex("read")));
                    sms.setTime(cursor.getInt(cursor.getColumnIndex("date")));
                    String folderName = cursor.getString(cursor.getColumnIndex("type")).contains("1") ? "inbox" : "sent";
                    sms.setFolderName(folderName);
                    smsList.add(sms);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return smsList;
    }



    private long getLastProcessedTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(LAST_PROCESSED_TIME, 0);
    }
    private void setLastProcessedTime(long time) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_PROCESSED_TIME, time);
        editor.apply();
    }


    private float getLastProcessedSum() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(LAST_PROCESSED_SUM, 0);
    }

    private void setLastProcessedSum(double sum) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(LAST_PROCESSED_SUM, (float)sum);
        editor.apply();
    }





    private void saveBarEntries(List<BarEntry> barEntries) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(barEntries);
        editor.putString("barEntries", json);
        editor.apply();
    }




    private List<BarEntry> getBarEntries() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("barEntries", "");
        Type type = new TypeToken<List<BarEntry>>() {}.getType();
        List<BarEntry> barEntries = gson.fromJson(json, type);

        // Check if barEntries is null and initialize if necessary
        if (barEntries == null) {
            barEntries = new ArrayList<>();
            for (int i = 1; i <= 31; i++) {
                barEntries.add(new BarEntry(i, 0));
            }
        }

        return barEntries;
    }


    public ArrayList<Sms> get_req_SMS(ArrayList<String> req_sms_add) {
        ArrayList<Sms> req_sms_list = new ArrayList<Sms>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            List<Sms> smsList = getAllSms();
            if (!smsList.isEmpty()) {


                //Initialising lists for storage
                ArrayList<String> reqAdds = new ArrayList<>();
                ArrayList<Integer> getTimes = new ArrayList<>();

                //Lokking for required SMS only
                for (Sms sms : smsList) {
                    if (req_sms_add.contains(sms.getAddress()) & sms.getMsg().contains("debited")) {// Enter the address how you get you payment messages
                        reqAdds.add(sms.getMsg());
                        getTimes.add(sms.getTime());
                        req_sms_list.add(sms);
                    }
                }
            }
        } else {
            uiHandler.post(() -> Toast.makeText(MainActivity.this, "SMS permission not granted.", Toast.LENGTH_SHORT).show());
            Log.e(TAG, "SMS permission not granted.");
            requestSmsPermission();
        }
        return req_sms_list;
    }


    public static ArrayList<BarEntry> make_bar_entries_for_new_month() {
        ArrayList<BarEntry> barEntries;
        barEntries = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            barEntries.add(new BarEntry(i, 0));
        }

        return  barEntries;
    }

    public double get_sms_total(List<String> reqAdds,List<Integer> getTimes,String month, String year, List<BarEntry> barEntries){
        final double[] sum = {0.00};
        for (int k = 0; k < reqAdds.size(); k++) {
            String msg = reqAdds.get(k);
            int msgTime = getTimes.get(k);
            String[] msgArray = msg.split(" ");
            for (int i = 1; i < msgArray.length; i++) {
                if ("on".equals(msgArray[i])) {
                    String wordBeforeOn = msgArray[i - 1];
                    String wordAfterOn = msgArray[i + 1].substring(0, msgArray[i + 1].length() - 1);
                    ArrayList<String> date = new ArrayList<>(Arrays.asList(wordAfterOn.split("-")));
                    Log.e("TAG",date.get(1));

                    if (year.equals(date.get(2)) && month.equals(date.get(1))) {
                        try {
                            sum[0] += Double.parseDouble(wordBeforeOn);
                            Log.d(TAG, String.valueOf(sum[0]));
                        } catch (NumberFormatException e) {
                            ArrayList<String> four_dig_list = new ArrayList<>(Arrays.asList(wordBeforeOn.split(",")));
                            String four_dig = String.join("", four_dig_list);
                            sum[0] += Double.parseDouble(four_dig);
                        }

                        if (barEntries.get(Integer.parseInt(date.get(0))).getY() != 0) {
                            float old_val = barEntries.get(Integer.parseInt(date.get(0))).getY();
                            double to_add = Double.parseDouble(wordBeforeOn);
                            float new_val = (float) to_add + old_val;
                            barEntries.set(Integer.parseInt(date.get(0)), new BarEntry(Integer.parseInt(date.get(0)), new_val));
                        } else {
                            float val = (float) Double.parseDouble(wordBeforeOn);
                            barEntries.set(Integer.parseInt(date.get(0)), new BarEntry(Integer.parseInt(date.get(0)), val));
                        }
                    }
                }
            }
        }
        return sum[0];

    }

    public List<BarEntry> getBarEntries(List<String> reqAdds, List<Integer> getTimes, String month, String year, List<BarEntry> barEntries) {
        for (int k = 0; k < reqAdds.size(); k++) {
            String msg = reqAdds.get(k);
            int msgTime = getTimes.get(k);
            String[] msgArray = msg.split(" ");

            for (int i = 1; i < msgArray.length; i++) {
                if ("on".equals(msgArray[i])) {
                    String wordBeforeOn = msgArray[i - 1];
                    String wordAfterOn = msgArray[i + 1].substring(0, msgArray[i + 1].length() - 1);
                    String[] date = wordAfterOn.split("-");

                    if (date.length == 3 && year.equals(date[2]) && month.equals(date[1])) {
                        int day = Integer.parseInt(date[0]);
                        float valueToAdd = Float.parseFloat(wordBeforeOn);

                        if (day < barEntries.size()) {
                            BarEntry existingEntry = barEntries.get(day);
                            float updatedValue = existingEntry.getY() + valueToAdd;
                            barEntries.set(day, new BarEntry(day, updatedValue));
                        } else {
                            barEntries.add(new BarEntry(day, valueToAdd));
                        }
                    }
                }
            }
        }
        return barEntries;
    }

    private void saveTemporaryMonthData(List<BarEntry> barEntries, float sum, String keyPrefix) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(barEntries);
        editor.putString(keyPrefix + "_barEntries", json);
        editor.putFloat(keyPrefix + "_sum", sum);
        editor.apply();
    }

    private List<BarEntry> getTemporaryBarEntries(String keyPrefix) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(keyPrefix + "_barEntries", "");
        Type type = new TypeToken<List<BarEntry>>() {}.getType();
        List<BarEntry> barEntries = gson.fromJson(json, type);

        if (barEntries == null) {
            barEntries = new ArrayList<>();
            for (int i = 1; i <= 31; i++) {
                barEntries.add(new BarEntry(i, 0));
            }
        }

        return barEntries;
    }

    private float getTemporarySum(String keyPrefix) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(keyPrefix + "_sum", 0.0f);
    }



}



