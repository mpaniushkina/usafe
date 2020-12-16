package com.covrsecurity.io.ui.adapter;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.app.GlideApp;
import com.covrsecurity.io.domain.entity.TemplateEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.model.TimerInfo;
import com.covrsecurity.io.ui.component.CircleTimer;
import com.covrsecurity.io.ui.component.CovrCircleTimer;
import com.covrsecurity.io.ui.fragment.authorized.StandingByFragment;
import com.covrsecurity.io.utils.ActivityUtils;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.SoundUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.PendingRequestViewHolder> {

    public interface IPendingRequestsCountChangeListener {
        void onPendingRequestsCountChange(int count);
    }

    private final Fragment fragment;
    private final Animation hideLogoAnimation;
    private final Animation showLogoAnimation;

    private List<ListPendingRequest> mPendingRequestsList;
    private IPendingRequestsCountChangeListener mIPendingRequestsCountChangeListener;
    private IPendingRequestListListener mPendingRequestClickListener;
    private View mEmptyView;
    private View mLogoView;
    private ItemsManager mItemManager;
    private int maxImageHeight;
    private int mActivePosition;

    static class PendingRequestViewHolder extends RecyclerView.ViewHolder {

        private TextView mPendingTitle;
        private ImageView mPendingLogo;
        private TextView mAction;
        private ViewGroup mItem;
        private FrameLayout mTimerWrapper;
        private CovrCircleTimer mTimer;

        public PendingRequestViewHolder(View view) {
            super(view);
            mPendingLogo = view.findViewById(R.id.iv_pending_logo);
            mPendingTitle = view.findViewById(R.id.pendingTitle);
            mAction = view.findViewById(R.id.tv_pending_action);
            mItem = view.findViewById(R.id.rl_partnership_item);
            mTimer = null;
            mTimerWrapper = view.findViewById(R.id.timer_wrapper);
        }
    }

    public PendingRequestsAdapter(List<TransactionEntity> pendingTransactions,
                                  Fragment fragment,
                                  IPendingRequestListListener partnershipClickListener,
                                  IPendingRequestsCountChangeListener pendingRequestsCountChangeListener) {
        this.fragment = fragment;
        this.hideLogoAnimation = AnimationUtils.loadAnimation(((StandingByFragment) partnershipClickListener).getActivity(), R.anim.logo_hide);
        this.showLogoAnimation = AnimationUtils.loadAnimation(((StandingByFragment) partnershipClickListener).getActivity(), R.anim.logo_show);
        this.mPendingRequestsList = toListPendingRequests(pendingTransactions);
        this.mPendingRequestClickListener = partnershipClickListener;
        this.mIPendingRequestsCountChangeListener = pendingRequestsCountChangeListener;
        this.mEmptyView = null;
        this.mLogoView = null;
        this.mItemManager = new ItemsManager();
        maxImageHeight = (int) IamApp.getInstance().getResources().getDimension(R.dimen.max_image_height);
        validateEmptyViewVisibility();
        notifyRequestsCountChange();
    }

    public void setData(List<TransactionEntity> pendingTransactions) {
        if (pendingTransactions != null) {
            mPendingRequestsList = toListPendingRequests(pendingTransactions);
        } else {
            mPendingRequestsList = new ArrayList<>();
        }
        notifyDataSetChanged();
        notifyRequestsCountChange();
        validateEmptyViewVisibility();
    }

    public void setEmptyView(View view) {
        mEmptyView = view;
    }

    public void setLogoView(View view) {
        mLogoView = view;
    }

    @NotNull
    @Override
    public PendingRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PendingRequestViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_pending_request, parent, false));
    }

    @Override
    public void onBindViewHolder(PendingRequestViewHolder holder, int position) {
        final ListPendingRequest request = getItem(position);

        if (request == null) {
            return;
        }

        request.setSmoothOpen(false);
        holder.mPendingLogo.setImageDrawable(null);
        validateEmptyViewVisibility();

        holder.mItem.setOnClickListener(v -> {
            if (mPendingRequestsList.size() == 0) {
                return;
            }
            if (mPendingRequestClickListener != null) {
                mPendingRequestClickListener.onPendingRequestClicked(request);
            }
        });
//        holder.mNegativeButton.setOnClickListener(v -> {
//            if (!request.isEnabled()) {
//                return;
//            }
//            request.setEnabled(false);
//            request.setIsPosBtnActivated(false);
//            setColorPressed(holder.mNegativeButton, true);
//            mPendingRequestClickListener.onRejectRequestClicked(request);
//        });
//        holder.mPositiveButton.setOnClickListener(v -> {
//            if (!request.isEnabled()) {
//                return;
//            }
//            request.setEnabled(false);
//            request.setIsPosBtnActivated(true);
//            setColorPressed(holder.mPositiveButton, true);
//            mPendingRequestClickListener.onAcceptRequestClicked(request);
//        });
        if (holder.mTimer != null) {
            holder.mTimer.removeListener();
        }

        CovrCircleTimer timer = CovrCircleTimer.newInstance(holder.mTimerWrapper.getContext(), new TimerInfo(request.getCreated(), request.getValidTo()));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holder.mTimerWrapper.removeAllViews();
        holder.mTimerWrapper.addView(timer, params);
        holder.mTimer = timer;
        if (!TextUtils.isEmpty(request.getRequest().getTitle())) {
            holder.mAction.setText(request.getRequest().getTitle());
        }

        Timber.d("Set Timer listener for: %s", request.getCompany().getName());
        holder.mTimer.setListener(new CircleTimer.TimerAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                Timber.d("Set Timer finished! Remove item %s", request.getCompany().getName());
                AppAdapter.cancelTimerSound(request);
                SoundUtils.playTimeoutSound();
                queueRemoveItem(request, RemovalReason.TIMED_OUT);
                mPendingRequestClickListener.onPendingRequestTimedOut();
            }
        });
        holder.mPendingTitle.setText(request.getCompany().getName());
        holder.mTimer.startAnim();

