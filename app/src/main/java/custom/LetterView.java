package custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.jason.textindicatordemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 2017/1/3.
 */

public class LetterView extends View {
    private int white = Color.parseColor("#FFFFFF");
    private int grey = Color.parseColor("#B3393A3F");
    private int green = Color.parseColor("#ff99cc00");
    private int dWidth = 60;
    private int textSize = 40;
    private int textSpace = 10;
    private int textHeight;
    private int selectColor = green;
    private int defaultTextColor=grey;
    private int touchTextColor=white;
    private int touchBgColor=grey;

    private STATE state = STATE.DEFAULT;

    private Paint mPaint;

    private RectF oval1;
    private RectF oval2;
    private Rect bound;

    private int currentPos;

    private List<Character> letters;

    private LetterOnClickListener mListener;

    public LetterView(Context context) {
        this(context, null);
    }

    public LetterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLetters();
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.LetterView);
        int attrCount = attributes.getIndexCount();
        for (int i = 0; i < attrCount; i++) {
            int attr = attributes.getIndex(i);
            switch (attr) {
                case R.styleable.LetterView_textSize:
                    textSize = attributes.getDimensionPixelSize(R.styleable.LetterView_textSize, 40);
                    break;
                case R.styleable.LetterView_textSpace:
                    textSpace = attributes.getDimensionPixelSize(R.styleable.LetterView_textSpace, 10);
                    break;
                case R.styleable.LetterView_dWidth:
                    dWidth = attributes.getDimensionPixelSize(R.styleable.LetterView_dWidth, 60);
                    break;
                case R.styleable.LetterView_selectColor:
                    selectColor = attributes.getColor(R.styleable.LetterView_selectColor, green);
                    break;
                case R.styleable.LetterView_touchBgColor:
                    touchBgColor=attributes.getColor(R.styleable.LetterView_touchBgColor,grey);
                    break;
                case R.styleable.LetterView_touchTextColor:
                    touchTextColor=attributes.getColor(R.styleable.LetterView_touchTextColor,white);
                    break;
                case R.styleable.LetterView_defaultTextColor:
                    defaultTextColor=attributes.getColor(R.styleable.LetterView_defaultTextColor,grey);
                    break;
            }
        }
        attributes.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        bound = new Rect();
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(String.valueOf(letters.get(0)), 0, 1, bound);//测量字母的高度
        textHeight = bound.height();

        oval1 = new RectF(0, 0, dWidth, dWidth);// 画圆弧或者扇形外面的方形轮廓，扫描测量  这里需要绘制半圆所以轮廓是正方形
        oval2 = new RectF(0, 26 * textHeight + textSpace * 25, dWidth, dWidth + 26 * textHeight + textSpace * 25);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = dWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 26 * textHeight + dWidth + textSpace * 25;//设置字母高度*26+字母间隔高度*25+上下2个半圆
        }
        setMeasuredDimension(width, height);//设置高度
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x;
        int y;
        switch (state) {
            case DEFAULT://默认状态  透明背景+灰色字母
                mPaint.setColor(defaultTextColor);
                mPaint.setTextSize(textSize);
                y = dWidth/2 + textHeight;//绘制字母的y方向起始位置
                for (int i = 0; i < 26; i++) {
                    mPaint.getTextBounds(Character.toString(letters.get(i)), 0, 1, bound);//测量字母 获取字母的宽度
                    x = (getMeasuredWidth() - bound.width()) / 2;//绘制字母的x方向起始位置
                    canvas.drawText(Character.toString(letters.get(i)), x, y, mPaint);
                    y += textSpace + textHeight;
                }
                break;
            case TOUCH://选中状态 灰色背景+白色字母+选中绿色字母
                mPaint.setColor(touchBgColor);
                canvas.drawArc(oval1, 180, 180, true, mPaint);// 画弧，参数1是RectF，参数2是角度的开始，参数3是多少度，参数4为true时画扇形，为false时画弧线
                canvas.drawRect(0, dWidth/2, dWidth, 26 * textHeight + dWidth/2 + textSpace * 25, mPaint);
                canvas.drawArc(oval2, 0, 180, true, mPaint);

                mPaint.setColor(touchTextColor);
                mPaint.setTextSize(textSize);//设置字母的大小

                y = dWidth/2 + textHeight;
                for (int i = 0; i < 26; i++) {
                    mPaint.getTextBounds(Character.toString(letters.get(i)), 0, 1, bound);
                    x = (getMeasuredWidth() - bound.width()) / 2;
                    if (i == currentPos) {
                        mPaint.setColor(selectColor);//如果当前字母被选中 显示绿色
                    } else {
                        mPaint.setColor(white);//如果当前字母未被选中 显示白色
                    }
                    canvas.drawText(Character.toString(letters.get(i)), x, y, mPaint);
                    y += textSpace + textHeight;
                }
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//View点击监听事件
        super.onTouchEvent(event);
        int action = event.getAction();//获取屏幕触摸事件动作
        float y;
        switch (action) {
            case MotionEvent.ACTION_UP:
                state = STATE.DEFAULT;//恢复默认状态
                invalidate();//刷新view
                break;
            case MotionEvent.ACTION_DOWN://按下选中状态
            case MotionEvent.ACTION_MOVE://滑动选中状态
                y = event.getY();
                if (y <= textHeight + dWidth/2) {//如果y滑动高度超过view的Top时是负的，所以需要判断
                    currentPos = 0;
                } else if (y >= getMeasuredHeight() - dWidth/2) {//如果y滑动高度超过view的字母栏最高位置时，防止数组越界，所以需要判断
                    currentPos = letters.size() - 1;
                } else {
                    y = y - textHeight - dWidth/2;//去掉字母A高度和上半圆高度
                    currentPos = (int) (y / (textHeight + textSpace)) + 1;//因为去掉了字母A 所以这里需要补1
                }
                state = STATE.TOUCH;//设置为选中状态
                invalidate();//刷新view
                if (mListener != null) {
                    mListener.onItemClickListener(currentPos, letters.get(currentPos));//设置监听
                }
                break;
        }
        return true;
    }

    public void initLetters() {
        letters = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            letters.add(c);
        }
    }

    private enum STATE {//状态枚举类
        TOUCH, DEFAULT
    }

    public void setListener(LetterOnClickListener mListener) {
        this.mListener = mListener;
    }

    public interface LetterOnClickListener {
        void onItemClickListener(int position, char letter);
    }

}
