/*
 * Copyright (C) 2021  AniTrend
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package co.anitrend.data.news.source

import androidx.paging.PagedList
import co.anitrend.arch.data.paging.FlowPagedListBuilder
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.util.PAGING_CONFIGURATION
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.data.arch.helper.data.ClearDataHelper
import co.anitrend.data.arch.helper.data.contract.IClearDataHelper
import co.anitrend.data.cache.repository.contract.ICacheStorePolicy
import co.anitrend.data.news.NewPagedController
import co.anitrend.data.news.cache.NewsCache
import co.anitrend.data.news.converter.NewsEntityConverter
import co.anitrend.data.news.datasource.local.NewsLocalSource
import co.anitrend.data.news.datasource.remote.NewsRemoteSource
import co.anitrend.data.news.source.contract.NewsSource
import co.anitrend.data.util.graphql.GraphUtil.toQueryContainerBuilder
import co.anitrend.domain.news.entity.News
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

internal class NewsSourceImpl(
    private val remoteSource: NewsRemoteSource,
    private val localSource: NewsLocalSource,
    private val clearDataHelper: IClearDataHelper,
    private val controller: NewPagedController,
    private val converter: NewsEntityConverter,
    cachePolicy: ICacheStorePolicy,
    dispatcher: ISupportDispatcher
) : NewsSource(cachePolicy, dispatcher) {

    override fun observable(): Flow<PagedList<News>> {
        val dataSourceFactory = localSource
            .factoryDesc()
            .map { converter.convertFrom(it) }

        return FlowPagedListBuilder(
            dataSourceFactory,
            PAGING_CONFIGURATION,
            null,
            this
        ).buildFlow()
    }

    override suspend fun getNews(requestCallback: RequestCallback): Boolean {
        val deferred = async {
            remoteSource.getNews(query.locale)
        }

        val result = controller(deferred, requestCallback)

        return !result.isNullOrEmpty()
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     *
     * @param context Dispatcher context to run in
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        clearDataHelper(context) {
            cachePolicy.invalidateLastRequest(
                NewsCache.Identity.NEWS.id
            )
            localSource.clear()
        }
    }
}