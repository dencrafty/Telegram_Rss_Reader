package dev.dencrafty.telegramrssreader.ui.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import dev.dencrafty.telegramrssreader.data.model.RssFeed
import dev.dencrafty.telegramrssreader.databinding.FeedViewholderBinding

class FeedAdapter(diffCallback: DiffUtil.ItemCallback<RssFeed>) :
    PagingDataAdapter<RssFeed, FeedViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedViewHolder {
        return FeedViewHolder(
            FeedViewholderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }
}

object UserComparator : DiffUtil.ItemCallback<RssFeed>() {
    override fun areItemsTheSame(oldItem: RssFeed, newItem: RssFeed): Boolean {
        return oldItem.index == newItem.index
    }

    override fun areContentsTheSame(oldItem: RssFeed, newItem: RssFeed): Boolean {
        return oldItem == newItem
    }
}