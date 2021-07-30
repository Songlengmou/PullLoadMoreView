package com.anningtex.pullloadmoreview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.anningtex.pullloadmoreview.R;

/**
 * @author Song
 * desc:下拉查看更多的自定义View
 */
public class PullLoadMoreView extends LinearLayout {
    /**
     * 顶部view的子控件,由用户添加
     */
    private ViewGroup headLayout;
    /**
     * 滑动器
     */
    private Scroller mScroller;
    /**
     * 是否拦截点击事件
     */
    private boolean mTouchEvent = false;
    /**
     * 手指Y轴滑动的距离
     */
    private float scrollYValue = 0f;
    /**
     * 顶部view的height
     */
    private int subViewHeight = 0;
    private NestedScrollView nestedScrollView;
    /**
     * 阻尼系数
     */
    private double decayRatio = 0.5;
    /**
     * 顶部view的paddingBottom
     */
    private int mPaddingBottom = 0;
    /**
     * 顶部view的PaddingTop
     */
    private int mPaddingTop = 0;
    /**
     * 手指按下的y轴坐标值
     */
    private float mLastY = 0f;
    /**
     * 手指按下的y轴坐标值
     */
    private float mLastX = 0f;
    /**
     * 顶部view的显示状态,默认是关闭状态
     */
    private VIewState stateView = VIewState.CLOSE;
    /**
     * 手势滑动状态
     */
    private TouchStateMove stateMove = TouchStateMove.NORMAL;
    /**
     * 小圆点
     */
    private DotView dotView;
    /**
     * 顶部view的父控件
     */
    private LinearLayout topLayout;
    /**
     * 顶部view的背景颜色
     */
    private int topBackGroundColor;

    public PullLoadMoreView(Context context) {
        super(context);
    }

    public PullLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PullLoadMoreView);
        topBackGroundColor = array.getColor(R.styleable.PullLoadMoreView_top_background_color, Color.GRAY);
        array.recycle();
        setOrientation(VERTICAL);
    }

    /**
     * 头部view
     */
    public ViewGroup addHeadView(int resLayout) {
        headLayout = (ViewGroup) View.inflate(getContext(), resLayout, null);
        headLayout.setVisibility(INVISIBLE);
        buildView();
        return headLayout;
    }

    /**
     * 构建整个view布局
     */
    private void buildView() {
        //子布局第一个必须为NestedScrollView
        nestedScrollView = (NestedScrollView) getChildAt(0);
        //小圆点自定义View
        dotView = new DotView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 50);
        topLayout = new LinearLayout(getContext());
        topLayout.setClickable(true);
        topLayout.setBackgroundColor(topBackGroundColor);
        topLayout.setOrientation(VERTICAL);
        topLayout.post(() -> {
            subViewHeight = topLayout.getHeight();
            mPaddingTop = -subViewHeight;
            int paddingLeft = topLayout.getPaddingLeft();
            int paddingRight = topLayout.getPaddingRight();
            mPaddingBottom = topLayout.getPaddingTop();
            topLayout.setPadding(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom);
        });

        //动态设置的顶部view高度
        LayoutParams layoutParams1 = new LayoutParams(LayoutParams.MATCH_PARENT, 900);
        topLayout.addView(headLayout, layoutParams1);
        //默认的顶部view高度
