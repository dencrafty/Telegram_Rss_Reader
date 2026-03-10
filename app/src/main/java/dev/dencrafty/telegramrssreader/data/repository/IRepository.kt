package dev.dencrafty.telegramrssreader.data.repository

import dev.dencrafty.telegramrssreader.data.model.RssChannel
import dev.dencrafty.telegramrssreader.data.model.RssFeed

interface IRepository {

    suspend fun channelInfo(channelId: String) : RssChannel
    // Вытягиваю один пост по имени канала и номеру поста.
    suspend fun singleFeed(channelId: String, feedId: Int) : RssFeed
    /*
     Вытягиваю условную страницу канала для формирования PagingSource. По факту делается 20 асинхронных
     запросов с помощью singleFeed и формируется список из 20 сообщений.
     */
    suspend fun pageFeed(channelId: String, channelSize: Int, nextPageNumber: Int) : List<RssFeed>

}