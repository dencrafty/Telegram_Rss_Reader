package dev.dencrafty.telegramrssreader.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.dencrafty.telegramrssreader.data.model.RssChannel
import dev.dencrafty.telegramrssreader.data.model.RssFeed
import dev.dencrafty.telegramrssreader.data.repository.Repository
import javax.inject.Inject

const val PAGE_SIZE = 20

/*
Здесь формирую данные для отображения в recycler view.
 */

class FeedPagingSource @Inject constructor(
    private val repository: Repository
) : PagingSource<Int, RssFeed>() {

    lateinit var channel: RssChannel

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RssFeed> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response =
                repository.pageFeed(channel.id, channel.size, nextPageNumber)
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.size < PAGE_SIZE) null else (nextPageNumber + 1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, RssFeed>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}