//        TemplateEntity template = request.getTemplate();

//        String imageName = template.getTransactionImage();
//        if (TextUtils.isEmpty(imageName)) {
//            imageName = template.getBackgroundImage();
//        }
//        String backgroundImageURL = "";
//        if (!TextUtils.isEmpty(imageName)) {
//            backgroundImageURL = imageName;//CDNUtils.getImageUrl(companyId, imageName);
//        }
//        holder.mTransactionImage.setVisibility(View.GONE);
//        int screenWidth = getScreenWidth();
//        if (!TextUtils.isEmpty(backgroundImageURL)) {
//            showTransactionImage(holder.mTransactionImage, backgroundImageURL, screenWidth);
//        }

        GlideApp.with(fragment)
                .load(request.getCompany().getLogo())
                .fitCenter()
                .error(R.drawable.iamlogo2x)
                .into(holder.mPendingLogo);
    }

//    private void showTransactionImage(ImageView imageView, String backgroundImageURL, int screenWidth) {
//        imageView.setVisibility(View.VISIBLE);
//        GlideApp.with(fragment)
//                .load(backgroundImageURL)
//                .fitCenter()
//                .override(screenWidth, maxImageHeight)
//                .error(R.drawable.iamlogo2x)
//                .into(imageView);
//    }

//    private int getScreenWidth() {
//        WindowManager wm = (WindowManager) AppAdapter.context().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        return size.x;
//    }

    public void openTopItem() {
        mActivePosition = 0;
        for (int i = 0; i < mPendingRequestsList.size(); i++) {
            final ListPendingRequest pendingRequest = mPendingRequestsList.get(i);
            pendingRequest.setOpen(i == 0);
            pendingRequest.setSmoothOpen(i == 0);
        }
        notifyDataSetChanged();
    }

