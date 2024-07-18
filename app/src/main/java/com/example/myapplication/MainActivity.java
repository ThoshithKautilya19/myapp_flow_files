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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    int Low_lim_month;
    int Upper_lim_month;

    int Low_lim_year;
    int Upper_lim_year;


    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SMS_PERMISSION = 1;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String LAST_PROCESSED_TIME = "LastProcessedTime";
    private static final String FIRST_RUN = "FirstRun";
    private static final String LAST_PROCESSED_SUM = "LastProcessedSum";

    Dictionary<Integer, String> dict_int_to_mon;
    Dictionary<String, Integer> dict_mon_to_int;



    TextView prog_disp, tv3,tot_head;
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
        tot_head=findViewById(R.id.tot_head);
        hom_but = findViewById(R.id.home_but);
        chang_but = findViewById(R.id.change_but);
        log_but = findViewById(R.id.log_but);
        barChart = findViewById(R.id.chart_space);

        go_prev_month=findViewById(R.id.prev_month_but);
        go_next_month=findViewById(R.id.next_month_but);

        prog_disp.setText("Click on Calculate");

        final Integer[] curr_month_int = {0};
        final Integer[] curr_year_int = {0};



        List<String> months_list = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec");
        List<Integer> int_list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

// Initialising dicts for reference to prev and next months
        dict_int_to_mon = new Hashtable<>();
        for (Integer i : int_list) {
            dict_int_to_mon.put(i, months_list.get(i));
        }

        dict_mon_to_int = new Hashtable<>();
        for (int i = 0; i < months_list.size(); i++) {
            dict_mon_to_int.put(months_list.get(i), i);
        }




        // Request SMS permissions
        requestSmsPermission();

        // Fetch the SMS addresses from the intent extras
        Bundle bundle = getIntent().getExtras();
        ArrayList<String> sms_add = new ArrayList<>();
        if (bundle != null && bundle.size() != 0) {
            sms_add = (ArrayList<String>) bundle.get("addr");
            System.out.println(sms_add);
            if(sms_add==null){
                sms_add=(ArrayList<String>) bundle.get("sms_adds_back");
                System.out.println(sms_add);
            }
        }

        //Fetch the month
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        ArrayList<String> CDL = new ArrayList<>(Arrays.asList(formattedDate.split("-")));
        Log.e("TAG", String.valueOf(CDL));
        CDL.set(2, CDL.get(2).substring(2));
        Log.e("TAG", String.valueOf(CDL));


        // Start the FetchSmsTask in a new thread
        curr_month_int[0]=dict_mon_to_int.get(CDL.get(1));
        curr_year_int[0]=Integer.parseInt(CDL.get(2));

        //Putting in limits
        if(curr_month_int[0]-11<0){
            Low_lim_month=11-(11-dict_mon_to_int.get(CDL.get(1)));
            Low_lim_year=Integer.parseInt(CDL.get(2))-1;
        }
        else{
            Low_lim_month=dict_mon_to_int.get(CDL.get(1))-11;
            Low_lim_year=Integer.parseInt(CDL.get(2));
        }

        Upper_lim_month=dict_mon_to_int.get(CDL.get(1));
        Upper_lim_year=Integer.parseInt(CDL.get(2));
        //Putting limits done

        Log.e("TAG",String.valueOf(curr_month_int[0]));

        new Thread(new FetchSmsTasknew(sms_add,CDL.get(1),CDL.get(2))).start();

        chang_but.setOnClickListener(v -> {
            Intent addr_page = new Intent(MainActivity.this, Graph.class);
            startActivity(addr_page);
        });

        ArrayList<String> finalSms_add = sms_add;
        log_but.setOnClickListener(v -> {

//  OLD CODE
//            Intent log_page = new Intent(MainActivity.this, payment_log_logic.class);
//            log_page.putExtra("list_sms",get_req_sms_list(finalSms_add,getAllSms()));
//            log_page.putExtra("sms_adds",finalSms_add);
//            try {
//                startActivity(log_page);
//            }
//            catch (Exception e){
//                Intent lp_to_gp = new Intent(MainActivity.this, Graph.class);
//                Toast.makeText(this,"Too many messages to processs. Kindly excuse us",Toast.LENGTH_SHORT).show();
//                startActivity(lp_to_gp);
//            }

            //new testing code
            ExtendedDataHolder extras = ExtendedDataHolder.getInstance();
            extras.putExtra("list_sms", get_req_sms_list(finalSms_add,getAllSms()));
            extras.putExtra("sms_adds", finalSms_add);
            startActivity(new Intent(MainActivity.this, payment_log_logic.class));
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
                    if(curr_month_int[0]<Low_lim_month & curr_year_int[0]==Low_lim_year){
                        uiHandler.post(()->Toast.makeText(MainActivity.this,"We deal with data not older than one year",Toast.LENGTH_SHORT).show());
                    }
                    else {
                        new Thread(new FetchSmsTasknew(finalSms_add1, prev_month, String.valueOf(curr_year_int[0]))).start();
                        Log.e("TAG", String.valueOf(curr_month_int[0]));
                    }
                }
                else{
                    if(curr_month_int[0]<Low_lim_month & curr_year_int[0]==Low_lim_year){
                        uiHandler.post(()->Toast.makeText(MainActivity.this,"We deal with data not older than one year",Toast.LENGTH_SHORT).show());
                    }
                    else {
                        String prev_month = dict_int_to_mon.get(prev_month_int);
                        new Thread(new FetchSmsTasknew(finalSms_add1, prev_month, String.valueOf(curr_year_int[0]))).start();
                        Log.e("TAG", String.valueOf(curr_month_int[0]));
                    }
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

                    if(Upper_lim_month==0 & curr_year_int[0]+1==Upper_lim_year){
                        uiHandler.post(()->Toast.makeText(MainActivity.this,"No one knows what the future holds for us",Toast.LENGTH_SHORT).show());

                    }
                    else {
                        new Thread(new FetchSmsTasknew(finalSms_add1, prev_month, String.valueOf(curr_year_int[0]))).start();
                        Log.e("TAG", String.valueOf(curr_month_int[0]));
                    }
                }
                else{
                    String next_month=dict_int_to_mon.get(next_month_int);

                    if(curr_month_int[0]>Upper_lim_month & curr_year_int[0]==Upper_lim_year){
                        uiHandler.post(()->Toast.makeText(MainActivity.this,"No one knows what the future holds for us",Toast.LENGTH_SHORT).show());
                    }
                    else {
                        new Thread(new FetchSmsTasknew(finalSms_add1, next_month, String.valueOf(curr_year_int[0]))).start();
                        Log.e("TAG", String.valueOf(curr_month_int[0]));
                    }
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
                    sms.setTime(cursor.getLong(cursor.getColumnIndex("date")));
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







    //New functions added--> WRT Dropdown
    public ArrayList<Sms> get_req_sms_list(ArrayList<String> received_bank_names, List<Sms> all_sms_list){

        int sms_month_int=0;
        int sms_year_int=0;

        ArrayList<Sms> req_sms_list = new ArrayList<>();
        boolean matchFound = false;
        ArrayList<String> sms_date;

        Log.e("TAG", "Number of SMS in all_sms_list: " + all_sms_list.size());
        Log.e("TAG", "Number of received bank names: " + received_bank_names.size());

        for(int i = 0; i < all_sms_list.size(); i++){
            Sms sms = all_sms_list.get(i);
//            Log.e("TAG", "Processing SMS " + (i + 1) + ": " + sms.getAddress());


            //Just breaking from the loop if month and year above lowest limit
            long long_sms_date=all_sms_list.get(i).getTime();

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(long_sms_date);
            ArrayList<String> sms_date_arr = new ArrayList<>(Arrays.asList(formattedDate.split("-")));
//            Log.e("TAG", String.valueOf(sms_date_arr));
            sms_date_arr.set(2, sms_date_arr.get(2).substring(2));
//            Log.e("TAG", String.valueOf(sms_date_arr));

//                sms_date=String.join("-",CDL);

            String sms_month=sms_date_arr.get(1);
            if(Objects.equals(sms_month, "Sep")){
                sms_month="Sept";
            }


            sms_month_int=dict_mon_to_int.get(sms_month);
            sms_year_int=Integer.parseInt(sms_date_arr.get(2));

            System.out.println(sms_month_int+" "+sms_year_int);
            if(sms_month_int<Low_lim_month & sms_year_int==Low_lim_year){
                Log.e("TAG","Reached lowest month and year limit: "+Low_lim_month+" "+Low_lim_year);
                break;
            }

            for(int j = 0; j < received_bank_names.size(); j++){
                String Bank = received_bank_names.get(j);
//                Log.e("TAG", "Checking bank " + (j + 1) + ": " + Bank);
                if(sms.getAddress().contains(Bank) && sms.getMsg().contains("debited")){
//                    Log.e("TAG","This bank is present: " + Bank);
                    if(sms.getMsg().contains("requested")){
                        continue;
                    }
                    else{
                        req_sms_list.add(sms);
                        matchFound = true;
                        // You may want to break here if you only need one match per SMS
                        break;
                    }
                } else {
//                    Log.e("TAG","No matches found for this bank: BRUH" + Bank);
                }
            }

            if(!matchFound) {
//                Log.e("TAG","No matches found at all for SMS: BRUH" + sms.getAddress());
            }
            matchFound = false; // Reset for the next SMS
        }

        return req_sms_list;
    }


    public ArrayList<ArrayList<String>> get_req_sms_date_list(ArrayList<String> received_bank_names,List<Sms> all_sms_list){
        ArrayList<ArrayList<String>> req_sms_list_times= new ArrayList<>();
        for(Sms sms: all_sms_list){
            for(String Bank:received_bank_names){
                if(sms.getAddress().contains(Bank) & (sms.getMsg().contains("debited")| sms.getMsg().contains("Debit")| sms.getMsg().contains("debit")|sms.getMsg().contains("Debited"))) {
                    Log.e("TAG", "This bank is present :" + Bank);
                    long sms_time = sms.getTime();
//                    System.out.println(sms_time);
                    //Milliseconds to date function
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(sms_time);
                    ArrayList<String> sms_date_list = new ArrayList<>(Arrays.asList(formattedDate.split("-")));
//                    System.out.println(sms_date_list);


//                    sms_date_list.set(2, sms_date_list.get(2).substring(2)); TO TAKE THE LAST 2 NUMBERS IMPPPPPPPPPPPPPP

                    req_sms_list_times.add(sms_date_list);
                }
                else{
//                    Log.e("TAG","No matches found for this bank LOL:"+Bank);

                }
            }
//            Log.e("TAG","No matches found at all LOL" +received_bank_names);
        }

        return  req_sms_list_times;
    }

    public double getSum(String Bank, String bank_msg) {
        // Initialize total sum
        double tot_sum = 0.00;

        // Split bank_msg into parts
        String[] parts = bank_msg.split(" ");
        ArrayList<String> bank_msg_list = new ArrayList<>(Arrays.asList(parts));

        // Process each bank's specific logic
        if (Objects.equals(Bank, "ICICI")) {
            // ICICI bank processing
            // Example logic: look for "on" and extract amount
            int index_of_on = bank_msg_list.indexOf("on");
            if (index_of_on != -1 && index_of_on > 0 && index_of_on < bank_msg_list.size()) {
                String word_before_on = bank_msg_list.get(index_of_on - 1);
                try {
                    double curr_val = Double.parseDouble(word_before_on.replace(",", ""));
                    tot_sum += curr_val;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid amount format in ICICI SMS: " + word_before_on);
                }
                // Return total sum for ICICI and exit the method
                return tot_sum;
            } else {
                Log.e(TAG, "Index of 'on' not found or out of bounds in ICICI SMS: " + index_of_on);
            }
        } else if (Objects.equals(Bank, "IDBI")) {
            // IDBI bank processing
            // Example logic: look for "INR." or "Rs" and extract amount
            int index_of_key = bank_msg_list.indexOf("INR.");
            if (index_of_key == -1) {
                index_of_key = bank_msg_list.indexOf("Rs");
            }
            if (index_of_key != -1 && index_of_key + 1 < bank_msg_list.size()) {
                String word_after_key = bank_msg_list.get(index_of_key + 1);
                try {
                    double curr_val = Double.parseDouble(word_after_key.replace(",", ""));
                    tot_sum += curr_val;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid amount format in IDBI SMS: " + word_after_key);
                }
                // Return total sum for IDBI and exit the method
                return tot_sum;
            } else {
                Log.e(TAG, "Index of 'INR.' or 'Rs' not found or out of bounds in IDBI SMS: " + index_of_key);
            }
        } else if (Objects.equals(Bank, "SBI")) {
            // SBI bank processing
            // Example logic: look for "on" and extract amount
            int index_of_on = bank_msg_list.indexOf("on");
            if (index_of_on != -1 && index_of_on > 0 && index_of_on < bank_msg_list.size()) {
                String word_before_on = bank_msg_list.get(index_of_on - 1);
                try {
                    double curr_val = Double.parseDouble(word_before_on.replace(",", ""));
                    tot_sum += curr_val;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid amount format in SBI SMS: " + word_before_on);
                }
                // Return total sum for SBI and exit the method
                return tot_sum;
            } else {
                Log.e(TAG, "Index of 'on' not found or out of bounds in SBI SMS: " + index_of_on);
            }
        } else if (Objects.equals(Bank, "KOTAKB")) {

            //Getting index of INR
            int index_of_inr= bank_msg_list.indexOf("INR");
            if (index_of_inr==-1){
                index_of_inr=bank_msg_list.indexOf("Rs");
            }
            if (index_of_inr != -1 && index_of_inr > 0 && index_of_inr < bank_msg_list.size()) {
                String word_after_inr = bank_msg_list.get(index_of_inr + 1);
                try {
                    double curr_val = Double.parseDouble(word_after_inr.replace(",", ""));
                    tot_sum += curr_val;
                } catch (NumberFormatException e) {
                    Log.e("TAG", "Invalid amount format in SBI SMS: " + word_after_inr);
                }
                // Return total sum for KOTAKB and exit the method
                return tot_sum;
            } else {
                Log.e("TAG", "Index of 'on' not found or out of bounds in SBI SMS: " + index_of_inr);
            }
        } else if (Objects.equals(Bank, "AXIS")) {
            //Getting index of INR
            int index_of_inr= bank_msg_list.indexOf("INR");
            if (index_of_inr==-1){
                index_of_inr=bank_msg_list.indexOf("Rs");
            }
            if (index_of_inr != -1 && index_of_inr > 0 && index_of_inr < bank_msg_list.size()) {
                String word_after_inr = bank_msg_list.get(index_of_inr + 1);
                try {
                    double curr_val = Double.parseDouble(word_after_inr.replace(",", ""));
                    tot_sum += curr_val;
                } catch (NumberFormatException e) {
                    Log.e("TAG", "Invalid amount format in AXIS SMS: " + word_after_inr);
                }
                // Return total sum for AXIS and exit the method
                return tot_sum;
            } else {
                Log.e("TAG", "Index of 'on' not found or out of bounds in AXIS SMS: " + index_of_inr);
            }

        }else if(Objects.equals(Bank, "SCB")){

            //Getting index of INR
            int index_of_inr= bank_msg_list.indexOf("INR");
            if (index_of_inr==-1){
                index_of_inr=bank_msg_list.indexOf("Rs");
            }
            if (index_of_inr != -1 && index_of_inr > 0 && index_of_inr < bank_msg_list.size()) {
                String word_after_inr = bank_msg_list.get(index_of_inr + 1);
                try {
                    double curr_val = Double.parseDouble(word_after_inr.replace(",", ""));
                    tot_sum += curr_val;
                } catch (NumberFormatException e) {
                    Log.e("TAG", "Invalid amount format in SCB SMS: " + word_after_inr);
                }
                // Return total sum for AXIS and exit the method
                return tot_sum;
            } else {
                Log.e("TAG", "Index of 'on' not found or out of bounds in SCB SMS: " + index_of_inr);
            }
        }else if (Objects.equals(Bank, "HDFC")) {
            // HDFC bank processing
            //logic: look for "INR." or "Rs" and extract amount
            int index_of_spent = bank_msg_list.indexOf("spent");
            if (index_of_spent == -1) {
                index_of_spent = bank_msg_list.indexOf("Rs.");
            }
            if (index_of_spent != -1 && index_of_spent + 1 < bank_msg_list.size()) {
                String word_after_key = bank_msg_list.get(index_of_spent + 1);
                try {
                    double curr_val = Double.parseDouble(word_after_key.replace(",", ""));
                    tot_sum += curr_val;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid amount format in HDFC SMS: " + word_after_key);
                }
                // Return total sum for HDFC and exit the method
                return tot_sum;
            } else {
                Log.e(TAG, "Index of 'INR.' or 'Rs' not found or out of bounds in HDFC SMS: " + index_of_spent);
            }
        }

        else {
            // Handle unsupported banks
            Log.e(TAG, "This bank is not supported yet: " + Bank);
            uiHandler.post(()-> Toast.makeText(MainActivity.this, "This bank is not supported yet: "+Bank,Toast.LENGTH_SHORT).show());
        }

        // Return total sum if bank was processed successfully, otherwise 0.00
        return tot_sum;
    }



    private class FetchSmsTasknew implements Runnable {
        private final ArrayList<String> Banks_to_get;
        private final String month;
        private final String year;

        private FetchSmsTasknew(ArrayList<String> Banks_to_get, String month, String year) {
            this.Banks_to_get = Banks_to_get;
            this.month = month;
            this.year = year;
        }

        @Override
        public void run() {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                List<Sms> smsList = getAllSms();
                Log.e("TAG", String.valueOf(smsList.size()));

                if (!smsList.isEmpty()) {
                    System.out.println("All sms list is not empty");
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "payment_db")
                            .fallbackToDestructiveMigration()
                            .build();
                    UserDao dao = db.userDao();

                    System.out.println("Barentries initialized");
                    List<BarEntry> barEntries = new ArrayList<>();


                    for (int i = 1; i <= 31; i++) {
                        barEntries.add(new BarEntry(i, 0));
                    }

                    System.out.println(barEntries);
                    System.out.println("These are the Barentries and they all should be empty for now");
                    uiHandler.post(() -> prog_disp.setText("Calculating"));

//                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//                    boolean isFirstRun = sharedPreferences.getBoolean(FIRST_RUN, true);
//                    Log.e("TAG", "isFirstRun hit");
//
//                    long lastProcessedTime = getLastProcessedTime();
//                    long maxTime = lastProcessedTime;

//                    System.out.println("Getting the required sms messages");
                    ArrayList<Sms> reqAdds = get_req_sms_list(Banks_to_get, smsList);
//                    System.out.println(reqAdds);
//                    System.out.println("Getting the required message dates");
//                    ArrayList<ArrayList<String>> getTimes = get_req_sms_date_list(Banks_to_get, smsList);
//                    System.out.println("Checking if both are of the same size or not");
//                    System.out.println(reqAdds.size() == getTimes.size());



                    Log.e("TAG", String.valueOf(!reqAdds.isEmpty()));
                    if (!reqAdds.isEmpty()) {
                        System.out.println("The required sms messages are not empty, hence there are banks provided in the bank_list");
                        final double[] sumArray = {0.00};

                        for (int k = 0; k < reqAdds.size(); k++) {
//                            System.out.println("Started going through SMS");
                            double perSMSamount = 0.00;

//                            System.out.println("SMS Date and Message initialized");
                            String msg = reqAdds.get(k).getMsg();
                            double time = reqAdds.get(k).getTime();

//                            System.out.println("This is the sender's addresss" +reqAdds.get(k).getAddress());


                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                            String formattedDate = df.format(time);
                            ArrayList<String> msgTime = new ArrayList<>(Arrays.asList(formattedDate.split("-")));


                            for (String bank : Banks_to_get) {
                                double curr_amount = getSum(bank, msg);
                                if (curr_amount > 0 ) {
                                    perSMSamount = curr_amount;
//                                    System.out.println();
//                                    System.out.println(curr_amount);
                                    if(curr_amount>40000){
                                        System.out.println(msgTime);
                                    }
                                    break;
                                }
                            }

//                            System.out.println("This SMSs total " + perSMSamount);

                            int day = Integer.parseInt(msgTime.get(0));
//                            System.out.println("This is the date of the message");
////                            System.out.println(msgTime);



                            if (Objects.equals(year, msgTime.get(2).substring(2)) && Objects.equals(month, msgTime.get(1))) {
//                                Log.d("TAG", "Setting BarEntries for this SMS");
                                BarEntry barEntry = barEntries.get(day - 1);
                                if (barEntry != null) {
//                                    System.out.println("This bar Entry is already populated with :"+barEntry.getY());
                                    float old_val = barEntry.getY();
                                    float new_val = (float) perSMSamount + old_val;
                                    barEntries.set(day - 1, new BarEntry(day, new_val));
                                    Log.d("TAG", "Updated bar entry for day " + day);
//                                    System.out.println("Old val :" + old_val);
//                                    System.out.println("New val " + new_val);
                                    sumArray[0] += perSMSamount;
//                                    System.out.println("Total till now: " + sumArray[0]);
                                }
                                else{
//                                    System.out.println("This barENTRY is going to get its new value");
                                    float val_to_add = (float) perSMSamount;
                                    barEntries.set(day-1,new BarEntry(day,val_to_add));
//                                    Log.d("TAG", "Updated bar entry for day " + day);
//                                    System.out.println("val_added :" + val_to_add);
                                    sumArray[0] += perSMSamount;
//                                    System.out.println("Total till now: " + sumArray[0]);
                                }

                            }
                        }

                        System.out.println("All required SMSs read, now we are making the graph");

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
                            tv3.setText(Double.toString(sumArray[0]));
                            tot_head.setText("Spending in "+month+" "+year);
                        });

                    } else {
                        Log.e("TAG", "No such messages found from the banks mentioned in the list " + Banks_to_get);
                        uiHandler.post(()->Toast.makeText(MainActivity.this,"No such messages found from the banks mentioned in the list",Toast.LENGTH_SHORT).show());
                        startActivity(new Intent(MainActivity.this,Graph.class));

                    }
                }

            }
            else {
                uiHandler.post(() -> Toast.makeText(MainActivity.this, "No SMS could be pulled. Please check your permissions.", Toast.LENGTH_SHORT).show());
            }
        }
    }






}



