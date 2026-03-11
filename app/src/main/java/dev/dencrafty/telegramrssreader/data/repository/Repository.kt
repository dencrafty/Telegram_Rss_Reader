package dev.dencrafty.telegramrssreader.data.repository

import dev.dencrafty.telegramrssreader.data.source.DataSource
import dev.dencrafty.telegramrssreader.data.model.RssChannel
import dev.dencrafty.telegramrssreader.data.model.RssFeed
import dev.dencrafty.telegramrssreader.data.paging.PAGE_SIZE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val dataSource: DataSource
) : IRepository {

    override suspend fun channelInfo(channelId: String): RssChannel {
        val size = dataSource.fetchChannelSize(channelId)
//        val description = dataSource.fetchChannelDescription(channelId)
        return RssChannel(channelId, size)
    }

    override suspend fun singleFeed(channelId: String, feedId: Int): RssFeed {
        return dataSource.fetchFeed(channelId, feedId)
    }

    /*
    Вся логика формирования одной страницы с сообщениями в диапазоне startRange и endRange.
    Например, если тг канал имеет 99 постов, то 1я страница это посты с 90 по 99, 2я это 80-89 и т.д. по убыванию.
     */
    override suspend fun pageFeed(
        channelId: String,
        channelSize: Int,
        nextPageNumber: Int
    ): List<RssFeed> {
        val list = mutableListOf<RssFeed>()
        val startRange = channelSize - PAGE_SIZE * (nextPageNumber - 1)
        val endRange = channelSize - PAGE_SIZE * nextPageNumber + 1
        for (i in startRange downTo endRange) {
            val feed = singleFeed(channelId, i)
            list.add(feed)
        }
        return list
    }
}