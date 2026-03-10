package dev.dencrafty.telegramrssreader.ui.paging

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.dencrafty.telegramrssreader.R
import dev.dencrafty.telegramrssreader.data.model.RssFeed
import dev.dencrafty.telegramrssreader.databinding.FeedViewholderBinding

class FeedViewHolder(
    private val binding: FeedViewholderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(rssFeed: RssFeed) {
        binding.apply {
            binding.title.text = rssFeed.index.toString()
            binding.description.text = rssFeed.message

            if (rssFeed.isCommercial) {
                binding.commercialDetected.visibility = View.VISIBLE
                binding.description.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.faint_black
                    )
                )
            } else {
                binding.commercialDetected.visibility = View.GONE
                binding.description.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.black
                    )
                )
            }

            if (rssFeed.message.isEmpty()) {
                binding.withoutMessage.visibility = View.VISIBLE
            } else {
                binding.withoutMessage.visibility = View.GONE
            }
        }
    }
}