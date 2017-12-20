package com.example.fauza.zmdb.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.fauza.zmdb.R;

public class MoreReviewHolder extends RecyclerView.ViewHolder {

    private TextView textViewReview;

    public MoreReviewHolder(View itemView) {
        super(itemView);
        textViewReview = itemView.findViewById(R.id.textView_more_review_content);
    }

    public TextView getTextViewReview() {
        return textViewReview;
    }
}
