package com.kr.messaging.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.kr.messaging.datamodel.BugleDatabaseOperations;
import com.kr.messaging.datamodel.DataModel;
import com.kr.messaging.datamodel.DatabaseHelper;
import com.kr.messaging.datamodel.DatabaseWrapper;
import com.kr.messaging.datamodel.MessagingContentProvider;
import com.kr.messaging.datamodel.action.InsertNewMessageAction;
import com.kr.messaging.datamodel.data.MessageData;
import com.kr.messaging.datamodel.data.MessagePartData;
import com.kr.messaging.datamodel.data.ParticipantData;
import com.kr.messaging.util.AlertDialogHelper;
import com.kr.messaging.util.LogUtil;
import com.kr.messaging.util.OsUtil;
import com.kr.messaging.util.PhoneUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class Test extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        long current = Calendar.getInstance().getTimeInMillis();
//        Cursor cursor = DataModel.get().getDatabase().rawQuery("select * from scheduled where " + DatabaseHelper.MessageColumns.SENT_TIMESTAMP + " between " + (current - 1000) + " and " + (current + 1000), new String[] {});
//        cursor.moveToLast();
//        MessageData msg = intent.getParcelableExtra("message");
//        msg.bind(cursor);
        Calendar current = Calendar.getInstance();
        current.set(Calendar.SECOND, 0);
        Log.d("---------", intent.getStringExtra("id") + ":");
        findMessageToSend(DataModel.get().getDatabase(), current.getTimeInMillis());
//        Log.d("---------", msg.getSentTimeStamp() + ":" + cursor.getString(3));
//        ArrayList<MessagePartData> temp = (ArrayList<MessagePartData>) msg.getParts();
//        Log.d("-----------", temp.get(0).getText());
//        InsertNewMessageAction.insertNewMessage(msg);
    }

    public static void findMessageToSend(final DatabaseWrapper db, final long now) {
        db.beginTransaction();
        Cursor sending = null;
        Cursor cursor = null;
        long time = Calendar.getInstance().getTimeInMillis();
        try {
            // First check to see if we have any messages already sending
            sending = db.query(DatabaseHelper.MESSAGES_TABLE,
                    MessageData.getProjection(),
                    DatabaseHelper.MessageColumns.STATUS + " IN (?, ?)",
                    new String[]{Integer.toString(MessageData.BUGLE_STATUS_OUTGOING_SENDING),
                            Integer.toString(MessageData.BUGLE_STATUS_OUTGOING_RESENDING)},
                    null,
                    null,
                    DatabaseHelper.MessageColumns.RECEIVED_TIMESTAMP + " ASC");
            final boolean messageCurrentlySending = sending.moveToNext();
            // Look for messages we could send
            final ContentValues values = new ContentValues();
            values.put(DatabaseHelper.MessageColumns.STATUS,
                    MessageData.BUGLE_STATUS_OUTGOING_FAILED);
            cursor = db.query(DatabaseHelper.MESSAGES_TABLE,
                    MessageData.getProjection(),
                    DatabaseHelper.MessageColumns.STATUS + " IN ("
                            + MessageData.BUGLE_STATUS_OUTGOING_YET_TO_SEND + ","
                            + MessageData.BUGLE_STATUS_OUTGOING_AWAITING_RETRY + ") AND "
                            + DatabaseHelper.MessageColumns.SENT_TIMESTAMP + " BETWEEN " + (now - 1000) + " AND " + (now + 1000),
                    null,
                    null,
                    null,
                    DatabaseHelper.MessageColumns.RECEIVED_TIMESTAMP + " ASC");

            while (cursor.moveToNext()) {
                final MessageData message = new MessageData();
                message.bind(cursor);
                if (message.getInResendWindow(now)) {
                    // If no messages currently sending
                    if (!messageCurrentlySending) {
                        // Resend this message
                        Log.d("----------1", message.getSentTimeStamp() + ":" + message.getMessageId());
                        InsertNewMessageAction.insertNewMessage(message);
                        // Before queuing the message for resending, check if the message's self is
                        // active. If not, switch back to the system's default subscription.
//                        if (OsUtil.isAtLeastL_MR1()) {
//                            final ParticipantData messageSelf = BugleDatabaseOperations
//                                    .getExistingParticipant(db, message.getSelfId());
//                            if (messageSelf == null || !messageSelf.isActiveSubscription()) {
//                                final ParticipantData defaultSelf = BugleDatabaseOperations
//                                        .getOrCreateSelf(db, PhoneUtils.getDefault()
//                                                .getDefaultSmsSubscriptionId());
//                                if (defaultSelf != null) {
//                                    message.bindSelfId(defaultSelf.getId());
//                                    final ContentValues selfValues = new ContentValues();
//                                    selfValues.put(DatabaseHelper.MessageColumns.SELF_PARTICIPANT_ID,
//                                            defaultSelf.getId());
//                                    BugleDatabaseOperations.updateMessageRow(db,
//                                            message.getMessageId(), selfValues);
//                                    MessagingContentProvider.notifyMessagesChanged(
//                                            message.getConversationId());
//                                }
//                            }
//                        }
                    }
                    break;
                } else {
                    // Mark message as failed
                    BugleDatabaseOperations.updateMessageRow(db, message.getMessageId(), values);
                    MessagingContentProvider.notifyMessagesChanged(message.getConversationId());
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
            if (sending != null) {
                sending.close();
            }
        }
    }
}
