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

package co.anitrend.data.source.media

import android.os.Bundle
import co.anitrend.data.api.endpoint.MediaEndPoint
import co.anitrend.data.arch.source.WorkerDataSource
import co.anitrend.data.dao.DatabaseHelper
import co.anitrend.data.mapper.media.MediaGenreMapper
import co.anitrend.data.util.graphql.GraphUtil
import io.wax911.support.data.model.NetworkState
import kotlinx.coroutines.async
import org.koin.core.inject

class MediaGenreDataSource(
private val mediaEndPoint: MediaEndPoint
) : WorkerDataSource() {

    override val databaseHelper by inject<DatabaseHelper>()

    /**
     * Handles the requesting data from a the network source and informs the
     * network state that it is in the loading state
     *
     * @param bundle request parameters or more
     */
    override suspend fun startRequestForType(bundle: Bundle?): NetworkState {
        val futureResponse = async {
            mediaEndPoint.getMediaGenres(
                GraphUtil.getDefaultQuery()
            )
        }

        val mapper = MediaGenreMapper(
            databaseHelper.mediaGenreDao()
        )

        return mapper.handleResponse(futureResponse)
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     *
     * @param bundle the request request parameters to use
     */
    override suspend fun refreshOrInvalidate(bundle: Bundle?): NetworkState {
        databaseHelper.mediaTagDao().deleteAll()
        return startRequestForType(bundle)
    }
}