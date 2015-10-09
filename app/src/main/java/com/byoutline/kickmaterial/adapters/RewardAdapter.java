package com.byoutline.kickmaterial.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.model.Reward;
import com.byoutline.kickmaterial.model.RewardItem;
import com.byoutline.secretsauce.utils.ViewUtils;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class RewardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final List<RewardItem> dataset = new ArrayList<>();
    private final int semiGreenColor;
    private Context context;
    private RewardClickListener rewardClickListener;

    // Adapter's Constructor
    public RewardAdapter(Context context, RewardClickListener rewardClickListener) {
        this.context = context;
        this.rewardClickListener = rewardClickListener;
        semiGreenColor = ContextCompat.getColor(context, R.color.green_light);
    }


    public RewardItem getItem(int position) {
        if (dataset != null) {
            return dataset.get(position);
        } else {
            return null;
        }
    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the row item xml.
        View v = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case RewardItem.ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_list_item, parent, false);
                holder = new RewardViewHolder(v, rewardClickListener);
                break;

            case RewardItem.HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_header_item, parent, false);
                holder = new RewardHeaderViewHolder(v);
                break;
        }

        // Set the view to the ViewHolder

        return holder;
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        switch (type) {
            case RewardItem.ITEM:
                RewardViewHolder rewardHolder = (RewardViewHolder) holder;
                Reward reward = (Reward) getItem(position);

                if (position == 0) {
                    rewardHolder.rewardItemContainerCv.setCardBackgroundColor(semiGreenColor);
                } else {
                    rewardHolder.rewardItemContainerCv.setCardBackgroundColor(Color.WHITE);
                }

                if (reward != null) {

                    rewardHolder.rewardItemAmountTv.setText(context.getString(R.string.reward_value, reward.minimum));
//                    ViewUtils.setTextOrClear(rewardHolder.rewardItemAmountTv, Double.toString(reward.minimum));
                    ViewUtils.setTextOrClear(rewardHolder.rewardItemDescTv, reward.description);
                    ViewUtils.setTextOrClear(rewardHolder.rewardItemTitleTv, reward.reward);
                }
                break;

            case RewardItem.HEADER:
                RewardHeaderViewHolder headerHolder = (RewardHeaderViewHolder) holder;
                RewardHeader header = (RewardHeader) getItem(position);

                headerHolder.rewardHeaderTv.setText(context.getString(R.string.reward_more_than, header.minimum));

                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemType();
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setItems(List<Reward> items) {
        synchronized (dataset) {
            double currentTreshold = 100.0;
            double step = 10;
            dataset.clear();

            ArrayList<RewardItem> rewardItems = new ArrayList<>();

            for (Reward reward : items) {

                if (reward.minimum >= currentTreshold) {
                    rewardItems.add(new RewardHeader((int) currentTreshold));
                    currentTreshold *= step;
                }
                rewardItems.add(reward);
            }

            dataset.addAll(rewardItems);
            notifyDataSetChanged();
            rewardItems.clear();
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'reward_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

    // Create the ViewHolder class to keep references to your views
    public static class RewardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public RewardClickListener listener;
        @Bind(R.id.reward_item_amount_tv)
        TextView rewardItemAmountTv;
        @Bind(R.id.reward_item_currency_tv)
        TextView rewardItemCurrencyTv;
        @Bind(R.id.reward_item_type_tv)
        TextView rewardItemTitleTv;
        @Bind(R.id.reward_item_desc_tv)
        TextView rewardItemDescTv;

        @Bind(R.id.reward_container_cv)
        CardView rewardItemContainerCv;


        /**
         * Constructor
         *
         * @param v The container view which holds the elements from the row item xml
         */
        public RewardViewHolder(View v, RewardClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.rewardClicked(getPosition());
            }
        }
    }

    @Parcel(Parcel.Serialization.FIELD)
    public class RewardHeader implements RewardItem {

        public int minimum;

        public RewardHeader(int minimum) {
            this.minimum = minimum;
        }

        @Override
        public int getItemType() {
            return HEADER;
        }
    }

    class RewardHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.reward_header_tv)
        TextView rewardHeaderTv;

        RewardHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}