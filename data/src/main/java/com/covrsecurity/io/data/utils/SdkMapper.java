package com.covrsecurity.io.data.utils;

import androidx.annotation.Nullable;

import com.covrsecurity.io.domain.entity.BiometricEntity;
import com.covrsecurity.io.domain.entity.BiometricTypeEntity;
import com.covrsecurity.io.domain.entity.CompanyEntity;
import com.covrsecurity.io.domain.entity.NtSignatureEntity;
import com.covrsecurity.io.domain.entity.RequestEntity;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TemplateEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.sdk.response.Biometric;
import com.covrsecurity.io.sdk.response.BiometricType;
import com.covrsecurity.io.sdk.response.Company;
import com.covrsecurity.io.sdk.response.NtSignature;
import com.covrsecurity.io.sdk.response.Request;
import com.covrsecurity.io.sdk.response.Status;
import com.covrsecurity.io.sdk.response.Template;
import com.covrsecurity.io.sdk.response.Transaction;

public class SdkMapper {

    @Nullable
    public static Status getStatusSdk(@Nullable StatusEntity statusEntity) {
        if (statusEntity == null) {
            return null;
        }
        switch (statusEntity) {
            case ACTIVE:
                return Status.ACTIVE;
            case EXPIRED:
                return Status.EXPIRED;
            case ACCEPTED:
                return Status.ACCEPTED;
            case REJECTED:
                return Status.REJECTED;
            case FAILED_BIOMETRIC:
                return Status.FAILED_BIOMETRIC;
            default:
                return null;
        }
    }

    public static Transaction getTransactionSdk(TransactionEntity transactionEntity) {

        CompanyEntity companyEntity = transactionEntity.getCompany();
        Company company = new Company(
                companyEntity.getId(),
                companyEntity.getUserName(),
                companyEntity.getPublicKey(),
                companyEntity.getCreatedDate(),
                companyEntity.getCompanyId(),
                companyEntity.getName(),
                companyEntity.getFullName(),
                companyEntity.getWebsiteUrl(),
                companyEntity.getWebsiteName(),
                companyEntity.getLogo(),
                companyEntity.isActive(),
                companyEntity.isViewed(),
                getStatusSdk(companyEntity.getStatus())
        );

        TemplateEntity templateEntity = transactionEntity.getTemplate();
        Template template = new Template(
                templateEntity.getLayoutType(),
                templateEntity.getBackgroundImage(),
                templateEntity.getTransactionImage()
        );

        RequestEntity requestEntity = transactionEntity.getRequest();
        Request request = new Request(
                requestEntity.getTitle(),
                requestEntity.getMessage(),
                requestEntity.getAccept(),
                requestEntity.getReject()
        );


        NtSignatureEntity entitySignature = transactionEntity.getSignature();
        NtSignature signature = new NtSignature(
                entitySignature.getCompanySignature(),
                entitySignature.getCovrSignature(),
                entitySignature.getClientSignature(),
                entitySignature.getRegistrationSignature()
        );

        BiometricEntity biometricEntity = transactionEntity.getBiometric();
        Biometric biometric = null;
        if (biometricEntity != null) {
            biometric = new Biometric(
                    getBiometricType(biometricEntity.getBiometricType()),
                    biometricEntity.getMaxAttemptCount()
            );
        }

        return new Transaction(
                transactionEntity.getId(),
                company,
                transactionEntity.getCompanyClientId(),
                transactionEntity.getTemplateId(),
                template,
                transactionEntity.getValidTo(),
                transactionEntity.getValidFrom(),
                getStatusSdk(transactionEntity.getStatus()),
                transactionEntity.getCreatedByIp(),
                transactionEntity.getVerifiedByIp(),
                transactionEntity.getCreated(),
                transactionEntity.getUpdatedAt(),
                transactionEntity.getAcceptHistoryMessage(),
                transactionEntity.getRejectHistoryMessage(),
                transactionEntity.getExpiredHistoryMessage(),
                transactionEntity.getFailedBiometricHistoryMessage(),
                request,
                transactionEntity.isViewed(),
                signature,
                biometric
        );
    }

    private static BiometricType getBiometricType(BiometricTypeEntity biometricType) {
        switch (biometricType) {
            case NONE:
                return BiometricType.NONE;
            case ACCEPT:
                return BiometricType.ACCEPT;
            case REJECT:
                return BiometricType.REJECT;
            case ALL:
                return BiometricType.ALL;
        }
        return null;
    }
}
