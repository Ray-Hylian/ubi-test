package com.example.ubi_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubi_test.Utils.Capture;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_SCAN = 10;
    private final String TAG = MainActivity.class.getSimpleName();
    private TextView mainText;
    private TextView resultText;
    private ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainText = findViewById(R.id.main_txt);
        resultText = findViewById(R.id.result_txt);
        resultImage = findViewById(R.id.result_img);

    }

    /**
     * Initializing the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Initializing the different choices
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // if click on scan, the simple scan is launched
            case R.id.scan:
                //Initialize intent integrator
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                //Set prompt text
                intentIntegrator.setPrompt("For flash use volume up");
                //Set beep
                intentIntegrator.setBeepEnabled(true);
                //Locked orientation
                intentIntegrator.setOrientationLocked(true);

                //Set capture activity
                //intentIntegrator.setCaptureActivity(Capture.class);

                //Initiate scan
                intentIntegrator.initiateScan();
                return true;

            // if click on timer, the countdown is launched and the number of scanned codes should be displayed
            case R.id.timer:
                Intent intent = new Intent(getApplicationContext(), TimerScannerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Results management according to scan (simple or timer)
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //for timer scanner
        if (requestCode == REQUEST_CODE_SCAN) {
            mainText.setText("Total: " + data.getIntExtra("result", 0));
            commentResult();

            //for simple scan
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * Scoring according to number of scanned items
     */
    public void commentResult(){
        if(REQUEST_CODE_SCAN <= 5){
            resultText.setText("Not enough, 1/3");
            resultImage.setImageResource(R.drawable.insufficient);
        }
        if(REQUEST_CODE_SCAN < 6 && REQUEST_CODE_SCAN >= 10){
            resultText.setText("Not too bad, 2/3");
            resultImage.setImageResource(R.drawable.average);
        }
        else{
            resultText.setText("Very good, 3/3");
            resultImage.setImageResource(R.drawable.good);

        }
    }
}