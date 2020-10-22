package com.covrsecurity.io.data.utils;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.BiometricEntity;
import com.covrsecurity.io.domain.entity.BiometricTypeEntity;
import com.covrsecurity.io.domain.entity.CompanyEntity;
import com.covrsecurity.io.domain.entity.LockTypeEntity;
import com.covrsecurity.io.domain.entity.NtSignatureEntity;
import com.covrsecurity.io.domain.entity.RequestEntity;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TemplateEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.domain.entity.response.AppUnlockTimeEntity;
import com.covrsecurity.io.sdk.exception.AppLockedException;
import com.covrsecurity.io.sdk.response.AppUnlockTime;
import com.covrsecurity.io.sdk.response.Biometric;
import com.covrsecurity.io.sdk.response.BiometricType;
import com.covrsecurity.io.sdk.response.Company;
import com.covrsecurity.io.sdk.response.NtSignature;
import com.covrsecurity.io.sdk.response.Request;
import com.covrsecurity.io.sdk.response.Status;
import com.covrsecurity.io.sdk.response.Template;
import com.covrsecurity.io.sdk.response.Transaction;
import com.covrsecurity.io.sdk.storage.model.LockType;

import static com.covrsecurity.io.common.ConstantsUtils.MILLISECONDS_IN_SECOND;

public class EntityMapper {

    @Nullable
    public static StatusEntity getStatusEntity(@Nullable Status status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case ACTIVE:
                return StatusEntity.ACTIVE;
            case EXPIRED:
                return StatusEntity.EXPIRED;
            case ACCEPTED:
                return StatusEntity.ACCEPTED;
            case REJECTED:
                return StatusEntity.REJECTED;
            case FAILED_BIOMETRIC:
                return StatusEntity.FAILED_BIOMETRIC;
            default:
                return null;
        }
    }

    public static CompanyEntity getCompanyEntity(Company company) {
        return new CompanyEntity(
                company.getId(),
                company.getUserName(),
                company.getPublicKey(),
                company.getCreatedDate(),
                company.getCompanyId(),
                company.getName(),
                company.getFullName(),
                company.getWebsiteUrl(),
                company.getWebsiteName(),
                company.getLogo(),
                company.isActive(),
                company.isViewed(),
                getStatusEntity(company.getStatus())
        );
    }

    public static TemplateEntity getTemplateEntity(Template template) {
        return new TemplateEntity(
                template.getLayoutType(),
                template.getBackgroundImage(),
                template.getTransactionImage()
        );
    }

    public static RequestEntity getRequestEntity1(Request request) {
        return new RequestEntity(
                request.getTitle(),
                request.getMessage(),
                request.getAccept(),
                request.getReject()
        );
    }

    public static NtSignatureEntity getNtSignatureEntity(NtSignature signature) {
        return new NtSignatureEntity(
                signature.getCompanySignature(),
                signature.getCovrSignature(),
                signature.getClientSignature(),
                signature.getRegistrationSignature()
        );
    }

    @Nullable
    private static BiometricEntity getBiometricEntity(@Nullable Biometric biometric) {
        if (biometric == null) {
            return null;
        }
        BiometricTypeEntity biometricTypeEntity = getBiometricTypeEntity(biometric.getBiometricType());
        return new BiometricEntity(biometricTypeEntity, biometric.getMaxAttemptCount());
    }

    private static BiometricTypeEntity getBiometricTypeEntity(BiometricType biometricType) {
        switch (biometricType) {
            case NONE:
                return BiometricTypeEntity.NONE;
            case ACCEPT:
                return BiometricTypeEntity.ACCEPT;
            case REJECT:
                return BiometricTypeEntity.REJECT;
            case ALL:
                return BiometricTypeEntity.ALL;
        }
        return null;
    }

    public static TransactionEntity getTransactionEntity(Transaction transaction) {

        Company company = transaction.getCompany();
        CompanyEntity companyEntity = company != null ? getCompanyEntity(company) : null;
        TemplateEntity templateEntity = getTemplateEntity(transaction.getTemplate());
        StatusEntity statusEntity = getStatusEntity(transaction.getStatus());
        RequestEntity requestEntity = getRequestEntity1(transaction.getRequest());
        NtSignatureEntity ntSignatureEntity = getNtSignatureEntity(transaction.getSignature());
        BiometricEntity biometricEntity = getBiometricEntity(transaction.getBiometric());

        return new TransactionEntity(
                transaction.getId(),
                companyEntity,
                transaction.getCompanyClientId(),
                transaction.getTemplateId(),
                templateEntity,
                transaction.getValidTo() * MILLISECONDS_IN_SECOND,
                transaction.getValidFrom() * MILLISECONDS_IN_SECOND,
                statusEntity,
                transaction.getCreatedByIp(),
                transaction.getVerifiedByIp(),
                transaction.getCreated() * MILLISECONDS_IN_SECOND,
                transaction.getUpdatedAt() * MILLISECONDS_IN_SECOND,
                transaction.getAcceptHistoryMessage(),
                transaction.getRejectHistoryMessage(),
                transaction.getExpiredHistoryMessage(),
                transaction.getFailedBiometricHistoryMessage(),
                requestEntity,
                transaction.isViewed(),
                ntSignatureEntity,
                biometricEntity
        );
    }

    public static AppUnlockTimeEntity getAppUnlockTimeEntity(AppLockedException appLockedException) {
        final AppUnlockTime appUnlockTime = new AppUnlockTime(
                appLockedException.getAttemptsLeft(),
                appLockedException.getUnlockTime(),
                appLockedException.getLockType()
        );
        return getAppUnlockTimeEntity(appUnlockTime);
    }

    public static AppUnlockTimeEntity getAppUnlockTimeEntity(AppUnlockTime appUnlockTime) {
        return new AppUnlockTimeEntity(
                appUnlockTime.getAttemptsLeft(),
                appUnlockTime.getUnlockedTime(),
                getLockTypeEntity(appUnlockTime.getLockType())
        );
    }

    private static LockTypeEntity getLockTypeEntity(LockType lockType) {
        switch (lockType) {
            case NOT_LOCKED:
                return LockTypeEntity.NOT_LOCKED;
            case LOCKED_NO_TIME_OUT:
                return LockTypeEntity.LOCKED_NO_TIME_OUT;
            case LOCKED_WITH_TIME_OUT:
                return LockTypeEntity.LOCKED_WITH_TIME_OUT;
        }
        throw new IllegalStateException();
    }
}
