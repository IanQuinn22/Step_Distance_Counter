package android.e.stepdistancecounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView steps;
    private TextView distance;
    private Spinner lengths;
    private Button walk;
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private boolean isStepSensorPresent;
    private boolean registerFlag;
    int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createUI();
        init();
    }

    private void createUI(){
        steps = findViewById(R.id.steps);
        distance = findViewById(R.id.distance);
        lengths = findViewById(R.id.lengths);
        walk = findViewById(R.id.walk);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Integer[] typical_stride_lengths = {15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item,typical_stride_lengths);
        lengths.setAdapter(adapter);

        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (walk.getText().toString().equalsIgnoreCase("WALK")){
                    register();
                    walk.setText("STOP");
                    registerFlag =true;
                } else {
                    unregister();
                    walk.setText("WALK");
                    int currentSteps = stepCount;
                    stepCount = 0;
                    int stride_length = (int)lengths.getSelectedItem();
                    double dist = ((double)currentSteps * (double)stride_length) / 12.0;
                    distance.setText(String.valueOf(dist) + " ft");
                }
            }
        });
    }

    private void init(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isStepSensorPresent = true;
        } else {
            steps.setText("Sensor not Present");
            isStepSensorPresent = false;
        }
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
    }

    private void register(){
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            sensorManager.registerListener(this,stepCounter,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void unregister(){
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            sensorManager.unregisterListener(this,stepCounter);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == stepCounter){
            if (!registerFlag){
                stepCount++;
            }
            registerFlag = false;
            steps.setText(String.valueOf(stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}