package hughpearse.myapplication004;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SelectFileActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 2;
    private static final String TAG = "Class-SelectFileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        Log.d(TAG, "Loading file");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    data.putExtra("fileUri", uri);
                } else {
                    Log.d(TAG, "Result not OK");
                }
                break;
        }
        this.setResult(RESULT_OK, data);
        this.finish();
    }
}
