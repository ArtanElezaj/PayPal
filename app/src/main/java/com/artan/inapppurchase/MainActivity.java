package com.artan.inapppurchase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    Button mBtnPay;
    TextView mResponse;
    PayPalConfiguration mConfiguration;
    //The id is the link to the paypal account, we have to create a REST API app in the paypal and get its id and paste it here
    String mPayPalClientId = "AQXpEbswXlsdhriHYp89KKeEGbmYbnHUl55SS6dkejQQuhbsvlVHITyN3Zv6I8Uk0FSqr2crBXpPX3pJ";
    Intent mService;
    int mPayPalRequestCode = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnPay = (Button)findViewById(R.id.btn_pay);
        mResponse = (TextView)findViewById(R.id.tv_response);
        mConfiguration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // sandbox for test, production for real
                .clientId(mPayPalClientId);

        mService = new Intent(this, PayPalService.class);
        mService.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mConfiguration); // configuration above
        startService(mService); // paypal service, listening to calls to paypal app

        mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });
    }
    public void pay(){
        PayPalPayment payment= new PayPalPayment(new BigDecimal(10), "USD", "Check out:",
                                    PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, mPayPalRequestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == mPayPalRequestCode){

            if(resultCode == Activity.RESULT_OK){

                //we have to confirm that payment worked to avoid fraud
                PaymentConfirmation paymentConfirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (paymentConfirmation != null){
                    String state = paymentConfirmation.getProofOfPayment().getState();

                    if (state.equals("approved")){
                        mResponse.setText("Payment Approved!");
                    }else{
                        mResponse.setText("Error in payment!");
                    }

                }else{
                    mResponse.setText("Confirmation is null");
                }


            }

        }


    }
}
