package com.qiqi;

import com.qiqi.utils.FileScanHelper;
import com.qiqi.utils.FileUtil;
import com.qiqi.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseBuilder {

    protected Map<String, FileScanHelper.FileInfo> mFileMap = new HashMap<>();
    protected Set<String> mCompileList = new HashSet<>();//编译文件
    protected String mFileInfoPath; // 文件消息路径
    protected List<String> mPathList;//java或资源文件路径
    protected List<String> mIncreaseFilePathList;
    protected List<String> mExpelFilePathList;

    public BaseBuilder(String fileInfoPath, List<String> pathList, List<String> increaseFilePathList, List<String> expelFilePathList) {
        mFileInfoPath = fileInfoPath;
        mPathList = pathList;
        mIncreaseFilePathList = increaseFilePathList;
        mExpelFilePathList = expelFilePathList;
    }

    protected void preStart() {
        readInfoFile();
        increaseFile();
        expelFile();
    }

    protected void readInfoFile() {
        FileScanHelper helper = new FileScanHelper();
        for (String path : mPathList) {
            if (FileUtil.dirExists(path)) {
                Log.i("扫描" + path);
                scanFile(helper, path);
            }
        }
        mFileMap = FileScanHelper.readFile(mFileInfoPath);
        for (FileScanHelper.FileInfo info : helper.pathList) {
            FileScanHelper.FileInfo search = mFileMap.get(info.path);
            if (search == null) {
                Log.i("增加 " + info.path);
                mCompileList.add(info.path);
            } else if (!search.eq(info)) {
                Log.i("修改 " + info.path);
                mCompileList.add(info.path);
            }
        }
    }

    protected void increaseFile() {
        if (mIncreaseFilePathList != null) {
            for (String c : mIncreaseFilePathList) {
                for (Map.Entry<String, FileScanHelper.FileInfo> entry : mFileMap.entrySet()) {
                    if (entry.getKey().equals(c) || canIncrease(entry.getKey(), c)) {
                        Log.i("手动增加 " + entry.getKey());
                        mCompileList.add(entry.getKey());
                        break;
                    }
                }
                if (FileUtil.fileExists(c)){
                    mCompileList.add(c);
                }
            }
        }
    }

    protected void expelFile() {
        List<String> mExpelList = new ArrayList<>();
        if (mExpelFilePathList != null) {
            for (String c : mExpelFilePathList) {
                for (String path : mCompileList) {
                    if ((path.contains(c) || canExpel(path, c))) {
                        Log.i("手动去除 " + path);
                        mExpelList.add(path);
                        break;
                    }
                }
                if (FileUtil.fileExists(c)){
                    mExpelList.add(c);
                }
            }
            mCompileList.removeAll(mExpelList);
        }
    }

    protected boolean canIncrease(String path, String name) {
        return path.contains(name);
    }

    protected boolean canExpel(String path, String name) {
        return path.contains(name);
    }

    protected void scanFile(FileScanHelper helper, String path) {
        helper.scan(new File(path));
    }

    //使用缓存文件
    protected boolean mUseCache = false;
    protected List<FileScanHelper.FileInfo> mCacheInfoList = new ArrayList<>();
    protected List<String> mCacheList = new ArrayList<>();

    private void readCache() {
        if (mUseCache) {

        }
    }

    private void saveCache() {
        if (mUseCache) {

        }
    }
}
