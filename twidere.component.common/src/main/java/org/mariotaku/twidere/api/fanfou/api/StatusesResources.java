package org.mariotaku.twidere.api.fanfou.api;

import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.param.Query;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.Paging;
import org.mariotaku.twidere.api.twitter.model.ResponseList;
import org.mariotaku.twidere.api.twitter.model.Status;

/**
 * Created by mariotaku on 16/3/10.
 */
public interface StatusesResources {

    @GET("/statuses/mentions.json")
    ResponseList<Status> getMentions(@Query Paging paging) throws TwitterException;

    @GET("/statuses/context_timeline.json")
    ResponseList<Status> getContextTimeline(@Query("id") String id, @Query Paging paging) throws TwitterException;

}
