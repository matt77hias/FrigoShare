package com.frigoshare.gcm;

import com.frigoshare.data.User;
import com.frigoshare.endpoints.UserEndpoint;
import com.google.android.gcm.server.Sender;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.googlecode.objectify.VoidWork;

import java.io.IOException;
import java.util.logging.Logger;

import static com.frigoshare.OfyService.ofy;

public final class Messaging {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = "AIzaSyBWZYS67gVEBxatLJfIIvXKMdmeTg02WAI";

    private static final Logger log = Logger.getLogger(Messaging.class.getName());

    private static final int MAXIMAL_NUMBER_OF_RETRIES = 5;

    public static void sendMessage(User user, String message) throws IOException {

        if (!user.isReachableByGCM()) {
            log.warning("Not sending message because the user has no registration id");
            return;
        }
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }

        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }

        final String userId = user.getInfo().getId();
        final String regId = user.getLastRegId();
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        final Result result = sender.send(msg, regId, MAXIMAL_NUMBER_OF_RETRIES);

        Void t = ofy().transactNew(new VoidWork() {
            @Override
            public void vrun() {
                User u = (new UserEndpoint()).gUser(userId);
                if (u != null) {
                    if (result.getMessageId() != null) {
                        log.info("Message sent to " + regId);

                        String canonicalRegId = result.getCanonicalRegistrationId();
                        if (canonicalRegId != null) {
                            // if the regId changed, we have to update the datastore
                            log.info("Registration Id changed for " + regId + " updating to " + canonicalRegId);
                            u.removeRegId(regId);
                            u.addRegId(canonicalRegId);
                            (new UserEndpoint()).iUser(u);
                        }
                    } else {
                        String error = result.getErrorCodeName();
                        if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                            log.warning("Registration Id " + regId + " no longer registered with GCM, removing from datastore");
                            u.removeRegId(regId);
                            (new UserEndpoint()).iUser(u);
                        } else {
                            log.warning("Error when sending message: " + error);
                        }
                    }
                }
            }
        });
    }
}
