package com.marco.lib_audio.db;

import android.database.sqlite.SQLiteDatabase;

import com.marco.lib_audio.app.AudioHelper;
import com.marco.lib_audio.model.Favorite;
import com.marco.lib_audio.model.Track;

import org.greenrobot.greendao.query.QueryBuilder;

public class GreenDaoHelper {
    private static final String DB_NAME = "music_db";
    //数据库帮助类，用来创建数据库，升级数据库
    private static DaoMaster.DevOpenHelper sHelper;
    //最终创建的数据库
    private static SQLiteDatabase sDb;
    //管理数据库
    private static DaoMaster sDaoMaster;
    //管理各种实体Dao
    private static DaoSession sDaoSession;

    public static void initDatabase() {
        sHelper = new DaoMaster.DevOpenHelper(AudioHelper.getContext(), DB_NAME);
        sDb = sHelper.getWritableDatabase();
        sDaoMaster = new DaoMaster(sDb);
        sDaoSession = sDaoMaster.newSession();
    }

    /**
     * 添加收藏
     * @param track
     */
    public static void addFavorite(Track track) {
        FavoriteDao dao = sDaoSession.getFavoriteDao();
        Favorite favorite = new Favorite();
        favorite.setAudioId(track.id);
        favorite.setTrack(track);
        dao.insertOrReplace(favorite);
    }

    /**
     * 移除收藏
     * @param track
     */
    public static void removeFavorite(Track track) {
        FavoriteDao dao = sDaoSession.getFavoriteDao();
        Favorite favorite = selectFavorite(track);
        dao.delete(favorite);
    }

    /**
     * 查询收藏
     * @param track
     * @return
     */
    public static Favorite selectFavorite(Track track) {
        FavoriteDao dao = sDaoSession.getFavoriteDao();
        Favorite favorite = dao.queryBuilder().where(FavoriteDao.Properties.AudioId.eq(track.id)).unique();
        return favorite;
    }
}
