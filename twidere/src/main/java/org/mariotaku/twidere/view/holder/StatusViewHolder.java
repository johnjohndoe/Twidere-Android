package org.mariotaku.twidere.view.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.BidiFormatter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.R;
import org.mariotaku.twidere.adapter.iface.IStatusesAdapter;
import org.mariotaku.twidere.graphic.like.LikeAnimationDrawable;
import org.mariotaku.twidere.model.ParcelableLocation;
import org.mariotaku.twidere.model.ParcelableMedia;
import org.mariotaku.twidere.model.ParcelableStatus;
import org.mariotaku.twidere.model.UserKey;
import org.mariotaku.twidere.model.util.ParcelableLocationUtils;
import org.mariotaku.twidere.util.AsyncTwitterWrapper;
import org.mariotaku.twidere.util.HtmlSpanBuilder;
import org.mariotaku.twidere.util.MediaLoaderWrapper;
import org.mariotaku.twidere.util.TwidereLinkify;
import org.mariotaku.twidere.util.TwitterCardUtils;
import org.mariotaku.twidere.util.UnitConvertUtils;
import org.mariotaku.twidere.util.UserColorNameManager;
import org.mariotaku.twidere.util.Utils;
import org.mariotaku.twidere.view.ActionIconThemedTextView;
import org.mariotaku.twidere.view.CardMediaContainer;
import org.mariotaku.twidere.view.ForegroundColorView;
import org.mariotaku.twidere.view.IconActionView;
import org.mariotaku.twidere.view.NameView;
import org.mariotaku.twidere.view.ShortTimeView;
import org.mariotaku.twidere.view.holder.iface.IStatusViewHolder;
import org.mariotaku.twidere.view.iface.IColorLabelView;

import java.lang.ref.WeakReference;

import static org.mariotaku.twidere.util.HtmlEscapeHelper.toPlainText;
import static org.mariotaku.twidere.util.Utils.getUserTypeIconRes;

/**
 * IDE gives me warning if I don't change default comment, so I wrote this XD
 * <p/>
 * Created by mariotaku on 14/11/19.
 */
public class StatusViewHolder extends ViewHolder implements Constants, IStatusViewHolder {

    @NonNull
    private final IStatusesAdapter<?> adapter;

    private final ImageView statusInfoIcon;
    private final ImageView profileImageView;
    private final ImageView profileTypeView;
    private final ImageView extraTypeView;
    private final TextView textView;
    private final TextView quotedTextView;
    private final NameView nameView;
    private final NameView quotedNameView;
    private final TextView statusInfoLabel;
    private final ShortTimeView timeView;
    private final CardMediaContainer mediaPreview, quoteMediaPreview;
    private final IconActionView replyIconView, retweetIconView, favoriteIconView;
    private final TextView replyCountView, retweetCountView, favoriteCountView;
    private final View replyView, retweetView, favoriteView;
    private final IColorLabelView itemContent;
    private final ForegroundColorView quoteIndicator;
    private final View actionButtons;
    private final View itemMenu;
    private final View profileImageSpace;
    private final View statusInfoSpace;
    @Nullable
    private final View statusContentUpperSpace, statusContentLowerSpace;
    @Nullable
    private final View textMediaSpace, quotedTextMediaSpace;
    private final EventListener eventListener;

    private StatusClickListener statusClickListener;