//    private void closePrevious(int newPosition) {
//        int previousPosition = -1;
//        for (int i = 0; i < mPendingRequestsList.size(); i++) {
//            if (i == newPosition) {
//                continue;
//            }
//            final ListPendingRequest pendingRequest = mPendingRequestsList.get(i);
//            if (pendingRequest.isOpen()) {
//                previousPosition = i;
//                pendingRequest.setOpen(false);
//                pendingRequest.setSmoothOpen(true);
//            }
//        }
//        if (previousPosition != -1) {
//            notifyItemChanged(previousPosition);
//        }
//    }

    @Override
    public void onViewDetachedFromWindow(PendingRequestViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.mTimer.removeListener();
    }

    public void removeItemById(String id, RemovalReason reason) {
        for (TransactionEntity r : mPendingRequestsList) {
            if (r.getId().equals(id)) {
                queueRemoveItem(r, reason);
                return;
            }
        }
    }

    private synchronized void queueRemoveItem(TransactionEntity item, RemovalReason reason) {
        mItemManager.removeItem(item, reason);
    }

    public void addItem(TransactionEntity request) {
        mItemManager.addItem(request);
    }

    @Override
    public int getItemCount() {
        return mPendingRequestsList == null ? 0 : mPendingRequestsList.size();
    }

    private ListPendingRequest getItem(int position) {
        return mPendingRequestsList == null ? null : mPendingRequestsList.get(position);
    }

    public void setItemButtonDeactivated(String itemId) {
        for (ListPendingRequest request : mPendingRequestsList) {
            if (request.getId().equals(itemId)) {
                request.setIsPosBtnActivated(null);
                request.setEnabled(true);
            }
        }
        notifyDataSetChanged();
    }

    public void resetActiveItem() {
        if (mPendingRequestsList.size() != 0) {
            final ListPendingRequest pendingRequest = mPendingRequestsList.get(mActivePosition);
            pendingRequest.setEnabled(true);
            pendingRequest.setIsPosBtnActivated(null);
            notifyItemChanged(mActivePosition);
        }
    }

    private void validateEmptyViewVisibility() {
        if (mEmptyView != null && mLogoView != null) {
            if (mPendingRequestsList.size() == 0) {
                performAnimationWithLogos(true);
            } else {
                performAnimationWithLogos(false);
            }
        }
    }

    private void performAnimationWithLogos(boolean isDirect) {
        View viewToHide = isDirect ? mLogoView : mEmptyView;
        View viewToShow = isDirect ? mEmptyView : mLogoView;
        ActivityUtils.scheduleOnMainThread(() -> {
            if (viewToHide.getVisibility() == View.VISIBLE) {
                viewToHide.startAnimation(hideLogoAnimation);
                viewToHide.setVisibility(View.INVISIBLE);
            }
            if (viewToShow.getVisibility() == View.INVISIBLE) {
                viewToShow.startAnimation(showLogoAnimation);
                viewToShow.setVisibility(View.VISIBLE);
            }
        });
    }

    public interface IPendingRequestListListener {

        void onPendingRequestClicked(TransactionEntity pendingTransaction);

        void onPendingRequestTimedOut();
    }

    private class ItemsManager {
        private AtomicBoolean mIsBusy = new AtomicBoolean(false);
        private ConcurrentLinkedQueue<Task> mQueue = new ConcurrentLinkedQueue<>();
        private static final int ACTION_DELAY = 400;

        public void setBusy(boolean isBusy) {
            log("setBusy(): %b", isBusy);
            mIsBusy.set(isBusy);
        }

        public synchronized void addItem(TransactionEntity item) {
            if (!mIsBusy.get()) {
                setBusy(true);
                addItemImpl(item);
            } else {
                addTask(new Task(TaskType.ADD, item));
            }
        }

        public synchronized void removeItem(TransactionEntity request, RemovalReason reason) {
            if (!mIsBusy.get()) {
                setBusy(true);
                removeItemImpl(request, reason);
            } else {
                addTask(new Task(TaskType.REMOVE, request, reason));
            }
        }

        public synchronized void addTask(Task task) {
            log("Queue task: %s", task.toString());
            mQueue.offer(task);
        }

        public class Task {

            public TaskType type;
            public TransactionEntity item;
            public RemovalReason reason;

            public Task(TaskType type, TransactionEntity item) {
                this.type = type;
                this.item = item;
                this.reason = RemovalReason.NONE;
            }

            public Task(TaskType type, TransactionEntity item, RemovalReason removalReason) {
                this.type = type;
                this.item = item;
                this.reason = removalReason;
            }

            @Override
            public String toString() {
                if (type == TaskType.ADD) {
                    return "ADD " + item.getCompany().getName();
                } else {
                    return "REMOVE " + item.getCompany().getName();
                }
            }
        }

        private void removeItemImpl(TransactionEntity request, RemovalReason reason) {
            log(">>>>>>> removeItemImpl(): %s", request.getCompany().getName());
            int position = mPendingRequestsList.indexOf(request);
            if (position != -1) {
                mPendingRequestsList.remove(position);
                notifyRequestsCountChange();
                ActivityUtils.scheduleOnMainThread(() -> checkQueue(), ACTION_DELAY);
                playRemovalSound(reason);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mPendingRequestsList.size());
                validateEmptyViewVisibility();
                ActivityUtils.scheduleOnMainThread(() -> openTopItem(), ConstantsUtils.SIX_HUNDRED_MILLISECONDS);
            } else {
                Timber.e("Item to be deleted is not found! Ignoring - %s", request.getCompany().getName());
                checkQueue();
            }
        }

        private void playRemovalSound(RemovalReason reason) {
            switch (reason) {
                case APPROVED:
                case REJECTED:
                    SoundUtils.playResponseSound();
                    break;
                case TIMED_OUT:
                    SoundUtils.playTimeoutSound();
                    break;
            }
        }

        private void addItemImpl(TransactionEntity request) {
            ListPendingRequest lpr = new ListPendingRequest(request);
            mPendingRequestsList.add(lpr);
            notifyRequestsCountChange();
            notifyItemInserted(mPendingRequestsList.indexOf(lpr));
            ActivityUtils.scheduleOnMainThread(() -> {
                checkQueue();
            }, ACTION_DELAY);
        }

        private void checkQueue() {
            log("CheckQueue(): size = %d", mQueue.size());
            Task t = mQueue.poll();
            if (t != null) {
                log("Dequeue task: %s", t.toString());
                if (t.type == TaskType.ADD) {
                    addItemImpl(t.item);
                } else {
                    removeItemImpl(t.item, t.reason);
                }
            } else {
                setBusy((false));
            }
        }

        private void log(String s, Object... args) {
            //Timber.d(s, args);
        }
    }

    private enum TaskType {
        ADD,
        REMOVE
    }

    public enum RemovalReason {
        APPROVED,
        REJECTED,
        TIMED_OUT,
        NONE
    }

    private void notifyRequestsCountChange() {
        if (mIPendingRequestsCountChangeListener != null) {
            mIPendingRequestsCountChangeListener.onPendingRequestsCountChange(getItemCount());
        }
    }


    public static class ListPendingRequest extends TransactionEntity {

        private boolean mIsOpen = false;
        @Nullable
        private Boolean mIsPosBtnActivated; // null if none
        private boolean mSmoothOpen;
        private boolean mEnabled = true;

        public ListPendingRequest(TransactionEntity r) {
            super(r.getId(), r.getCompany(), r.getCompanyClientId(), r.getTemplateId(), r.getTemplate(),
                    r.getValidTo(), r.getValidFrom(), r.getStatus(), r.getCreatedByIp(), r.getVerifiedByIp(),
                    r.getCreated(), r.getUpdatedAt(), r.getAcceptHistoryMessage(), r.getRejectHistoryMessage(),
                    r.getExpiredHistoryMessage(), r.getFailedBiometricHistoryMessage(), r.getRequest(),
                    r.isViewed(), r.getSignature(), r.getBiometric(), r.getReferenceId(), r.getReferenceType());
        }

        public boolean isOpen() {
            return this.mIsOpen;
        }

        public void setOpen(boolean isOpen) {
            this.mIsOpen = isOpen;
        }

        @Nullable
        public Boolean getIsPosBtnActivated() {
            return mIsPosBtnActivated;
        }

        public void setIsPosBtnActivated(@Nullable Boolean isPosBtnActivated) {
            mIsPosBtnActivated = isPosBtnActivated;
        }

        public boolean isSmoothOpen() {
            return mSmoothOpen;
        }

        public void setSmoothOpen(boolean mSmoothOpen) {
            this.mSmoothOpen = mSmoothOpen;
        }

        public boolean isEnabled() {
            return mEnabled;
        }

        public void setEnabled(boolean enabled) {
            this.mEnabled = enabled;
        }
    }

    private List<ListPendingRequest> toListPendingRequests(List<TransactionEntity> requests) {
        ArrayList<ListPendingRequest> listRequests = new ArrayList<>();
        if (requests != null) {
            for (TransactionEntity r : requests) {
                ListPendingRequest lpr = new ListPendingRequest(r);
                listRequests.add(lpr);
            }
        }
        return listRequests;
    }
}