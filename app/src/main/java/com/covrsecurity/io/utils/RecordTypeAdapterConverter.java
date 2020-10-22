package com.covrsecurity.io.utils;

import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.ui.adapter.model.CovrVaultItemModel;

/**
 * Created by Lenovo on 16.05.2017.
 */

public class RecordTypeAdapterConverter {

    public static CovrVaultItemModel convert(RecordType type) {
        RecordTypeCompat typeCompat = RecordTypeCompat.getTypeCompat(type);
        return new CovrVaultItemModel(typeCompat.getLabel(), typeCompat.getCategory(), typeCompat);
    }
}
