package net.ee.pfanalyzer.math.coordinate;

public interface ICoordinateConverter {

	/**
	 * Returns the screen coordinate x for the given latitude.
	 * @param E the latitude
	 * @return screen coordinate x
	 */
	int getX(double E);
	
	/**
	 * Returns the screen coordinate y for the given longitude.
	 * @param N the longitude
	 * @return screen coordinate y
	 */
	int getY(double N);
	
	/**
	 * Returns the latitude for the given screen coordinate x.
	 * @param x screen coordinate x
	 * @return the latitude
	 */
	double getLatitude(int x);
	
	/**
	 * Returns the longitude for the given screen coordinate y.
	 * @param y screen coordinate y
	 * @return the longitude
	 */
	double getLongitude(int y);
}
