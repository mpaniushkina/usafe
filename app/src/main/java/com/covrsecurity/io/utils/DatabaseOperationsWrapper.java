package com.covrsecurity.io.utils;

import android.content.Context;
import androidx.annotation.WorkerThread;
import android.util.Log;

import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.event.DatabaseOperationCompletedEvent;
import com.covrsecurity.io.greendao.model.database.CovrVaultDbModel;
import com.covrsecurity.io.greendao.model.database.CovrVaultDbModelDao;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.model.EncryptedEnvelope;
import com.covrsecurity.io.model.databinding.CovrVaultBaseModel;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by Lenovo on 01.06.2017.
 */
@Deprecated
public class DatabaseOperationsWrapper {

    public static final String TAG = DatabaseOperationsWrapper.class.getSimpleName();

    private Context mContext;
    private CovrVaultDbModelDao mCovrVaultDao;
    private ExecutorService mExecutorService;

    public DatabaseOperationsWrapper(Context context) {
        mContext = context;
        mCovrVaultDao = IamApp.getInstance().getDaoSession().getCovrVaultDbModelDao();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    public <T extends CovrVaultBaseModel> void insertOrReplaceAsync(final T model, final long dbRecordId) {
        insertOrReplaceAsync(model, dbRecordId, null, false);
    }

    public <T extends CovrVaultBaseModel> void insertOrReplaceAsync(final T model, final long dbRecordId, final Result<Void> callBack, final boolean notifyOnMainThread) {
        mExecutorService.submit(() -> {
            try {
                insertOrReplace(model, dbRecordId);
                if (notifyOnMainThread && callBack != null) {
                    ActivityUtils.runOnMainThread(() -> callBack.onSuccess(null));
                } else if (callBack != null) {
                    callBack.onSuccess(null);
                }
            } catch (IOException | GeneralSecurityException e) {
                if (notifyOnMainThread && callBack != null) {
                    ActivityUtils.runOnMainThread(() -> callBack.onError(e));
                } else if (callBack != null) {
                    callBack.onError(e);
                }
                AppAdapter.bus().postSticky(new DatabaseOperationCompletedEvent(AsyncOperation.OperationType.InsertOrReplace, dbRecordId));
            }
        });
    }

    @WorkerThread
    private <T extends CovrVaultBaseModel> void insertOrReplace(final T model, final long dbRecordId) throws GeneralSecurityException, IOException {
        LogUtil.printJsonNamed("Database: Saving %1$s %2$s", model.getRecordType().name(), model);
        DefaultEncryptHelper encryptHelper = AppAdapter.settings().getDefaultEncryptHelper();
        EncryptedEnvelope envelope = encryptHelper.getEncryptedEnvelope(model);
        CovrVaultDbModel dbModel;
        if (dbRecordId != -1) {
            dbModel = new CovrVaultDbModel(dbRecordId, envelope.getKey(), envelope.getValue(), model.getRecordType(),
                    model.isFavorite(), model.getDescription(), model.getAdditional(), System.currentTimeMillis());
        } else {
            dbModel = new CovrVaultDbModel(envelope.getKey(), envelope.getValue(), model.getRecordType(),
                    false, model.getDescription(), model.getAdditional(), System.currentTimeMillis());
        }
        mCovrVaultDao.insertOrReplace(dbModel);
        LogUtil.printJsonNamed("Database: Save encrypted %1$s %2$s", model.getRecordType().name(), model);
    }


    public void deleteAsync(final long dbRecordId, final SuccessResult<Void> callBack, final boolean notifyOnMainThread) {
        mExecutorService.submit(() -> {
            delete(dbRecordId);
            if (notifyOnMainThread && callBack != null) {
                ActivityUtils.runOnMainThread(() -> callBack.onSuccess(null));
            } else if (callBack != null) {
                callBack.onSuccess(null);
            }
            AppAdapter.bus().postSticky(new DatabaseOperationCompletedEvent(AsyncOperation.OperationType.DeleteByKey, dbRecordId));
        });
    }

    @WorkerThread
    public void delete(final long dbRecordId) {
        LogUtil.printJson("Database: Deleting record №%d", dbRecordId);
        mCovrVaultDao.deleteByKey(dbRecordId);
    }

    public void setFavoriteAsync(final long dbRecordId, final boolean favorite) {
        setFavoriteAsync(dbRecordId, favorite, null, false);
    }

    private void setFavoriteAsync(final long dbRecordId, final boolean favorite, final SuccessResult<Void> callBack, final boolean notifyOnMainThread) {
        mExecutorService.submit(() -> {
            setFavorite(dbRecordId, favorite);
            if (notifyOnMainThread && callBack != null) {
                ActivityUtils.runOnMainThread(() -> callBack.onSuccess(null));
            } else if (callBack != null) {
                callBack.onSuccess(null);
            }
            AppAdapter.bus().postSticky(new DatabaseOperationCompletedEvent(AsyncOperation.OperationType.Update, dbRecordId));
        });
    }

    @WorkerThread
    private void setFavorite(final long dbRecordId, final boolean favorite) {
        CovrVaultDbModel dbModel = mCovrVaultDao.queryBuilder()
                .where(CovrVaultDbModelDao.Properties.MId.eq(dbRecordId))
                .unique();
        dbModel.setMFavorite(favorite);
        mCovrVaultDao.update(dbModel);
    }

    @WorkerThread
    public <T extends CovrVaultBaseModel> T queryUnique(final long dbRecordId, final Class<T> aClass) {
        CovrVaultDbModel dbModel = mCovrVaultDao.queryBuilder().where(CovrVaultDbModelDao.Properties.MId.eq(dbRecordId)).unique();
        LogUtil.printJson("Database: Loading encrypted data %s", dbModel);
        T decryptedModel = null;
        try {
            DefaultEncryptHelper encryptHelper = AppAdapter.settings().getDefaultEncryptHelper();
            decryptedModel = encryptHelper.getDecryptedModel(dbModel, aClass);
            decryptedModel.setFavorite(dbModel.getMFavorite());
            Timber.tag(TAG).d("decryptedModel - " + decryptedModel);
        } catch (IOException | GeneralSecurityException e) {
            Log.e(TAG, e.getMessage());
        }
        LogUtil.printJson("Database: Decrypt loaded data %s", decryptedModel);
        return decryptedModel;
    }

    public void queryAllRecordsAsync(final SuccessResult<List<CovrVaultDbModel>> callBack, final boolean notifyOnMainThread) {
        mExecutorService.submit(() -> {
            final List<CovrVaultDbModel> list = queryAllRecords();
            if (notifyOnMainThread && callBack != null) {
                ActivityUtils.runOnMainThread(() -> callBack.onSuccess(list));
            } else if (callBack != null) {
                callBack.onSuccess(list);
            }
            AppAdapter.bus().postSticky(new DatabaseOperationCompletedEvent(AsyncOperation.OperationType.QueryList, -1));
        });
    }

    @WorkerThread
    private List<CovrVaultDbModel> queryAllRecords() {
        return mCovrVaultDao.queryBuilder().build().list();
    }

    @WorkerThread
    public List<CovrVaultDbModel> queryList(final RecordType recordType) {
        return queryList(CovrVaultDbModelDao.Properties.MType.eq(recordType), false);
    }

    @Deprecated
    @WorkerThread
    public List<CovrVaultDbModel> queryListWithCondition(final WhereCondition condition, boolean orderAsc, Property... orderProperties) {
        return queryList(condition, orderAsc, orderProperties);
    }

    @Deprecated
    @WorkerThread
    public List<CovrVaultDbModel> queryListWithCondition(final WhereCondition condition) {
        return queryList(condition, false);
    }

    @WorkerThread
    private List<CovrVaultDbModel> queryList(final WhereCondition condition, boolean orderAsc, Property... orderProperties) {
        QueryBuilder<CovrVaultDbModel> queryBuilder = mCovrVaultDao
                .queryBuilder()
                .where(condition);
        if (orderProperties != null && orderProperties.length != 0) {
            if (orderAsc) {
                queryBuilder.orderAsc(orderProperties);
            } else {
                queryBuilder.orderDesc(orderProperties);
            }
        }
        return queryBuilder.build().list();
    }

    public interface Result<T> extends SuccessResult<T> {
        void onError(Exception e);
    }

    public interface SuccessResult<T> {
        void onSuccess(T result);
    }
}
