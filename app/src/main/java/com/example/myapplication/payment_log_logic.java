package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;

public class payment_log_logic extends AppCompatActivity {

    ImageButton hom_but_lp, chang_but_lp, log_but_lp;
    ArrayList<pay_log_struct> pay_dataset;

    ArrayList<Sms> sms_List;
    ArrayList<String> req_sms_add;
    Bundle bundle_mp_lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_logs);

        Bundle bundle_mp_lp= getIntent().getExtras();
        if (!bundle_mp_lp.isEmpty()) {
            sms_List = (ArrayList<Sms>) bundle_mp_lp.get("list_sms");
            req_sms_add=(ArrayList<String>) bundle_mp_lp.get("sms_adds");
        }

        ArrayList<pay_log_struct> made_dataset=make_pay_dataset(sms_List);

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

    public ArrayList<pay_log_struct> make_pay_dataset(ArrayList<Sms> sms_list){
        ArrayList<pay_log_struct> pay_dataset = new ArrayList<pay_log_struct>();
        for(int k=0;k<sms_list.size();k++){
            Sms curr_sms=sms_list.get(k);
                String[] msgArray = curr_sms.getMsg().split(" ");
                for (int i = 1; i < msgArray.length; i++) {
                    if ("on".equals(msgArray[i])) {

                        //Got the "on" in the message; now just assign the amount and dates with a bit of cleaning;
                        String wordBeforeOn = msgArray[i - 1];

                        String payee= msgArray[i+2];


                        String wordAfterOn = msgArray[i + 1].substring(0, msgArray[i + 1].length() - 1);
                        ArrayList<String> date = new ArrayList<String>(Arrays.asList(wordAfterOn.split("-")));
                        pay_log_struct curr_sms_pls= new pay_log_struct(payee,wordBeforeOn,wordAfterOn);
                        pay_dataset.add(curr_sms_pls);
                        break;
                    }
                }

            }
        return pay_dataset;

    }

}
