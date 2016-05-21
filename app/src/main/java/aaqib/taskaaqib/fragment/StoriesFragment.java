package aaqib.taskaaqib.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import aaqib.taskaaqib.R;
import aaqib.taskaaqib.activity.CommentsActivity;
import aaqib.taskaaqib.adapter.StoriesAdapter;
import aaqib.taskaaqib.listener.NetworkCallListener;
import aaqib.taskaaqib.models.ApiResponse;
import aaqib.taskaaqib.models.Item;
import aaqib.taskaaqib.network.HackerNewsAPI;
import aaqib.taskaaqib.network.NetworkClient;
import aaqib.taskaaqib.util.AppUtil;

/**
 * Fragment to display various stories(top, new, best, ask, show) and jobs
 */
public class StoriesFragment extends Fragment implements StoriesAdapter.StoryListener {

    /**
     * The type identifies the type of content to load from network
     */
    public static final int TYPE_TOP_STORIES = 0;
    public static final int TYPE_NEW_STORIES = 1;
    public static final int TYPE_BEST_STORIES = 2;
    public static final int TYPE_ASK_STORIES = 3;
    public static final int TYPE_SHOW_STORIES = 4;
    public static final int TYPE_JOB_STORIES = 5;

    private static final String PARAM_TYPE = "param_type";
    private static final String PARAM_LIST_IDS = "param_list_ids";
    private static final String PARAM_LIST_ITEMS = "param_list_items";

    private int mType;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ArrayList<Long> mItemIDs = new ArrayList<>();
    private StoriesAdapter mAdapter;
    private ArrayList<Long> mItemsNetworkCalled = new ArrayList<>();
    private Handler mHandler = new Handler();

    public StoriesFragment() {

    }

