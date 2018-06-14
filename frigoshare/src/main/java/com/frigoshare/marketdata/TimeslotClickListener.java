package com.frigoshare.marketdata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

import com.frigoshare.MarketSearchActivity;
import com.frigoshare.R;
import com.frigoshare.endpoint.model.Leftover;
import com.frigoshare.endpoint.model.TimeSlot;
import com.frigoshare.proxy.LeftoverProxy;
import com.frigoshare.user.UserTools;

import java.io.IOException;

public class TimeslotClickListener implements View.OnClickListener {

    private final Leftover leftover;
    private final TimeSlot timeslot;

    public Leftover getLeftover() {
        return this.leftover;
    }

    public TimeSlot getTimeslot() {
        return this.timeslot;
    }

    public TimeslotClickListener(Leftover leftover, TimeSlot timeslot) {
        super();
        this.leftover = leftover;
        this.timeslot = timeslot;
    }

    @Override
    public void onClick(final View v) {
        new AlertDialog.Builder(v.getContext())
                .setTitle(R.string.reserve_entry_title)
                .setMessage(R.string.reserve_entry_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClaimTaskPayload ctp = new ClaimTaskPayload(UserTools.getCurrentUser().getId(), getLeftover().getId(), getTimeslot());
                        createClaimTask().execute(ctp);
                        showResultDialog(v);
                        MarketSearchActivity.removeLeftover(leftover);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void showResultDialog(final View v) {
//        new AlertDialog.Builder(v.getContext())
//                .setTitle("Processing claim")
//                .setMessage("Claim is being processed, you will receive a notification of the result shortly.")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        //Do nothing
//                    }
//                })
//                .show();
    }

    protected static ClaimTask createClaimTask() {
        return new ClaimTask();
    }

    protected static class ClaimTask extends AsyncTask<ClaimTaskPayload, Void, Void> {

        @Override
        protected Void doInBackground(ClaimTaskPayload... params) {
            for (ClaimTaskPayload ctp : params) {
                try {
                    LeftoverProxy.claim(ctp.getUserId(), ctp.getOfferId(), ctp.getTimeSlot());
                } catch (IOException e) {
                }
            }
            return null;
        }
    }

    protected final class ClaimTaskPayload {
        private final String userId;
        private final Long offerId;
        private final TimeSlot timeSlot;

        public String getUserId() {
            return userId;
        }

        public Long getOfferId() {
            return offerId;
        }

        public TimeSlot getTimeSlot() {
            return timeSlot;
        }

        public ClaimTaskPayload(String userId, Long offerId, TimeSlot timeSlot) {
            this.userId = userId;
            this.offerId = offerId;
            this.timeSlot = timeSlot;
        }
    }
}
