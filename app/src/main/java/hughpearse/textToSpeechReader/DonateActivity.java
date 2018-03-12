package hughpearse.textToSpeechReader;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "Class-DonateActivity";
    public static final int REQUEST_CODE = 1001;
    boolean iapBind = false;

    IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        Button donateButton1 = findViewById(R.id.donateButton1);
        Button donateButton5 = findViewById(R.id.donateButton5);
        Button donateButton10 = findViewById(R.id.donateButton10);
        Button donateButton15 = findViewById(R.id.donateButton15);


        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        iapBind = bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        donateButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                purchaseItem("donate_1_euro");
            }
        });

        donateButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                purchaseItem("donate_5_euro");
            }
        });

        donateButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                purchaseItem("donate_10_euro");
            }
        });

        donateButton15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                purchaseItem("donate_15_euro");
            }
        });
        /*
        try {
            int isBillingSupported = mService.isBillingSupported(3, getPackageName(), "inapp");
            Log.d(TAG, "isBillingSupported() - success : return " + String.valueOf(isBillingSupported));
        } catch (RemoteException e) {
            Log.d(TAG, "isBillingSupported() - fail!");
            e.printStackTrace();
        }
        */
    }

    private void purchaseItem(String sku){
        if (!iapBind) return;
        if (mService == null) return;

        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", "");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            startIntentSenderForResult(
                    pendingIntent.getIntentSender(),
                    REQUEST_CODE,
                    new Intent(),
                    0,
                    0,
                    0
            );
        } catch (IntentSender.SendIntentException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String token = jo.getString("purchaseToken");
                    Log.d(TAG, "You have bought the " + sku + ".");
                    int response = mService.consumePurchase(3, getPackageName(), token);
                }
                catch (RemoteException | JSONException e) {
                    Log.d(TAG, "Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}