    public StatusViewHolder(@NonNull final IStatusesAdapter<?> adapter, @NonNull final View itemView) {
        super(itemView);
        this.adapter = adapter;
        this.eventListener = new EventListener(this);
        itemContent = (IColorLabelView) itemView.findViewById(R.id.item_content);
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        profileTypeView = (ImageView) itemView.findViewById(R.id.profile_type);
        extraTypeView = (ImageView) itemView.findViewById(R.id.extra_type);
        textView = (TextView) itemView.findViewById(R.id.text);
        quotedTextView = (TextView) itemView.findViewById(R.id.quoted_text);
        nameView = (NameView) itemView.findViewById(R.id.name);
        quotedNameView = (NameView) itemView.findViewById(R.id.quoted_name);
        statusInfoIcon = (ImageView) itemView.findViewById(R.id.status_info_icon);
        statusInfoLabel = (TextView) itemView.findViewById(R.id.status_info_label);
        timeView = (ShortTimeView) itemView.findViewById(R.id.time);
        profileImageSpace = itemView.findViewById(R.id.profile_image_space);
        statusInfoSpace = itemView.findViewById(R.id.status_info_space);

        mediaPreview = (CardMediaContainer) itemView.findViewById(R.id.media_preview);
        quoteMediaPreview = (CardMediaContainer) itemView.findViewById(R.id.quoted_media_preview);

        quoteIndicator = (ForegroundColorView) itemView.findViewById(R.id.quote_indicator);

        itemMenu = itemView.findViewById(R.id.item_menu);
        actionButtons = itemView.findViewById(R.id.action_buttons);

        replyView = itemView.findViewById(R.id.reply);
        retweetView = itemView.findViewById(R.id.retweet);
        favoriteView = itemView.findViewById(R.id.favorite);

        replyIconView = (IconActionView) itemView.findViewById(R.id.reply_icon);
        retweetIconView = (IconActionView) itemView.findViewById(R.id.retweet_icon);
        favoriteIconView = (IconActionView) itemView.findViewById(R.id.favorite_icon);

        replyCountView = (ActionIconThemedTextView) itemView.findViewById(R.id.reply_count);
        retweetCountView = (ActionIconThemedTextView) itemView.findViewById(R.id.retweet_count);
        favoriteCountView = (ActionIconThemedTextView) itemView.findViewById(R.id.favorite_count);

        statusContentUpperSpace = itemView.findViewById(R.id.status_content_upper_space);
        statusContentLowerSpace = itemView.findViewById(R.id.status_content_lower_space);
        textMediaSpace = itemView.findViewById(R.id.text_media_space);
        quotedTextMediaSpace = itemView.findViewById(R.id.quoted_text_media_space);
        //TODO
        // profileImageView.setSelectorColor(ThemeUtils.getUserHighlightColor(itemView.getContext()));

        if (adapter.isMediaPreviewEnabled()) {
            View.inflate(mediaPreview.getContext(), R.layout.layout_card_media_preview, mediaPreview);
            View.inflate(quoteMediaPreview.getContext(), R.layout.layout_card_media_preview, quoteMediaPreview);
        }
    }

    public void displaySampleStatus() {
        final boolean profileImageEnabled = adapter.isProfileImageEnabled();
        profileImageView.setVisibility(profileImageEnabled ? View.VISIBLE : View.GONE);
        if (profileImageSpace != null) {
            profileImageSpace.setVisibility(profileImageEnabled ? View.VISIBLE : View.GONE);
        }
        if (statusInfoSpace != null) {
            statusInfoSpace.setVisibility(profileImageEnabled ? View.VISIBLE : View.GONE);
        }
        if (statusContentUpperSpace != null) {
            statusContentUpperSpace.setVisibility(View.VISIBLE);
        }

        profileImageView.setImageResource(R.mipmap.ic_launcher);
        nameView.setName(TWIDERE_PREVIEW_NAME);
        nameView.setScreenName("@" + TWIDERE_PREVIEW_SCREEN_NAME);
        nameView.updateText(adapter.getBidiFormatter());
        if (adapter.getLinkHighlightingStyle() == VALUE_LINK_HIGHLIGHT_OPTION_CODE_NONE) {
            final TwidereLinkify linkify = adapter.getTwidereLinkify();
            final CharSequence text = HtmlSpanBuilder.fromHtml(TWIDERE_PREVIEW_TEXT_HTML,
                    TWIDERE_PREVIEW_TEXT_UNESCAPED);
            textView.setText(linkify.applyAllLinks(text, null, -1, false,
                    adapter.getLinkHighlightingStyle(), true));
        } else {
            textView.setText(toPlainText(TWIDERE_PREVIEW_TEXT_HTML));
        }
        timeView.setTime(System.currentTimeMillis());
        final boolean showCardActions = isCardActionsShown();
        if (adapter.isMediaPreviewEnabled()) {
            mediaPreview.setVisibility(View.VISIBLE);
            if (textMediaSpace != null) {
                textMediaSpace.setVisibility(View.GONE);
            }
        } else {
            mediaPreview.setVisibility(View.GONE);
            if (textMediaSpace != null) {
                textMediaSpace.setVisibility(showCardActions ? View.GONE : View.VISIBLE);
            }
        }
        actionButtons.setVisibility(showCardActions ? View.VISIBLE : View.GONE);
        itemMenu.setVisibility(showCardActions ? View.VISIBLE : View.GONE);
        if (statusContentLowerSpace != null) {
            statusContentLowerSpace.setVisibility(showCardActions ? View.GONE : View.VISIBLE);
        }
        quoteMediaPreview.setVisibility(View.GONE);
        if (quotedTextMediaSpace != null) {
            quotedTextMediaSpace.setVisibility(View.GONE);
        }
        mediaPreview.displayMedia(R.drawable.nyan_stars_background);
        extraTypeView.setImageResource(R.drawable.ic_action_gallery);
    }

