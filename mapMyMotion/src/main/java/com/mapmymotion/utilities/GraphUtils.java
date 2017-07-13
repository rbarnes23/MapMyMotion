package com.mapmymotion.utilities;

import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import com.mapmymotion.pojo.Motion;

import android.graphics.Color;

public class GraphUtils {

	public static XYSeriesRenderer createXYSeriesRenderer(int color) {
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setLineWidth(1);
		renderer.setColor(Color.BLUE);
		FillOutsideLine fill = new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ABOVE);
		fill.setColor(color);
		renderer.addFillOutsideLine(fill);
		// Include low and max value
		renderer.setDisplayBoundingPoints(true);
		// we add point markers
		//renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setPointStrokeWidth(15);
		renderer.setChartValuesTextSize(15); 
		renderer.setDisplayChartValues(false);
		renderer.setFillPoints(false);

		return renderer;
	}

	// private static XYSeries[] createXYDataSet(String title,
	// ArrayList<Motion> motionList) {
	//
	// XYSeries seriesX = new XYSeries(title);
	// XYSeries seriesY = new XYSeries(title);
	//
	// int size=motionList.size();
	// //for (Motion motion : motionList) {
	// for (int i =0; i<size;i++) {
	// seriesX.add(i,motionList.get(i).getCurrentTime());
	// seriesY.add(i,motionList.get(i).getCurrentDistance());
	// }
	// XYSeries[2] series;
	// series[0]=seriesX;
	// series[1]=seriesY;
	//
	// return series;
	// }
	public static XYSeries createXYSeries(String title) {
		XYSeries xySeries= new XYSeries(title);
		return xySeries;
	}

	public static TimeSeries createTimeSeries(String title) {
		TimeSeries timeSeries= new TimeSeries(title);
		return timeSeries;
	}
	
	
	public static XYSeries createXYSeries(String title, List<Motion> motionList) {
		XYSeries xySeries = new XYSeries(title);
		int size = motionList.size();
		// for (Motion motion : motionList) {
		for (int i = 0; i < size; i++) {
			// seriesX.add(i, motionList.get(i).getCurrentTime());
			Double d = motionList.get(i).getCurrentDistance();
			int distance = d.intValue();
			xySeries.add(i, distance);
		}
		return xySeries;
	}
	public static XYMultipleSeriesDataset createXYSeriesDataSet(XYSeries xySeries) {
//		xySeries=createXYSeries(titleY, motionList);
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		// dataset.addSeries(seriesX);
		dataset.addSeries(xySeries);
		return dataset;
	}
	public static XYMultipleSeriesDataset createXYSeriesDataSet(TimeSeries timeSeries) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(timeSeries);
		return dataset;
	}

	
	public static XYMultipleSeriesRenderer createRendererForEachSeries(
			XYSeriesRenderer renderer,String chartTitle,String XTitle,String YTitle,double min,double max) {
		XYMultipleSeriesRenderer xySeriesRenderer = new XYMultipleSeriesRenderer();
		xySeriesRenderer.addSeriesRenderer(renderer);
		// xySeriesRenderer.addSeriesRenderer(renderer);
		// We want to avoid black border
		xySeriesRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent
		xySeriesRenderer.setXTitle(XTitle);
		xySeriesRenderer.setYTitle(YTitle);
		xySeriesRenderer.setChartTitle(chartTitle);
		xySeriesRenderer.setChartTitleTextSize(42);
		xySeriesRenderer.setBackgroundColor(Color.YELLOW);
		xySeriesRenderer.setAxisTitleTextSize(32);
		xySeriesRenderer.setGridColor(Color.BLACK);
		xySeriesRenderer.setLabelsColor(Color.BLUE);
		xySeriesRenderer.setAxesColor(Color.GREEN);
		xySeriesRenderer.setLabelsTextSize(18);
		xySeriesRenderer.setXLabelsAngle(45);
		xySeriesRenderer.setYLabelsAngle(45);
		// margins
		// Disable Pan on Y axis
		xySeriesRenderer.setPanEnabled(true, true);

		// xySeriesRenderer.setXAxisMax(100000);
		xySeriesRenderer.setYAxisMin(min);
		xySeriesRenderer.setYAxisMax(max);
	//	xySeriesRenderer.setYAxisMin(0);
		xySeriesRenderer.setShowGrid(false); // we show the grid
		xySeriesRenderer.setZoomButtonsVisible(true);
	//	xySeriesRenderer.setBarSpacing(.1);
	//	xySeriesRenderer.setBarWidth(20);
		return xySeriesRenderer;
	}

