package com.witt.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by IDEA
 * user:witt
 * date:15-4-9
 */

public class ChartView extends View {

    /**
     * 绘制椭圆的样式
     */
    private Paint arcPaint;
    /**
     * 文本绘制的样式
     */
    private Paint textPaint;
    /**
     * 图片绘制的位置
     */
    private RectF arcRectF;

    /**
     * 控件中的数据
     */
    private float[] data;
    private String[] arrTitle;
    private final int[] arrColor = new int[]{Color.GREEN,Color.RED,Color.YELLOW,Color.BLACK,Color.BLUE,Color.GRAY,Color.TRANSPARENT};

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 私有初始化方法
     */
    private void init()
    {
        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setAntiAlias(true);
        arcRectF = new RectF(10,10,300,300);

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
    }

    /**
     * 绘制饼图
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //TODO 绘制饼图
        if(data==null || arrTitle==null) {
            canvas.drawColor(Color.WHITE);
            //循环绘制支出情况的比例
            float startAngle = 0;
            float sweepAngle = 0;
            for (int i = 0; i < data.length; i++) {
                sweepAngle = 360 * data[i] / 100;
                arcPaint.setColor(arrColor[i]);
                canvas.drawArc(arcRectF, startAngle, sweepAngle, true, arcPaint);
                startAngle += sweepAngle;
            }
            //循环绘制各种颜色代表的支出类型

            int height = 310;
            for (int i = 0; i < data.length; i++) {
                height += 50;
                arcPaint.setColor(arrColor[i]);
                canvas.drawRect(10, height, 50, height + 40, arcPaint);
                canvas.drawText(arrTitle[i], 60, height + 35, textPaint);
            }
        }
    }

    /**
     * 设置数据
     */
    public void setData(float[] data,String[] arrTitle)
    {
        this.data = data;
        this.arrTitle = arrTitle;
    }

}
