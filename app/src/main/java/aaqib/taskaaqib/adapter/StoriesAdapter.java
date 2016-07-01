package aaqib.taskaaqib.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import aaqib.taskaaqib.R;
import aaqib.taskaaqib.models.Item;
import aaqib.taskaaqib.util.AppUtil;

/**
 * Handles loading of views in the stories list
 */
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    /**
     * Callback used for interacting with a single story
     */
    public interface StoryListener {
        void onStoryClicked(String URL);

        void onCommentsClicked(List<Long> commentIDs);
    }


    private ArrayList<Long> mItemIDs;
    private Hashtable<Long, Item> mItems;
    private StoryListener mListener;

    /**
     * Default Constructor
     */
    public StoriesAdapter() {
        mItemIDs = new ArrayList<>();
        mItems = new Hashtable<>();
    }

    /**
     * Constructor with list of items IDs and listener
     *
     * @param itemIDs  List of item IDs of the stories
     * @param listener Callback interface to report story interaction
     */
    public StoriesAdapter(ArrayList<Long> itemIDs, StoryListener listener) {
        mItemIDs = new ArrayList<>(itemIDs);
        mListener = listener;
        mItems = new Hashtable<>();
    }

    /**
     * Constructor with list of item IDs, items and listener
     *
     * @param itemIDs  List of item IDs of the stories
     * @param items    Hashtable of items mapped with their IDs
     * @param listener Callback interface to report story interaction
     */
    public StoriesAdapter(ArrayList<Long> itemIDs, Hashtable<Long, Item> items, StoryListener listener) {
        mItemIDs = new ArrayList<>(itemIDs);
        mItems = items;
        mListener = listener;
    }

    /**
     * Update the list of item IDs with a new set
     *
     * @param itemIDs New list of item IDs which will replace the current
     */
    public void setItemIDs(ArrayList<Long> itemIDs) {
        mItemIDs = new ArrayList<>(itemIDs);
        if (mItems == null) {
            mItems = new Hashtable<>();
        }
        mItems.clear();
    }

    /**
     * Add a story to the Hashtable {@link #mItems} of storyItems
     *
     * @param storyItem The item to add
     */
    public void setStory(Item storyItem) {
        if (mItems == null) {
            mItems = new Hashtable<>();
        }
        mItems.put(storyItem.getId(), storyItem);
    }

    /**
     * Get the story items
     *
     * @return Hashtable of story items mapped to their IDs
     */
    public Hashtable<Long, Item> getItems() {
        return mItems != null ? mItems : new Hashtable<Long, Item>();
    }

    /**
     * Inflate the layout
     *
     * @param viewGroup The view group under which the view belongs
     * @param i         The view type
     * @return ViewHolder {@link ViewHolder}
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_stories, viewGroup, false);
        return new ViewHolder(v);
    }

    /**
     * Assign data to a ViewHolder {@link ViewHolder}
     * This first checks the data availability in the Hashtable {@link #mItems}
     * I available, then loads the data else
     * notifies the caller to load data
     *
     * @param viewHolder The ViewHolder in operation
     * @param i          The position of the view in the list
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        long itemID = mItemIDs.get(i);
        Item item = mItems.get(itemID);
        if (item == null) {
            viewHolder.mContentView.setVisibility(View.GONE);
            viewHolder.mProgressBar.setVisibility(View.VISIBLE);
            viewHolder.mContentView.setTag(null);
            viewHolder.mCommentsText.setTag(null);
        } else {
            viewHolder.mContentView.setVisibility(View.VISIBLE);
            viewHolder.mProgressBar.setVisibility(View.GONE);
            viewHolder.mScoreText.setText(String.valueOf(item.getScore()));
            if (!TextUtils.isEmpty(item.getTitle())) {
                viewHolder.mTitleText.setVisibility(View.VISIBLE);
                viewHolder.mTitleText.setText(item.getTitle());
            } else {
                viewHolder.mTitleText.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.getText())) {
                viewHolder.mBodyText.setVisibility(View.VISIBLE);
                viewHolder.mBodyText.setText(Html.fromHtml(item.getText()));
            } else {
                viewHolder.mBodyText.setVisibility(View.GONE);
            }
            viewHolder.mTimeText.setText(AppUtil.getItemTime(viewHolder.mTimeText.getContext(), System.currentTimeMillis(), item.getTime() * 1000));
            if (!TextUtils.isEmpty(item.getBy())) {
                viewHolder.mUserText.setVisibility(View.VISIBLE);
                viewHolder.mUserText.setText(viewHolder.mUserText.getContext().getString(R.string.user_text, item.getBy()));
            } else {
                viewHolder.mUserText.setVisibility(View.GONE);
            }
            String str = viewHolder.mCommentsText.getContext().getString(R.string.comments_text, item.getDescendants());
            if (item.getDescendants() > 0) {
                str = "<a href=''>" + str + "</a>";
            }
            viewHolder.mCommentsText.setText(Html.fromHtml(str));
            viewHolder.mContentView.setTag(item.getUrl());
            viewHolder.mCommentsText.setTag(item.getKids());
        }
    }

    /**
     * Get total number of items in the list
     *
     * @return number of items in the list
     */
    @Override
    public int getItemCount() {
        return mItemIDs.size();
    }

    /**
     * A class to hold references to views to avoid costly findViewById() calls
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mContentView;
        private TextView mScoreText;
        private TextView mTitleText;
        private TextView mBodyText;
        private TextView mTimeText;
        private TextView mUserText;
        private TextView mCommentsText;
        private ProgressBar mProgressBar;

        ViewHolder(View v) {
            super(v);
            mContentView = v.findViewById(R.id.content_layout);
            mScoreText = (TextView) v.findViewById(R.id.tv_score);
            mTitleText = (TextView) v.findViewById(R.id.tv_title);
            mBodyText = (TextView) v.findViewById(R.id.tv_body);
            mTimeText = (TextView) v.findViewById(R.id.tv_time);
            mUserText = (TextView) v.findViewById(R.id.tv_user);
            mCommentsText = (TextView) v.findViewById(R.id.tv_comments);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mCommentsText.setOnClickListener(commentsClick);
            mContentView.setOnClickListener(storyClick);
        }
    }


    /**
     * Click listener for a single story
     */
    View.OnClickListener storyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onStoryClicked((String) v.getTag());
            }
        }
    };

    /**
     * Click listener for comments of a single story
     */
    View.OnClickListener commentsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onCommentsClicked((List<Long>) v.getTag());
            }
        }
    };

}