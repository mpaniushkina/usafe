package com.covrsecurity.io.domain.repository;

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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface RegisteredRepository {

    Completable validatePinCode(ValidatePinCodeRequestEntity requestEntity);

    Completable changePinCode(ChangePinCodeRequestEntity requestEntity);

    Single<GetConnectionsResponseEntity> getConnections(GetConnectionsRequestEntity requestEntity);

    Single<MarkConnectionAsViewedResponseEntity> markConnectionAsViewed(MarkConnectionAsViewedRequestEntity requestEntity);

    Single<TransactionsResponseEntity> getTransactions(GetTransactionsRequestEntity requestEntity);

    Observable<GetPushNotificationsResponseEntity> getPushNotifications();

    Single<TransactionsResponseEntity> getTransactionHistory(GetTransactionHistoryRequestEntity requestEntity);

    Single<MarkHistoryAsViewedResponseEntity> markHistoryAsViewed();

    Single<TransactionEntity> getTransactionDetails(GetTransactionDetailsRequestEntity requestEntity);

    Single<TransactionEntity> postTransaction(TransactionConfirmationRequestEntity requestEntity);

    Single<QrCodeClaimResponseEntity> verifyQrCodeClaim(QrCodeClaimRequestEntity requestEntity);

    Single<TransactionClaimResponseEntity> transactionClaimComplete(TransactionClaimRequestEntity requestEntity);

    Single<PostQrCodeResponseEntity> addConnection(PostQrCodeRequestEntity requestEntity);

    Single<GetUnreadHistoryCountResponseEntity> getUnreadHistoryCount();

    Completable notificationHubRegistration(NotificationHubRegistrationRequestEntity requestEntity);

    Completable registerBiometricRecovery(RegisterRecoveryRequestEntity requestEntity);
}