package org.mariotaku.twidere.task;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
public class CreateFriendshipTask extends AbsFriendshipOperationTask {

    public CreateFriendshipTask(final Context context) {
        super(context, FriendshipTaskEvent.Action.FOLLOW);
    }

    @NonNull
    @Override
    protected User perform(@NonNull Twitter twitter, @NonNull ParcelableCredentials credentials, @NonNull Arguments args) throws TwitterException {
        switch (ParcelableAccountUtils.getAccountType(credentials)) {
            case ParcelableAccount.Type.FANFOU: {
                return twitter.createFanfouFriendship(args.userKey.getId());
            }
        }
        return twitter.createFriendship(args.userKey.getId());
    }

    @Override
    protected void succeededWorker(@NonNull Twitter twitter, @NonNull ParcelableCredentials credentials, @NonNull Arguments args, @NonNull ParcelableUser user) {
        Utils.setLastSeen(context, user.key, System.currentTimeMillis());
    }

    @Override
    protected void showErrorMessage(@NonNull Arguments params, @Nullable Exception exception) {
        if (USER_TYPE_FANFOU_COM.equals(params.accountKey.getHost())) {
            // Fanfou returns 403 for follow request
            if (exception instanceof TwitterException) {
                TwitterException te = (TwitterException) exception;
                if (te.getStatusCode() == 403 && !TextUtils.isEmpty(te.getErrorMessage())) {
                    Utils.showErrorMessage(context, te.getErrorMessage(), false);
                    return;
                }
            }
        }
        Utils.showErrorMessage(context, R.string.action_following, exception, false);
    }

    @Override
    protected void showSucceededMessage(@NonNull Arguments params, @NonNull ParcelableUser user) {
        final String message;
        final boolean nameFirst = preferences.getBoolean(KEY_NAME_FIRST);
        if (user.is_protected) {
            message = context.getString(R.string.sent_follow_request_to_user,
                    manager.getDisplayName(user, nameFirst, true));
        } else {
            message = context.getString(R.string.followed_user,
                    manager.getDisplayName(user, nameFirst, true));
        }
        Utils.showOkMessage(context, message, false);
    }

}
