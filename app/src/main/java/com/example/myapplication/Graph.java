package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Graph extends AppCompatActivity {

    EditText sms_address;
    Button sub_but;

    ImageButton hom_but_sp,chang_but_sp,log_but_sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.graph_page);

        sms_address=(EditText)findViewById(R.id.add_input);

        //buttons
        sub_but= (Button) findViewById(R.id.sub_add_but);

        hom_but_sp= findViewById(R.id.home_but);
        chang_but_sp=findViewById(R.id.change_but);
        log_but_sp=findViewById(R.id.log_but);







        sub_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address= sms_address.getText().toString();
                Log.d("TAG",address);
                if (address.isEmpty() || address.equals("Ex. \"AD-ICICIT\"")){
                    Toast.makeText(Graph.this, "Enter a proper SMS Address", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent main_page = new Intent(Graph.this, MainActivity.class);
                    ArrayList<String> addr_list= new ArrayList<>(Arrays.asList((address.split(","))));
                    main_page.putExtra("addr", addr_list);
                    startActivity(main_page);
                }
            }
        });

        hom_but_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String address= sms_address.getText().toString();
                if (address.isEmpty() || address.equals("Ex. \"AD-ICICIT\"")){
                    Toast.makeText(Graph.this, "Enter a proper SMS Address", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent gp_to_main_page = new Intent(Graph.this,MainActivity.class);
                    startActivity(gp_to_main_page);
                }


            }
        });

        chang_but_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gp_to_gp = new Intent(Graph.this,Graph.class);
                startActivity(gp_to_gp);
            }
        });

        log_but_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String address= sms_address.getText().toString();
                if (address.isEmpty() || address.equals("Ex. \"AD-ICICIT\"")){
                    Toast.makeText(Graph.this, "Enter a proper SMS Address", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent gp_to_main_page = new Intent(Graph.this,MainActivity.class);
                    startActivity(gp_to_main_page);
                }
            }
        });
    }



}










