package dev.dencrafty.telegramrssreader.data.source

import dev.dencrafty.telegramrssreader.data.model.RssFeed

interface IDataSource {

    // Узнаю общее количество выпущенных постов на канале с начала основания из url. Посты в тг нумеруются от 1 до N.
    suspend fun fetchChannelSize(channelId: String): Int

    // Вытягивается один пост по url
    suspend fun fetchFeed(channelId: String, feedId: Int): RssFeed
}