    /**
     * Gets a new instance of the fragment with the specified type
     *
     * @param type The type of content to load
     * @return StoriesFragment instance
     */
    public static StoriesFragment newInstance(int type) {
        StoriesFragment fragment = new StoriesFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Get the type of content displayed
     * This is used in Tab headers
     *
     * @param position The tab position
     * @return String representing the type of content
     */
    public static String getType(int position) {
        switch (position) {
            case TYPE_TOP_STORIES:
                return "TOP";
            case TYPE_NEW_STORIES:
                return "NEW";
            case TYPE_BEST_STORIES:
                return "BEST";
            case TYPE_ASK_STORIES:
                return "ASK";
            case TYPE_SHOW_STORIES:
                return "SHOW";
            case TYPE_JOB_STORIES:
                return "JOB";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Gets the number of different types of content
     *
     * @return the number of different types of content
     */
    public static int getTypeCount() {
        return 6;
    }

    /**
     * Save content data in case the fragment is destroyed
     *
     * @param outState The bundle which will be saved
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mAdapter != null) {
            Hashtable<Long, Item> items = mAdapter.getItems();
            if (items != null && items.size() > 0) {
                outState.putSerializable(PARAM_LIST_ITEMS, items);
            }
            if (mItemIDs != null && mItemIDs.size() > 0) {
                outState.putSerializable(PARAM_LIST_IDS, mItemIDs);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mType = args.getInt(PARAM_TYPE);

        View v = inflater.inflate(R.layout.fragment_stories, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        } else {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }

        if (!restoreData(savedInstanceState)) {
            loadData();
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        return v;
    }

    /**
     * Invoked when user swipes to refresh completely
     */
    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(false);
            loadData();
        }
    };

    /**
     * Restore content if fragment was destroyed previously
     *
     * @param savedInstanceState The recovered bundle
     * @return true if restoring happened else false
     */
    private boolean restoreData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PARAM_LIST_IDS)) {
                mItemIDs = (ArrayList<Long>) savedInstanceState.getSerializable(PARAM_LIST_IDS);
                if (savedInstanceState.containsKey(PARAM_LIST_ITEMS)) {
                    Hashtable<Long, Item> items = (Hashtable<Long, Item>) savedInstanceState.getSerializable(PARAM_LIST_ITEMS);
                    mAdapter = new StoriesAdapter(mItemIDs, items, this);
                    mRecyclerView.setAdapter(mAdapter);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Loads appropriate data from network {@link HackerNewsAPI}
     * depending upon content type {@link #mType}
     */
    private void loadData() {
        String api = null;
        switch (mType) {
            case TYPE_TOP_STORIES:
                api = HackerNewsAPI.getApiTopStories();
                break;
            case TYPE_NEW_STORIES:
                api = HackerNewsAPI.getApiNewStories();
                break;
            case TYPE_BEST_STORIES:
                api = HackerNewsAPI.getApiBestStories();
                break;
            case TYPE_ASK_STORIES:
                api = HackerNewsAPI.getApiAskStories();
                break;
            case TYPE_SHOW_STORIES:
                api = HackerNewsAPI.getApiShowStories();
                break;
            case TYPE_JOB_STORIES:
                api = HackerNewsAPI.getApiJobStories();
                break;
        }
        if (api != null) {
            NetworkClient.executeGet(
                    api,
                    null,
                    storiesListCallListener
            );
        } else {
            Toast.makeText(getContext(), R.string.error_no_url, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initiate a new Adapter
     * or update the Adapter and notify data set changed
     */
    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new StoriesAdapter(mItemIDs, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItemIDs(mItemIDs);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Invoked when a story content is clicked
     * Opens an app selector to view the URL
     *
     * @param URL The URL of the story (if any)
     */
    @Override
    public void onStoryClicked(String URL) {
        if (!TextUtils.isEmpty(URL)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(URL));
            startActivity(Intent.createChooser(intent, "Select App"));
        }
    }

    /**
     * Invoked when Comments text is clicked on a story item
     * which has more than 0 comments
     * This will open up the Comments Activity {@link CommentsActivity}
     *
     * @param commentIDs List of comment IDs
     */
    @Override
    public void onCommentsClicked(List<Long> commentIDs) {
        Intent intent = new Intent(getContext(), CommentsActivity.class);
        intent.putExtra(CommentsActivity.PARAM_LIST_IDS, (ArrayList<Long>) commentIDs);
        startActivity(intent);
    }

    /**
     * Load a single story data from network
     * {@link HackerNewsAPI}
     *
     * @param storyID The item ID of the story
     */
    @Override
    public void loadStoryData(long storyID) {
        if (!mItemsNetworkCalled.contains(storyID)) {
            mItemsNetworkCalled.add(storyID);
            NetworkClient.executeGet(
                    HackerNewsAPI.getApiItem(String.valueOf(storyID)),
                    null,
                    storyCallListener
            );
        }
    }

    /**
     * Updates the story content and
     * notifies the adapter of the change
     *
     * @param storyItem The item which was added
     */
    private void setStory(final Item storyItem) {
        mAdapter.setStory(storyItem);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemChanged(mItemIDs.indexOf(storyItem.getId()));
            }
        });
    }

    /**
     * Handles the network response of the stories list API
     * {@link HackerNewsAPI}
     */
    NetworkCallListener storiesListCallListener = new NetworkCallListener() {
        @Override
        public void onCallStart() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResponse(ApiResponse apiResponse) {
            if (apiResponse != null && apiResponse.getResponse() != null) {
                try {
                    JSONArray jsonArray = new JSONArray(apiResponse.getResponse());
                    mItemIDs.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mItemIDs.add(jsonArray.getLong(i));
                    }
                    setAdapter();
                } catch (JSONException jsonE) {
                    jsonE.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), R.string.error_get_stories_failed, Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        }
    };

    /**
     * Handles the network response of item API
     * {@link HackerNewsAPI}
     */
    NetworkCallListener storyCallListener = new NetworkCallListener() {
        @Override
        public void onCallStart() {

        }

        @Override
        public void onResponse(ApiResponse apiResponse) {
            if (apiResponse != null) {
                long storyID = AppUtil.getItemIDFromURL(apiResponse.getUrl());
                if (storyID != -1) {
                    mItemsNetworkCalled.remove(storyID);
                }
                if (apiResponse.getResponse() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(apiResponse.getResponse());
                        setStory(new Item(jsonObject));
                    } catch (JSONException jsonE) {
                        jsonE.printStackTrace();
                    }
                }
            }
        }
    };
}
