package org.mariotaku.twidere.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 16/3/9.
 */
@ParcelablePlease
@JsonObject
public class ParcelableGroup implements Parcelable, Comparable<ParcelableGroup> {

    @ParcelableThisPlease
    @JsonField(name = "account_key")
    public UserKey account_key;
    @ParcelableThisPlease
    @JsonField(name = "id")
    public long id;

    @ParcelableThisPlease
    @JsonField(name = "nickname")
    public String nickname;
    @ParcelableThisPlease
    @JsonField(name = "homepage")
    public String homepage;
    @ParcelableThisPlease
    @JsonField(name = "fullname")
    public String fullname;
    @ParcelableThisPlease
    @JsonField(name = "url")
    public String url;
    @ParcelableThisPlease
    @JsonField(name = "description")
    public String description;
    @ParcelableThisPlease
    @JsonField(name = "location")
    public String location;

    @ParcelableThisPlease
    @JsonField(name = "position")
    public long position;

    @ParcelableThisPlease
    @JsonField(name = "created")
    public long created;
    @ParcelableThisPlease
    @JsonField(name = "modified")
    public long modified;
    @ParcelableThisPlease
    @JsonField(name = "admin_count")
    public long admin_count;
    @ParcelableThisPlease
    @JsonField(name = "member_count")
    public long member_count;

    @ParcelableThisPlease
    @JsonField(name = "original_logo")
    public String original_logo;
    @ParcelableThisPlease
    @JsonField(name = "homepage_logo")
    public String homepage_logo;
    @ParcelableThisPlease
    @JsonField(name = "stream_logo")
    public String stream_logo;
    @ParcelableThisPlease
    @JsonField(name = "mini_logo")
    public String mini_logo;

    @ParcelableThisPlease
    @JsonField(name = "blocked")
    public boolean blocked;
    @ParcelableThisPlease
    @JsonField(name = "member")
    public boolean member;

    @Override
    public String toString() {
        return "ParcelableGroup{" +
                "account_key=" + account_key +
                ", id=" + id +
                ", nickname='" + nickname + '\'' +
                ", homepage='" + homepage + '\'' +
                ", fullname='" + fullname + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", admin_count=" + admin_count +
                ", member_count=" + member_count +
                ", original_logo='" + original_logo + '\'' +
                ", homepage_logo='" + homepage_logo + '\'' +
                ", stream_logo='" + stream_logo + '\'' +
                ", mini_logo='" + mini_logo + '\'' +
                ", blocked=" + blocked +
                ", member=" + member +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableGroupParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParcelableGroup that = (ParcelableGroup) o;

        if (id != that.id) return false;
        return account_key.equals(that.account_key);

    }

    @Override
    public int hashCode() {
        int result = account_key.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    public static final Creator<ParcelableGroup> CREATOR = new Creator<ParcelableGroup>() {
        public ParcelableGroup createFromParcel(Parcel source) {
            ParcelableGroup target = new ParcelableGroup();
            ParcelableGroupParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ParcelableGroup[] newArray(int size) {
            return new ParcelableGroup[size];
        }
    };

    @Override
    public int compareTo(@NonNull ParcelableGroup another) {
        return (int) (this.position - another.position);
    }
}
