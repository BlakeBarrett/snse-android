package com.blakebarrett.snse.utils;

import android.content.Context;
import android.content.Intent;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import com.blakebarrett.snse.MainActivity;
import com.blakebarrett.snse.SentimentListActivity;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;


/**
 * Class mostly lifed from: https://proandroiddev.com/5-steps-to-implement-biometric-authentication-in-android-dbeb825aeee8
 * Very minor changes made by me(Blake Barrett).
 */
public class FingerprintUtils {

    private static final String KEY_NAME = "snse-key";

    private KeyGenerator keyGenerator;
    private KeyStore keyStore;
    private Cipher cipher;

    public FingerprintUtils() {
        generateKey();
        try {
            initCipher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateKey() {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
        }
    }


    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;

        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {

            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void authenticate(final Context context) {
        /*
         * Step 1: Instantiate a FingerprintManagerCompat and call the authenticate method.
         * The authenticate method requires the:
         * 1. cryptoObject
         * 2. The second parameter is always zero.
         *    The Android documentation identifies this as set of flags and is most likely
         *    reserved for future use.
         * 3. The third parameter, cancellationSignal is an object used to turn off the
         *    fingerprint scanner and cancel the current request.
         * 4. The fourth parameteris a class that subclasses the AuthenticationCallback abstract class.
         *    This will be the same as the BiometricAuthenticationCallback
         * 5. The fifth parameter is an optional Handler instance.
         *    If a Handler object is provided, the FingerprintManager will use the Looper from that
         *    object when processing the messages from the fingerprint hardware.
         */
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);

        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);

        fingerprintManagerCompat.authenticate(cryptoObject, 0, new CancellationSignal(),
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        MainActivity.Companion.setAuthenticated(true);
                        final Intent intent = new Intent(context.getApplicationContext(), SentimentListActivity.class);
                        context.startActivity(intent);
                    }


                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                }, null);
    }

    public void showPrompt(final Context context) {
        authenticate(context);
    }
}
