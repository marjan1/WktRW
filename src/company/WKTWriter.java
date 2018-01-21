package company;

import com.sinergise.geometry.*;

import java.io.IOException;
import java.util.Iterator;

public class WKTWriter {

    private static final String EMPTY = " EMPTY";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String OPEN_BRACKET_WITH_SPACE = " (";
    private static final String COMMA_WITH_SPACE = ", ";
    private static final String CLOSE_BRACKET_WITH_COMMA_AND_SPACE = "), ";

    /**
     * Transforms the input Geometry object into WKT-formatted String. e.g.
     * <pre><code>
     * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
     * //returns "LINESTRING (30 10, 10 30, 40 40)"
     * </code></pre>
     * <p>
     * write(new Point(30, 10)) returns "POINT (30 10)"
     * <p>
     * write(new LineString()) returns "LINESTRING EMPTY"
     * <p>
     * write(new GeometryCollection<Geometry>(new Geometry[]{new Point(4,6), new LineString(new double[] {4,6,7,10})}))
     * returns "GEOMETRYCOLLECTION (POINT (4 6), LINESTRING (4 6, 7 10))"
     */
    public String write(Geometry geom) throws IOException {

        if (geom.isEmpty()) {
            return geom.getClass().getSimpleName().toUpperCase() + EMPTY;
        }

        if (geom instanceof MultiPolygon) {
            return getMultiPolygonString(geom);
        }
        if (geom instanceof MultiPoint) {
            return getMultiPointString(geom);
        }
        if (geom instanceof MultiLineString) {
            return getMultiLineString(geom);
        }
        if (geom instanceof GeometryCollection) {
            return getGeometryCollectionString(geom);
        }
        if (geom instanceof LineString) {

            return concat(geom, getLineStringCoordinates((LineString) geom));
        }
        if (geom instanceof Point) {
            return concat(geom, getPointCoordinates((Point) geom));

        }
        if (geom instanceof Polygon) {
            return concat(geom, OPEN_BRACKET, getPolygonCoordinates(((Polygon) geom)), CLOSE_BRACKET);
        }
        throw new IOException("Parsing not good");
    }

    private String getGeometryCollectionString(Geometry geom) throws IOException {
        Iterator it = ((GeometryCollection) geom).iterator();
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + OPEN_BRACKET_WITH_SPACE;
        while (it.hasNext()) {
            returnValue = returnValue + write((Geometry) it.next()) + COMMA_WITH_SPACE;
        }
        return removeLastTwoChars(returnValue) + CLOSE_BRACKET;
    }

    private String getMultiLineString(Geometry geom) {
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + OPEN_BRACKET_WITH_SPACE;

        for (int i = 0; i < ((MultiLineString) geom).size(); i++) {
            returnValue = returnValue + getLineStringCoordinates(((MultiLineString) geom).get(i)) + COMMA_WITH_SPACE;
        }
        return removeLastTwoChars(returnValue) + CLOSE_BRACKET;
    }

    private String getMultiPointString(Geometry geom) {
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + OPEN_BRACKET_WITH_SPACE;
        for (int i = 0; i < ((MultiPoint) geom).size(); i++) {
            returnValue = returnValue + getPointCoordinates(((MultiPoint) geom).get(i)) + COMMA_WITH_SPACE;
        }
        return removeLastTwoChars(returnValue) + CLOSE_BRACKET;
    }

    private String getMultiPolygonString(Geometry geom) {
        String returnValue = geom.getClass().getSimpleName().toUpperCase() + OPEN_BRACKET_WITH_SPACE;
        for (int i = 0; i < ((MultiPolygon) geom).size(); i++) {
            returnValue = returnValue + OPEN_BRACKET + getPolygonCoordinates(((MultiPolygon) geom).get(i)) + CLOSE_BRACKET_WITH_COMMA_AND_SPACE;
        }
        return removeLastTwoChars(returnValue) + CLOSE_BRACKET;
    }

    private String concat(Geometry geometry, String... parts) {

        return geometry.getClass().getSimpleName().toUpperCase() + " " + String.join("", parts);
    }


    private String getPolygonCoordinates(Polygon geom) {
        if (geom.isEmpty()) {
            return geom.getClass().getSimpleName().toUpperCase() + EMPTY;
        }
        String returnValue = getLineStringCoordinates(geom.getOuter());
        if (geom.getNumHoles() > 0) {
            for (int i = 0; i < geom.getNumHoles(); i++) {
                returnValue = returnValue + COMMA_WITH_SPACE + getLineStringCoordinates(geom.getHole(i));
            }
        }
        return returnValue;
    }

    private String getPointCoordinates(Point geom) {
        if (geom.isEmpty()) {
            return geom.getClass().getSimpleName().toUpperCase() + EMPTY;
        }
        return OPEN_BRACKET + (int) geom.getX() + " " + (int) geom.getY() + CLOSE_BRACKET;
    }

    private String getLineStringCoordinates(LineString geom) {
        if (geom.isEmpty()) {
            return geom.getClass().getSimpleName().toUpperCase() + EMPTY;
        }
        String returnValue = OPEN_BRACKET;
        for (int i = 0; i < geom.getNumCoords(); i++) {
            returnValue = returnValue + (int) geom.getX(i) + " ";
            returnValue = returnValue + (int) geom.getY(i) + COMMA_WITH_SPACE;
        }
        return removeLastTwoChars(returnValue) + CLOSE_BRACKET;
    }

    private CharSequence removeLastTwoChars(String returnValue) {
        if (returnValue.length() > 2) {
            return returnValue.subSequence(0, returnValue.length() - 2);
        }
        return returnValue.subSequence(0, returnValue.length() - 1);
    }


}
