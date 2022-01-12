package com.top.androidx.superview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.top.androidx.R;


/**
 * ELabMcAndroid
 * <p>
 * Created by 李阳 on 2020/9/10 0010
 * Copyright © 2020年 新广科技. All rights reserved.
 * <p>
 * Describe: 基于组合控件实现的Super
 */
public class SuperAppCompatEditText extends RelativeLayout implements TextWatcher, View.OnClickListener {

    /**
     * 编辑框模式
     */
    public enum SuperEditTextMode {
        SUPER_ACCOUNT,
        SUPER_PASS_WORD,
        SUPER_PHONE,
        SUPER_VERIFICATION,
        SUPER_OTHER;

        public static SuperEditTextMode get(int superType) {
            if (superType == 0) {
                return SUPER_ACCOUNT;
            } else if (superType == 1) {
                return SUPER_PASS_WORD;
            } else if (superType == 2) {
                return SUPER_PHONE;
            } else if (superType == 3) {
                return SUPER_VERIFICATION;
            } else if (superType == 4) {
                return SUPER_OTHER;
            }
            return null;
        }
    }

    //private int superType;

    private SuperEditTextMode mNowEditTextMode = SuperEditTextMode.SUPER_ACCOUNT;
    private AppCompatImageView clean;
    private AppCompatImageView hideOrShow;
    //用来标记当前密码显示状态
    private boolean pwdIsSecret = false;

    public AppCompatEditText getInputEditText() {
        return inputEt;
    }

    private AppCompatEditText inputEt;
    private String contant;
    private Context context;
    private Drawable iconDrawable;
    private AppCompatTextView getVerificationCode;


    /**
     * 验证码长度，默认是 6
     */
    private int verificationLength = 6;
    private boolean showDeleteIcon;


    private CountDownTimer timer;


