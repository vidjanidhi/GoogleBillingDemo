package demo.googlebillingdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import demo.googlebillingdemo.util.IabHelper;
import demo.googlebillingdemo.util.IabResult;
import demo.googlebillingdemo.util.Inventory;
import demo.googlebillingdemo.util.Purchase;

public class MainActivity extends AppCompatActivity {
    private Button clickButton;
    private Button buyButton;
    private static final String TAG =
            "demo.googlebillingdemo";
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buyButton = (Button) findViewById(R.id.buyButton);
        clickButton = (Button) findViewById(R.id.clickButton);
        if (clickButton != null) {
            clickButton.setEnabled(false);
        }
        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjnYNEUkKYZRITRq5gyTXOqI3XNyk1PZTd9DfyIG0Z7+mIwu24CIL53ROJ+zVN2PnsBbusQthZ8cTzagm4pzBAPGYbPC97eXAMU9Yf4/2NwGvjxvWxekfSsfoEfeWsKPrR6mNw0aBGo9wZZVBRSQt9Sdildwn6Hc1EEPqJ7FtDUUnIitA1s/pzPdb6vtQkwssBToAXxEKGEwfcMiGcDDjwFIkLVSPm/r68tuYWBUA8H59buYmlEgLRPVo31z1DCdn9bmoXoccDVZeOU7ShK/2KZzHJRkcJ+uOUMHYXknOiPRHpy//5h1ZJxQsXTbbUew6fcRixbKRX+CzDoCaeYvZuwIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
                buyButton.setEnabled(false);
            }

        }
    };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {

                    } else {
                        // handle error
                    }
                }
            };
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
                clickButton.setEnabled(true);
            }
        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    public void buttonClicked(View view) {
        mHelper.launchPurchaseFlow(this, "demo.google123_secondbilling", IabHelper.ITEM_TYPE_SUBS, 10002, mPurchaseFinishedListener, "");
    }

    public void buyClick(View view) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
