/*
 * Copyright (C) 2019  AniTrend
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

package co.anitrend.data.tag.datasource

import androidx.lifecycle.LiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.source.core.SupportCoreDataSource
import co.anitrend.data.extensions.koinOf
import co.anitrend.data.model.core.media.MediaTag
import co.anitrend.data.util.graphql.GraphUtil
import io.github.wax911.library.model.request.QueryContainerBuilder

abstract class MediaTagSource : SupportCoreDataSource(koinOf()) {

    /**
     * Registers a dispatcher executing a unit of work and then returns a
     * [androidx.lifecycle.LiveData] observable
     */
    protected abstract val observable: ISourceObservable<Nothing?, List<MediaTag>>

    /**
     * Handles dispatcher for requesting media tags
     */
    abstract fun getMediaTags(
        queryContainerBuilder: QueryContainerBuilder = GraphUtil.getDefaultQuery()
    ): LiveData<List<MediaTag>>
}