    public SuperAppCompatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }


    public SuperAppCompatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    /**
     * 初始化布局
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {

        // 获取控件资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SuperAppCompatEditText);
        //用来确认是 密码还是手机号输入框
        int superType = typedArray.getInteger(R.styleable.SuperAppCompatEditText_super_edit_type, 0);
        mNowEditTextMode = SuperEditTextMode.get(superType);
        String hint = typedArray.getString(R.styleable.SuperAppCompatEditText_super_edit_text_hint);
        verificationLength = typedArray.getInteger(R.styleable.SuperAppCompatEditText_super_edit_verification_length, verificationLength);
        int underLineHeight = (int) typedArray.getDimension(R.styleable.SuperAppCompatEditText_super_edit_under_line_height, dip2px(1));

        showDeleteIcon = typedArray.getBoolean(R.styleable.SuperAppCompatEditText_super_edit_show_delete_icon, false);


        View.inflate(context, R.layout.view_super_edittext, SuperAppCompatEditText.this);
        inputEt = findViewById(R.id.super_edit_text_input_et);
        clean = findViewById(R.id.super_edit_text_clean);
        hideOrShow = findViewById(R.id.super_edit_text_status);
        getVerificationCode = findViewById(R.id.super_edit_get_verification_img);
        AppCompatImageView icon = (AppCompatImageView) findViewById(R.id.super_edit_text_icon);


        if (typedArray.hasValue(R.styleable.SuperAppCompatEditText_super_edit_text_icon)) {
            iconDrawable = typedArray.getDrawable(R.styleable.SuperAppCompatEditText_super_edit_text_icon);

            if (iconDrawable == null) {
                icon.setVisibility(View.GONE);
            } else {
                icon.setImageDrawable(iconDrawable);
            }
        } else {
            icon.setVisibility(View.GONE);
            inputEt.setPadding(dip2px(10), 0, 0, 0);
        }

        if (typedArray.hasValue(R.styleable.SuperAppCompatEditText_super_edit_text_background)) {
            Drawable backGroundDrawable = typedArray.getDrawable(R.styleable.SuperAppCompatEditText_super_edit_text_background);

            if (backGroundDrawable == null) {
                //inputEt.setBackgroundDrawable(backGroundDrawable);
            } else {
                inputEt.setBackgroundDrawable(backGroundDrawable);
            }
        }

        if (underLineHeight == 0) {
            findViewById(R.id.v_under_line).setVisibility(View.GONE);
        }

//        if (mNowEditTextMode == SuperEditTextMode.SUPER_VERIFICATION) {
//            getVerificationCode.setVisibility(VISIBLE);
//            if (timer == null) {
//                initCountDownTimer();
//            }
//        } else {
//            hideOrShow.setVisibility(GONE);
//        }
        getVerificationCode.setOnClickListener(this);
        inputEt.addTextChangedListener(this);
        clean.setOnClickListener(this);
        hideOrShow.setOnClickListener(this);
        inputEt.setHint(hint);
        initEditText();
        typedArray.recycle();
    }

    /**
     * 初始化 输入框
     */
    private void initEditText() {
        hideOrShow.setVisibility(GONE);
        getVerificationCode.setVisibility(View.GONE);
        switch (mNowEditTextMode) {

            case SUPER_ACCOUNT:
                inputEt.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

                inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                break;
            case SUPER_PASS_WORD:
                inputEt.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

                inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                inputEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hideOrShow.setVisibility(VISIBLE);
                break;
            case SUPER_PHONE:
                inputEt.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

                inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                break;
            case SUPER_VERIFICATION:
                inputEt.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                initCountDownTimer();
                inputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                getVerificationCode.setVisibility(View.VISIBLE);
                break;

            case SUPER_OTHER:
                inputEt.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                getVerificationCode.setVisibility(View.GONE);
                break;

            default:
                break;

        }

    }


    private void showDeleteIcon() {
        //内容为空情况下，必须隐藏
        //内容不为空的情况下，是否隐藏取决于showDeleteIcon
        boolean b = TextUtils.isEmpty(contant) || !showDeleteIcon;
        clean.setVisibility(b ? GONE : VISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //判断 是否  输入框是否有内容
        //有内容
        contant = s.toString();

        showDeleteIcon();
        boolean isStandard = false;
        if (null != superEditTextListener) {
            switch (mNowEditTextMode) {
                case SUPER_PHONE:
                    isStandard = isPhoneNumber(contant);
                    break;
                case SUPER_ACCOUNT:
                    isStandard = accountStandard(contant);
                    break;
                case SUPER_VERIFICATION:
                    isStandard = isVerificationCode(contant);
                    break;
                case SUPER_PASS_WORD:
                    isStandard = passwordStandard(contant);
                    break;
                case SUPER_OTHER:
                    //isStandard = passwordStandard(contant);
                    break;
                default:
                    break;

            }
            superEditTextListener.onChanged(SuperAppCompatEditText.this, isStandard, contant);
        }
    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        int vID = v.getId();
        if (vID == R.id.super_edit_text_clean) {
            inputEt.setText("");
        } else if (vID == R.id.super_edit_text_status) {
            initPwd(pwdIsSecret);
            pwdIsSecret = !pwdIsSecret;
        } else if (vID == R.id.super_edit_get_verification_img) {
            if (superEditTextListener != null) {
                superEditTextListener.getImgVerificationCode(v);
            }
        }
    }

    public void startTimer() {
        if (timer != null) {
            timer.start();
        }
        getVerificationCode.setEnabled(false);
    }


    /**
     * 设置模式
     *
     * @param mode
     */
    public void setSuperEditTextMode(SuperEditTextMode mode, String hint) {
        mNowEditTextMode = mode;
        inputEt.setHint(hint);
        inputEt.setText("");
        initEditText();
    }

    /**
     * 获取编辑框内容
     *
     * @return
     */
    public String getContent() {
        return inputEt.getText().toString();
    }

    /**
     * 是否是验证码模式
     *
     * @return
     */
    public boolean isVerificationMode() {
        if (mNowEditTextMode == SuperEditTextMode.SUPER_VERIFICATION) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 置换密码  密文或者明文
     *
     * @param pwdIsSecret
     */
    private void initPwd(Boolean pwdIsSecret) {
        //显示明文  密文
        inputEt.setTransformationMethod(pwdIsSecret ? PasswordTransformationMethod.getInstance() : HideReturnsTransformationMethod.getInstance());
        inputEt.setSelection(contant.length());
        if (pwdIsSecret) {
            hideOrShow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eye_close_gray));
        } else {
            hideOrShow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_eye_open_gray));
        }
    }

    SuperEditTextListener superEditTextListener;

    public void setSuperEditTextListener(SuperEditTextListener textOnchange) {

        this.superEditTextListener = textOnchange;

    }

    private boolean isHaveGetVerificayionImg;

    private void initCountDownTimer() {

        if (timer != null) {
            return;
        }
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long temp = millisUntilFinished / 1000;
                if (temp != 0) {
                    getVerificationCode.setText(millisUntilFinished / 1000 + "s");
                }
            }

            @Override
            public void onFinish() {
                getVerificationCode.setEnabled(true);
                isHaveGetVerificayionImg = false;
                getVerificationCode.setText(context.getString(R.string.login_get_verification_code));
            }
        };

    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void exchangeVerificationStatus(boolean isPass) {
        getVerificationCode.setEnabled(isPass);
    }


    private int PHONE_NUMBER_LENGTH = 11;
    private int ID_NUMBER_LENGTH = 18;

    /**
     * 目前 要求 密码符合6位即可
     *
     * @param pwd 密码文本
     * @return is success
     */
    private boolean passwordStandard(String pwd) {
        return !TextUtils.isEmpty(pwd);
    }

    /**
     * 目前只要 校验 是否是 手机号 或者 是否是身份证号  11 或 18
     *
     * @param account
     * @return is success
     */
    private boolean accountStandard(String account) {
        return account.length() == PHONE_NUMBER_LENGTH || account.length() == ID_NUMBER_LENGTH;
    }


    /**
     * 是否是验证码
     *
     * @param verification
     * @return
     */
    private boolean isVerificationCode(String verification) {
        return verification.length() == verificationLength;
    }

    private boolean isPhoneNumber(String phoneNumber) {
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表下一位为数字可以是几，"[0-9]"代表可以为0-9中的一个，"[5,7,9]"表示可以是5,7,9中的任意一位,[^4]表示除4以外的任何一个,\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(phoneNumber))
            return false;
        else
            return phoneNumber.matches(telRegex);

    }

    public interface SuperEditTextListener {
        /**
         * 输入框文本发生变化
         *
         * @param view       edittext
         * @param isStandard 输入问题是否符合规范
         * @param text       对应文本
         */
        void onChanged(View view, boolean isStandard, String text);

        /**
         * 显示 图形验证码 dialog
         */
        void getImgVerificationCode(View v);

    }

    /**
     * dip转化像素
     *
     * @param dipValue
     * @return
     */
    public int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);

    }
}
