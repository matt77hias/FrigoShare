package com.frigoshare.worker;

import com.frigoshare.data.Leftover;
import com.frigoshare.data.TimeSlot;
import com.frigoshare.data.User;
import com.frigoshare.endpoints.LeftoverEndpoint;
import com.frigoshare.endpoints.UserEndpoint;
import com.frigoshare.gcm.Messaging;
import com.googlecode.objectify.VoidWork;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.frigoshare.OfyService.ofy;

public class ClaimWorker extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ObjectInputStream objectInputStream = new ObjectInputStream(req.getInputStream());
        try {
            ClaimWorkerPayload workerPayload = (ClaimWorkerPayload) objectInputStream.readObject();
            claimLeftoverOffer(workerPayload.getUserId(), workerPayload.getOfferId(), workerPayload.getTimeSlot());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void claimLeftoverOffer(String userId, Long offerId, TimeSlot timeSlot) {
        final String uid = userId;
        final Long oid = offerId;
        final TimeSlot ts = timeSlot;

        Void t = ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                LeftoverEndpoint lep = new LeftoverEndpoint();

                Leftover unclaimedLeftoverOffer = lep.gLeftover(oid);
                if (unclaimedLeftoverOffer == null) { return; }
                User offerer = unclaimedLeftoverOffer.getOfferer();
                User claimer = (new UserEndpoint()).gUser(uid);
                if (claimer == null) { return; }

                try {
                    unclaimedLeftoverOffer.claimOffer(claimer, ts);
                } catch (IllegalArgumentException e) {
                    return;
                } catch (IllegalStateException e) {
                    try {
                        Messaging.sendMessage(claimer, e.getMessage());
                    } catch (IOException i) {

                    } finally {
                        return;
                    }
                }

                ofy().save().entity(unclaimedLeftoverOffer).now();

                try {
                    Messaging.sendMessage(offerer, getClaimMessageForOfferer(unclaimedLeftoverOffer, ts));
                    Messaging.sendMessage(claimer, getClaimMessageForClaimer(unclaimedLeftoverOffer, ts));
                } catch (IOException e) {
                    return;
                }
            }
        });
    }

    public static String getClaimMessageForClaimer(Leftover leftover, TimeSlot timeslot) {
        return "You claimed " + leftover.getDescription().getName() + " at " + timeslot.toString();
    }

    public static String getClaimMessageForOfferer(Leftover leftover, TimeSlot timeslot) {
        return leftover.getClaimerInfo().getFullName() + " claimed " + leftover.getDescription().getName() + " at " + timeslot.toString();
    }
}
