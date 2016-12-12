package edu.temple.bitcoindashboard.DataBase;

import android.provider.BaseColumns;

/*
Class for maintaining SQL commands for database storing wallet addresses
 */

public class WalletDBContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WalletEntry.TABLE_NAME + " (" +
                    WalletEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    WalletEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE  +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WalletEntry.TABLE_NAME;

    public static abstract class WalletEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ADDRESS = "address";
    }
}
