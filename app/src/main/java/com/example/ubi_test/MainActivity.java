package com.example.ubi_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ubi_test.databinding.ActivityMainBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_SCAN = 10;
    private final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
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
            binding.mainTxt.setText("Total: " + data.getIntExtra("result", 0));
            commentResult(data);

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
     * @param data
     */
    public void commentResult(Intent data){

        int score = data.getIntExtra("result", 0);

        if(score <= 5){
            binding.resultTxt.setText("Not enough");
            binding.resultImg.setImageResource(R.drawable.insufficient);
        }

        else if(score > 5 && score <= 10){
            binding.resultTxt.setText("Not too bad");
            binding.resultImg.setImageResource(R.drawable.average);
        }

        else{
            binding.resultTxt.setText("Very good");
            binding.resultImg.setImageResource(R.drawable.good);

        }
    }
}