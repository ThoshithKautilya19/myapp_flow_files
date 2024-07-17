package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Graph extends AppCompatActivity {

    EditText sms_address;
    Button sub_but;

    ImageButton hom_but_sp, chang_but_sp, log_but_sp;

    Spinner spinnerBanks;

    ArrayList<String> Bank_names;

    boolean default_selected = true; // Flag to check if it's the first selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.graph_page);

        // sms_address = (EditText) findViewById(R.id.add_input);

        // buttons
        sub_but = (Button) findViewById(R.id.sub_add_but);

        hom_but_sp = findViewById(R.id.home_but);
        chang_but_sp = findViewById(R.id.change_but);
        log_but_sp = findViewById(R.id.log_but);

        Bank_names = new ArrayList<>();

        // Initialising spinner for drop downs
        spinnerBanks = findViewById(R.id.input_bank_name);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.banks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinnerBanks.setAdapter(adapter);

        spinnerBanks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedBank = parent.getItemAtPosition(position).toString();
                    selectedBank=selectedBank.trim();
                    Log.e("TAG",selectedBank);
                    if(default_selected){
                        default_selected=false;
                    }
                    else {
                        if (!Bank_names.contains(selectedBank)) {
                            Bank_names.add(selectedBank);
                            Toast.makeText(parent.getContext(), "Bank selected: " + selectedBank, Toast.LENGTH_SHORT).show();
                        }
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sub_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> bank_name_to_take = Bank_names;
                // String address = sms_address.getText().toString();
                Log.d("TAG", String.valueOf(bank_name_to_take));
                if (bank_name_to_take == null || bank_name_to_take.isEmpty()) {
                    Toast.makeText(Graph.this, "Select some options", Toast.LENGTH_SHORT).show();
                } else {
                    Intent main_page = new Intent(Graph.this, MainActivity.class);
                    main_page.putExtra("addr", bank_name_to_take);
                    startActivity(main_page);
                }
            }
        });

        hom_but_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // final String address = sms_address.getText().toString();
                if (Bank_names.isEmpty()) {
                    Toast.makeText(Graph.this, "Please select some options", Toast.LENGTH_SHORT).show();
                } else {
                    Intent gp_to_main_page = new Intent(Graph.this, MainActivity.class);
                    startActivity(gp_to_main_page);
                }

            }
        });

        chang_but_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gp_to_gp = new Intent(Graph.this, Graph.class);
                startActivity(gp_to_gp);
            }
        });

        log_but_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // final String address = sms_address.getText().toString();
                if (Bank_names.isEmpty()) {
                    Toast.makeText(Graph.this, "Please select some options", Toast.LENGTH_SHORT).show();
                } else {
                    Intent gp_to_main_page = new Intent(Graph.this, MainActivity.class);
                    startActivity(gp_to_main_page);
                }
            }
        });
    }
}
