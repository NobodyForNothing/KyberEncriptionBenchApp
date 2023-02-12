package de.rausch.richard.kyberEncriptionBenchApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.security.Provider;
import java.security.Security;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.removeProvider("BC");
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new BouncyCastlePQCProvider());

        // setup view
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        EditText iterField = findViewById(R.id.editTextNumber);
        iterField.setText(String.valueOf(1000));

        // setup spinner
        Spinner spinnerModes = findViewById(R.id.spinner2);
        spinnerModes.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> modes = ArrayAdapter.createFromResource(this, R.array.encription_modes, android.R.layout.simple_spinner_item);
        modes.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerModes.setAdapter(modes);
    }

    public void doTest(View view) {

        switch (position) {
            case 0:
                kyberTest();
                break;
            case 1:
                rsaTest();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void kyberTest() {
        EditText iterField = findViewById(R.id.editTextNumber);
        long iterCount = Integer.parseInt(iterField.getText().toString());

        long firstKeyGenTime = 0;
        long secondKeyGenTime = 0;
        long kemTime = 0;
        long startTimestamp = System.nanoTime();

        try {
            for (int i = 0; i < iterCount; i++) {
                long loopStartTimeStamp = System.nanoTime();

                // beide Kommunikationspartner erstellen ihre symmetrischen schlüssel
                KyberCommunicationPartner a = new KyberCommunicationPartner();
                long firstKeyGenTimestamp = System.nanoTime();

                KyberCommunicationPartner b = new KyberCommunicationPartner();
                long secondKeyGenTimestamp = System.nanoTime();

                // der AES Schlüssel wird ausgetauscht, ohne ihn direkt zu versenden
                a.connectTo(b);
                long kemTimestamp = System.nanoTime();

                // calculate times
                firstKeyGenTime += (firstKeyGenTimestamp-loopStartTimeStamp);
                secondKeyGenTime += (secondKeyGenTimestamp-firstKeyGenTimestamp);
                kemTime += (kemTimestamp-secondKeyGenTimestamp);
            }
            // capture Results
            long endTimestamp = System.nanoTime();
            String resultText = iterCount + " local Kyber key establishments finished in:\t" + ((endTimestamp-startTimestamp)/1000000000.0) + "s\n";

            resultText += "first keygen took a total of: " + (firstKeyGenTime/1000000000.0) + "s\n";
            resultText += "second keygen took a total of: " + (secondKeyGenTime/1000000000.0) + "s\n";
            resultText += "kems took a total of: " + (kemTime/1000000000.0) + "s\n";


            // add result to resultsTxtView
            TextView resultsTxtView = findViewById(R.id.textView);
            resultsTxtView.setText(resultText);

        } catch (Exception e) {
            TextView resultsTxtView = findViewById(R.id.textView);
            resultsTxtView.setText("error:\n"+e);
        }
    }

    private void rsaTest() {
        EditText iterField = findViewById(R.id.editTextNumber);
        long iterCount = Integer.parseInt(iterField.getText().toString());

        long firstKeyGenTime = 0;
        long secondKeyGenTime = 0;
        long kemTime = 0;
        long startTimestamp = System.nanoTime();

        try {
            for (int i = 0; i < iterCount; i++) {
                long loopStartTimeStamp = System.nanoTime();

                // beide Kommunikationspartner erstellen ihre symmetrischen schlüssel
                CommunicationPartner a = new RSACommunicationPartner();
                long firstKeyGenTimestamp = System.nanoTime();

                CommunicationPartner b = new RSACommunicationPartner();
                long secondKeyGenTimestamp = System.nanoTime();

                // der AES Schlüssel wird ausgetauscht, ohne ihn direkt zu versenden
                a.connectTo(b);
                long kemTimestamp = System.nanoTime();

                // calculate times
                firstKeyGenTime += (firstKeyGenTimestamp-loopStartTimeStamp);
                secondKeyGenTime += (secondKeyGenTimestamp-firstKeyGenTimestamp);
                kemTime += (kemTimestamp-secondKeyGenTimestamp);
            }
            // capture Results
            long endTimestamp = System.nanoTime();
            String resultText = iterCount + " local Kyber key establishments finished in:\t" + ((endTimestamp-startTimestamp)/1000000000.0) + "s\n";

            resultText += "first keygen took a total of: " + (firstKeyGenTime/1000000000.0) + "s\n";
            resultText += "second keygen took a total of: " + (secondKeyGenTime/1000000000.0) + "s\n";
            resultText += "kems took a total of: " + (kemTime/1000000000.0) + "s\n";


            // add result to resultsTxtView
            TextView resultsTxtView = findViewById(R.id.textView);
            resultsTxtView.setText(resultText);

        } catch (Exception e) {
            TextView resultsTxtView = findViewById(R.id.textView);
            resultsTxtView.setText("error:\n"+e);
        }
    }
}