    @Override
    public void displayStatus(final ParcelableStatus status, final boolean displayInReplyTo) {
        displayStatus(status, displayInReplyTo, true);
    }

    @Override
    public void displayStatus(@NonNull final ParcelableStatus status, final boolean displayInReplyTo,
                              final boolean shouldDisplayExtraType) {

        final MediaLoaderWrapper loader = adapter.getMediaLoader();
        final AsyncTwitterWrapper twitter = adapter.getTwitterWrapper();
        final TwidereLinkify linkify = adapter.getTwidereLinkify();
        final UserColorNameManager manager = adapter.getUserColorNameManager();
        final BidiFormatter formatter = adapter.getBidiFormatter();
        final Context context = adapter.getContext();
        final boolean nameFirst = adapter.isNameFirst();
        final boolean showCardActions = isCardActionsShown();

        actionButtons.setVisibility(showCardActions ? View.VISIBLE : View.GONE);
        itemMenu.setVisibility(showCardActions ? View.VISIBLE : View.GONE);
        if (statusContentLowerSpace != null) {
            statusContentLowerSpace.setVisibility(showCardActions ? View.GONE : View.VISIBLE);
        }

        final long replyCount = status.reply_count;
        final long retweetCount;
        final long favoriteCount;

        if (TwitterCardUtils.isPoll(status)) {
            statusInfoLabel.setText(R.string.label_poll);
            statusInfoIcon.setImageResource(R.drawable.ic_activity_action_poll);
            statusInfoLabel.setVisibility(View.VISIBLE);
            statusInfoIcon.setVisibility(View.VISIBLE);

            if (statusContentUpperSpace != null) {
                statusContentUpperSpace.setVisibility(View.GONE);
            }
        } else if (status.retweet_id != null) {
            final String retweetedBy = manager.getDisplayName(status.retweeted_by_user_id,
                    status.retweeted_by_user_name, status.retweeted_by_user_screen_name, nameFirst, false);
            statusInfoLabel.setText(context.getString(R.string.name_retweeted, formatter.unicodeWrap(retweetedBy)));
            statusInfoIcon.setImageResource(R.drawable.ic_activity_action_retweet);
            statusInfoLabel.setVisibility(View.VISIBLE);
            statusInfoIcon.setVisibility(View.VISIBLE);

            if (statusContentUpperSpace != null) {
                statusContentUpperSpace.setVisibility(View.GONE);
            }
        } else if (status.in_reply_to_status_id != null && status.in_reply_to_user_id != null && displayInReplyTo) {
            final String inReplyTo = manager.getDisplayName(status.in_reply_to_user_id,
                    status.in_reply_to_name, status.in_reply_to_screen_name, nameFirst, false);
            statusInfoLabel.setText(context.getString(R.string.in_reply_to_name, formatter.unicodeWrap(inReplyTo)));
            statusInfoIcon.setImageResource(R.drawable.ic_activity_action_reply);
            statusInfoLabel.setVisibility(View.VISIBLE);
            statusInfoIcon.setVisibility(View.VISIBLE);

            if (statusContentUpperSpace != null) {
                statusContentUpperSpace.setVisibility(View.GONE);
            }
        } else {
            statusInfoLabel.setVisibility(View.GONE);
            statusInfoIcon.setVisibility(View.GONE);

            if (statusContentUpperSpace != null) {
                statusContentUpperSpace.setVisibility(View.VISIBLE);
            }
        }


        boolean skipLinksInText = status.extras != null && status.extras.support_entities;
        if (status.is_quote) {

            quotedNameView.setVisibility(View.VISIBLE);
            quotedTextView.setVisibility(View.VISIBLE);
            quoteIndicator.setVisibility(View.VISIBLE);

            quotedNameView.setName(manager.getUserNickname(status.quoted_user_id, status.quoted_user_name, false));
            quotedNameView.setScreenName("@" + status.quoted_user_screen_name);

            if (adapter.getLinkHighlightingStyle() != VALUE_LINK_HIGHLIGHT_OPTION_CODE_NONE
                    && !TextUtils.isEmpty(status.quoted_text_html)) {
                final CharSequence text = HtmlSpanBuilder.fromHtml(status.quoted_text_html,
                        status.quoted_text_unescaped);
                if (text instanceof Spanned) {
                    quotedTextView.setText(linkify.applyAllLinks(text, status.account_key,
                            getLayoutPosition(), status.is_possibly_sensitive,
                            adapter.getLinkHighlightingStyle(), skipLinksInText));
                }
            } else {
                final String text = status.quoted_text_unescaped;
                quotedTextView.setText(text);
            }

            quoteIndicator.setColor(manager.getUserColor(status.quoted_user_id, false));
            itemContent.drawStart(manager.getUserColor(status.user_key, false));
        } else {

            quotedNameView.setVisibility(View.GONE);
            quotedTextView.setVisibility(View.GONE);
            quoteIndicator.setVisibility(View.GONE);

            if (status.is_retweet) {
                final int retweetUserColor = manager.getUserColor(status.retweeted_by_user_id, false);
                final int userColor = manager.getUserColor(status.user_key, false);
                if (retweetUserColor == 0) {
                    itemContent.drawStart(userColor);
                } else if (userColor == 0) {
                    itemContent.drawStart(retweetUserColor);
                } else {
                    itemContent.drawStart(retweetUserColor, userColor);
                }
            } else {
                itemContent.drawStart(manager.getUserColor(status.user_key, false));
            }
        }

        if (status.is_retweet) {
            timeView.setTime(status.retweet_timestamp);
        } else {
            timeView.setTime(status.timestamp);
        }
        nameView.setName(manager.getUserNickname(status.user_key, status.user_name, false));
        nameView.setScreenName("@" + status.user_screen_name);

        if (statusInfoSpace != null) {
            statusInfoSpace.setVisibility(View.VISIBLE);
        }

        if (adapter.isProfileImageEnabled()) {
            profileImageView.setVisibility(View.VISIBLE);
            if (profileImageSpace != null) {
                profileImageSpace.setVisibility(View.VISIBLE);
            }
            loader.displayProfileImage(profileImageView, status);

            profileTypeView.setImageResource(getUserTypeIconRes(status.user_is_verified, status.user_is_protected));
            profileTypeView.setVisibility(View.VISIBLE);
        } else {
            profileTypeView.setVisibility(View.GONE);
            profileImageView.setVisibility(View.GONE);
            if (profileImageSpace != null) {
                profileImageSpace.setVisibility(View.GONE);
            }

            loader.cancelDisplayTask(profileImageView);

            profileTypeView.setImageDrawable(null);
            profileTypeView.setVisibility(View.GONE);
        }

        if (adapter.shouldShowAccountsColor()) {
            itemContent.drawEnd(status.account_color);
        } else {
            itemContent.drawEnd();
        }

        if (adapter.isMediaPreviewEnabled() && (adapter.isSensitiveContentEnabled() || !status.is_possibly_sensitive)) {
            mediaPreview.setStyle(adapter.getMediaPreviewStyle());
            quoteMediaPreview.setStyle(adapter.getMediaPreviewStyle());

            final boolean showQuotedMedia = !ArrayUtils.isEmpty(status.quoted_media);
            final boolean showMedia = !showQuotedMedia && !ArrayUtils.isEmpty(status.media);

            mediaPreview.setVisibility(showMedia ? View.VISIBLE : View.GONE);
            if (textMediaSpace != null) {
                textMediaSpace.setVisibility(!status.is_quote && (showMedia || showCardActions) ?
                        View.GONE : View.VISIBLE);
            }
            quoteMediaPreview.setVisibility(showQuotedMedia ? View.VISIBLE : View.GONE);
            if (quotedTextMediaSpace != null) {
                quotedTextMediaSpace.setVisibility(!status.is_quote || showQuotedMedia ?
                        View.GONE : View.VISIBLE);
            }

            mediaPreview.displayMedia(status.media, loader, status.account_key, -1, this,
                    adapter.getMediaLoadingHandler());
            quoteMediaPreview.displayMedia(status.quoted_media, loader, status.account_key, -1, this,
                    adapter.getMediaLoadingHandler());
        } else {
            mediaPreview.setVisibility(View.GONE);
            quoteMediaPreview.setVisibility(View.GONE);
            if (textMediaSpace != null) {
                textMediaSpace.setVisibility(showCardActions && !status.is_quote ? View.GONE : View.VISIBLE);
            }
            if (quotedTextMediaSpace != null) {
                quotedTextMediaSpace.setVisibility(status.is_quote ? View.VISIBLE : View.GONE);
            }
        }
        if (adapter.getLinkHighlightingStyle() == VALUE_LINK_HIGHLIGHT_OPTION_CODE_NONE) {
            textView.setText(status.text_unescaped);
        } else {
            final CharSequence text = HtmlSpanBuilder.fromHtml(status.text_html, status.text_unescaped);
            textView.setText(linkify.applyAllLinks(text, status.account_key, getLayoutPosition(),
                    status.is_possibly_sensitive, adapter.getLinkHighlightingStyle(), skipLinksInText));
        }

        if (replyCount > 0) {
            replyCountView.setText(UnitConvertUtils.calculateProperCount(replyCount));
            replyCountView.setVisibility(View.VISIBLE);
        } else {
            replyCountView.setText(null);
            replyCountView.setVisibility(View.GONE);
        }

        if (twitter.isDestroyingStatus(status.account_key, status.my_retweet_id)) {
            retweetIconView.setActivated(false);
            retweetCount = Math.max(0, status.retweet_count - 1);
        } else {
            final boolean creatingRetweet = twitter.isCreatingRetweet(status.account_key, status.id);
            retweetIconView.setActivated(creatingRetweet || status.retweeted ||
                    Utils.isMyRetweet(status.account_key, status.retweeted_by_user_id,
                            status.my_retweet_id));
            retweetCount = status.retweet_count + (creatingRetweet ? 1 : 0);
        }
        if (retweetCount > 0) {
            retweetCountView.setText(UnitConvertUtils.calculateProperCount(retweetCount));
            retweetCountView.setVisibility(View.VISIBLE);
        } else {
            retweetCountView.setText(null);
            retweetCountView.setVisibility(View.GONE);
        }
        if (twitter.isDestroyingFavorite(status.account_key, status.id)) {
            favoriteIconView.setActivated(false);
            favoriteCount = Math.max(0, status.favorite_count - 1);
        } else {
            final boolean creatingFavorite = twitter.isCreatingFavorite(status.account_key, status.id);
            favoriteIconView.setActivated(creatingFavorite || status.is_favorite);
            favoriteCount = status.favorite_count + (creatingFavorite ? 1 : 0);
        }
        if (favoriteCount > 0) {
            favoriteCountView.setText(UnitConvertUtils.calculateProperCount(favoriteCount));
            favoriteCountView.setVisibility(View.VISIBLE);
        } else {
            favoriteCountView.setText(null);
            favoriteCountView.setVisibility(View.GONE);
        }
        if (shouldDisplayExtraType) {
            displayExtraTypeIcon(status.card_name, status.media, status.location,
                    status.place_full_name, status.is_possibly_sensitive);
        } else {
            extraTypeView.setVisibility(View.GONE);
        }

        nameView.updateText(formatter);
        quotedNameView.updateText(formatter);
    }

