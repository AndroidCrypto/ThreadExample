package de.androidcrypto.threadexample;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class MainActivity extends AppCompatActivity {

    EditText passphrase, iterations, result;
    Button runWithoutThread, runWithThread;
    final byte[] SALT = new byte[32]; // fixed salt for equal results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passphrase = findViewById(R.id.etPassphrase);
        iterations = findViewById(R.id.etNumberOfIterations);
        result = findViewById(R.id.etResult);
        runWithoutThread = findViewById(R.id.btnRunPbkdf2WithoutThread);
        runWithThread = findViewById(R.id.btnRunPbkdf2WithThread);

        runWithoutThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("PBKDF2 without Thread started"); // will not appear as UI is blocked
                // get password as char array
                int passphraseLength = passphrase.length();
                char[] password = new char[passphraseLength];
                passphrase.getText().getChars(0, passphraseLength, password, 0);
                int iterationsPbkdf2 = Integer.parseInt(iterations.getText().toString());
                String resultBase64 = doPbkdf2WithoutThread(password, iterationsPbkdf2, SALT);
                result.setText("PBKDF2 without Thread: " + resultBase64);
            }
        });

        runWithThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("PBKDF2 with Thread started"); // will appear as UI is NOT blocked
                // get password as char array
                int passphraseLength = passphrase.length();
                char[] password = new char[passphraseLength];
                passphrase.getText().getChars(0, passphraseLength, password, 0);
                int iterationsPbkdf2 = Integer.parseInt(iterations.getText().toString());
                Thread thread = new Thread() {
                    public void run() {
                        doPbkdf2WithThread(password, iterationsPbkdf2, SALT);
                    }
                };
                thread.start();
            }
        });
    }

    private String doPbkdf2WithoutThread(char[] passphraseChar, int iterations, byte[] salt) {
        int keyLength = 32;
        byte[] secretKey = new byte[0];
        SecretKeyFactory secretKeyFactory = null;
        try {
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(passphraseChar, salt, iterations, keyLength * 8);
            secretKey = secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return "";
        }
        return base64Encoding(secretKey);
    }

    // this method is running in a background thread so do not write to any UI elements.
    // for updating UI elements like EditText you need to use runOnUiThread (see below).
    private void doPbkdf2WithThread(char[] passphraseChar, int iterations, byte[] salt) {
        int keyLength = 32;
        byte[] secretKey = new byte[0];
        String resultString = "";
        SecretKeyFactory secretKeyFactory = null;
        try {
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(passphraseChar, salt, iterations, keyLength * 8);
            secretKey = secretKeyFactory.generateSecret(keySpec).getEncoded();
            resultString = base64Encoding(secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            resultString = "";
        }
        String finalResultString = "PBKDF2 with Thread: " + resultString;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UI related things
                result.setText(finalResultString);
            }
        });
    }

    private static String base64Encoding(byte[] input) {
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }
}