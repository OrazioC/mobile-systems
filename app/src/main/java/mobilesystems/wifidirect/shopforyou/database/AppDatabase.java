package mobilesystems.wifidirect.shopforyou.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {ItemEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "item_db";

    public abstract ItemDao itemDao();

    private static AppDatabase db;

    public static AppDatabase getInstance(final @NonNull Context context) {
        if (db == null) {
            db = buildDatabase(context);
        }
        return db;
    }

    /**
     * The SQLite database is only created when it's accessed for the first time.
     */
    private static AppDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .build();
    }
}
