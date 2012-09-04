package com.example.fitsbypact;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CreditCardActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_credit_card, menu);
        return true;
    }
}
