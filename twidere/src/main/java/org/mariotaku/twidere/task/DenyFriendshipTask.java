package org.mariotaku.twidere.task;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mariotaku.twidere.R;
import org.mariotaku.twidere.api.twitter.Twitter;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableAccount;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.twidere.model.ParcelableUser;
import org.mariotaku.twidere.model.message.FriendshipTaskEvent;
import org.mariotaku.twidere.model.util.ParcelableAccountUtils;
import org.mariotaku.twidere.util.Utils;

/**
 * Created by mariotaku on 16/3/11.
 */
public class DenyFriendshipTask extends AbsFriendshipOperationTask {

    public DenyFriendshipTask(final Context context) {
        super(context, FriendshipTaskEvent.Action.DENY);
    }

    @NonNull
    @Override
    protected User perform(@NonNull Twitter twitter, @NonNull ParcelableCredentials credentials, @NonNull Arguments args) throws TwitterException {
        switch (ParcelableAccountUtils.getAccountType(credentials)) {
            case ParcelableAccount.Type.FANFOU: {
                return twitter.denyFanfouFriendship(args.userKey.getId());
            }
        }
        return twitter.denyFriendship(args.userKey.getId());
    }

    @Override
    protected void succeededWorker(@NonNull Twitter twitter, @NonNull ParcelableCredentials credentials, @NonNull Arguments args, @NonNull ParcelableUser user) {
        Utils.setLastSeen(context, user.key, -1);
    }

    @Override
    protected void showErrorMessage(@NonNull Arguments params, @Nullable Exception exception) {
        Utils.showErrorMessage(context, R.string.action_denying_follow_request, exception, false);
    }

    @Override
    protected void showSucceededMessage(@NonNull Arguments params, @NonNull ParcelableUser user) {
        final boolean nameFirst = preferences.getBoolean(KEY_NAME_FIRST);
        final String message = context.getString(R.string.denied_users_follow_request,
                manager.getDisplayName(user, nameFirst, true));
        Utils.showOkMessage(context, message, false);
    }

}
