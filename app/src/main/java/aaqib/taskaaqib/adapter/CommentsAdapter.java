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

import aaqib.taskaaqib.R;
import aaqib.taskaaqib.models.Item;
import aaqib.taskaaqib.util.AppUtil;

/**
 * Handles loading of views in the comments list
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<Long> mItemIDs;
    private Hashtable<Long, Item> mItems;

    /**
     * Default Constructor
     */
    public CommentsAdapter() {
        mItemIDs = new ArrayList<>();
        mItems = new Hashtable<>();
    }

    /**
     * Constructor with list of items IDs and listener
     *
     * @param itemIDs List of item IDs of the comments
     */
    public CommentsAdapter(ArrayList<Long> itemIDs) {
        mItemIDs = new ArrayList<>(itemIDs);
        mItems = new Hashtable<>();
    }

    /**
     * Constructor with list of item IDs, items and listener
     *
     * @param itemIDs List of item IDs of the comments
     * @param items   Hashtable of items mapped with their IDs
     */
    public CommentsAdapter(ArrayList<Long> itemIDs, Hashtable<Long, Item> items) {
        mItemIDs = new ArrayList<>(itemIDs);
        mItems = items;
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
     * Removes an item from the List of commentIDs {@link #mItemIDs}
     *
     * @param index the index of the item to remove
     */
    public void removeItem(int index) {
        mItemIDs.remove(index);
    }

    /**
     * Add a comment to the Hashtable {@link #mItems} of commentItems
     *
     * @param commentItem The item to add
     */
    public void setComment(Item commentItem) {
        if (mItems == null) {
            mItems = new Hashtable<>();
        }
        mItems.put(commentItem.getId(), commentItem);
    }

    /**
     * Get the comment items
     *
     * @return Hashtable of comment items mapped to their IDs
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_comment, viewGroup, false);
        return new ViewHolder(v);
    }

    /**
     * Assign data to a ViewHolder {@link ViewHolder}
     * This first checks the data availability in the Hashtable {@link #mItems}
     * If available, then loads the data else
     * shows the progress bar.
     * Same is repeated for the nested comment.
     *
     * @param viewHolder The ViewHolder in operation
     * @param i          The position of the view in the list
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        long itemID = mItemIDs.get(i);
        Item item = mItems.get(itemID);

        if (item == null) {
            toggleProgressBar1(viewHolder, true);
            viewHolder.mContentView2.setVisibility(View.GONE);
        } else {
            toggleProgressBar1(viewHolder, false);
            if (!TextUtils.isEmpty(item.getText())) {
                viewHolder.mBodyText.setVisibility(View.VISIBLE);
                viewHolder.mBodyText.setText(Html.fromHtml(item.getText()));
            } else {
                viewHolder.mBodyText.setVisibility(View.GONE);
            }
            viewHolder.mTimeText.setText(AppUtil.getItemTime(viewHolder.mTimeText.getContext(), System.currentTimeMillis(), item.getTime() * 1000));
            if (!TextUtils.isEmpty(item.getBy())) {
                viewHolder.mUserText.setVisibility(View.VISIBLE);
                viewHolder.mUserText.setText(item.getBy());
            } else {
                viewHolder.mUserText.setVisibility(View.GONE);
            }

            if (item.getKids() != null && item.getKids().size() > 0) {
                viewHolder.mContentView2.setVisibility(View.VISIBLE);
                long itemID2 = item.getKids().get(0);
                Item item2 = mItems.get(itemID2);
                if (item2 == null) {
                    toggleProgressBar2(viewHolder, true);
                } else {
                    if (item2.isDeleted() || item2.isDead()) {
                        viewHolder.mContentView2.setVisibility(View.GONE);
                    } else {
                        toggleProgressBar2(viewHolder, false);
                        if (!TextUtils.isEmpty(item2.getText())) {
                            viewHolder.mBodyText2.setVisibility(View.VISIBLE);
                            viewHolder.mBodyText2.setText(Html.fromHtml(item2.getText()));
                        } else {
                            viewHolder.mBodyText2.setVisibility(View.GONE);
                        }
                        viewHolder.mTimeText2.setText(AppUtil.getItemTime(viewHolder.mTimeText2.getContext(), System.currentTimeMillis(), item2.getTime() * 1000));
                        if (!TextUtils.isEmpty(item2.getBy())) {
                            viewHolder.mUserText2.setVisibility(View.VISIBLE);
                            viewHolder.mUserText2.setText(item2.getBy());
                        } else {
                            viewHolder.mUserText2.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                viewHolder.mContentView2.setVisibility(View.GONE);
            }
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

        private View mContentView2;
        private TextView mBodyText, mBodyText2;
        private TextView mTimeText, mTimeText2;
        private TextView mUserText, mUserText2;
        private ProgressBar mProgressBar, mProgressBar2;

        ViewHolder(View v) {
            super(v);
            mBodyText = (TextView) v.findViewById(R.id.tv_body);
            mTimeText = (TextView) v.findViewById(R.id.tv_time);
            mUserText = (TextView) v.findViewById(R.id.tv_user);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            mContentView2 = v.findViewById(R.id.content_layout2);
            mBodyText2 = (TextView) v.findViewById(R.id.tv_body2);
            mTimeText2 = (TextView) v.findViewById(R.id.tv_time2);
            mUserText2 = (TextView) v.findViewById(R.id.tv_user2);
            mProgressBar2 = (ProgressBar) v.findViewById(R.id.progress_bar2);
        }
    }

    /**
     * Toggles the visibility of progressBar for the main comment
     *
     * @param vh     ViewHolder reference
     * @param enable true to show progressBar and hide content,
     *               else false to hide progressBar and show content
     */
    private void toggleProgressBar1(ViewHolder vh, boolean enable) {
        if (enable) {
            vh.mBodyText.setVisibility(View.GONE);
            vh.mTimeText.setVisibility(View.GONE);
            vh.mUserText.setVisibility(View.GONE);
            vh.mProgressBar.setVisibility(View.VISIBLE);
        } else {
            vh.mBodyText.setVisibility(View.VISIBLE);
            vh.mTimeText.setVisibility(View.VISIBLE);
            vh.mUserText.setVisibility(View.VISIBLE);
            vh.mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Toggles the visibility of progressBar for the nested comment
     *
     * @param vh     ViewHolder reference
     * @param enable true to show progressBar and hide content,
     *               else false to hide progressBar and show content
     */
    private void toggleProgressBar2(ViewHolder vh, boolean enable) {
        if (enable) {
            vh.mBodyText2.setVisibility(View.GONE);
            vh.mTimeText2.setVisibility(View.GONE);
            vh.mUserText2.setVisibility(View.GONE);
            vh.mProgressBar2.setVisibility(View.VISIBLE);
        } else {
            vh.mBodyText2.setVisibility(View.VISIBLE);
            vh.mTimeText2.setVisibility(View.VISIBLE);
            vh.mUserText2.setVisibility(View.VISIBLE);
            vh.mProgressBar2.setVisibility(View.GONE);
        }
    }

}