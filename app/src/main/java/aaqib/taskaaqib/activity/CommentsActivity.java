package aaqib.taskaaqib.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import aaqib.taskaaqib.R;
import aaqib.taskaaqib.adapter.CommentsAdapter;
import aaqib.taskaaqib.decorator.DividerItemDecoration;
import aaqib.taskaaqib.listener.NetworkCallListener;
import aaqib.taskaaqib.models.ApiResponse;
import aaqib.taskaaqib.models.Item;
import aaqib.taskaaqib.network.HackerNewsAPI;
import aaqib.taskaaqib.network.NetworkClient;
import aaqib.taskaaqib.util.AppUtil;

/**
 * This displays all the comments for a single story
 * along with one level of nested comment
 */
public class CommentsActivity extends AppCompatActivity {

    public static final String PARAM_LIST_IDS = "param_list_ids";
    private static final String PARAM_LIST_ITEMS = "param_list_items";

    private RecyclerView mRecyclerView;
    private ArrayList<Long> mItemIDs = new ArrayList<>();
    private CommentsAdapter mAdapter;
    private ArrayList<Long> mItemsNetworkCalled = new ArrayList<>();
    private Handler mHandler = new Handler();

    /**
     * Save content data in case the activity is destroyed
     *
     * @param outState The bundle which will be saved
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.comments_list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.seperator));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                loadVisibleComments();
            }
        });

        if (!restoreData(savedInstanceState)) {
            loadData();
        }
    }

    /**
     * Restore content if activity was destroyed previously
     * Also loads visible comment's data {@link #loadVisibleComments()}
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
                    mAdapter = new CommentsAdapter(mItemIDs, items);
                    mRecyclerView.setAdapter(mAdapter);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadVisibleComments();
                        }
                    }, 1000);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the list of comment IDs passed to the activity
     * and sets the Adapter {@link #setAdapter()}
     */
    private void loadData() {
        if (getIntent() != null && getIntent().hasExtra(PARAM_LIST_IDS)) {
            mItemIDs = (ArrayList<Long>) getIntent().getSerializableExtra(PARAM_LIST_IDS);
            setAdapter();
        } else {
            Toast.makeText(this, R.string.error_comments_list, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initiate a new Adapter
     * or update the Adapter and notify data set changed.
     * Also loads visible comment's data {@link #loadVisibleComments()}
     */
    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new CommentsAdapter(mItemIDs);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItemIDs(mItemIDs);
            mAdapter.notifyDataSetChanged();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadVisibleComments();
            }
        }, 1000);
    }

    /**
     * Load a single comment data from network
     * {@link HackerNewsAPI}
     *
     * @param commentID The item ID of the comment
     */
    public void loadCommentData(long commentID) {
        if (!mItemsNetworkCalled.contains(commentID)) {
            mItemsNetworkCalled.add(commentID);
            NetworkClient.executeGet(
                    HackerNewsAPI.getApiItem(String.valueOf(commentID)),
                    null,
                    commentCallListener
            );
        }
    }

    /**
     * This loads all comments that are visible on the screen
     * including one level of nested comment, if any,
     * when the state of the recycler view is SCROLL_STATE_IDLE.
     * This helps to avoid unnecessary network calls when scrolling
     * through many items in a list which might have 100s of items
     */
    private void loadVisibleComments() {
        if (mRecyclerView != null && mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (manager != null && mAdapter != null) {
                int min = manager.findFirstVisibleItemPosition();
                int max = manager.findLastVisibleItemPosition();
                Hashtable<Long, Item> itemsTable = mAdapter.getItems();
                long id;
                if (mItemIDs.size() > 0) {
                    for (int i = min; i <= max; i++) {
                        try {
                            id = mItemIDs.get(i);
                            Item item = itemsTable.get(id);
                            if (item == null) {
                                loadCommentData(id);
                            } else if (item.getKids() != null && item.getKids().size() > 0) {
                                id = item.getKids().get(0);
                                Item childItem = itemsTable.get(id);
                                if (childItem == null) {
                                    loadCommentData(id);
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the comment content and
     * notifies the adapter of the change
     * This also handles nested comments and notifies
     * the appropriate cell in the list (If comment ID not
     * found in list of IDs then get parent ID and find it
     * again in the list)
     *
     * @param commentItem The item which was added
     */
    private void setComment(final Item commentItem) {
        mAdapter.setComment(commentItem);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mItemIDs.indexOf(commentItem.getId()) == -1) {
                    if (mItemIDs.indexOf(commentItem.getParent()) != -1) {
                        mAdapter.notifyItemChanged(mItemIDs.indexOf(commentItem.getParent()));
                    }
                } else {
                    if (commentItem.isDeleted() || commentItem.isDead()) {
                        int index = mItemIDs.indexOf(commentItem.getId());
                        mItemIDs.remove(index);
                        mAdapter.removeItem(index);
                        mAdapter.notifyItemRemoved(index);
                    } else {
                        mAdapter.notifyItemChanged(mItemIDs.indexOf(commentItem.getId()));
                    }
                }
            }
        });
    }

    /**
     * Handles the network response of item API
     * {@link HackerNewsAPI}
     */
    NetworkCallListener commentCallListener = new NetworkCallListener() {
        @Override
        public void onCallStart() {

        }

        @Override
        public void onResponse(ApiResponse apiResponse) {
            if (apiResponse != null) {
                long commentID = AppUtil.getItemIDFromURL(apiResponse.getUrl());
                if (commentID != -1) {
                    mItemsNetworkCalled.remove(commentID);
                }
                if (apiResponse.getResponse() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(apiResponse.getResponse());
                        setComment(new Item(jsonObject));
                    } catch (JSONException jsonE) {
                        jsonE.printStackTrace();
                    }
                }
            }
        }
    };

}