package com.huinong.truffle.payment.order.mono.util;


import java.math.BigDecimal;

/**
 * 科学计算数字的帮助类
 * @author peng
 *
 */
public class MathUtils {
	/**
	 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精 确的浮点数运算，包括加减乘除和四舍五入。
	 */
	// 默认除法运算精度 (保留小数点)
	private static final int DEF_DIV_SCALE = 2;

	/**
	 * 两个数相乘 d1*d2，预设为四舍五入; 精度预设为2位小数;
	 * 
	 * @param d1
	 *            数1
	 * @param d2
	 *            数2
	 * @return 精度为2位小数的数;
	 * @author lgx
	 * @date 创建时间：2014年3月5日 下午8:59:44
	 * 
	 */
	public static double mulDouble(double d1, double d2)
	{

		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		BigDecimal r = b1.multiply(b2).setScale(DEF_DIV_SCALE,
				BigDecimal.ROUND_HALF_UP);
		return r.doubleValue();
	}

	/**
	 * 两个数相加 d1+d2，预设为四舍五入; 精度预设为2位小数;
	 * 
	 * @param d1
	 *            数1
	 * @param d2
	 *            数2
	 * @return d1和d2的和，精度为2位小数
	 * @author lgx
	 * @date 创建时间：2014年3月5日 下午8:58:11
	 * 
	 */
	public static double addDouble(double d1, double d2)
	{

		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		BigDecimal r = b1.add(b2).setScale(DEF_DIV_SCALE,
				BigDecimal.ROUND_HALF_UP);
		return r.doubleValue();
	}

	/**
	 * 两个数相减 d1-d2，预设为四舍五入; 精度预设为2位小数;
	 * 
	 * @author lgx
	 * @date 创建时间：2014年6月23日 上午10:27:32
	 * @param d1
	 * @param d2
	 * @return
	 * 
	 */
	public static double minusDouble(double d1, double d2)
	{
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		BigDecimal r = b1.subtract(b2).setScale(DEF_DIV_SCALE,
				BigDecimal.ROUND_HALF_UP);
		return r.doubleValue();
	}

	/**
	 * 两个数相除d1/d2,预设为四舍五入; 精度预设为2位小数;
	 * 
	 * @author lgx
	 * @date 创建时间：2014年6月25日 下午3:36:12
	 * @param d1
	 * @param d2
	 * @return
	 * 
	 */
	public static double divideDouble(double d1, double d2)
	{
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		BigDecimal r = b1.divide(b2).setScale(DEF_DIV_SCALE,
				BigDecimal.ROUND_HALF_UP);
		return r.doubleValue();
	}
}
