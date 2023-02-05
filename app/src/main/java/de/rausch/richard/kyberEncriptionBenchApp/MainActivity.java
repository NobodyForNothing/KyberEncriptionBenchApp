package de.rausch.richard.kyberEncriptionBenchApp;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

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
    }

    public void doTest(View view) {
        EditText iterField = findViewById(R.id.editTextNumber);
        long iterCount = Integer.parseInt(iterField.getText().toString());

        long start = System.nanoTime();
        try {
            for (int i = 0; i < iterCount; i++) {
                // beide Kommunikationspartner erstellen ihre symmetrischen schlüssel
                Alice alice = new Alice();
                Bob bob = new Bob();

                // der AES Schlüssel wird ausgetauscht, ohne ihn direkt zu versenden
                alice.connectTo(bob);
            }
            // capture Results
            long elapsedTime = System.nanoTime() - start;
            String resultText = String.valueOf(iterCount) + " local Kyber key establishments finished\n" +
                    "in:\t" + (elapsedTime/1000000000.0) + "s";

            // add result to resultsTxtView
            TextView resultsTxtView = findViewById(R.id.textView);
            resultsTxtView.setText(resultText);

        } catch (Exception e) {
            TextView resultsTxtView = findViewById(R.id.textView);
            resultsTxtView.setText("error:\n"+e.toString());
        }
    }
}