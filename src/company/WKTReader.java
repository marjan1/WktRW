package company;

import com.sinergise.geometry.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WKTReader {

    private static final String EMPTY = "EMPTY";
    private static final String GEOMETRYCOLLECTION = "GEOMETRYCOLLECTION";
    private static final String LINESTRING = "LINESTRING";
    private static final String MULTILINESTRING = "MULTILINESTRING";
    private static final String MULTIPOINT = "MULTIPOINT";
    private static final String MULTIPOLYGON = "MULTIPOLYGON";
    private static final String POINT = "POINT";
    private static final String POLYGON = "POLYGON";
    private static final char OPEN_BRACKET = '(';
    private static final char CLOSE_BRACKET = ')';
    private static final String COMMA = ",";
    private static final String EMPTY_CHAR = " ";

    /**
     * Transforms the input WKT-formatted String into Geometry object
     * <p>
     * GEOMETRYCOLLECTION (POINT (4 6), MULTIPOLYGON (((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)), ((4 6, 7 10, 4 6))))
     * GEOMETRYCOLLECTION (POINT (4 6), POLYGON ((4 6, 7 10, 4 6), (1 2, 3 4, 1 2), (5 6, 7 8, 5 6)))
     * GEOMETRYCOLLECTION (POINT (4 6), MULTIPOINT ((1 2), (5 6)))
     * MULTILINESTRING ((1 2, 3 4, 1 2), (5 6, 7 8, 5 6))
     */

    public Geometry read(String wktString) throws IOException {

        wktString = wktString.trim();
        String objectPart = wktString.substring(0, wktString.indexOf(EMPTY_CHAR));
        String paramPart = wktString.substring(wktString.indexOf(EMPTY_CHAR) + 1);

        switch (objectPart) {
            case GEOMETRYCOLLECTION: {
                return createGeometryCollectionObject(paramPart);
            }
            case LINESTRING: {
                return createLineStringObject(paramPart);
            }
            case MULTILINESTRING: {
                return createMultiLineStringObject(paramPart);
            }
            case MULTIPOINT: {
                return createMultiPointObject(paramPart);
            }
            case MULTIPOLYGON: {
                return createMultiPolygonObject(paramPart);
            }
            case POINT: {
                return createPointObject(paramPart);
            }
            case POLYGON: {
                return createPolygonObject(paramPart);
            }
            default:
                throw new IOException("String is not in correct format");
        }
    }

    private Polygon createPolygonObject(String paramPart) {
        if ("EMPTY".equals(paramPart))
            return new Polygon();

        paramPart = paramPart.substring(1, paramPart.length() - 1);
        if ("POLYGON EMPTY".equals(paramPart)) {
            return new Polygon();
        }

        String[] stringElements = split(paramPart);
        LineString outer = createLineStringObject(stringElements[0]);
        LineString[] holes = null;

        if (stringElements.length > 1) {
            holes = new LineString[stringElements.length - 1];
            for (int i = 1; i < stringElements.length; i++) {
                holes[i - 1] = createLineStringObject(stringElements[i]);
            }
        }
        return new Polygon(outer, holes);
    }

    private LineString createLineStringObject(String paramPart) {
        paramPart = paramPart.trim();
        if ("LINESTRING EMPTY".equals(paramPart) || "EMPTY".equals(paramPart)) {
            return new LineString();
        }

        if (paramPart.charAt(0) == OPEN_BRACKET) {
            paramPart = paramPart.substring(1);
        }
        if (paramPart.charAt(paramPart.length() - 1) == CLOSE_BRACKET) {
            paramPart = paramPart.substring(0, paramPart.length() - 1);
        }
        paramPart = paramPart.replaceAll(",", "");
        String[] stringElements = paramPart.split(" ");
        List<Double> coords = new ArrayList<>();
        for (String element : stringElements) {
            element = element.trim();
            coords.add(Double.parseDouble(element));
        }
        return new LineString(coords.stream().mapToDouble(Double::doubleValue).toArray());
    }


    private Geometry createMultiLineStringObject(String paramPart) throws IOException {
        if (paramPart.equals(EMPTY)) return new MultiLineString();
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        paramPart = checkNumberOfLineStrings(paramPart);
        List<LineString> lineStringsList = new ArrayList<>();
        if ("LINESTRING".equals(paramPart.split(" ")[0])) {
            lineStringsList.add((LineString) read(paramPart));

        } else {
            String[] parts = paramPart.split(COMMA);
            for (String part : parts) {
                part = part.trim();
                lineStringsList.add(createLineStringObject(part));
            }
        }
        return new MultiLineString(lineStringsList.toArray(new LineString[lineStringsList.size()]));
    }

    private String checkNumberOfLineStrings(String paramPart) {

        StringBuilder stringBuilder = new StringBuilder(paramPart);

        boolean closed = true;

        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '(') {
                closed = false;
            }
            if (stringBuilder.charAt(i) == ')') {
                closed = true;
            }
            if (stringBuilder.charAt(i) == ',' && !closed) {
                stringBuilder.deleteCharAt(i);

            }
        }

        return stringBuilder.toString();

    }


    private Geometry createMultiPointObject(String paramPart) throws IOException {
        if (paramPart.equals(EMPTY)) return new MultiPoint();
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        List<Point> pointsList = new ArrayList<>();
        String[] parts = paramPart.split(COMMA);
        for (String part : parts) {
            part = part.trim();
            if (part.equals("POINT EMPTY")) {
                pointsList.add(new Point());
            } else {
                pointsList.add(createPointObject(part));
            }
        }
        return new MultiPoint(pointsList.toArray(new Point[pointsList.size()]));
    }

    private Geometry createGeometryCollectionObject(String paramPart) throws IOException {
        if (paramPart.equals(EMPTY)) return new GeometryCollection<>();
        List<Geometry> elements = new ArrayList<>();
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = split(paramPart);
        for (String element : stringElements) {
            elements.add(read(element));
        }
        return new GeometryCollection<>(elements);
    }

    private Geometry createMultiPolygonObject(String paramPart) {
        if (paramPart.equals(EMPTY)) return new MultiPolygon();
        paramPart = paramPart.substring(1, paramPart.length() - 1);
        String[] stringElements = split(paramPart);
        Polygon[] polygons = new Polygon[stringElements.length];
        for (int i = 0; i < stringElements.length; i++) {
            polygons[i] = createPolygonObject(stringElements[i].trim());
        }

        return new MultiPolygon(polygons);
    }

    private Point createPointObject(String paramPart) {
        if (paramPart.equals(EMPTY)) {
            return new Point();
        }
        if (paramPart.charAt(0) == OPEN_BRACKET && paramPart.charAt(paramPart.length() - 1) == CLOSE_BRACKET) {
            paramPart = paramPart.substring(1, paramPart.length() - 1);
        }
        String[] stringElements = paramPart.split(EMPTY_CHAR);
        return new Point(Double.parseDouble(stringElements[0].trim()), Double.parseDouble(stringElements[1].trim()));
    }


    private String[] split(String string) {
        int lastIndex = 0;
        int level = 0;
        List<String> result = new LinkedList<>();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == OPEN_BRACKET) level++;
            if (string.charAt(i) == CLOSE_BRACKET) level--;
            if (string.charAt(i) == ',' && level == 0) {
                result.add(string.substring(lastIndex, i));
                lastIndex = i + 1;
            }
        }
        result.add(string.substring(lastIndex, string.length()));
        return result.toArray(new String[result.size()]);
    }


}
