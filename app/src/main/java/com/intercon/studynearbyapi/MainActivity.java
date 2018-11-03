package com.intercon.studynearbyapi;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private MessageListener mMessageListener;
    private String TAG = "NearbyAPI_Test";
    private Message message;

    private EditText edtLinkedin;
    private EditText edtFacebook;
    private EditText edtPhoto;
    private EditText edtTwitter;

    private TextView txtLinkedin;
    private TextView txtFacebook;
    private ImageView imgPhoto;
    private TextView txtTwitter;

    private LinearLayout ctnReceivedCard;
    private LinearLayout ctnMyCard;

    private String[] partsMessage;

    private final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(Strategy.TTL_SECONDS_MAX).build();

    private final PublishOptions PUB_OPTIONS = new PublishOptions.Builder()
            .setStrategy(PUB_SUB_STRATEGY)
            .setCallback(new PublishCallback(){
                @Override
                public void onExpired() {
                    Toast.makeText(
                            MainActivity.this,
                            "Experid message",
                            Toast.LENGTH_LONG).show();
                }
            })
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtFacebook = findViewById(R.id.edtFacebook);
        edtLinkedin = findViewById(R.id.edtLinkedin);
        edtPhoto = findViewById(R.id.edtPhoto);
        edtTwitter = findViewById(R.id.edtTwitter);

        txtFacebook = findViewById(R.id.txtFacebook);
        txtLinkedin = findViewById(R.id.txtLinkedin);
        txtTwitter = findViewById(R.id.txtTwitter);
        imgPhoto = findViewById(R.id.imgPhoto);

        ctnMyCard = findViewById(R.id.ctnMyCard);
        ctnReceivedCard = findViewById(R.id.ctnReceivedCard);

        mMessageListener = new MessageListener() {

            @Override
            public void onFound(Message message) {
                String messageStr = new String(message.getContent());
                partsMessage = messageStr.split(" ");
                txtFacebook.setText("Facebook: " + partsMessage[0]);
                txtTwitter.setText("Twitter: " + partsMessage[1]);
                txtLinkedin.setText("Linkedin: " + partsMessage[2]);
                Picasso.get().load(partsMessage[3]).into(imgPhoto);

                ctnReceivedCard.setVisibility(View.VISIBLE);
                ctnMyCard.setVisibility(View.GONE);
                Toast.makeText(
                        MainActivity.this,
                        "Found message: " + new String(message.getContent()),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLost(Message message) {
                Toast.makeText(
                        MainActivity.this,
                        "Lost sight of message: " + new String(message.getContent()),
                        Toast.LENGTH_LONG).show();
            }
        };
    }

    public void facebook(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + partsMessage[0]));
        startActivity(intent);
    }

    public void twitter(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + partsMessage[1]));
        startActivity(intent);
    }

    public void linkedin(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/" + partsMessage[2] + "/"));
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    public void onStop() {
        if (message != null)
            Nearby.getMessagesClient(this).unpublish(message);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);

        super.onStop();
    }

    public void share(View view) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(edtFacebook.getText() + " ");
        buffer.append(edtTwitter.getText() + " ");
        buffer.append(edtLinkedin.getText() + " ");
        buffer.append(edtPhoto.getText());
        message = new Message(buffer.toString().getBytes());

        Nearby.getMessagesClient(this)
                .publish(message, PUB_OPTIONS)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "sucesso: " + aVoid);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "sucesso: " + e.getLocalizedMessage());
                    }
                });
    }

    public void ok(View view){
        ctnReceivedCard.setVisibility(View.GONE);
        ctnMyCard.setVisibility(View.VISIBLE);
    }
}
