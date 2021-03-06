package org.kashiyatra.ky18.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kashiyatra.ky18.R;
import org.kashiyatra.ky18.SplashActivity;
import org.kashiyatra.ky18.adapters.UpdateAdapter;

import java.text.SimpleDateFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UpdatesFragment extends Fragment {
    RecyclerView updateRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SharedPreferences prefs;
    SharedPreferences.Editor prefEditor;

    public UpdatesFragment() {
        // Required empty public constructor
    }

    public static UpdatesFragment newInstance() {
        return new UpdatesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_updates, container, false);
        prefs = getActivity().getSharedPreferences(SplashActivity.storeUserDetails, Context.MODE_PRIVATE);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getUpdates().execute();
            }
        });
        updateRecyclerView = rootView.findViewById(R.id.update_recycler);
        RecyclerView.LayoutManager updateLinearLayoutManager = new LinearLayoutManager(getActivity());
        updateRecyclerView.setLayoutManager(updateLinearLayoutManager);

        new getUpdates().execute();

        return rootView;
    }

    public class getUpdates extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... voids) {
            try {
                Request request = new Request.Builder()
                        .url("https://kashiyatra.herokuapp.com/api/mobile/notifications/")
                        .build();
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                JSONArray updates = new JSONArray(response.body().string());
                prefEditor = prefs.edit();
                prefEditor.putString("updates_json", updates.toString());
                prefEditor.commit();
                return updates;

            } catch (Exception e) {
                Log.e("UpdatesFragment", e.toString());
                e.printStackTrace();
            }
            return new JSONArray();
        }

        @Override
        protected void onPostExecute(JSONArray updates) {
            if (updates.length() == 0) {
                try {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                    updates = new JSONArray(prefs.getString("updates_json", "[\n" +
                            "    {\n" +
                            "        \"body\": \"No new updates. Check us out later\",\n" +
                            "        \"image_url\": null,\n" +
                            "        \"link\": \"http://www.kashiyatra.org/\",\n" +
                            "        \"timestamp\": \"2017-12-31T18:30:00Z\"\n" +
                            "    }\n" +
                            "]"));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("UpdatesFragment", e.toString());
                }
            }
            String[] bodies = new String[updates.length()];
            String[] imageUrls = new String[updates.length()];
            String[] links = new String[updates.length()];
            long[] times = new long[updates.length()];

            for (int i = 0; i < updates.length(); i++) {
                try {
                    JSONObject update = updates.getJSONObject(i);
                    bodies[i] = update.getString("body");
                    imageUrls[i] = update.getString("image_url");
                    times[i] = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .parse(update.getString("timestamp").replace("Z", "+00:00")).getTime();
                    links[i] = updates.getJSONObject(i).getString("link");
                } catch (Exception e) {
                    Log.e("Response Invalid", updates.toString());
                    e.printStackTrace();
                }
            }

            RecyclerView.Adapter updateAdapter = new UpdateAdapter(getContext(), bodies, imageUrls, links, times);
            updateRecyclerView.swapAdapter(updateAdapter, false);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
