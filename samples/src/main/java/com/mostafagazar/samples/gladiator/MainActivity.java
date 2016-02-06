package com.mostafagazar.samples.gladiator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mostafagazar.samples.gladiator.util.IntentsUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isHandled = IntentsUtil.composeEmail(MainActivity.this,
                        "mmegazar+gladiator@gmail.com", "[gladiator] support");

                if (!isHandled) {
                    Snackbar.make(view, "Contact me for support at mmegazar+gladiator@gmail.com",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
