package com.frigoshare.endpoints;

import com.frigoshare.data.Leftover;
import com.frigoshare.data.TimeSlot;
import com.frigoshare.data.User;
import com.frigoshare.utils.Marshaller;
import com.frigoshare.utils.VisibilityUtils;
import com.frigoshare.worker.ClaimWorkerPayload;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Named;

import static com.frigoshare.OfyService.ofy;

@Api(name = "endpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "frigoshare.com", ownerName = "frigoshare.com", packagePath=""))
public final class LeftoverEndpoint {

    private static Queue queue = QueueFactory.getDefaultQueue();

    public LeftoverEndpoint() {

    }

    @ApiMethod(name = "Leftovers.insertLeftover")
    public void iLeftover(@Named("userid") String userId, Leftover leftover) {
        leftover.setOfferer((new UserEndpoint().gUser(userId)));
        ofy().save().entity(leftover).now();
    }

    @ApiMethod(httpMethod = "GET", name = "Leftovers.getLeftover")
    public Leftover gLeftover(@Named("id") Long id) {
        return ofy().load().type(Leftover.class).id(id).now();
    }

    @ApiMethod(name = "Leftovers.deleteLeftover")
    public void dLeftover(@Named("id") Long id) {
        ofy().delete().type(Leftover.class).id(id);
    }

    @ApiMethod(name = "Leftovers.claimLeftover")
    public void claimLeftoverOffer(@Named("userid") String userId, @Named("id") Long offerId, TimeSlot timeSlot) {
        try {
            byte[] payload = Marshaller.marshall(new ClaimWorkerPayload(userId, offerId, timeSlot));
            queue.add(TaskOptions.Builder.withUrl("/claimworker").payload(payload));
            //ClaimWorker.claimLeftoverOffer(userId, offerId, timeSlot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiMethod(name = "Leftovers.getAllUnclaimedLeftovers")
    public Collection<Leftover> getAllUnclaimedLeftovers(@Named("userid") String userId) {
        Collection<Leftover> unclaimedLeftoverOffers = new ArrayList<Leftover>();
        User requester = (new UserEndpoint()).gUser(userId);
        if (requester != null) {
            for (User offerer : ofy().load().type(User.class)) {
                if (VisibilityUtils.isVisible(requester, offerer)) {
                    unclaimedLeftoverOffers.addAll(offerer.getPendingNonClaimedOffers());
                }
            }
        }
        return unclaimedLeftoverOffers;
    }
}
