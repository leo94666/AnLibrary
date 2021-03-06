/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.top.update.widget;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.top.update.R;
import com.top.update._XUpdate;
import com.top.update.entity.PromptEntity;
import com.top.update.entity.UpdateEntity;
import com.top.update.proxy.IPrompterProxy;
import com.top.update.service.OnFileDownloadListener;
import com.top.update.utils.ColorUtils;
import com.top.update.utils.DrawableUtils;
import com.top.update.utils.UpdateUtils;

import java.io.File;

import static com.top.update.entity.UpdateError.ERROR.DOWNLOAD_PERMISSION_DENIED;
import static com.top.update.entity.UpdateError.ERROR.PROMPT_UNKNOWN;


/**
 * ????????????????????????DialogFragment?????????
 *
 * @author xuexiang
 * @since 2018/7/2 ??????11:40
 */
public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    public final static String KEY_UPDATE_ENTITY = "key_update_entity";
    public final static String KEY_UPDATE_PROMPT_ENTITY = "key_update_prompt_entity";

    public final static int REQUEST_CODE_REQUEST_PERMISSIONS = 111;

    //======??????========//
    /**
     * ????????????
     */
    private ImageView mIvTop;
    /**
     * ??????
     */
    private TextView mTvTitle;
    //======????????????========//
    /**
     * ??????????????????
     */
    private TextView mTvUpdateInfo;
    /**
     * ????????????
     */
    private Button mBtnUpdate;
    /**
     * ????????????
     */
    private Button mBtnBackgroundUpdate;
    /**
     * ????????????
     */
    private TextView mTvIgnore;
    /**
     * ?????????
     */
    private NumberProgressBar mNumberProgressBar;
    //======??????========//
    /**
     * ????????????
     */
    private LinearLayout mLlClose;
    private ImageView mIvClose;

    //======????????????========//
    /**
     * ????????????
     */
    private UpdateEntity mUpdateEntity;
    /**
     * ????????????
     */
    private static IPrompterProxy sIPrompterProxy;
    /**
     * ?????????????????????
     */
    private PromptEntity mPromptEntity;

    /**
     * ??????????????????
     *
     * @param fragmentManager fragment?????????
     * @param updateEntity    ????????????
     * @param prompterProxy   ????????????
     * @param promptEntity    ?????????????????????
     * @return
     */
    public static void show(@NonNull FragmentManager fragmentManager, @NonNull UpdateEntity updateEntity, @NonNull IPrompterProxy prompterProxy, @NonNull PromptEntity promptEntity) {
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_UPDATE_ENTITY, updateEntity);
        args.putParcelable(KEY_UPDATE_PROMPT_ENTITY, promptEntity);
        fragment.setArguments(args);
        setsIPrompterProxy(prompterProxy);
        fragment.show(fragmentManager);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _XUpdate.setIsShowUpdatePrompter(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.XUpdate_Fragment_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    private void initDialog() {
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                //????????????????????????????????????????????????
                return keyCode == KeyEvent.KEYCODE_BACK && mUpdateEntity != null && mUpdateEntity.isForce();
            }
        });

        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = window.getAttributes();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            if (mPromptEntity.getWidthRatio() > 0 && mPromptEntity.getWidthRatio() < 1) {
                lp.width = (int) (displayMetrics.widthPixels * mPromptEntity.getWidthRatio());
            }
            if (mPromptEntity.getHeightRatio() > 0 && mPromptEntity.getHeightRatio() < 1) {
                lp.height = (int) (displayMetrics.heightPixels * mPromptEntity.getHeightRatio());
            }
            window.setAttributes(lp);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xupdate_dialog_app, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        //????????????
        mIvTop = view.findViewById(R.id.iv_top);
        //??????
        mTvTitle = view.findViewById(R.id.tv_title);
        //????????????
        mTvUpdateInfo = view.findViewById(R.id.tv_update_info);
        //????????????
        mBtnUpdate = view.findViewById(R.id.btn_update);
        //??????????????????
        mBtnBackgroundUpdate = view.findViewById(R.id.btn_background_update);
        //??????
        mTvIgnore = view.findViewById(R.id.tv_ignore);
        //?????????
        mNumberProgressBar = view.findViewById(R.id.npb_progress);

        //????????????+??? ???????????????
        mLlClose = view.findViewById(R.id.ll_close);
        //????????????
        mIvClose = view.findViewById(R.id.iv_close);
    }

    /**
     * ???????????????
     */
    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPromptEntity = bundle.getParcelable(KEY_UPDATE_PROMPT_ENTITY);
            //???????????????
            if (mPromptEntity == null) {
                //?????????????????????????????????
                mPromptEntity = new PromptEntity();
            }
            initTheme(mPromptEntity.getThemeColor(), mPromptEntity.getTopResId());
            mUpdateEntity = bundle.getParcelable(KEY_UPDATE_ENTITY);
            if (mUpdateEntity != null) {
                initUpdateInfo(mUpdateEntity);
                initListeners();
            }
        }
    }

    /**
     * ?????????????????????
     *
     * @param updateEntity
     */
    private void initUpdateInfo(UpdateEntity updateEntity) {
        //???????????????
        final String newVersion = updateEntity.getVersionName();
        String updateInfo = UpdateUtils.getDisplayUpdateInfo(getContext(), updateEntity);
        //????????????
        mTvUpdateInfo.setText(updateInfo);
        mTvTitle.setText(String.format(getString(R.string.xupdate_lab_ready_update), newVersion));

        //??????????????????????????????????????????
        if (UpdateUtils.isApkDownloaded(mUpdateEntity)) {
            showInstallButton(UpdateUtils.getApkFileByUpdateEntity(mUpdateEntity));
        }

        //????????????,?????????????????????
        if (updateEntity.isForce()) {
            mLlClose.setVisibility(View.GONE);
        } else {
            //?????????????????????????????????
            if (updateEntity.isIgnorable()) {
                mTvIgnore.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * ??????????????????
     */
    private void initTheme(@ColorInt int themeColor, @DrawableRes int topResId) {
        if (themeColor == -1) {
            themeColor = ColorUtils.getColor(getContext(), R.color.xupdate_default_theme_color);
        }
        if (topResId == -1) {
            topResId = R.drawable.xupdate_bg_app_top;
        }
        setDialogTheme(themeColor, topResId);
    }

    /**
     * ??????
     *
     * @param color    ??????
     * @param topResId ??????
     */
    private void setDialogTheme(int color, int topResId) {
        mIvTop.setImageResource(topResId);
        mBtnUpdate.setBackgroundDrawable(DrawableUtils.getDrawable(UpdateUtils.dip2px(4, getActivity()), color));
        mBtnBackgroundUpdate.setBackgroundDrawable(DrawableUtils.getDrawable(UpdateUtils.dip2px(4, getActivity()), color));
        mNumberProgressBar.setProgressTextColor(color);
        mNumberProgressBar.setReachedBarColor(color);
        //?????????????????????
        mBtnUpdate.setTextColor(ColorUtils.isColorDark(color) ? Color.WHITE : Color.BLACK);
    }

    private void initListeners() {
        mBtnUpdate.setOnClickListener(this);
        mBtnBackgroundUpdate.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvIgnore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        //?????????????????????????????????apk???
        if (i == R.id.btn_update) {
            //???????????????????????????????????????????????????
            int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!UpdateUtils.isPrivateApkCacheDir(mUpdateEntity) && flag != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_REQUEST_PERMISSIONS);
            } else {
                installApp();
            }
        } else if (i == R.id.btn_background_update) {
            //????????????????????????
            if (sIPrompterProxy != null) {
                sIPrompterProxy.backgroundDownload();
            }
            dismissDialog();
        } else if (i == R.id.iv_close) {
            //??????????????????
            if (sIPrompterProxy != null) {
                sIPrompterProxy.cancelDownload();
            }
            dismissDialog();
        } else if (i == R.id.tv_ignore) {
            //??????????????????
            UpdateUtils.saveIgnoreVersion(getActivity(), mUpdateEntity.getVersionName());
            dismissDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //??????
                installApp();
            } else {
                _XUpdate.onUpdateError(DOWNLOAD_PERMISSION_DENIED);
                dismissDialog();
            }
        }

    }

    private void installApp() {
        if (UpdateUtils.isApkDownloaded(mUpdateEntity)) {
            onInstallApk();
            //???????????????
            //??????????????????????????????????????????????????????????????????????????????????????????app?????????????????????????????????????????????????????????????????????
            if (!mUpdateEntity.isForce()) {
                dismissDialog();
            } else {
                showInstallButton(UpdateUtils.getApkFileByUpdateEntity(mUpdateEntity));
            }
        } else {
            if (sIPrompterProxy != null) {
                sIPrompterProxy.startDownload(mUpdateEntity, mOnFileDownloadListener);
            }
            //??????????????????????????????????????????
            if (mUpdateEntity.isIgnorable()) {
                mTvIgnore.setVisibility(View.GONE);
            }
        }
    }

    /**
     * ??????????????????
     */
    private OnFileDownloadListener mOnFileDownloadListener = new OnFileDownloadListener() {
        @Override
        public void onStart() {
            if (!UpdateDialogFragment.this.isRemoving()) {
                mNumberProgressBar.setVisibility(View.VISIBLE);
                mNumberProgressBar.setProgress(0);
                mBtnUpdate.setVisibility(View.GONE);
                if (mPromptEntity.isSupportBackgroundUpdate()) {
                    mBtnBackgroundUpdate.setVisibility(View.VISIBLE);
                } else {
                    mBtnBackgroundUpdate.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onProgress(float progress, long total) {
            if (!UpdateDialogFragment.this.isRemoving()) {
                mNumberProgressBar.setProgress(Math.round(progress * 100));
                mNumberProgressBar.setMax(100);
            }
        }

        @Override
        public boolean onCompleted(File file) {
            if (!UpdateDialogFragment.this.isRemoving()) {
                mBtnBackgroundUpdate.setVisibility(View.GONE);
                if (mUpdateEntity.isForce()) {
                    showInstallButton(file);
                } else {
                    dismissDialog();
                }
            }
            //??????true???????????????apk??????
            return true;
        }

        @Override
        public void onError(Throwable throwable) {
            if (!UpdateDialogFragment.this.isRemoving()) {
                dismissDialog();
            }
        }
    };

    /**
     * ?????????????????????
     */
    private void showInstallButton(final File apkFile) {
        mNumberProgressBar.setVisibility(View.GONE);
        mBtnUpdate.setText(R.string.xupdate_lab_install);
        mBtnUpdate.setVisibility(View.VISIBLE);
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInstallApk(apkFile);
            }
        });
    }

    private void onInstallApk() {
        _XUpdate.startInstallApk(getContext(), UpdateUtils.getApkFileByUpdateEntity(mUpdateEntity), mUpdateEntity.getDownLoadEntity());
    }

    private void onInstallApk(File apkFile) {
        _XUpdate.startInstallApk(getContext(), apkFile, mUpdateEntity.getDownLoadEntity());
    }

    /**
     * ????????????
     */
    private void dismissDialog() {
        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (manager.isDestroyed()) {
                return;
            }
        }
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            _XUpdate.onUpdateError(PROMPT_UNKNOWN, e.getMessage());
        }
    }

    /**
     * ??????????????????
     *
     * @param manager
     */
    public void show(FragmentManager manager) {
        show(manager, "update_dialog");
    }

    @Override
    public void onDestroyView() {
        _XUpdate.setIsShowUpdatePrompter(false);
        clearIPrompterProxy();
        super.onDestroyView();
    }

    private static void setsIPrompterProxy(IPrompterProxy sIPrompterProxy) {
        UpdateDialogFragment.sIPrompterProxy = sIPrompterProxy;
    }

    private static void clearIPrompterProxy() {
        if (sIPrompterProxy != null) {
            sIPrompterProxy.recycle();
            sIPrompterProxy = null;
        }
    }

}

