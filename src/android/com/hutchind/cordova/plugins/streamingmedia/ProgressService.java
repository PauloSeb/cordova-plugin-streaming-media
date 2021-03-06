package com.hutchind.cordova.plugins.streamingmedia;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by paul on 20/11/2017.
 * Based on https://developer.android.com/reference/android/app/Service.html
 */

public class ProgressService extends Service {
    /** Keeps track of all current registered clients. */
    Messenger mSimpleVideoStream;
    Messenger mStreamingMedia;
    /** Holds last progress set by a client. */
    int mProgress = 0;

    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to update progress.  This can be sent to the
     * service to supply a progress, and will be sent by the service to
     * any registered clients with the new progress.
     */
    static final int MSG_PROGRESS = 3;

    /**
     * Command to service to update progress.  This can be sent to the
     * service to supply a progress, and will be sent by the service to
     * any registered clients with the new progress.
     */
    static final int MSG_STOP = 4;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mStreamingMedia = msg.replyTo;
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mStreamingMedia = null;
                    break;
                case MSG_PROGRESS:
                    mSimpleVideoStream = msg.replyTo;
                    mProgress = msg.arg1;
                    try {
                        if (mStreamingMedia != null) {
                            mStreamingMedia.send(Message.obtain(null,
                                    MSG_PROGRESS, mProgress, 0));
                        }
                    } catch (RemoteException e) {
                        // The client is dead.  Remove it from the list;
                        // we are going through the list from back to front
                        // so this is safe to do inside the loop.
                        mSimpleVideoStream = null;
                        mStreamingMedia = null;
                    }
                    break;
                case MSG_STOP:
                    try {
                        if (mSimpleVideoStream != null) {
                            mSimpleVideoStream.send(Message.obtain(null,
                                    MSG_STOP, 0, 0));
                        }
                    } catch (RemoteException e) {
                        // The client is dead.  Remove it from the list;
                        // we are going through the list from back to front
                        // so this is safe to do inside the loop.
                        mSimpleVideoStream = null;
                        mStreamingMedia = null;
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