    @Override
    public ImageView getProfileImageView() {
        return profileImageView;
    }

    @Override
    public ImageView getProfileTypeView() {
        return profileTypeView;
    }

    @Override
    public void onMediaClick(View view, ParcelableMedia media, UserKey accountId, long extraId) {
        if (statusClickListener == null) return;
        final int position = getLayoutPosition();
        statusClickListener.onMediaClick(this, view, media, position);
    }


    public void setOnClickListeners() {
        setStatusClickListener(adapter.getStatusClickListener());
    }

    @Override
    public void setStatusClickListener(StatusClickListener listener) {
        statusClickListener = listener;
        ((View) itemContent).setOnClickListener(eventListener);
        ((View) itemContent).setOnLongClickListener(eventListener);

        itemMenu.setOnClickListener(eventListener);
        profileImageView.setOnClickListener(eventListener);
        replyView.setOnClickListener(eventListener);
        retweetView.setOnClickListener(eventListener);
        favoriteView.setOnClickListener(eventListener);
    }

    @Override
    public void setTextSize(final float textSize) {
        nameView.setPrimaryTextSize(textSize);
        quotedNameView.setPrimaryTextSize(textSize);
        textView.setTextSize(textSize);
        quotedTextView.setTextSize(textSize);
        nameView.setSecondaryTextSize(textSize * 0.85f);
        quotedNameView.setSecondaryTextSize(textSize * 0.85f);
        timeView.setTextSize(textSize * 0.85f);
        statusInfoLabel.setTextSize(textSize * 0.75f);
        replyCountView.setTextSize(textSize);
        retweetCountView.setTextSize(textSize);
        favoriteCountView.setTextSize(textSize);
    }

