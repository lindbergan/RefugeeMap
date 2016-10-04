package dat255.refugeemap.model;

public class DistanceCalculator
{
	private static final double EARTH_RADIUS_KM = 6372.8;

	// versin(a) = 1 - cos(a) = 2sin^2(a/2)
	// haversin(a) = versin(a)/2 = sin(a/2)^2
	private static double haversine(double radians)
	{ return Math.pow(Math.sin(radians/2), 2); }

	/**
	 * Returns the great-circle distance (in km) between the points A
	 * (with latitude {@code lat1}° and longitude {@code lon1}°) and B
	 * (with latitude {@code lat2}° and longitude {@code lon2}°).
	 *
	 * (This method is based on the <i>haversine formula</i>,
	 * which is meant for relatively small distances.)
	 *
	 * Preconditions:
	 * - The two points are located relatively close on Earth
	 *   (preferably in the same country)
	 *
	 * @throws IllegalArgumentException If the formula fails as a result of the
	 * 	points being at nearly opposite ends of the Earth
	 */
	public static double getGreatCircleDistance(double lat1, double lon1,
		double lat2, double lon2)
	{
		final double rlat1 = Math.toRadians(lat1),
			rlat2 = Math.toRadians(lat2),
			rlon1 = Math.toRadians(lon1),
			rlon2 = Math.toRadians(lon2);

		// hav(lat2-lat1) + cos(lat1) cos(lat2) hav(lon2-lon1)
		final double h = haversine(rlat2-rlat1) + (Math.cos(rlat1) *
			Math.cos(rlat2) * haversine(rlon2-rlon1));

		// This should only happen when the two points are on nearly opposite
		// sides of the Earth, which should obviously never occur
		if (h > 1) throw new IllegalArgumentException("The distance" +
			"between the two points are too large");

		return (2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(h)));
	}
}