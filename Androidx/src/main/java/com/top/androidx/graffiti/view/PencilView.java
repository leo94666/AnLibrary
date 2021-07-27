package com.top.androidx.graffiti.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.top.androidx.R;
import com.top.androidx.graffiti.StrokeColor;
import com.top.androidx.graffiti.StrokeType;
import com.top.androidx.superview.CircleView;

import org.jetbrains.annotations.NotNull;

public class PencilView extends ConstraintLayout {


    private Context mContext;
    private CircleView circleView;
    private int defaultSize;


    private OnSelectClickListener onSelectClickListener;


    public PencilView(@NonNull @NotNull Context context) {
        super(context);

        initView(context);
    }

    public PencilView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public PencilView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);

    }

    public PencilView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);

    }


    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sketch_pencil, this);
        RadioGroup rgType = view.findViewById(R.id.rg_type);
        RadioGroup rgColorType = view.findViewById(R.id.rg_color_type);
        circleView = view.findViewById(R.id.iv_seek);
        AppCompatSeekBar seekBar = view.findViewById(R.id.seek_bar);


        //画笔宽度缩放基准参数
        Drawable circleDrawable = getResources().getDrawable(R.drawable.stroke_color_rbtn_red);
        assert circleDrawable != null;
        defaultSize = circleDrawable.getIntrinsicWidth();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSeekBarProgress(progress, StrokeType.STROKE_TYPE_DRAW);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (onSelectClickListener==null){
                return;
            }
            if (checkedId == R.id.stroke_type_pencil) {
                onSelectClickListener.onSelectType(StrokeType.STROKE_TYPE_DRAW);
            } else if (checkedId == R.id.stroke_type_line) {
                onSelectClickListener.onSelectType(StrokeType.STROKE_TYPE_LINE);
            } else if (checkedId == R.id.stroke_type_circle) {
                onSelectClickListener.onSelectType(StrokeType.STROKE_TYPE_CIRCLE);
            } else if (checkedId == R.id.stroke_type_rect) {
                onSelectClickListener.onSelectType(StrokeType.STROKE_TYPE_RECTANGLE);
            }else if (checkedId == R.id.stroke_type_text) {
                onSelectClickListener.onSelectType(StrokeType.STROKE_TYPE_TEXT);
            }
        });


        rgColorType.setOnCheckedChangeListener((group, checkedId) -> {
            if (onSelectClickListener==null){
                return;
            }
            if (checkedId == R.id.stroke_color_white) {
                onSelectClickListener.onSelectColor(StrokeColor.STROKE_COLOR_WHITE);
                circleView.setColor(StrokeColor.STROKE_COLOR_WHITE.getColor());
            }else if (checkedId == R.id.stroke_color_black) {
                onSelectClickListener.onSelectColor(StrokeColor.STROKE_COLOR_BLACK);
                circleView.setColor(StrokeColor.STROKE_COLOR_BLACK.getColor());
            } else if (checkedId == R.id.stroke_color_green) {
                onSelectClickListener.onSelectColor(StrokeColor.STROKE_COLOR_GREEN);
                circleView.setColor(StrokeColor.STROKE_COLOR_GREEN.getColor());
            } else if (checkedId == R.id.stroke_color_yellow) {
                onSelectClickListener.onSelectColor(StrokeColor.STROKE_COLOR_YELLOW);
                circleView.setColor(StrokeColor.STROKE_COLOR_YELLOW.getColor());
            } else if (checkedId == R.id.stroke_color_pueple) {
                onSelectClickListener.onSelectColor(StrokeColor.STROKE_COLOR_PURPLE);
                circleView.setColor(StrokeColor.STROKE_COLOR_PURPLE.getColor());
            } else if (checkedId == R.id.stroke_color_red) {
                onSelectClickListener.onSelectColor(StrokeColor.STROKE_COLOR_RED);
                circleView.setColor(StrokeColor.STROKE_COLOR_RED.getColor());
            }
        });

    }

    private void setSeekBarProgress(int progress, StrokeType strokeTypeDraw) {
        int calcProgress = progress > 1 ? progress : 1;
        if (strokeTypeDraw == StrokeType.STROKE_TYPE_DRAW) {
            circleView.setRadius((float) (calcProgress / 200.0));
            if (onSelectClickListener!=null){
                onSelectClickListener.onSelectStroke(calcProgress);
            }
        }
    }

    public void setOnSelectClickListener(OnSelectClickListener onSelectClickListener) {
        this.onSelectClickListener = onSelectClickListener;
    }

    public interface OnSelectClickListener {
        void onSelectType(StrokeType type);
        void onSelectStroke( int progress);
        void onSelectColor(StrokeColor color);
    }


}
