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
    private String[] str;
    private final int[] colors = new int[]{Color.GREEN,Color.RED,Color.YELLOW,Color.BLACK,Color.BLUE,Color.GRAY,Color.TRANSPARENT};


    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }

    /**
     * 初始化方法
     */
    private void init(Context context,AttributeSet attrs) {

        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.FILL);
        arcPaint.setAntiAlias(true);
        arcRectF = new RectF(10,10,300,300);

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //TODO 绘制饼状图
        canvas.drawColor(Color.WHITE);
        if(data[0]==-1){
            canvas.drawText("暂无数据！", 60, 35, textPaint);
        }else {
            float startAngle = 0;
            float sweepAngle = 0;
            //循环画出占有率
            for (int i = 0; i < data.length; i++) {
                sweepAngle = 360 * data[i];
                arcPaint.setColor(colors[i]);
                canvas.drawArc(arcRectF, startAngle, sweepAngle, true, arcPaint);
                startAngle += sweepAngle;
            }
            int height = 310;
            //循环画图
            for (int i = 0; i < data.length; i++) {
                height += 50;
                arcPaint.setColor(colors[i]);
                canvas.drawRect(10, height, 50, height + 40, arcPaint);
                canvas.drawText(str[i], 60, height + 35, textPaint);
            }
        }
    }
    /**
     * 设置数据
     */
    public void setData(float[] data,String[] arrTitle)
    {
        this.data = data;
        this.str = arrTitle;
        postInvalidate();
    }

}
