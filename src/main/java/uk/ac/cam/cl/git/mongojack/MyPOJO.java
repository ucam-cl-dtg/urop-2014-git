/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git.mongojack;

/**
 * @author ird28
 * 
 */
public class MyPOJO {
	
	private int x;
	private int y;
	private double t;
	private String label;
	private String _id;
	
	public MyPOJO(int x0, int y0, double t0, String label0) {
		x = x0;
		y = y0;
		t = t0;
		label = label0;
	}
	
	public MyPOJO() {
		x = 0;
		y = 0;
		t = 0.0;
		label = "This is the default label";
	}
	
	public void incrementX() { x++; }
	public void transfer(int amount) { x -= amount; y += amount; }
	public int evaluateFunction() { int temp = y; y = 0; return x+temp; }
	
	public void setT(double newT) { t = newT; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	public double getT() { return t; }
	public String getLabel() { return label; }
	public String get_id() { return _id; }

}
