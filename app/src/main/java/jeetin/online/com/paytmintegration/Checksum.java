package jeetin.online.com.paytmintegration;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    String custid="", orderId="", mid="",orderAmt="";

    //payment response variables

    TextView statusText;
    TextView paymentDetails;
    boolean paymentStatus = false;
    TextView refrenceIdTextResponse,paymentModeTextResponse,transactionStatusTextResponse,transactionTimeTextResponse;
    TextView refrenceIdText,paymentModeText,transactionStatusText,transactionTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_response );
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        statusText = findViewById(R.id.text_status);

        statusText = findViewById(R.id.text_status);
        paymentDetails = findViewById(R.id.paymentsDetails);
        refrenceIdTextResponse = findViewById(R.id.refrenceIdText);
        paymentModeTextResponse = findViewById(R.id.paymentModeText);
        transactionStatusTextResponse = findViewById(R.id.transactionStatusText);
        transactionTimeTextResponse  = findViewById(R.id.transactionTimeText);

        refrenceIdText = findViewById(R.id.refrenceid);
        paymentModeText = findViewById(R.id.paymentmode);
        transactionStatusText = findViewById(R.id.transactionstatus);
        transactionTimeText  = findViewById(R.id.transactiontime);



        statusText.setVisibility(View.GONE);

        paymentDetails.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);

        refrenceIdTextResponse.setVisibility(View.GONE);
        paymentModeTextResponse.setVisibility(View.GONE);
        transactionTimeTextResponse.setVisibility(View.GONE);
        transactionStatusTextResponse.setVisibility(View.GONE);
        refrenceIdText.setVisibility(View.GONE);
        paymentModeText.setVisibility(View.GONE);
        transactionStatusText.setVisibility(View.GONE);
        transactionTimeText.setVisibility(View.GONE);

        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderid");
        custid = intent.getExtras().getString("custid");
        mid = "GHPKIg50882916777390";                                       /// place your marchant key here
        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @SuppressLint("StaticFieldLeak")
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(Checksum.this);
        //private String orderId , mid, custid, amt;
        String url ="https://jeetinkranand.000webhostapp.com/generateChecksum.php";   // your url/api which returns you the checksum value
        String varifyurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;    // this url is for staging(Testing) purpose , provided by paytm.

        //"https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";      // For production Level this url is needed to be used.
        //"https://securegw-stage.paytm.in/theia/processTransaction"

        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(Checksum.this);
            String param=
                            "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custid+
                            "&CHANNEL_ID=WAP"+
                            "&TXN_AMOUNT=1000"+
                            "&WEBSITE=WEBSTAGING"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);

            // Here your will recieve checksum and order id

            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();   // This is for staging purpose.

            // PaytmPGService  Service = PaytmPGService.getProductionService();  // when app is ready to publish use production service

            // now call paytm service here

            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values

            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid);                             //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", "1000");
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            //paramMap.put( "EMAIL" , "abc@gmail.com");              // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");           // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");                // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(Checksum.this, true, true,
                    Checksum.this  );
        }
    }
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        if(bundle.getString("STATUS").equals("TXN_SUCCESS")){
            statusText.setVisibility(View.VISIBLE);
            paymentDetails.setVisibility(View.VISIBLE);
            refrenceIdTextResponse.setVisibility(View.VISIBLE);
            paymentModeTextResponse.setVisibility(View.VISIBLE);
            transactionTimeTextResponse.setVisibility(View.VISIBLE);
            transactionStatusTextResponse.setVisibility(View.VISIBLE);
            refrenceIdText.setVisibility(View.VISIBLE);
            paymentModeText.setVisibility(View.VISIBLE);
            transactionStatusText.setVisibility(View.VISIBLE);
            transactionTimeText.setVisibility(View.VISIBLE);
            statusText.setText("Congratulations!!");
            refrenceIdTextResponse.setText(bundle.getString("BANKTXNID"));
            paymentModeTextResponse.setText(bundle.getString("BANKNAME"));
            transactionStatusTextResponse.setText(bundle.getString("STATUS"));
            transactionTimeTextResponse.setText(bundle.getString("TXNDATE"));
        }else{
            statusText.setVisibility(View.VISIBLE);
            statusText.setText("Txn Failed");
        }

    }
    @Override
    public void networkNotAvailable() {
    }
    @Override
    public void clientAuthenticationFailed(String s) {
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
    }
}