    public void setupViewOptions() {
        setTextSize(adapter.getTextSize());
        mediaPreview.setStyle(adapter.getMediaPreviewStyle());
//        profileImageView.setStyle(adapter.getProfileImageStyle());

        final boolean nameFirst = adapter.isNameFirst();
        nameView.setNameFirst(nameFirst);
        quotedNameView.setNameFirst(nameFirst);

        final int favIcon, favStyle, favColor;
        final Context context = adapter.getContext();
        if (adapter.shouldUseStarsForLikes()) {
            favIcon = R.drawable.ic_action_star;
            favStyle = LikeAnimationDrawable.Style.FAVORITE;
            favColor = ContextCompat.getColor(context, R.color.highlight_favorite);
        } else {
            favIcon = R.drawable.ic_action_heart;
            favStyle = LikeAnimationDrawable.Style.LIKE;
            favColor = ContextCompat.getColor(context, R.color.highlight_like);
        }
        final Drawable icon = ContextCompat.getDrawable(context, favIcon);
        final LikeAnimationDrawable drawable = new LikeAnimationDrawable(icon,
                favoriteCountView.getTextColors().getDefaultColor(), favColor, favStyle);
        drawable.mutate();
        favoriteIconView.setImageDrawable(drawable);
        timeView.setShowAbsoluteTime(adapter.isShowAbsoluteTime());

        favoriteIconView.setActivatedColor(favColor);
    }

