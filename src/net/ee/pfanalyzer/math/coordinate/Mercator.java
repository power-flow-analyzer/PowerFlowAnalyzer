package net.ee.pfanalyzer.math.coordinate;


public class Mercator implements ICoordinateConverter {

	public final static double earth_radius = 6371000;
	
	public int getX(double E) {
		return (int) Math.round( E * earth_radius * Math.PI / 180 );
	}

	public int getY(double N) {
		return (int) Math.round( Math.log(Math.tan(N * Math.PI / 360 + Math.PI / 4)) * earth_radius );
	}
	
	public double getLatitude(int x) {
		return x * 180 / (earth_radius * Math.PI);
	}
	
	public double getLongitude(int y) {
		return 2 * (Math.atan(Math.exp(y / earth_radius)) - Math.PI / 4) / Math.PI * 180;
	}

	public static void main(String[] args) {
		Mercator cc = new Mercator();
		System.out.println("X=" + cc.getX(12));
		System.out.println("Y=" + cc.getY(54));
		System.out.println("X=" + cc.getX(14));
		System.out.println("Y=" + cc.getY(50));
		System.out.println("X=" + cc.getX(6));
		System.out.println("Y=" + cc.getY(51));
//		System.out.println("X=" + cc.getX(-8.578056));
//		System.out.println("Y=" + cc.getY(-53.544167));
//		System.out.println("X=" + cc.getX(8.578056));
//		System.out.println("Y=" + cc.getY(53.544167));
//		System.out.println("Latitude=" + cc.getLatitude(953836));
//		System.out.println("Longitude=" + cc.getLongitude(7076368));
	}
}
