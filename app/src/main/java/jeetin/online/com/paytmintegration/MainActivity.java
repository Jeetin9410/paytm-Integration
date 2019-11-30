package jeetin.online.com.paytmintegration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    private TextView mPayBtn;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        mPayBtn = (TextView) findViewById( R.id.pay_btn );
        imageView = (ImageView)findViewById(R.id.image);

        final int orderId = getRandomNumberInRange(100,5000);
        final int cust_id = getRandomNumberInRange(100,5000);

        mPayBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Checksum.class);
                intent.putExtra("orderid",orderId+"");
                intent.putExtra("custid", cust_id+"");
                startActivity(intent);
            }
        } );



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
    }
    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return (int)(Math.random() * ((max - min) + 1)) + min;
    }

}