    @Override
    public void playLikeAnimation(@NonNull LikeAnimationDrawable.OnLikedListener listener) {
        boolean handled = false;
        final Drawable drawable = favoriteIconView.getDrawable();
        if (drawable instanceof LikeAnimationDrawable) {
            ((LikeAnimationDrawable) drawable).setOnLikedListener(listener);
            ((LikeAnimationDrawable) drawable).start();
            handled = true;
        }
        if (!handled) {
            listener.onLiked();
        }
    }

    private boolean isCardActionsShown() {
        return adapter.isCardActionsShown(getLayoutPosition());
    }

    private void showCardActions() {
        adapter.showCardActions(getLayoutPosition());
    }

    private boolean hideTempCardActions() {
        adapter.showCardActions(RecyclerView.NO_POSITION);
        return !adapter.isCardActionsShown(RecyclerView.NO_POSITION);
    }

    private void displayExtraTypeIcon(String cardName, ParcelableMedia[] media, ParcelableLocation location, String placeFullName, boolean sensitive) {
        if (TwitterCardUtils.CARD_NAME_AUDIO.equals(cardName)) {
            extraTypeView.setImageResource(sensitive ? R.drawable.ic_action_warning : R.drawable.ic_action_music);
            extraTypeView.setVisibility(View.VISIBLE);
        } else if (TwitterCardUtils.CARD_NAME_ANIMATED_GIF.equals(cardName)) {
            extraTypeView.setImageResource(sensitive ? R.drawable.ic_action_warning : R.drawable.ic_action_movie);
            extraTypeView.setVisibility(View.VISIBLE);
        } else if (TwitterCardUtils.CARD_NAME_PLAYER.equals(cardName)) {
            extraTypeView.setImageResource(sensitive ? R.drawable.ic_action_warning : R.drawable.ic_action_play_circle);
            extraTypeView.setVisibility(View.VISIBLE);
        } else if (!ArrayUtils.isEmpty(media)) {
            if (hasVideo(media)) {
                extraTypeView.setImageResource(sensitive ? R.drawable.ic_action_warning : R.drawable.ic_action_movie);
            } else {
                extraTypeView.setImageResource(sensitive ? R.drawable.ic_action_warning : R.drawable.ic_action_gallery);
            }
            extraTypeView.setVisibility(View.VISIBLE);
        } else if (ParcelableLocationUtils.isValidLocation(location) || !TextUtils.isEmpty(placeFullName)) {
            extraTypeView.setImageResource(R.drawable.ic_action_location);
            extraTypeView.setVisibility(View.VISIBLE);
        } else {
            extraTypeView.setVisibility(View.GONE);
        }
    }

