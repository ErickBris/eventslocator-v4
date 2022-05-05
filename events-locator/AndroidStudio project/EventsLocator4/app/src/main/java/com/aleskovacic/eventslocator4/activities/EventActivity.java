package com.aleskovacic.eventslocator4.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleskovacic.eventslocator4.R;
import com.aleskovacic.eventslocator4.pojo.DataObject;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

public class EventActivity extends AppCompatActivity implements View.OnClickListener {

    private DataObject dataObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dataObject = this.getIntent().getParcelableExtra(DataObject.EXTRA_OBJECT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView coverPhoto = (ImageView) findViewById(R.id.cover_photo);
        TextView attendeesCount = (TextView) findViewById(R.id.attendees_count);
        TextView startTime = (TextView) findViewById(R.id.start_time);
        TextView place = (TextView) findViewById(R.id.place);
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(dataObject.getTitle());
        }

        // set data
        try {
            Picasso.with(this).load(dataObject.getCoverPhoto()).into(coverPhoto);
            attendeesCount.setText(String.format(getResources()
                            .getString(R.string.people_attending),
                    dataObject.getAttendeesCount()));
            startTime.setText(dataObject.getStartDate());
            place.setText(dataObject.getPlaceName());
            title.setText(dataObject.getTitle());
            description.setText(dataObject.getDescription());
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

        // load ads in AdView
        final AdView mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        mAdView.loadAd(adRequest);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join_on_fb:
                try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://event/" + dataObject.getEventId())));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + dataObject.getEventId())));
                }
                break;
            case R.id.fab_share:
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.sharing_url));
                i.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/" + dataObject.getEventId());
                i.setType("text/plain");
                startActivity(Intent.createChooser(i, getResources().getString(R.string.share_url)));
                break;
        }
    }
}
