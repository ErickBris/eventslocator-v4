package com.aleskovacic.eventslocator4.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.aleskovacic.eventslocator4.App;
import com.aleskovacic.eventslocator4.R;
import com.aleskovacic.eventslocator4.adapters.ListAdapter;
import com.aleskovacic.eventslocator4.network.DataService;
import com.aleskovacic.eventslocator4.pojo.DataObject;
import com.aleskovacic.eventslocator4.pojo.DataResponse;
import com.aleskovacic.eventslocator4.views.SpacesItemDecorator;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SearchActivity extends AppCompatActivity implements ListAdapter.ClickListener, SearchView.OnQueryTextListener {

    private static final int LIST_SPACE_DIV = 16; // list items spacing
    private RecyclerView mRecyclerView; // list view
    private ListAdapter mListAdapter; // list adapter

    private SearchView mSearchView; // search view inside toolbar

    private Retrofit retrofit; // network service
    private DataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
            ab.setDisplayShowHomeEnabled(false);
        }

        // set up list
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mListAdapter = new ListAdapter(this, new ArrayList<DataObject>());
        mListAdapter.setClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SpacesItemDecorator(this, LIST_SPACE_DIV));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mListAdapter);

        // define a service for network requests
        retrofit = new Retrofit.Builder()
                .baseUrl(App.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dataService = retrofit.create(DataService.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // receive search queries
        if (mSearchView != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
            searchItem.expandActionView();
        }
        if (mSearchView != null) {
            final Point p = new Point();
            getWindowManager().getDefaultDisplay().getSize(p); // some value to override searchView maxWidth
            mSearchView.setMaxWidth(p.x); // overriding default maxWidth
            mSearchView.setOnQueryTextListener(this); // for handling onQueryTextSubmit and onQueryTextChange
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.requestFocus(); // open soft input keyboard automatically
        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        onQueryTextChange(s);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            doSearch(newText);
        }
        return true;
    }

    private void doSearch(String query) {
        Call<DataResponse> m = dataService.search(query);
        m.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Response<DataResponse> response, Retrofit retrofit) {
                DataResponse dr = response.body();
                if (dr.isSuccess()) {
                    mListAdapter.newData(dr.getData()); // populate list view with data
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("onFailure", t.getMessage());
            }
        });
    }

    @Override
    public void itemClicked(View view, int position) {
        // open event activity
        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
        intent.putExtra(DataObject.EXTRA_OBJECT, mListAdapter.getItem(position));
        startActivity(intent);
    }
}
