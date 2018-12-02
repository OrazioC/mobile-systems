package mobilesystems.wifidirect.shopforyou.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM items where id = :itemId")
    List<ItemEntity> loadItems(int itemId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemEntity item);
}
