package dev.dencrafty.telegramrssreader.data.model

import dev.dencrafty.telegramrssreader.data.source.COMMERCIAL_URL

/*
Сейчас вытягивается из одного тг поста его номер, сообщение и локально проверка на рекламный пост.
 */
data class RssFeed(
    val index: Int,
    val message: String
) {
    val isCommercial: Boolean = message.contains(COMMERCIAL_URL)
}
