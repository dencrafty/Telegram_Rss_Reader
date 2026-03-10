package dev.dencrafty.telegramrssreader.ui.paging

sealed class PagingUIState {
    /*
    Отправная точка для парсинга тг канала это получение индекса - общего количества постов канала
    на данный момент времени. После успешного получения индекса начинается формирование списка постов.
     */
    data object IndexSuccess : PagingUIState()

    data class Error(val exception: Throwable) : PagingUIState()

    data object InvalidChannelId : PagingUIState()
}