package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class payment_log_logic extends AppCompatActivity {

    ImageButton hom_but_lp, chang_but_lp, log_but_lp;
    ArrayList<pay_log_struct> pay_dataset;

    ArrayList<Sms> sms_List;
    ArrayList<String> req_sms_add;
    ArrayList<ArrayList<String>> req_sms_add_times;
    Bundle bundle_mp_lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_logs);

        Bundle bundle_mp_lp= getIntent().getExtras();
        if (!bundle_mp_lp.isEmpty()) {
            sms_List = (ArrayList<Sms>) bundle_mp_lp.get("list_sms");
            req_sms_add=(ArrayList<String>) bundle_mp_lp.get("sms_adds");
            req_sms_add_times= (ArrayList<ArrayList<String>>) bundle_mp_lp.get("sms_add_times");
        }


        ArrayList<pay_log_struct> made_dataset=make_pay_dataset(sms_List,req_sms_add,req_sms_add_times);

        // Initializing RecyclerView
        RecyclerView pay_logs = findViewById(R.id.pay_log_rv);

        // Setting up the RecyclerView with a layout manager and adapter
        pay_logs.setLayoutManager(new LinearLayoutManager(this));
        Pay_log_RV_Adapter prvAdapter = new Pay_log_RV_Adapter(this, made_dataset);
        pay_logs.setAdapter(prvAdapter);

        // Initializing buttons
        hom_but_lp = findViewById(R.id.home_but);
        chang_but_lp = findViewById(R.id.change_but);
        log_but_lp = findViewById(R.id.log_but);

        // Setting onClickListeners for the buttons
        hom_but_lp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lp_to_mp = new Intent(payment_log_logic.this, MainActivity.class);
                lp_to_mp.putExtra("sms_adds_back",req_sms_add);
                startActivity(lp_to_mp);

            }
        });

        chang_but_lp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lp_to_gp = new Intent(payment_log_logic.this, Graph.class);
                startActivity(lp_to_gp);
            }
        });

        log_but_lp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lp_to_lp = new Intent(payment_log_logic.this, payment_log_logic.class);
                startActivity(lp_to_lp);
            }
        });
    }


    public ArrayList<pay_log_struct> make_pay_dataset(ArrayList<Sms> req_sms_list,ArrayList<String> req_bank_list,ArrayList<ArrayList<String>> req_sms_dates){
        ArrayList<pay_log_struct> pay_dataset = new ArrayList<pay_log_struct>();
        for(int k=0;k<req_sms_list.size();k++){
            double curr_val=0.00;
            String hit_bank=null;
            for(String bank:req_bank_list){
                if(req_sms_list.get(k).getAddress().contains(bank)){
                    curr_val=getSum(bank,req_sms_list.get(k).getMsg());
                    if (curr_val>0){
                        hit_bank=bank;
                        System.out.println("Got a bank :"+hit_bank);
                        System.out.println("Got the value :"+curr_val);
                        break;
                    }
                }
                else {
                    System.out.println("Yep, not this bank's message :"+bank);
                    continue;
                }

            }
            if(curr_val!=0.0 & hit_bank!=null){
                System.out.println("Both bank and value are not null/zero");
                ArrayList<String> date = req_sms_dates.get(k);

                String payee=getPayee(hit_bank,req_sms_list.get(k).getMsg());
                System.out.println("This is the paymee: "+payee);
                String date_combined= String.join("-",date);
                System.out.println("This is the payment date: "+date);
                System.out.println("This is the value: "+curr_val);

                pay_log_struct curr_sms_pls= new pay_log_struct(payee,Double.toString(curr_val),date_combined);
                pay_dataset.add(curr_sms_pls);

            }

        }





//        for(int k=0;k<sms_list.size();k++){
//            Sms curr_sms=sms_list.get(k);
//                String[] msgArray = curr_sms.getMsg().split(" ");
//                for (int i = 1; i < msgArray.length; i++) {
//                    if ("on".equals(msgArray[i])) {
//
//                        //Got the "on" in the message; now just assign the amount and dates with a bit of cleaning;
//                        String wordBeforeOn = msgArray[i - 1];
//
//                        String payee= msgArray[i+2];
//
//
//                        String wordAfterOn = msgArray[i + 1].substring(0, msgArray[i + 1].length() - 1);
//                        ArrayList<String> date = new ArrayList<String>(Arrays.asList(wordAfterOn.split("-")));
//                        pay_log_struct curr_sms_pls= new pay_log_struct(payee,wordBeforeOn,wordAfterOn);
//                        pay_dataset.add(curr_sms_pls);
//                        break;
//                    }
//                }
//
//            }
        return pay_dataset;

    }

    //pay_log_struct curr_sms_pls= new pay_log_struct(payee-->(Bank names),wordBeforeOn--->(Amount),wordAfterOn-->(Date));
    //                        pay_dataset.add(curr_sms_pls);
    //                        break;

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
                    Log.e("TAG", "Invalid amount format in ICICI SMS: " + word_before_on);
                }
                // Return total sum for ICICI and exit the method
                return tot_sum;
            } else {
                Log.e("TAG", "Index of 'on' not found or out of bounds in ICICI SMS: " + index_of_on);
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
                    Log.e("TAG", "Invalid amount format in IDBI SMS: " + word_after_key);
                }
                // Return total sum for IDBI and exit the method
                return tot_sum;
            } else {
                Log.e("TAG", "Index of 'INR.' or 'Rs' not found or out of bounds in IDBI SMS: " + index_of_key);
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
                    Log.e("TAG", "Invalid amount format in SBI SMS: " + word_before_on);
                }
                // Return total sum for SBI and exit the method
                return tot_sum;
            } else {
                Log.e("TAG", "Index of 'on' not found or out of bounds in SBI SMS: " + index_of_on);
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
        } else {
            // Handle unsupported banks
            Log.e("TAG", "This bank is not supported yet: " + Bank);
            Toast.makeText(this,"This bank is not supported yet: "+Bank,Toast.LENGTH_SHORT).show();

        }

        // Return total sum if bank was processed successfully, otherwise 0.00
        return tot_sum;
    }

    public String getPayee(String Bank,String bank_msg){
        String payee= null;
        if(Objects.equals(Bank, "ICICI")){
            ArrayList<String> bank_msg_list= new ArrayList<>(Arrays.asList(bank_msg.split(" ")));
            int index_of_on=bank_msg_list.indexOf("on");
            String word_before_on=bank_msg_list.get(index_of_on-1);
            payee=bank_msg_list.get(index_of_on+2);
        } else if (Objects.equals(Bank, "IDBI")) {
            ArrayList<String> bank_msg_list= new ArrayList<>(Arrays.asList(bank_msg.split(" ")));
            int index_of_key=bank_msg_list.indexOf("INR.");

            if(index_of_key==-1){
                index_of_key=bank_msg_list.indexOf("Rs");
            }
            int payee_index=bank_msg_list.indexOf("credited")-1;

            if(payee_index==-1){
                payee="Unknown";
            }
            String word_after_key=bank_msg_list.get(index_of_key+1);
            payee=bank_msg_list.get(payee_index);
        } else if (Objects.equals(Bank, "SBI")) {

            ArrayList<String> bank_msg_list= new ArrayList<>(Arrays.asList(bank_msg.split(" ")));
            int index_of_to=bank_msg_list.indexOf("to");

            if(index_of_to==-1){
                payee="Unknown";
                Log.e("TAG","Couldn't process the Payee");
            }
            else{
                String word_before_to=bank_msg_list.get(index_of_to-1);
                payee=bank_msg_list.get(index_of_to+1);
            }

        } else if (Bank=="KOTAKB") {
            ArrayList<String> bank_msg_list= new ArrayList<>(Arrays.asList(bank_msg.split(" ")));

            int index_of_towards=bank_msg_list.indexOf("towards");

            if(index_of_towards!=0){
                payee=bank_msg_list.get(index_of_towards+1);
            }
            else{
                payee="Unknown";
            }
        } else{
            Log.e("TAG","This bank is not supported yet :"+Bank);

        }
        return payee;

    }

}
