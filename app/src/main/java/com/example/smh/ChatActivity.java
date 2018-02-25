package com.example.smh;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatActivity extends Activity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        String getPhoneNumber = b.getString("userPhoneNumber");
        initControls(getPhoneNumber);
    }

    private void initControls(final String getPhoneNumber) {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("My Buddy");

        // loadDummyHistory();
        loadSmsHistory(getPhoneNumber);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                try {
                    // Get the default instance of the SmsManager
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(getPhoneNumber,
                            null,
                            messageText,
                            null,
                            null);
                    Toast.makeText(getApplicationContext(), "Your sms has successfully sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Your sms has failed...",
                            Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");

                displayMessage(chatMessage);
            }
        });


    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadSmsHistory(String getPhoneNumber) {
        List<ChatMessage> lstSms = new ArrayList<ChatMessage>();
        ChatMessage objSms = new ChatMessage();
        Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
        Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri, new String[]{"_id", "thread_id", "address", "person", "date", "body", "type"}, null, null, null);
        startManagingCursor(cursor1);
        String[] columns = new String[]{"address", "person", "date", "body", "type"};
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            while (cursor1.moveToNext()) {
                String address = cursor1.getString(cursor1.getColumnIndex(columns[0]));

                if (address.equalsIgnoreCase(getPhoneNumber)) { //put your number here
                    //objSms.setAddress(cursor1.getString(cursor1.getColumnIndex(columns[0])));
                    //objSms.setId(cursor1.getString(cursor1.getColumnIndex(columns[1])));
                    String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
                    String body = cursor1.getString(cursor1.getColumnIndex(columns[3]));
                    objSms.setMessage(cursor1.getString(cursor1.getColumnIndex(columns[3])));
                    String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));
                    if (type.contains("1")) {
                        objSms.setMe(false);
                    } else {
                        objSms.setMe(true);
                    }

                    Log.d("*******", "body=" + body);

                }
            }
            lstSms.add(objSms);
            cursor1.close();
        }

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for (int i = 0; i < lstSms.size(); i++) {
            ChatMessage message = lstSms.get(i);
            displayMessage(message);
        }
    }

    private void loadDummyHistory() {

        chatHistory = new ArrayList<ChatMessage>();

        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        ChatMessage msg1 = new ChatMessage();
        msg1.setId(2);
        msg1.setMe(false);
        msg1.setMessage("How r u doing???");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

    }

}
