package com.top.androidx.graffiti.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.top.androidx.R;

import org.jetbrains.annotations.NotNull;

public class BottomSheetDialog extends BottomSheetDialogFragment {


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public void onStart() {
        super.onStart();
        //菜单全部显示
        final View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        AppCompatTextView tvCancel = view.findViewById(R.id.tv_cancel);
        AppCompatTextView tvSend = view.findViewById(R.id.tv_send);
        AppCompatTextView tvSave = view.findViewById(R.id.tv_save);

        tvCancel.setOnClickListener(v -> dismiss());

        tvSave.setOnClickListener(v -> {
            if (onClickListener!=null){
                onClickListener.onSave();
            }
            dismiss();
        });

        tvSend.setOnClickListener(v -> {
            dismiss();
            if (onClickListener!=null){
                onClickListener.onSend();
            }
        });
    }
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{
        void onSend();
        void onSave();
    }
}