    private boolean hasVideo(ParcelableMedia[] media) {
        if (media == null) return false;
        for (ParcelableMedia item : media) {
            if (item == null) continue;
            switch (item.type) {
                case ParcelableMedia.Type.VIDEO:
                case ParcelableMedia.Type.ANIMATED_GIF:
                case ParcelableMedia.Type.EXTERNAL_PLAYER:
                    return true;
            }
        }
        return false;
    }

    static class EventListener implements OnClickListener, OnLongClickListener {

        final WeakReference<StatusViewHolder> holderRef;

        EventListener(StatusViewHolder holder) {
            this.holderRef = new WeakReference<>(holder);
        }

        @Override
        public void onClick(View v) {
            StatusViewHolder holder = holderRef.get();
            if (holder == null) return;
            StatusClickListener listener = holder.statusClickListener;
            if (listener == null) return;
            final int position = holder.getLayoutPosition();
            switch (v.getId()) {
                case R.id.item_content: {
                    listener.onStatusClick(holder, position);
                    break;
                }
                case R.id.item_menu: {
                    listener.onItemMenuClick(holder, v, position);
                    break;
                }
                case R.id.profile_image: {
                    listener.onUserProfileClick(holder, position);
                    break;
                }
                case R.id.reply_count:
                case R.id.reply_icon:
                case R.id.reply: {
                    listener.onItemActionClick(holder, R.id.reply, position);
                    break;
                }
                case R.id.retweet_count:
                case R.id.retweet_icon:
                case R.id.retweet: {
                    listener.onItemActionClick(holder, R.id.retweet, position);
                    break;
                }
                case R.id.favorite_count:
                case R.id.favorite_icon:
                case R.id.favorite: {
                    listener.onItemActionClick(holder, R.id.favorite, position);
                    break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            StatusViewHolder holder = holderRef.get();
            if (holder == null) return false;
            StatusClickListener listener = holder.statusClickListener;
            if (listener == null) return false;
            final int position = holder.getLayoutPosition();
            switch (v.getId()) {
                case R.id.item_content: {
                    if (!holder.isCardActionsShown()) {
                        holder.showCardActions();
                        return true;
                    } else if (holder.hideTempCardActions()) {
                        return true;
                    }
                    return listener.onStatusLongClick(holder, position);
                }
            }
            return false;
        }
    }


}
