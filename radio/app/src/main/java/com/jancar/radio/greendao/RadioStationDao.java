package com.jancar.radio.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jancar.radio.entity.RadioStation;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RADIO_STATION".
*/
public class RadioStationDao extends AbstractDao<RadioStation, Long> {

    public static final String TABLENAME = "RADIO_STATION";

    /**
     * Properties of entity RadioStation.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Desp = new Property(1, String.class, "desp", false, "DESP");
        public final static Property Id = new Property(2, int.class, "id", false, "ID");
        public final static Property IsLove = new Property(3, int.class, "isLove", false, "IS_LOVE");
        public final static Property Kind = new Property(4, int.class, "kind", false, "KIND");
        public final static Property MBand = new Property(5, int.class, "mBand", false, "M_BAND");
        public final static Property MFreq = new Property(6, int.class, "mFreq", false, "M_FREQ");
        public final static Property Name = new Property(7, String.class, "name", false, "NAME");
        public final static Property Position = new Property(8, int.class, "position", false, "POSITION");
        public final static Property Rdsname = new Property(9, String.class, "rdsname", false, "RDSNAME");
        public final static Property Select = new Property(10, boolean.class, "select", false, "SELECT");
        public final static Property Frequency = new Property(11, int.class, "frequency", false, "FREQUENCY");
        public final static Property Location = new Property(12, int.class, "Location", false, "LOCATION");
    }


    public RadioStationDao(DaoConfig config) {
        super(config);
    }
    
    public RadioStationDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RADIO_STATION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"DESP\" TEXT," + // 1: desp
                "\"ID\" INTEGER NOT NULL ," + // 2: id
                "\"IS_LOVE\" INTEGER NOT NULL ," + // 3: isLove
                "\"KIND\" INTEGER NOT NULL ," + // 4: kind
                "\"M_BAND\" INTEGER NOT NULL ," + // 5: mBand
                "\"M_FREQ\" INTEGER NOT NULL ," + // 6: mFreq
                "\"NAME\" TEXT," + // 7: name
                "\"POSITION\" INTEGER NOT NULL ," + // 8: position
                "\"RDSNAME\" TEXT," + // 9: rdsname
                "\"SELECT\" INTEGER NOT NULL ," + // 10: select
                "\"FREQUENCY\" INTEGER NOT NULL ," + // 11: frequency
                "\"LOCATION\" INTEGER NOT NULL );"); // 12: Location
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RADIO_STATION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RadioStation entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String desp = entity.getDesp();
        if (desp != null) {
            stmt.bindString(2, desp);
        }
        stmt.bindLong(3, entity.getId());
        stmt.bindLong(4, entity.getIsLove());
        stmt.bindLong(5, entity.getKind());
        stmt.bindLong(6, entity.getMBand());
        stmt.bindLong(7, entity.getMFreq());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(8, name);
        }
        stmt.bindLong(9, entity.getPosition());
 
        String rdsname = entity.getRdsname();
        if (rdsname != null) {
            stmt.bindString(10, rdsname);
        }
        stmt.bindLong(11, entity.getSelect() ? 1L: 0L);
        stmt.bindLong(12, entity.getFrequency());
        stmt.bindLong(13, entity.getLocation());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RadioStation entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String desp = entity.getDesp();
        if (desp != null) {
            stmt.bindString(2, desp);
        }
        stmt.bindLong(3, entity.getId());
        stmt.bindLong(4, entity.getIsLove());
        stmt.bindLong(5, entity.getKind());
        stmt.bindLong(6, entity.getMBand());
        stmt.bindLong(7, entity.getMFreq());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(8, name);
        }
        stmt.bindLong(9, entity.getPosition());
 
        String rdsname = entity.getRdsname();
        if (rdsname != null) {
            stmt.bindString(10, rdsname);
        }
        stmt.bindLong(11, entity.getSelect() ? 1L: 0L);
        stmt.bindLong(12, entity.getFrequency());
        stmt.bindLong(13, entity.getLocation());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RadioStation readEntity(Cursor cursor, int offset) {
        RadioStation entity = new RadioStation( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // desp
            cursor.getInt(offset + 2), // id
            cursor.getInt(offset + 3), // isLove
            cursor.getInt(offset + 4), // kind
            cursor.getInt(offset + 5), // mBand
            cursor.getInt(offset + 6), // mFreq
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // name
            cursor.getInt(offset + 8), // position
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // rdsname
            cursor.getShort(offset + 10) != 0, // select
            cursor.getInt(offset + 11), // frequency
            cursor.getInt(offset + 12) // Location
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RadioStation entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDesp(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setId(cursor.getInt(offset + 2));
        entity.setIsLove(cursor.getInt(offset + 3));
        entity.setKind(cursor.getInt(offset + 4));
        entity.setMBand(cursor.getInt(offset + 5));
        entity.setMFreq(cursor.getInt(offset + 6));
        entity.setName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPosition(cursor.getInt(offset + 8));
        entity.setRdsname(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setSelect(cursor.getShort(offset + 10) != 0);
        entity.setFrequency(cursor.getInt(offset + 11));
        entity.setLocation(cursor.getInt(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RadioStation entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RadioStation entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(RadioStation entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
