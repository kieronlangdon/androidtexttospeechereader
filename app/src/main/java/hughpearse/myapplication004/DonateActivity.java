package hughpearse.myapplication004;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Inventory.Product;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.Sku;
import org.solovyev.android.checkout.Skus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;


public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "Class-DonateActivity";
    public static final int REQUEST_CODE = 1001;
    private final ActivityCheckout mCheckout = Checkout.forActivity(this, CheckoutApplication.get().getBilling());
    private Inventory mInventory;

    //listener for purchases
    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        @Override
        public void onSuccess(Purchase purchase) {
            //all purchased items are consumed by default
            consume(purchase);
        }

        @Override
        public void onError(int response, Exception e) {
            // handle errors here
        }
    }

    //listener for consumed items
    private <T> RequestListener<T> makeRequestListener() {
        return new RequestListener<T>() {
            @Override
            public void onSuccess(@Nonnull T result) {
                /*
                mInventory.load(
                        Inventory.Request.create()
                        .loadAllPurchases()
                        .loadSkus(ProductTypes.IN_APP, getInAppSkus()), new InventoryCallback());
                */
            }

            @Override
            public void onError(int response, @Nonnull Exception e) {
            }
        };
    }



    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(Inventory.Products products) {
            Button donateButton1 = (Button) findViewById(R.id.donateButton1);
            Button donateButton5 = (Button) findViewById(R.id.donateButton5);
            Button donateButton10 = (Button) findViewById(R.id.donateButton10);
            Button donateButton15 = (Button) findViewById(R.id.donateButton15);

            for(Product p : products){
                for(Sku sku : p.getSkus()){
                    if(sku.id.code == "donate_1_euro"){
                        donateButton1.setText("Donate €" + sku.detailedPrice.toString());
                    } else if(sku.id.code == "donate_5_euro"){
                        donateButton5.setText("Donate €" + sku.detailedPrice.toString());
                    } else if(sku.id.code == "donate_10_euro"){
                        donateButton10.setText("Donate €" + sku.detailedPrice.toString());
                    } else if(sku.id.code == "donate_15_euro"){
                        donateButton15.setText("Donate €" + sku.detailedPrice.toString());
                    }
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        mCheckout.start();
        mCheckout.createPurchaseFlow(new PurchaseListener());
        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                //.loadAllPurchases()
                .loadSkus(ProductTypes.IN_APP, getInAppSkus()), new InventoryCallback());

        Button donateButton1 = (Button) findViewById(R.id.donateButton1);
        Button donateButton5 = (Button) findViewById(R.id.donateButton5);
        Button donateButton10 = (Button) findViewById(R.id.donateButton10);
        Button donateButton15 = (Button) findViewById(R.id.donateButton15);

        donateButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, "donate_1_euro", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
        });

        donateButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, "donate_5_euro", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
        });

        donateButton10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, "donate_10_euro", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
        });

        donateButton15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, "donate_15_euro", null, mCheckout.getPurchaseFlow());
                        Inventory.Product mProduct = Inventory.Products.empty().get(ProductTypes.IN_APP);
                    }
                });
            }
        });
    }

    private static List<String> getInAppSkus() {
        final List<String> skus = new ArrayList<>();
        skus.addAll(Arrays.asList("donate_1_euro", "donate_5_euro", "donate_10_euro", "donate_15_euro"));
        return skus;
    }

    private void consume(final Purchase purchase) {
        mCheckout.whenReady(new Checkout.EmptyListener() {
            @Override
            public void onReady(@Nonnull BillingRequests requests) {
                requests.consume(purchase.token, makeRequestListener());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCheckout.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }
}