//	public static GraphicalView createLineChart(Context context, String titleX,
//			String titleY, ArrayList<Motion> motionList) {
//		XYSeries xySeries = createXYSeries(titleY, motionList);
//		// Create the data
//		XYMultipleSeriesDataset dataset = createXYSeriesDataSet(titleX, titleY,
//				xySeries);
//		XYSeriesRenderer renderer = createRenderer();
//
//		XYMultipleSeriesRenderer seriesRenderer = createRendererForEachSeries(renderer);
//		if (seriesRenderer.isInScroll()) {
//			seriesRenderer.setInScroll(true);
//		}
//		GraphicalView chartView = ChartFactory.getLineChartView(context,
//				dataset, seriesRenderer);
//		return chartView;
//	}
//
//	public static GraphicalView openChart(Context mContext) {
//		String[] mMonth = new String[] { "Jan", "Feb", "Mar", "Apr", "May",
//				"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
//
//		int[] x = { 1, 2, 3, 4, 5, 6, 7, 8 };
//		int[] income = { 2000, 2500, 2700, 3000, 2800, 3500, 3700, 3800 };
//		int[] expense = { 2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400 };
//
//		// Creating an XYSeries for Income
//		XYSeries incomeSeries = new XYSeries("Income");
//
//		// Creating an XYSeries for Expense
//		XYSeries expenseSeries = new XYSeries("Expense");
//		// Adding data to Income and Expense Series
//		for (int i = 0; i < x.length; i++) {
//			incomeSeries.add(x[i], income[i]);
//			expenseSeries.add(x[i], expense[i]);
//		}
//
//		// Creating a dataset to hold each series
//		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//		// Adding Income Series to the dataset
//		dataset.addSeries(incomeSeries);
//		// Adding Expense Series to dataset
//		// dataset.addSeries(expenseSeries);
//
//		// Creating XYSeriesRenderer to customize incomeSeries
//		XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
//		incomeRenderer.setColor(Color.BLUE);
//		incomeRenderer.setPointStyle(PointStyle.CIRCLE);
//		incomeRenderer.setFillPoints(true);
//		incomeRenderer.setLineWidth(12);
//		incomeRenderer.setDisplayChartValues(true);
//
//		// Creating XYSeriesRenderer to customize expenseSeries
//		XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
//		expenseRenderer.setColor(Color.RED);
//		expenseRenderer.setPointStyle(PointStyle.CIRCLE);
//		expenseRenderer.setFillPoints(true);
//		expenseRenderer.setLineWidth(12);
//		expenseRenderer.setDisplayChartValues(true);
//
//		// Creating a XYMultipleSeriesRenderer to customize the whole chart
//		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
//		multiRenderer.setXLabels(0);
//		multiRenderer.setChartTitle("Income vs Expense Chart");
//		multiRenderer.setXTitle("Year 2012");
//		multiRenderer.setYTitle("Amount in Dollars");
//		multiRenderer.setZoomButtonsVisible(true);
//		if (multiRenderer.isInScroll()) {
//			multiRenderer.setInScroll(true);
//		}
//
//		multiRenderer.setInScroll(false);
//		for (int i = 0; i < x.length; i++) {
//			multiRenderer.addXTextLabel(i + 1, mMonth[i]);
//		}
//
//		// Adding incomeRenderer and expenseRenderer to multipleRenderer
//		// Note: The order of adding dataseries to dataset and renderers to
//		// multipleRenderer
//		// should be same
//		multiRenderer.addSeriesRenderer(incomeRenderer);
//		multiRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent
//		// multiRenderer.addSeriesRenderer(expenseRenderer);
//
//		GraphicalView chartView = ChartFactory.getLineChartView(mContext,
//				dataset, multiRenderer);
//		return chartView;
//
//	}

}
