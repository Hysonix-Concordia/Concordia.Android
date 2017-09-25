package com.hysonix.concordia.concordiaapp;

import android.content.Context;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CallReceiver extends PhoneReceiver {

    private static final String uri = "https://concordia.hysonix.com/event";

    public CallReceiver()
    {
    }

    @Override
    protected void onIncomingCallReceived(Context ctx, final String number, final Date start)
    {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    WebClient conn = new WebClient();
                    conn.Send(uri, "{ \"Source\": \"CALL\", \"Type\": \"INCOMING\", \"Data\": { \"DateTime\": " + formatter.format(start) + ", \"Number\": " + number + ", \"Name\": \"Anne Hysong\" } }");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        //
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        //
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        //
    }
}
