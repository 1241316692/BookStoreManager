package com.app.bookstoremanager.model.impl;

import android.database.sqlite.SQLiteDatabase;

import com.app.bookstoremanager.BookApplication;
import com.app.bookstoremanager.bean.BookBean;
import com.app.bookstoremanager.bean.MarkBean;
import com.app.bookstoremanager.common.rx.RxSchedulers;
import com.app.bookstoremanager.gen.DaoMaster;
import com.app.bookstoremanager.gen.DaoSession;
import com.app.bookstoremanager.gen.MarkBeanDao;
import com.app.bookstoremanager.view.MarkContact;

import org.greenrobot.greendao.query.QueryBuilder;


import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by tctctc on 2017/4/21.
 * Function:
 */

public class MarkDao implements MarkContact.Model {
    private static MarkBeanDao sDao;
    public static final String dbName = "Book";
    public MarkBeanDao mDao;
    private BookBean mBookBean;

    public MarkDao(BookBean bean) {
        this.mBookBean = bean;
        mDao = getInstance();
    }

    public static MarkBeanDao getInstance() {
        if (sDao == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(BookApplication.getContext(), dbName);
            SQLiteDatabase database = helper.getWritableDatabase();
            DaoMaster master = new DaoMaster(database);
            DaoSession session = master.newSession();
            sDao = session.getMarkBeanDao();
        }
        return sDao;
    }

    @Override
    public Observable<List<MarkBean>> getMarkList() {

        return Observable.create(new ObservableOnSubscribe<List<MarkBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<MarkBean>> e) throws Exception {
                QueryBuilder builder = mDao.queryBuilder();
                List<MarkBean> list = builder.where(MarkBeanDao.Properties.BookId.eq(mBookBean.getBookId())).build().listLazy();
                e.onNext(list);
            }
        }).compose(RxSchedulers.<List<MarkBean>>ioMain());
    }
}
