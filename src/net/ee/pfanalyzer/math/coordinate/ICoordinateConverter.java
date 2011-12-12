package net.ee.pfanalyzer.math.coordinate;

public interface ICoordinateConverter {

	/**
	 * Returns the screen coordinate x for the given longitude.
	 * @param E the longitude
	 * @return screen coordinate x
	 */
	int getX(double E);
	
	/**
	 * Returns the screen coordinate y for the given latitude.
	 * @param N the latitude
	 * @return screen coordinate y
	 */
	int getY(double N);
	
	/**
	 * Returns the latitude for the given screen coordinate y.
	 * @param x screen coordinate y
	 * @return the latitude
	 */
	double getLatitude(int y);
	
	/**
	 * Returns the longitude for the given screen coordinate x.
	 * @param y screen coordinate x
	 * @return the longitude
	 */
	double getLongitude(int x);
}
