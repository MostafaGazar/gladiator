package com.mostafagazar.samples.gladiator.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * @author Mostafa Gazar <mmegazar@gmail.com>
 */
public class IntentsUtil {

    public static boolean composeEmail(Activity activity, String email, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
            return true;
        }

        return false;
    }

}
