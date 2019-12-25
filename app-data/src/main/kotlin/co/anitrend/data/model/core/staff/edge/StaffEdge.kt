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

package co.anitrend.data.model.core.staff.edge

import co.anitrend.domain.common.entity.IEntityEdge
import co.anitrend.data.model.core.staff.Staff

/** [StaffEdge](https://anilist.github.io/ApiV2-GraphQL-Docs/staffedge.doc.html)
 * Staff connection edge
 *
 * @param favouriteOrder The order the staff should be displayed from the users favourites
 * @param role The role of the staff member in the production of the media
 */
data class StaffEdge(
    val favouriteOrder: Int?,
    val role: String?,
    override val id: Long,
    override val node: Staff?
) : IEntityEdge<Staff>