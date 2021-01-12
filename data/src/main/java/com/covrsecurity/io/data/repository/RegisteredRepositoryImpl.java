package com.covrsecurity.io.data.repository;

import com.covrsecurity.io.common.ConstantsUtils;
import com.covrsecurity.io.data.utils.EntityMapper;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.domain.entity.request.ChangePinCodeRequestEntity;
import com.covrsecurity.io.domain.entity.request.GetConnectionsRequestEntity;
import com.covrsecurity.io.domain.entity.request.GetTransactionDetailsRequestEntity;
import com.covrsecurity.io.domain.entity.request.GetTransactionHistoryRequestEntity;
import com.covrsecurity.io.domain.entity.request.GetTransactionsRequestEntity;
import com.covrsecurity.io.domain.entity.request.MarkConnectionAsViewedRequestEntity;
import com.covrsecurity.io.domain.entity.request.NotificationHubRegistrationRequestEntity;
import com.covrsecurity.io.domain.entity.request.PostQrCodeRequestEntity;
import com.covrsecurity.io.domain.entity.request.QrCodeClaimRequestEntity;
import com.covrsecurity.io.domain.entity.request.RegisterRecoveryRequestEntity;
import com.covrsecurity.io.domain.entity.request.TransactionClaimRequestEntity;
import com.covrsecurity.io.domain.entity.request.TransactionConfirmationRequestEntity;
import com.covrsecurity.io.domain.entity.request.ValidatePinCodeRequestEntity;
import com.covrsecurity.io.domain.entity.response.GetConnectionsResponseEntity;
import com.covrsecurity.io.domain.entity.response.GetPushNotificationsResponseEntity;
import com.covrsecurity.io.domain.entity.response.GetUnreadHistoryCountResponseEntity;
import com.covrsecurity.io.domain.entity.response.MarkConnectionAsViewedResponseEntity;
import com.covrsecurity.io.domain.entity.response.MarkHistoryAsViewedResponseEntity;
import com.covrsecurity.io.domain.entity.response.PostQrCodeResponseEntity;
import com.covrsecurity.io.domain.entity.response.QrCodeClaimResponseEntity;
import com.covrsecurity.io.domain.entity.response.TransactionClaimResponseEntity;
import com.covrsecurity.io.domain.entity.response.TransactionsResponseEntity;
import com.covrsecurity.io.domain.repository.RegisteredRepository;
import com.covrsecurity.io.sdk.CovrNewMainInterface;
import com.covrsecurity.io.sdk.request.ChangePinCodeRequest;
import com.covrsecurity.io.sdk.request.GetConnectionsRequest;
import com.covrsecurity.io.sdk.request.GetTransactionDetailsRequest;
import com.covrsecurity.io.sdk.request.GetTransactionHistoryRequest;
import com.covrsecurity.io.sdk.request.GetTransactionsRequest;
import com.covrsecurity.io.sdk.request.MarkConnectionAsViewedRequest;
import com.covrsecurity.io.sdk.request.NotificationHubRegistrationRequest;
import com.covrsecurity.io.sdk.request.PostQrCodeRequest;
import com.covrsecurity.io.sdk.request.QrCodeClaimRequest;
import com.covrsecurity.io.sdk.request.RegisterRecoveryRequest;
import com.covrsecurity.io.sdk.request.TransactionClaimCompleteRequest;
import com.covrsecurity.io.sdk.request.TransactionConfirmationRequest;
import com.covrsecurity.io.sdk.request.ValidatePinCodeRequest;
import com.covrsecurity.io.sdk.response.MerchantsSdk;
import com.covrsecurity.io.sdk.response.Transaction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.covrsecurity.io.data.utils.EntityMapper.getCompanyEntity;
import static com.covrsecurity.io.data.utils.EntityMapper.getStatusEntity;
import static com.covrsecurity.io.data.utils.EntityMapper.getTransactionEntity;
import static com.covrsecurity.io.data.utils.SdkMapper.getTransactionSdk;

public class RegisteredRepositoryImpl implements RegisteredRepository {

    private final CovrNewMainInterface covrInterface;

    @Inject
    public RegisteredRepositoryImpl(CovrNewMainInterface covrInterface) {
        this.covrInterface = covrInterface;
    }

