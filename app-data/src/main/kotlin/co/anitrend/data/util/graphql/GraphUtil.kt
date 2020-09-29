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

package co.anitrend.data.util.graphql

import co.anitrend.arch.extension.util.pagination.SupportPagingHelper
import co.anitrend.data.arch.AniTrendExperimentalFeature
import co.anitrend.data.arch.database.settings.ISortOrderSettings
import co.anitrend.data.arch.extension.toPageQuery
import co.anitrend.domain.common.enums.contract.ISortable
import co.anitrend.domain.common.graph.IGraphPayload
import io.github.wax911.library.model.request.QueryContainerBuilder

/**
 * Graph request helper class
 */
internal object GraphUtil {

    private val SORT_ORDER_EXCEPTIONS = listOf(
        "SEARCH_MATCH"
    )

    private const val SORT_ORDER_DESC_POSTFIX = "_DESC"

    /**
     * Default per page loading limit for this application
     */
    const val PAGING_LIMIT = 30

    /**
     * Provides a default GraphQL Query and Variable Builder
     *
     * @param paging Optional paging helper
     * @param settings Optional sort order settings
     * @param ignoreNulls Ignore null values, defaults to true
     */
    internal fun IGraphPayload.toQueryContainerBuilder(
        paging: SupportPagingHelper? = null,
        settings: ISortOrderSettings? = null,
        ignoreNulls: Boolean = true
    ): QueryContainerBuilder {
        val queryContainerBuilder = QueryContainerBuilder()
        paging?.apply {
            queryContainerBuilder.putVariables(toPageQuery().toMap())
        }
        // A better way might be to perform changes on the `toMap()` contract itself
        val variables = toMap().toMutableMap()
        if (settings != null) {
            variables
                .filter { it.value is List<*> }
                .forEach { entry ->
                    val mapped = (entry.value as List<*>).map {
                        if (it is Enum<*> && it is ISortable)
                            return@map it.applySortOrderUsing(settings)
                        it
                    }
                    variables[entry.key] = mapped
                }
        }
        queryContainerBuilder.putVariables(
            if (ignoreNulls)
                variables.filterValues { it != null }
            else variables
        )
        return queryContainerBuilder
    }

    /**
     * Compacts the request body aiding in the shrinkage of the request payload
     *
     * @param shrink flag which allows or prevents minification
     */
    @AniTrendExperimentalFeature
    internal fun String.minify(shrink: Boolean): String? {
        return if (shrink) replace(
            "\n\n", " "
        ).replace(
            '\t', ' '
        ).replace(
            '\n', ' '
        ).replace(
            "    ", " "
        ) else this
    }

    /**
     * Applies order on sortable keys, if the key is not among the sort order exceptions
     *
     * @see SORT_ORDER_EXCEPTIONS
     */
    fun <E : Enum<E>> Enum<E>.applySortOrderUsing(settings: ISortOrderSettings): String {
        val sortType = name
        if (settings.isSortOrderDescending) {
            return if (SORT_ORDER_EXCEPTIONS.contains(sortType))
                sortType
            else
                sortType + SORT_ORDER_DESC_POSTFIX
        }
        return sortType
    }
}