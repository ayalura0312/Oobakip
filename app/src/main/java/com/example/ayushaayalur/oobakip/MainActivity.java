package com.example.android.oobakip;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;



import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    int firstDigit;
    int secondDigit;
    TextView firstDigitTextView;
    TextView secondDigitTextView;
    TextView resultBox;
    EditText answer;
    TextView scoreBox;
    int score = 0;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;




    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstDigitTextView = (TextView) findViewById(R.id.firstDigitTextView);
        secondDigitTextView = (TextView) findViewById(R.id.secondDigitTextView);

        resultBox = (TextView) findViewById(R.id.resultBox);
        answer = (EditText) findViewById(R.id.answer);
        scoreBox = (TextView) findViewById(R.id.scoreValueTextView);

        newProblem();


        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkAnswer();
                    return true;
                }
                return false;
            }
        });



    }

    private int generateInteger(int length) {
        char[] chars = "123456789".toCharArray();
        Random random = new Random();
        return random.nextInt(chars.length);
    }

    public void checkAnswer()  {
        resultBox.setText("Hello");
        int value = Integer.parseInt(answer.getText().toString());
        if (firstDigit * secondDigit == value) {
            resultBox.setText("Correct!");
            if(score < 4){
                score = score + 1;
                newProblem();
                scoreBox.setText(Integer.toString(score));
            }
            else{
                score = 0;
                scoreBox.setText(Integer.toString(score));
                //resultBox.setText("You get a treat!");
                answer.setText("");
                newProblem();
                sendBluetooth();
                resultBox.setText("Enjoy your treat!");
            }

        } else {
            resultBox.setText("Wrong :(");

            Context context = getApplicationContext();
            CharSequence text = "Try this: Add " + firstDigit + " to itself " + secondDigit + " time(s)!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            answer.setText("");


        }
    }

    public void newProblem() {
        resultBox.setText("");
        answer.setText("");
        firstDigit = generateInteger(1);
        secondDigit = generateInteger(1);

        firstDigitTextView.setText(Integer.toString(firstDigit));
        secondDigitTextView.setText(Integer.toString(secondDigit));
        answer.setText("");
    }

    public void sendBluetooth() {
        String address = null;
        String name = null;
        BluetoothSocket btSocket = null;
        final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        address = myBluetooth.getAddress();

        if (myBluetooth == null)
        {
            resultBox.setText("Device does not support Bluetooth");
        }
        else
        {
            pairedDevices = myBluetooth.getBondedDevices(); //Get list of devices
            //String stringsize = String.valueOf(pairedDevices.size());
            //Toast.makeText(getApplicationContext(),"# of devices = " +  stringsize, Toast.LENGTH_SHORT).show();
            if( pairedDevices.size()>0 )
            {
                for ( BluetoothDevice bt : pairedDevices) {
                    address=bt.getAddress().toString(); //MAC address
                    name = bt.getName().toString();
                    Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();
                }

            }

            BluetoothDevice myArduino = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
            try {
                btSocket = myArduino.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                btSocket.connect();
                resultBox.setText("BT Name: "+name+"\nBT Address: "+address);
                String LedOn="1";
                btSocket.getOutputStream().write(LedOn.toString().getBytes());
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String LedOff="0";
                btSocket.getOutputStream().write(LedOff.toString().getBytes());
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