    @Override
    public Completable validatePinCode(ValidatePinCodeRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new ValidatePinCodeRequest(entity.getOldCovrCode()))
                .flatMapCompletable(covrInterface::validatePinCode);
    }

    @Override
    public Completable changePinCode(ChangePinCodeRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new ChangePinCodeRequest(entity.getOldCovrCode(), entity.getNewCovrCode(), entity.isSkipWeekPassword()))
//                .flatMapCompletable(request -> covrInterface.changePinCode(requestEntity.getContext(), request));
        .flatMapCompletable(covrInterface::changePinCode);
    }

    @Override
    public Single<GetConnectionsResponseEntity> getConnections(GetConnectionsRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new GetConnectionsRequest(entity.getPageNumber(), entity.getPageSize()))
                .flatMap(covrInterface::getConnections)
                .map(response -> {

                    List<MerchantsSdk> merchants = response.getMerchants();
                    List<MerchantEntity> merchantsEntities = new ArrayList<>(merchants.size());

                    for (MerchantsSdk merchant : merchants) {

                        MerchantEntity merchantEntity = new MerchantEntity(
                                merchant.getId(),
                                merchant.getUserName(),
                                merchant.getCreatedDate() * ConstantsUtils.MILLISECONDS_IN_SECOND,
                                getCompanyEntity(merchant.getCompany()),
                                getStatusEntity(merchant.getStatus())
                        );

                        merchantsEntities.add(merchantEntity);
                    }

                    return new GetConnectionsResponseEntity(
                            merchantsEntities,
                            response.getPageSize(),
                            response.getPageNumber(),
                            response.isHasNext()
                    );
                });
    }

    @Override
    public Single<MarkConnectionAsViewedResponseEntity> markConnectionAsViewed(MarkConnectionAsViewedRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(request -> new MarkConnectionAsViewedRequest(request.getCompanyId()))
                .flatMap(covrInterface::markConnectionAsViewed)
                .map(response -> new MarkConnectionAsViewedResponseEntity(response.isSucceeded()));
    }

    @Override
    public Single<TransactionsResponseEntity> getTransactions(GetTransactionsRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new GetTransactionsRequest(entity.getPageNumber(), entity.getPageSize()))
                .flatMap(covrInterface::getTransactions)
                .map(response -> {

                    List<Transaction> transactions = response.getTransactions();
                    List<TransactionEntity> transactionEntities = new ArrayList<>(transactions.size());

                    for (Transaction transaction : transactions) {
                        transactionEntities.add(getTransactionEntity(transaction));
                    }

                    return new TransactionsResponseEntity(
                            transactionEntities,
                            response.isHasNext(),
                            requestEntity.getPageNumber(),
                            response.getPageSize()
                    );
                });
    }

    @Override
    public Observable<GetPushNotificationsResponseEntity> getPushNotifications() {
        return covrInterface.getPushNotifications()
                .map(response -> new GetPushNotificationsResponseEntity(
                        getTransactionEntity(response.getTransaction()),
                        response.getLockToken()
                ));
    }

    @Override
    public Single<TransactionsResponseEntity> getTransactionHistory(GetTransactionHistoryRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new GetTransactionHistoryRequest(entity.getPageNumber(), entity.getPageSize()))
                .flatMap(covrInterface::getTransactionHistory)
                .map(response -> {

                    List<Transaction> transactions = response.getTransactions();
                    List<TransactionEntity> transactionEntities = new ArrayList<>(transactions.size());

                    for (Transaction transaction : transactions) {
                        transactionEntities.add(getTransactionEntity(transaction));
                    }

                    return new TransactionsResponseEntity(
                            transactionEntities,
                            response.isHasNext(),
                            requestEntity.getPageNumber(),
                            response.getPageSize()
                    );
                });
    }

    @Override
    public Single<MarkHistoryAsViewedResponseEntity> markHistoryAsViewed() {
        return covrInterface.markHistoryAsViewed()
                .map(response -> new MarkHistoryAsViewedResponseEntity(response.isSucceeded()));
    }

    @Override
    public Single<TransactionEntity> getTransactionDetails(GetTransactionDetailsRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new GetTransactionDetailsRequest(entity.getTransactionId(), entity.getCompanyId()))
                .flatMap(covrInterface::getTransactionDetails)
                .map(EntityMapper::getTransactionEntity);
    }

    @Override
    public Single<TransactionEntity> postTransaction(TransactionConfirmationRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new TransactionConfirmationRequest(getTransactionSdk(entity.getTransaction()), entity.isAccept(), entity.getBioMetricData()))
                .flatMap(covrInterface::postTransaction)
                .map(EntityMapper::getTransactionEntity);
    }

    @Override
    public Single<QrCodeClaimResponseEntity> claimQrCode(QrCodeClaimRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new QrCodeClaimRequest(entity.getReference_id(), entity.getExpires_at(), entity.getType(), entity.getStatus(), entity.getScopes()))
                .flatMap(covrInterface::claimQrCode)
                .map(response -> new QrCodeClaimResponseEntity(
                        response.isValid()
                ));
    }

    @Override
    public Single<TransactionClaimResponseEntity> transactionClaimComplete(TransactionClaimRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new TransactionClaimCompleteRequest(entity.getReferenceId(), entity.getCompanyId()))
                .flatMap(covrInterface::transactionClaimComplete)
                .map(response -> new TransactionClaimResponseEntity(
                        response.getId(),
                        response.getUserName(),
                        response.getCreatedDate(),
                        getCompanyEntity(response.getMerchant()),
                        response.getStatus()
                ));
    }

    @Override
    public Single<QrCodeClaimResponseEntity> reuseQrCode(QrCodeClaimRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new QrCodeClaimRequest(entity.getReference_id(), entity.getExpires_at(), entity.getType(), entity.getStatus(), entity.getScopes()))
                .flatMap(covrInterface::reuseQrCode)
                .map(response -> new QrCodeClaimResponseEntity(
                        response.isValid()
                ));
    }

    @Override
    public Single<TransactionClaimResponseEntity> transactionReuseComplete(TransactionClaimRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new TransactionClaimCompleteRequest(entity.getReferenceId(), entity.getCompanyId()))
                .flatMap(covrInterface::transactionReuseComplete)
                .map(response -> new TransactionClaimResponseEntity(
                        response.getId(),
                        response.getUserName(),
                        response.getCreatedDate(),
                        getCompanyEntity(response.getMerchant()),
                        response.getStatus()
                ));
    }

    @Override
    public Single<QrCodeClaimResponseEntity> loginQrCode(QrCodeClaimRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new QrCodeClaimRequest(entity.getReference_id(), entity.getExpires_at(), entity.getType(), entity.getStatus(), entity.getScopes()))
                .flatMap(covrInterface::loginQrCode)
                .map(response -> new QrCodeClaimResponseEntity(
                        response.isValid()
                ));
    }

    @Override
    public Single<PostQrCodeResponseEntity> addConnection(PostQrCodeRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(entity -> new PostQrCodeRequest(entity.getQrCodeString()))
                .flatMap(covrInterface::addConnection)
                .map(response -> new PostQrCodeResponseEntity(
                        response.getId(),
                        response.getUserName(),
                        response.getCreatedDate(),
                        getCompanyEntity(response.getCompany()),
                        response.getSatatus()
                ));
    }

    @Override
    public Single<GetUnreadHistoryCountResponseEntity> getUnreadHistoryCount() {
        return covrInterface.getUnreadHistoryCount()
                .map(response -> new GetUnreadHistoryCountResponseEntity(response.getCount()));
    }

    @Override
    public Completable notificationHubRegistration(NotificationHubRegistrationRequestEntity requestEntity) {
        return Single.just(requestEntity)
                .map(request -> new NotificationHubRegistrationRequest(request.getNotificationKey()))
                .flatMapCompletable(covrInterface::notificationHubRegistration);
    }

    @Override
    public Completable registerBiometricRecovery(RegisterRecoveryRequestEntity requestEntity) {
        return Single.just(RegisterRecoveryRequest.getImageRecoveryRequest(requestEntity.getBiometricsBytes(), requestEntity.getImageIdCard()))
                .flatMapCompletable(covrInterface::registerBiometricRecovery);
    }
}
