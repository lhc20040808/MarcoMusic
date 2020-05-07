package com.marco.lib_audio.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.marco.lib_audio.db.DaoSession;
import com.marco.lib_audio.db.TrackDao;
import com.marco.lib_audio.db.FavoriteDao;

@Entity
public class Favorite {

    @Id(autoincrement = true)
    Long favouriteId;

    @NotNull
    String audioId;

    //一条收藏记录唯一对应一条实体
    @ToOne(joinProperty = "audioId")
    Track track;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 56835728)
    private transient FavoriteDao myDao;

    @Generated(hash = 2032249464)
    public Favorite(Long favouriteId, @NotNull String audioId) {
        this.favouriteId = favouriteId;
        this.audioId = audioId;
    }

    @Generated(hash = 459811785)
    public Favorite() {
    }

    public Long getFavouriteId() {
        return this.favouriteId;
    }

    public void setFavouriteId(Long favouriteId) {
        this.favouriteId = favouriteId;
    }

    public String getAudioId() {
        return this.audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    @Generated(hash = 77538765)
    private transient String track__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 846139466)
    public Track getTrack() {
        String __key = this.audioId;
        if (track__resolvedKey == null || track__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TrackDao targetDao = daoSession.getTrackDao();
            Track trackNew = targetDao.load(__key);
            synchronized (this) {
                track = trackNew;
                track__resolvedKey = __key;
            }
        }
        return track;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 400089714)
    public void setTrack(@NotNull Track track) {
        if (track == null) {
            throw new DaoException(
                    "To-one property 'audioId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.track = track;
            audioId = track.getId();
            track__resolvedKey = audioId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1250288999)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFavoriteDao() : null;
    }
    
}
