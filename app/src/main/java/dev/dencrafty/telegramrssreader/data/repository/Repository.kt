package dev.dencrafty.telegramrssreader.data.repository

import android.util.Log
import dev.dencrafty.telegramrssreader.data.source.DataSource
import dev.dencrafty.telegramrssreader.data.model.RssChannel
import dev.dencrafty.telegramrssreader.data.model.RssFeed
import dev.dencrafty.telegramrssreader.data.paging.PAGE_SIZE
import dev.dencrafty.telegramrssreader.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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
        val deferred = mutableListOf<Deferred<RssFeed>>()
        withContext(ioDispatcher) {
            val startRange = channelSize - PAGE_SIZE * (nextPageNumber - 1)
            val endRange = channelSize - PAGE_SIZE * nextPageNumber + 1
            for (i in startRange downTo endRange) {
                val def = async {
                    singleFeed(channelId, i)
                }
                deferred.add(def)
            }
        }
        // deffered await all сохраняет порядок коллекции, даже при асинхронных запросах
        val list = deferred.awaitAll()
        return list
    }
}