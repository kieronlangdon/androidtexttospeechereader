package hughpearse.myapplication004;

import org.solovyev.android.checkout.Billing;
import android.app.Application;
import javax.annotation.Nonnull;

public class CheckoutApplication extends Application {
    private static CheckoutApplication sInstance;

    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @Nonnull
        @Override
        public String getPublicKey() {
            return "Your public key, don't forget about encryption";
        }
    });

    public CheckoutApplication() {
        sInstance = this;
    }

    public static CheckoutApplication get() {
        return sInstance;
    }

    public Billing getBilling() {
        return mBilling;
    }
}
