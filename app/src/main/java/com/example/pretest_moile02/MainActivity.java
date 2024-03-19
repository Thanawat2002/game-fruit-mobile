package com.example.pretest_moile02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout conLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conLayout = (ConstraintLayout) findViewById(R.id.conLayout);
        conLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(new GraphicsView(MainActivity.this));
                Toast.makeText(MainActivity.this, "Play game now!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

