/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.api.twitter.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/5/13.
 */
@JsonObject
public class AccountSettings extends TwitterResponseObject {

    @JsonField(name = "geo_enabled")
    boolean geoEnabled;
    @JsonField(name = "trend_location")
    Location[] trendLocations;
    @JsonField(name = "language")
    String language;
    @JsonField(name = "always_use_https")
    boolean alwaysUseHttps;
    @JsonField(name = "time_zone")
    TimeZone timezone;

    public boolean isAlwaysUseHttps() {
        return alwaysUseHttps;
    }

    public String getLanguage() {
        return language;
    }

    public TimeZone getTimeZone() {
        return timezone;
    }

    public Location[] getTrendLocations() {
        return trendLocations;
    }

    public boolean isGeoEnabled() {
        return geoEnabled;
    }

}