//        topLayout.addView(headLayout);
        topLayout.addView(dotView, layoutParams);
        addView(topLayout, 0);
    }

    /**
     * 处理触摸拦截事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //按下
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getY();
                mLastX = ev.getX();
                mTouchEvent = false;
                break;
            //滑动
            case MotionEvent.ACTION_MOVE:
                float flX = ev.getX() - mLastX;
                float flY = ev.getY() - mLastY;
                float abs = Math.abs(flY);
                //手指滑动阈值
                int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (abs > scaledTouchSlop) {
                    mTouchEvent = nestedScrollView.getScrollY() == 0;
                    if (flY < 0 && stateView == VIewState.CLOSE) {
                        mTouchEvent = false;
                    }
                    if (stateView == VIewState.OPEN) {
                        if (Math.abs(flX) > abs) {
                            mTouchEvent = false;
                        }
                        //顶部区域打开后不消费事件
                        if (mLastY < subViewHeight) {
                            mTouchEvent = false;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //抬起
                mTouchEvent = false;
                break;
            default:
                break;
        }
        return mTouchEvent;
    }

    /**
     * 触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            //按下
            case MotionEvent.ACTION_DOWN:
                mTouchEvent = true;
                break;
            //滑动
            case MotionEvent.ACTION_MOVE:
                scrollYValue = (event.getY() - mLastY);
                float abs = Math.abs(scrollYValue);
                int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (abs > scaledTouchSlop) {
                    mTouchEvent = true;
                    if (scrollYValue > 0) {
                        if (nestedScrollView.getScrollY() == 0) {
                            if (stateView == VIewState.CLOSE) {
                                //向下滑动但是头部空间没完全显示
                                if (mPaddingTop < 0) {
                                    mPaddingTop = (int) (decayRatio * scrollYValue - subViewHeight);
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
                                    stateMove = TouchStateMove.DOWN_NO_OVER;
                                    dotView.setPercent(1 - ((float) mPaddingTop / (-subViewHeight)));
                                    if (mPaddingTop > -subViewHeight / 2) {
                                        showToDown(headLayout, (long) 200);
                                    }
                                    headLayout.setAlpha(1 - ((float) mPaddingTop / (-subViewHeight)));
                                } else if (mPaddingTop >= 0) {
                                    //头部空间没完全显示依然向下滑动
                                    mPaddingTop = (int) (0.5 * decayRatio * scrollYValue + 0.5 * (-subViewHeight));
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingTop);
                                    stateMove = TouchStateMove.DOWN_OVER;
                                    dotView.setPercent(1 - ((float) mPaddingTop / (-subViewHeight)));
                                    headLayout.setAlpha(1 - ((float) mPaddingTop / (-subViewHeight)));
                                }
                            } else {
                                mPaddingTop = (int) (0.5 * decayRatio * scrollYValue);
                                topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingTop);
                                stateMove = TouchStateMove.DOWN_OVER;
                            }
                        }
                    } else {
                        if (nestedScrollView.getScrollY() == 0) {
                            if (stateView == VIewState.CLOSE) {
                                mPaddingTop = -subViewHeight;
                            } else {
                                mPaddingTop = (int) (decayRatio * scrollYValue);
                                if (mPaddingTop <= -subViewHeight) {
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
                                    mPaddingTop = -subViewHeight;
                                    stateView = VIewState.CLOSE;
                                } else {
                                    topLayout.setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
                                    stateMove = TouchStateMove.UP;
                                }
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起/取消
            case MotionEvent.ACTION_CANCEL:
                if (mPaddingTop > -subViewHeight / 3 && mPaddingTop < 0 && stateMove == TouchStateMove.DOWN_NO_OVER) {
                    moveAnimation(-mPaddingTop, mPaddingTop);
                    stateView = VIewState.OPEN;
                    dotHideAnim();
                }
                if (mPaddingTop <= -subViewHeight / 3 && mPaddingTop < 0 && stateMove == TouchStateMove.DOWN_NO_OVER) {
                    moveAnimation(-mPaddingTop, subViewHeight);
                    stateView = VIewState.CLOSE;
                    headLayout.setVisibility(View.INVISIBLE);
                    dotView.setAlpha(1.0f);
                    dotView.setVisibility(View.VISIBLE);
                }
                if (stateMove == TouchStateMove.DOWN_OVER) {
                    moveAnimation(-mPaddingTop, mPaddingTop);
                    stateView = VIewState.OPEN;
                    dotHideAnim();
                }
                if (stateMove == TouchStateMove.UP) {
                    moveAnimation(-mPaddingTop, subViewHeight);
                    headLayout.setVisibility(View.INVISIBLE);
                    stateView = VIewState.CLOSE;
                    dotView.setAlpha(1.0f);
                    dotView.setVisibility(View.VISIBLE);
                }
                mTouchEvent = false;
                scrollYValue = 0f;
                mLastY = 0f;
                //返回监听回调
                if (listener != null) {
                    listener.onViewState(stateView);
                }
                break;
            default:
                break;
        }
        return mTouchEvent;
    }

    /**
     * view滚动回弹动画
     */
    private void moveAnimation(int startY, int y) {
        mScroller.startScroll(0, startY, 0, y, 400);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            topLayout.setPadding(getPaddingLeft(), -currY, getPaddingRight(), mPaddingBottom);
        }
        //刷新view
        invalidate();
    }

    /**
     * 触摸状态
     * DOWN_NO_OVER 向下滑动但是没有超出view的height值
     * DOWN_OVER 向下滑动并且超出了height值
     * UP 向上滑动
     * NORMAL 无状态
     */
    enum TouchStateMove {
        DOWN_NO_OVER, DOWN_OVER, UP, NORMAL
    }

    /**
     * 顶部view的显示状态
     * CLOSE 顶部为关闭
     * OPEN 顶部为打开状态
     */
    public enum VIewState {
        CLOSE, OPEN
    }

    /**
     * 顶部view向下平移动画
     *
     * @param view
     * @param time 动画时间
     */
    private void showToDown(final View view, Long time) {
        if (view.getVisibility() != View.VISIBLE) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "translationY", -30f, 0f);
            //渐变动画
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator1, alphaAnimator);
            animatorSet.setDuration(time);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    /**
     * 小圆点的隐藏动画
     */
    private void dotHideAnim() {
        ViewPropertyAnimator alpha = dotView.animate().alpha(0f);
        alpha.setDuration(400);
        alpha.start();
        dotView.setVisibility(View.GONE);
        topLayout.setAlpha(1f);
    }

    private ViewStateListener listener;

    public void setViewStateListener(ViewStateListener listener) {
        this.listener = listener;
    }

    public interface ViewStateListener {
        void onViewState(VIewState viewState);
    }
}