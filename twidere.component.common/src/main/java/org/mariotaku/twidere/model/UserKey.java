package org.mariotaku.twidere.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import org.mariotaku.twidere.api.twitter.util.TwitterDateConverter;

import java.util.List;

/**
 * Created by mariotaku on 16/3/5.
 */
@JsonObject
@ParcelablePlease
public class UserKey implements Comparable<UserKey>, Parcelable {

    public static final Creator<UserKey> CREATOR = new Creator<UserKey>() {
        public UserKey createFromParcel(Parcel source) {
            UserKey target = new UserKey();
            UserKeyParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public UserKey[] newArray(int size) {
            return new UserKey[size];
        }
    };

    @NonNull
    @JsonField(name = "id")
    @ParcelableThisPlease
    String id = "";
    @Nullable
    @JsonField(name = "host")
    @ParcelableThisPlease
    String host;

    public UserKey(@NonNull String id, @Nullable String host) {
        this.id = id;
        this.host = host;
    }

    UserKey() {

    }

    @NonNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getHost() {
        return host;
    }

    @Override
    public String toString() {
        if (host != null) return escapeText(id) + "@" + escapeText(host);
        return id;
    }

    @Override
    public int compareTo(@NonNull UserKey another) {
        if (this.id.equals(another.id)) {
            if (this.host != null && another.host != null) {
                return this.host.compareTo(another.host);
            } else if (this.host != null) {
                return 1;
            } else if (another.host != null) {
                return -1;
            }
            return 0;
        }
        return this.id.compareTo(another.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserKey userKey = (UserKey) o;

        if (!id.equals(userKey.id)) return false;
        return !(host != null ? !host.equals(userKey.host) : userKey.host != null);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (host != null ? host.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        UserKeyParcelablePlease.writeToParcel(this, dest, flags);
    }

    public boolean check(String accountId, String accountHost) {
        return this.id.equals(accountId);
    }

    @Nullable
    public static UserKey valueOf(@Nullable String str) {
        if (str == null) return null;
        boolean escaping = false, idFinished = false;
        StringBuilder idBuilder = new StringBuilder(), hostBuilder = new StringBuilder();
        for (int i = 0, j = str.length(); i < j; i++) {
            final char ch = str.charAt(i);
            boolean append = false;
            if (escaping) {
                // accept all characters if is escaping
                append = true;
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == '@') {
                idFinished = true;
            } else if (ch == ',') {
                // end of item, just jump out
                break;
            } else {
                append = true;
            }
            if (append) {
                if (idFinished) {
                    hostBuilder.append(ch);
                } else {
                    idBuilder.append(ch);
                }
            }
        }
        if (hostBuilder.length() != 0) {
            return new UserKey(idBuilder.toString(), hostBuilder.toString());
        } else {
            return new UserKey(idBuilder.toString(), null);
        }
    }

    @Nullable
    public static UserKey[] arrayOf(@Nullable String str) {
        if (str == null) return null;
        List<String> split = TwitterDateConverter.split(str, ",");
        UserKey[] keys = new UserKey[split.size()];
        for (int i = 0, splitLength = split.size(); i < splitLength; i++) {
            keys[i] = valueOf(split.get(i));
            if (keys[i] == null) return null;
        }
        return keys;
    }

    public static String[] getIds(UserKey[] ids) {
        String[] result = new String[ids.length];
        for (int i = 0, idsLength = ids.length; i < idsLength; i++) {
            result[i] = ids[i].getId();
        }
        return result;
    }


    public static String escapeText(String host) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, j = host.length(); i < j; i++) {
            final char ch = host.charAt(i);
            if (isSpecialChar(ch)) {
                sb.append('\\');
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    private static boolean isSpecialChar(char ch) {
        return ch == '\\' || ch == '@' || ch == ',';
    }

    public boolean maybeEquals(@Nullable UserKey another) {
        return another != null && another.getId().equals(id);
    }
}
