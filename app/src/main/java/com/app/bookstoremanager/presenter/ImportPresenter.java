package com.app.bookstoremanager.presenter;


import com.app.bookstoremanager.bean.BookBean;
import com.app.bookstoremanager.bean.ScanBook;
import com.app.bookstoremanager.model.impl.BookDao;
import com.app.bookstoremanager.utils.CustomUUId;
import com.app.bookstoremanager.utils.FileUtils;
import com.app.bookstoremanager.view.ImportContact;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by tctctc on 2017/3/25.
 * Function:
 */

public class ImportPresenter extends ImportContact.Presenter {


    public static final String TAG = "ImportPresenter";

    private BookDao mBookDao;
    private List<BookBean> mBooks;

    @Override
    public void onAddBooks(List<ScanBook> scanBooks) {
        List<BookBean> list = new ArrayList<>();
        for (ScanBook scanBook : scanBooks) {
            BookBean bean = new BookBean();
            File file = scanBook.getFile();
            //找出最后一个"."的位置，以此隔离出名字和后缀
            int i = file.getName().lastIndexOf(".");
            bean.setBookId(CustomUUId.get().nextId());
            bean.setBookName(file.getName().substring(0,i));
            bean.setMBookType(1);
            bean.setFileSize(FileUtils.getFileSize(file));
//            bean.setLength(file.length());
            bean.setPath(file.getAbsolutePath());
            list.add(bean);
        }

        mModel.addBookList(list).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                mView.toShelf();
            }
        });
    }

    @Override
    public void onGetFileList(File rootFile) {

        List<File> files = mModel.getFileList(rootFile);
        if(mBookDao == null){
            mBookDao = new BookDao();
        }
        mBooks = mBookDao.loadAll();
        List<ScanBook> scanBooks = new ArrayList<>();
        int num = 0;
        int importableNum = 0;
        for (File file : files) {
            ScanBook scanBook = new ScanBook(file,false);
            if (file.isFile()){
                ++num;
                for(BookBean bookBean:mBooks){
                    if(bookBean.getPath().equals(file.getAbsolutePath())){
                        scanBook.setImported(true);
                    }
                }

                if(!scanBook.isImported()){
                    ++importableNum;
                }
            }
            scanBooks.add(scanBook);
        }
        mView.showScanBookList(scanBooks,num,importableNum);
    }

    @Override
    public void onStart() {

    }
}
