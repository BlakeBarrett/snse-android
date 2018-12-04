package com.blakebarrett.snse.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import com.blakebarrett.snse.SentimentListActivity;

/**
 *
 * Shout out to this Blog Post: https://proandroiddev.com/5-steps-to-implement-biometric-authentication-in-android-dbeb825aeee8
 *
 */

public class BiometricUtils {


    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }


    /*
     * Condition I: Check if the android version in device is greater than
     * Marshmallow, since fingerprint authentication is only supported
     * from Android 6.0.
     * Note: If your project's minSdkversion is 23 or higher,
     * then you won't need to perform this check.
     *
     * */
    public static boolean isSdkVersionSupported() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }



    /*
     * Condition II: Check if the device has fingerprint sensors.
     * Note: If you marked android.hardware.fingerprint as something that
     * your app requires (android:required="true"), then you don't need
     * to perform this check.
     *
     * */
    public static boolean isHardwareSupported(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }



    /*
     * Condition III: Fingerprint authentication can be matched with a
     * registered fingerprint of the user. So we need to perform this check
     * in order to enable fingerprint authentication
     *
     * */
    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }



    /*
     * Condition IV: Check if the permission has been added to
     * the app. This permission will be granted as soon as the user
     * installs the app on their device.
     *
     * */
    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }


    /*
     * Here is where things go off the rails.
     * The rest of the code was written by me (Blake Barrett).
     * Everything above came from Blog posts.
     *
     */


    public static boolean biometrySupported(@NonNull final Context context) {
        return isBiometricPromptEnabled() &&
                isSdkVersionSupported() &&
                isHardwareSupported(context) &&
                isFingerprintAvailable(context) &&
                isPermissionGranted(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void showPrompt(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            final BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                }

                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    final Intent intent = new Intent(context.getApplicationContext(), SentimentListActivity.class);
                    context.startActivity(intent);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            };

            final BiometricPrompt prompt = BiometricUtils.generatePrompt(
                    context,
                    "",
                    "",
                    "",
                    "",
                    callback);
            final CancellationSignal cancellationSignal = new CancellationSignal();
            prompt.authenticate(cancellationSignal, context.getMainExecutor(), callback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static BiometricPrompt generatePrompt(@NonNull final Context context,
                                                 @NonNull final String title,
                                                 @NonNull final String subtitle,
                                                 @NonNull final String description,
                                                 @NonNull final String negativeButtonText,
                                                 @NonNull final BiometricPrompt.AuthenticationCallback biometricCallback) {
        return new BiometricPrompt.Builder(context)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButton(
                        negativeButtonText,
                        context.getMainExecutor(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                biometricCallback.onAuthenticationFailed();
                            }
                        }
                ).build();
    }
}
