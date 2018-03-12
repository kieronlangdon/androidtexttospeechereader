package hughpearse.textToSpeechReader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_SELECT_ACTIVITY_CODE = 1;
    private static final int HANDLE_FILE_TYPES_CODE = 3;
    private static final int DISPLAY_READER_CODE = 4;
    private static final int DONATE_ACTIVITY_CODE = 5;
    private static final String TAG = "Class-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestFileReadPermission();
        Button button1 = (Button) findViewById(R.id.openFileButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        SelectFileActivity.class);
                startActivityForResult(myIntent, FILE_SELECT_ACTIVITY_CODE);
            }
        });
        Button button2 = (Button) findViewById(R.id.donateMoneyButton);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        DonateActivity.class);
                startActivityForResult(myIntent, DONATE_ACTIVITY_CODE);
            }
        });
    }

    private boolean requestFileReadPermission () {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission. READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission. READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_ACTIVITY_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Uri uri = bundle.getParcelable("fileUri");
                    Intent myIntent = new Intent(MainActivity.this,
                            HandleFileTypes.class);
                    myIntent.putExtra("fileUri", uri);
                    startActivityForResult(myIntent, HANDLE_FILE_TYPES_CODE);
                } else {
                    Log.d(TAG, "Result not OK, could not select file");
                }
                break;
            case HANDLE_FILE_TYPES_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    ArrayList<String> extractedText = bundle.getStringArrayList("sentences");
                    Intent myIntent = new Intent(MainActivity.this,
                            ReaderActivity.class);
                    myIntent.putExtra("sentences", extractedText);
                    startActivity(myIntent);
                } else {
                    Log.d(TAG, "Result not OK, could not read file");
                }
                break;
            case DISPLAY_READER_CODE:
                if (resultCode == RESULT_OK) {

                }
                break;
            case DONATE_ACTIVITY_CODE:
                if (resultCode == RESULT_OK) {

                }
                break;
        }
    }
}
