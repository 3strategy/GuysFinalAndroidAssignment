package com.example.guysassignment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.guysassignment.databinding.ActivityMainBinding;


import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private long lastUpdate = 0;
    private static final int SHAKE_THRESHOLD = 800;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.guysassignment.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // added data initialization for testing:
        // Since data persists using androids SharedPreferences this initialization code only needs to be run once.
        // After that, lines should be commented and data will be fetched from the SharedPreferences .
        //        SharedViewModel vm = new ViewModelProvider(this).get(SharedViewModel.class);
        //        vm.setName("Guy");
        //        vm.setFamilyName("Siedes");
        //        vm.setBestScore(42);


        // ===================== sensor usage סנסורים וחיישנים ========================
        // ============================================================================

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Accelerometer not available!", Toast.LENGTH_SHORT).show();
        }
        // ================ end of accelerometer סנסורים וחיישנים =====================



        // ALARMS & NOTIFICATIONS ===============================
        // check for permissions
        try {


            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
        catch (Exception e)
        {
            Log.d("onCreate", "Exception: ");
        }

        setDailyReminder();
        // ============================= End of alarms and notifications



    }

    private void setDailyReminder() {
        try {


            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 13); // set hour for a scheduled alarm (notification)
            calendar.set(Calendar.MINUTE, 33);
            calendar.set(Calendar.SECOND, 0);

            // if the the time set for this alarm already passed today - schedule it for tomorrow.
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
        catch (Exception e)
        {
            Log.d("setDailyRem", "Exception in setDaily ");
        }
    }


    // ============ sensor events ==============================
    // =========================================================
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                long currentTime = System.currentTimeMillis();
                if ((currentTime - lastUpdate) > 100) { // sample every 100 ms
                    long timeDifference = (currentTime - lastUpdate);
                    lastUpdate = currentTime;

                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    float deltaX = x - lastX;
                    float deltaY = y - lastY;
                    float deltaZ = z - lastZ;
                    float acceleration = (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeDifference * 10000;

                    if (acceleration > SHAKE_THRESHOLD) {
                        Toast.makeText(MainActivity.this, "Shake detected! Closing application...", Toast.LENGTH_SHORT).show();
                        finish(); // close the application
                    }

                    lastX = x;
                    lastY = y;
                    lastZ = z;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // not required here
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

}