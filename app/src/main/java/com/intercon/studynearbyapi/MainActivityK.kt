package com.intercon.studynearbyapi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.PublishCallback
import com.google.android.gms.nearby.messages.PublishOptions
import com.google.android.gms.nearby.messages.Strategy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityK : AppCompatActivity() {

    private var mMessageListener: MessageListener? = null
    private val TAG = "NearbyAPI_Test"
    private var message: Message? = null

    private var partsMessage: Array<String>? = null

    private val PUB_SUB_STRATEGY = Strategy.Builder().setTtlSeconds(Strategy.TTL_SECONDS_MAX).build()

    private val PUB_OPTIONS = PublishOptions.Builder()
            .setStrategy(PUB_SUB_STRATEGY)
            .setCallback(object : PublishCallback() {
                override fun onExpired() {
                    "Experid message".toast(this@MainActivityK);
                }
            })
            .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMessageListener = object : MessageListener() {

            override fun onFound(message: Message?) {
                val messageStr = String(message!!.content)
                partsMessage = messageStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                txtFacebook.text = "Facebook: ${partsMessage!![0]}"
                txtTwitter.text = "Twitter: ${partsMessage!![1]}"
                txtLinkedin.text = "Linkedin: ${partsMessage!![1]}"
                Picasso.get().load(partsMessage!![3]).into(imgPhoto)

                ctnReceivedCard.visibility = View.VISIBLE
                ctnMyCard.visibility = View.GONE
                "Found message: ".toast(this@MainActivityK);
            }

            override fun onLost(message: Message?) {
                "Lost sight of message: ${String(message!!.content)}".toast(this@MainActivityK);
            }
        }
    }

    fun facebook(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/${partsMessage!![0]}"))
        startActivity(intent)
    }

    fun twitter(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/${partsMessage!![1]}"))
        startActivity(intent)
    }

    fun linkedin(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/${partsMessage!![2]}/"))
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()

        Nearby.getMessagesClient(this).subscribe(mMessageListener!!)
    }

    public override fun onStop() {
        if (message != null)
            Nearby.getMessagesClient(this).unpublish(message!!)
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener!!)

        super.onStop()
    }

    fun share(view: View) {
        val buffer = StringBuffer()
        buffer.append(edtFacebook.text.toString() + " ")
        buffer.append(edtTwitter.text.toString() + " ")
        buffer.append(edtLinkedin.text.toString() + " ")
        buffer.append(edtPhoto.text)
        message = Message(buffer.toString().toByteArray())

        Nearby.getMessagesClient(this)
                .publish(message!!, PUB_OPTIONS)
                .addOnSuccessListener(this) { aVoid -> Log.e(TAG, "sucesso: $aVoid") }
                .addOnFailureListener(this) { e -> Log.e(TAG, "sucesso: $e.localizedMessage") }
    }

    fun ok(view: View) {
        ctnReceivedCard.visibility = View.GONE
        ctnMyCard.visibility = View.VISIBLE
    }

    fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
        return Toast.makeText(context, this.toString(), duration).apply { show() }
    }
}