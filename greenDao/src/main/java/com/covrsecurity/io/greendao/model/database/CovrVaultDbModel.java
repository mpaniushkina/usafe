package com.covrsecurity.io.greendao.model.database;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by Lenovo on 16.05.2017.
 */
@Deprecated
@Entity
public class CovrVaultDbModel {

    @Id(autoincrement = true)
    @Property(nameInDb = "_ID")
    private Long mId;

    @Property(nameInDb = "KEY")
    private String mKey;

    @Property(nameInDb = "VALUE")
    private String mValue;

    @NotNull
    @Property(nameInDb = "TYPE")
    @Convert(converter = RecordTypeConverter.class, columnType = String.class)
    private RecordType mType;

    @NotNull
    @Property(nameInDb = "FAVORITE")
    private Boolean mFavorite;

    @NotNull
    @Property(nameInDb = "DESCRIPTION")
    private String mDescription;

    @Property(nameInDb = "ADDITIONAL")
    private String mAdditionalPlain;

    @NotNull
    @Property(nameInDb = "UPDATED_AT")
    private Long mUpdatedAt;

    @Generated(hash = 143909143)
    public CovrVaultDbModel() {
    }

    public CovrVaultDbModel(String mKey, String mValue, @NotNull RecordType mType, @NotNull boolean mFavorite,
                            @NotNull String description, String additionalPlain, @NotNull Long mUpdatedAt) {
        this.mKey = mKey;
        this.mValue = mValue;
        this.mType = mType;
        this.mDescription = description;
        this.mAdditionalPlain = additionalPlain;
        this.mFavorite = mFavorite;
        this.mUpdatedAt = mUpdatedAt;
    }

    @Generated(hash = 966229961)
    public CovrVaultDbModel(Long mId, String mKey, String mValue, @NotNull RecordType mType,
            @NotNull Boolean mFavorite, @NotNull String mDescription, String mAdditionalPlain,
            @NotNull Long mUpdatedAt) {
        this.mId = mId;
        this.mKey = mKey;
        this.mValue = mValue;
        this.mType = mType;
        this.mFavorite = mFavorite;
        this.mDescription = mDescription;
        this.mAdditionalPlain = mAdditionalPlain;
        this.mUpdatedAt = mUpdatedAt;
    }

    public Long getMId() {
        return this.mId;
    }

    public void setMId(Long mId) {
        this.mId = mId;
    }

    public String getMKey() {
        return this.mKey;
    }

    public void setMKey(String mKey) {
        this.mKey = mKey;
    }

    public String getMValue() {
        return this.mValue;
    }

    public void setMValue(String mValue) {
        this.mValue = mValue;
    }

    public RecordType getMType() {
        return this.mType;
    }

    public void setMType(RecordType mType) {
        this.mType = mType;
    }

    public Boolean getMFavorite() {
        return this.mFavorite;
    }

    public void setMFavorite(Boolean mFavorite) {
        this.mFavorite = mFavorite;
    }

    public String getMDescription() {
        return this.mDescription;
    }

    public void setMDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getMAdditionalPlain() {
        return this.mAdditionalPlain;
    }

    public void setMAdditionalPlain(String mAdditionalPlain) {
        this.mAdditionalPlain = mAdditionalPlain;
    }

    public Long getMUpdatedAt() {
        return this.mUpdatedAt;
    }

    public void setMUpdatedAt(Long mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }
}
