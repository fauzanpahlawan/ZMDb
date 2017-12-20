package com.example.fauza.zmdb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fauza.zmdb.R;
import com.example.fauza.zmdb.viewHolder.MoreReviewHolder;

import java.util.ArrayList;

public class MoreReviewAdapter extends RecyclerView.Adapter<MoreReviewHolder> {
    private Context mContext;
    private ArrayList<String> moreReviews;

    public MoreReviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setMoreReviews(ArrayList<String> moreReviews) {
        this.moreReviews = moreReviews;
    }

    @Override
    public MoreReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_more_review, parent, false);
        return new MoreReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoreReviewHolder holder, int position) {
        holder.getTextViewReview().setText(moreReviews.get(position));
    }

    @Override
    public int getItemCount() {
        if (moreReviews == null) {
            return 0;
        } else {
            return moreReviews.size();
        }
    }
}
