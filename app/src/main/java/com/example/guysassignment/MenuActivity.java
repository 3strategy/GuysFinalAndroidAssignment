package com.example.guysassignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btnMain   = findViewById(R.id.btn_open_main);
        Button btnWallet = findViewById(R.id.btn_open_wallet);

        btnMain.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, MainActivity.class))
        );

        btnWallet.setOnClickListener(v ->
                startActivity(new Intent(MenuActivity.this, WalletActivity.class))
        );
    }
}
