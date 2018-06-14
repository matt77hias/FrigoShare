package com.frigoshare.application;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class Contact {

    public static final String SUBJECT = "FrigoShare";
    public static final String ADMIN_EMAIL = "matthias.moulin@gmail.com";

    public static void gotoMail(Context context) {
        final Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", ADMIN_EMAIL, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
