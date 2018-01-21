package company;

import com.sinergise.geometry.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestWKTReaderAndWriter {

    private static WKTWriter wktWriter;
    private static WKTReader wktReader;

    @BeforeClass
    public static void setUp() {
        wktWriter = new WKTWriter();
        wktReader = new WKTReader();
    }

    @Test
    public void shouldWriteAndReadPoint() throws IOException {

        //Point
        System.out.println("*POINT*");

        Point point = new Point();
        String pointString = wktWriter.write(point);
        assertEquals("POINT EMPTY", pointString);

        Point pointObject = (Point) wktReader.read(pointString);
        assertEquals(point, pointObject);

        Point point1 = new Point(2, 3);
        String pointString1 = wktWriter.write(point1);
        assertEquals("POINT (2 3)", pointString1);

        Point pointObject1 = (Point) wktReader.read(pointString1);
        assertEquals(point1, pointObject1);

    }

    private void checkLineString(LineString lineString, String lsText) throws IOException {
        String lsWritten = wktWriter.write(lineString);
        assertEquals(lsText, lsWritten);

        LineString lineStringObject = (LineString) wktReader.read(lsWritten);
        assertEquals(lineString, lineStringObject);
    }

    @Test
    public void shouldWriteAndReadLineString() throws IOException {

        checkLineString(new LineString(), "LINESTRING EMPTY");
        checkLineString(new LineString(new double[]{1, 2}), "LINESTRING (1 2)");
        checkLineString(new LineString(new double[]{1, 2, 3, 4}), "LINESTRING (1 2, 3 4)");

        checkLineString(new LineString(new double[]{1, 2, 3, 4}), "LINESTRING (1 2, 3 4)");

        String lsWritten = wktWriter.write(new LineString(new double[]{1, 2, 3, 4, 5}));
        assertEquals("LINESTRING (1 2, 3 4)", lsWritten);

        checkLineString(new LineString(new double[]{1, 2, 3, 4, 5, 6}), "LINESTRING (1 2, 3 4, 5 6)");

        String lsWritten1 = wktWriter.write(new LineString(new double[]{1, 2, 3, 4, 5, 6, 7}));
        assertEquals("LINESTRING (1 2, 3 4, 5 6)", lsWritten1);

        checkLineString(new LineString(new double[]{-1, 0, -3.0d, 4.0d, 5, 6}), "LINESTRING (-1 0, -3 4, 5 6)");

    }

    private void checkMultiLineString(MultiLineString multiLineString, String mlsText) throws IOException {
        String mlsWritten = wktWriter.write(multiLineString);
        assertEquals(mlsText, mlsWritten);

        MultiLineString multiLineStringObject = (MultiLineString) wktReader.read(mlsWritten);
        assertEquals(multiLineString, multiLineStringObject);
    }


    @Test
    public void shouldWriteAndReadMultiLineString() throws IOException {

        checkMultiLineString(new MultiLineString(), "MULTILINESTRING EMPTY");
        MultiLineString mls1 = new MultiLineString(new LineString[]{new LineString()});
        String mlsWritten = wktWriter.write(mls1);
        assertEquals("MULTILINESTRING (LINESTRING EMPTY)", mlsWritten);
        checkMultiLineString(mls1, "MULTILINESTRING (LINESTRING EMPTY)");
        MultiLineString mls2 = new MultiLineString(new LineString[]{new LineString(new double[]{1, 2})});
        checkMultiLineString(mls2, "MULTILINESTRING ((1 2))");
        MultiLineString mls3 = new MultiLineString(new LineString[]{new LineString(new double[]{1, 2}), new LineString(new double[]{1, 2, 3, 4})});
        checkMultiLineString(mls3, "MULTILINESTRING ((1 2), (1 2, 3 4))");
        MultiLineString mls4 = new MultiLineString(
                new LineString[]{new LineString(new double[]{1, 2}), new LineString(new double[]{1, 2, 3, 4}), new LineString(new double[]{1, 2, 3, 4, 5, 6})});

        checkMultiLineString(mls4, "MULTILINESTRING ((1 2), (1 2, 3 4), (1 2, 3 4, 5 6))");

        MultiLineString mls5 = new MultiLineString(new LineString[]{new LineString(new double[]{1, 2}), new LineString(new double[]{1, 2, 3, 4}), new LineString(new double[]{1, 2, 3, 4, 5, 6}), new LineString(new double[]{-1, 0, -3.0d, 4.0d, 5, 6})});
        checkMultiLineString(mls5, "MULTILINESTRING ((1 2), (1 2, 3 4), (1 2, 3 4, 5 6), (-1 0, -3 4, 5 6))");
    }

    @Test
    public void shouldWriteAndReadMultiPoint() throws IOException {
        Point point = new Point();
        Point point1 = new Point(1, 2);

        MultiPoint mp = new MultiPoint();
        checkMultiPoint(mp, "MULTIPOINT EMPTY");
        MultiPoint mp1 = new MultiPoint(new Point[]{point});
        checkMultiPoint(mp1, "MULTIPOINT (POINT EMPTY)");
        MultiPoint mp2 = new MultiPoint(new Point[]{point1});
        checkMultiPoint(mp2, "MULTIPOINT ((1 2))");
        MultiPoint mp3 = new MultiPoint(new Point[]{point, point1});
        checkMultiPoint(mp3, "MULTIPOINT (POINT EMPTY, (1 2))");
        MultiPoint mp4 = new MultiPoint(new Point[]{point1, point1});
        checkMultiPoint(mp4, "MULTIPOINT ((1 2), (1 2))");
        MultiPoint mp5 = new MultiPoint(new Point[]{point, point1, point, point1});
        checkMultiPoint(mp5, "MULTIPOINT (POINT EMPTY, (1 2), POINT EMPTY, (1 2))");
    }

    private void checkMultiPoint(MultiPoint multiPoint, String mpText) throws IOException {
        String mpWritten = wktWriter.write(multiPoint);
        assertEquals(mpText, mpWritten);

        MultiPoint multiPointObject = (MultiPoint) wktReader.read(mpWritten);
        assertEquals(multiPoint, multiPointObject);
    }

    private void checkPolygon(Polygon polygon, String pText) throws IOException {
        String pWritten = wktWriter.write(polygon);
        assertEquals(pText, pWritten);

        Polygon polygonObject = (Polygon) wktReader.read(pWritten);
        assertEquals(polygon, polygonObject);
    }

    @Test
    public void shouldWriteAndReadPolygon() throws IOException {
        LineString lsP = new LineString();
        LineString lsP1 = new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30});
        LineString lsP2 = new LineString(new double[]{30, 40, 45, 45, 40, 30, 30, 40});
        Polygon pl = new Polygon();
        checkPolygon(pl, "POLYGON EMPTY");

        Polygon pl1 = new Polygon(new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}), new LineString[]{lsP1, lsP2});
        checkPolygon(pl1, "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30), (30 40, 45 45, 40 30, 30 40))");

        Polygon pl2 = new Polygon(new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}), new LineString[]{lsP});
        checkPolygon(pl2, "POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), LINESTRING EMPTY)");
    }

    @Test
    public void shouldReadAndWriteMultiPolygon() throws IOException {
        LineString lsP1 = new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30});
        LineString lsP2 = new LineString(new double[]{30, 40, 45, 45, 40, 30, 30, 40});
        Polygon pl = new Polygon();
        Polygon pl1 = new Polygon(new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}), new LineString[]{lsP1, lsP2});

        MultiPolygon mpg = new MultiPolygon();
        checkMultiPolygon(mpg, "MULTIPOLYGON EMPTY");
        MultiPolygon mpg1 = new MultiPolygon(new Polygon[]{pl, pl1});
        checkMultiPolygon(mpg1, "MULTIPOLYGON ((POLYGON EMPTY), ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30), (30 40, 45 45, 40 30, 30 40)))");
        MultiPolygon mpg2 = new MultiPolygon(new Polygon[]{pl1, pl1});
        checkMultiPolygon(mpg2, "MULTIPOLYGON (((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30), (30 40, 45 45, 40 30, 30 40)), ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30), (30 40, 45 45, 40 30, 30 40)))");

    }

    private void checkMultiPolygon(MultiPolygon multiPolygon, String mpText) throws IOException {
        String mpWritten = wktWriter.write(multiPolygon);
        assertEquals(mpText, mpWritten);

        MultiPolygon multiPolygonObject = (MultiPolygon) wktReader.read(mpWritten);
        assertEquals(multiPolygon, multiPolygonObject);
    }


    @Test
    public void shouldReadAndWriteGeometryObject() throws IOException {
        Point point = new Point();
        Point point1 = new Point(1, 2);


        LineString ls = new LineString();
        LineString ls1 = new LineString(new double[]{1, 2});
        LineString ls2 = new LineString(new double[]{1, 2, 3, 4});
        LineString ls3 = new LineString(new double[]{1, 2, 3, 4, 5});
        LineString ls4 = new LineString(new double[]{1, 2, 3, 4, 5, 6});
        LineString ls5 = new LineString(new double[]{1, 2, 3, 4, 5, 6, 7});
        LineString ls6 = new LineString(new double[]{-1, 0, -3.0d, 4.0d, 5, 6});

        MultiLineString mls = new MultiLineString();
        MultiLineString mls1 = new MultiLineString(new LineString[]{ls});
        MultiLineString mls2 = new MultiLineString(new LineString[]{ls1});
        MultiLineString mls3 = new MultiLineString(new LineString[]{ls1, ls2});
        MultiLineString mls4 = new MultiLineString(new LineString[]{ls1, ls2, ls4});
        MultiLineString mls5 = new MultiLineString(new LineString[]{ls1, ls2, ls4, ls6});

        MultiPoint mp = new MultiPoint();
        MultiPoint mp1 = new MultiPoint(new Point[]{point});
        MultiPoint mp2 = new MultiPoint(new Point[]{point1});
        MultiPoint mp3 = new MultiPoint(new Point[]{point, point});
        MultiPoint mp4 = new MultiPoint(new Point[]{point1, point1});
        MultiPoint mp5 = new MultiPoint(new Point[]{point, point1, point, point1});

        //Polygon
        LineString lsP = new LineString();
        LineString lsP1 = new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30});
        LineString lsP2 = new LineString(new double[]{30, 40, 45, 45, 40, 30, 30, 40});
        Polygon pl = new Polygon();
        Polygon pl1 = new Polygon(new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}), new LineString[]{lsP1, lsP2});
        Polygon pl2 = new Polygon(new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}), new LineString[]{lsP});


        //MultiPolygon
        MultiPolygon mpg = new MultiPolygon();
        MultiPolygon mpg1 = new MultiPolygon(new Polygon[]{pl, pl1});
        MultiPolygon mpg2 = new MultiPolygon(new Polygon[]{pl1, pl1});


        //GeometryCollection
        GeometryCollection gc = new GeometryCollection();
        checkGeometryCollection(gc, "GEOMETRYCOLLECTION EMPTY");
        GeometryCollection gc1 = new GeometryCollection(new Geometry[]{});
        checkGeometryCollection(gc1, "GEOMETRYCOLLECTION EMPTY");
        GeometryCollection gc2 = new GeometryCollection(new Geometry[]{point});
        checkGeometryCollection(gc2, "GEOMETRYCOLLECTION (POINT EMPTY)");
        GeometryCollection gc3 = new GeometryCollection(new Geometry[]{point1});
        checkGeometryCollection(gc3, "GEOMETRYCOLLECTION (POINT (1 2))");
        GeometryCollection gc4 = new GeometryCollection(new Geometry[]{point, point1});
        checkGeometryCollection(gc4, "GEOMETRYCOLLECTION (POINT EMPTY, POINT (1 2))");
        GeometryCollection gc5 = new GeometryCollection(new Geometry[]{point, point1, point, point1});
        checkGeometryCollection(gc5, "GEOMETRYCOLLECTION (POINT EMPTY, POINT (1 2), POINT EMPTY, POINT (1 2))");

        GeometryCollection gc6 = new GeometryCollection(new Geometry[]{ls1});
        checkGeometryCollection(gc6, "GEOMETRYCOLLECTION (LINESTRING (1 2))");
        GeometryCollection gc7 = new GeometryCollection(new Geometry[]{ls1, ls2, ls6});
        checkGeometryCollection(gc7, "GEOMETRYCOLLECTION (LINESTRING (1 2), LINESTRING (1 2, 3 4), LINESTRING (-1 0, -3 4, 5 6))");
        GeometryCollection gc8 = new GeometryCollection(new Geometry[]{point, point1, point, ls1, ls2, ls6});
        checkGeometryCollection(gc8, "GEOMETRYCOLLECTION (POINT EMPTY, POINT (1 2), POINT EMPTY, LINESTRING (1 2), LINESTRING (1 2, 3 4), LINESTRING (-1 0, -3 4, 5 6))");
        GeometryCollection gc9 = new GeometryCollection(new Geometry[]{mls, mls5});
        checkGeometryCollection(gc9, "GEOMETRYCOLLECTION (MULTILINESTRING EMPTY, MULTILINESTRING ((1 2), (1 2, 3 4), (1 2, 3 4, 5 6), (-1 0, -3 4, 5 6)))");
        GeometryCollection gc10 = new GeometryCollection(new Geometry[]{mls, mls5, point, point1, ls6});
        checkGeometryCollection(gc10, "GEOMETRYCOLLECTION (MULTILINESTRING EMPTY, MULTILINESTRING ((1 2), (1 2, 3 4), (1 2, 3 4, 5 6), (-1 0, -3 4, 5 6)), POINT EMPTY, POINT (1 2), LINESTRING (-1 0, -3 4, 5 6))");

        GeometryCollection gc11 = new GeometryCollection(new Geometry[]{mp, mp5});
        checkGeometryCollection(gc11, "GEOMETRYCOLLECTION (MULTIPOINT EMPTY, MULTIPOINT (POINT EMPTY, (1 2), POINT EMPTY, (1 2)))");
        GeometryCollection gc12 = new GeometryCollection(new Geometry[]{mp1, mp5, mls5, point, point1, ls6});
        checkGeometryCollection(gc12, "GEOMETRYCOLLECTION (MULTIPOINT (POINT EMPTY), MULTIPOINT (POINT EMPTY, (1 2), POINT EMPTY, (1 2)), MULTILINESTRING ((1 2), (1 2, 3 4), (1 2, 3 4, 5 6), (-1 0, -3 4, 5 6)), POINT EMPTY, POINT (1 2), LINESTRING (-1 0, -3 4, 5 6))");
        GeometryCollection gc13 = new GeometryCollection(new Geometry[]{mp1, mp5, mls5, point, point1, ls6, pl, pl1, mpg, mpg2});
        checkGeometryCollection(gc13, "GEOMETRYCOLLECTION (MULTIPOINT (POINT EMPTY), MULTIPOINT (POINT EMPTY, (1 2), POINT EMPTY, (1 2)), MULTILINESTRING ((1 2), (1 2, 3 4), (1 2, 3 4, 5 6), (-1 0, -3 4, 5 6)), POINT EMPTY, POINT (1 2), LINESTRING (-1 0, -3 4, 5 6), POLYGON EMPTY, POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30), (30 40, 45 45, 40 30, 30 40)), MULTIPOLYGON EMPTY, MULTIPOLYGON (((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30), (30 40, 45 45, 40 30, 30 40)), ((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30), (30 40, 45 45, 40 30, 30 40))))");
    }

    private void checkGeometryCollection(GeometryCollection geometryCollection, String gText) throws IOException {
        String gWritten = wktWriter.write(geometryCollection);
        assertEquals(gText, gWritten);

        GeometryCollection geometryCollectionObject = (GeometryCollection) wktReader.read(gWritten);
        assertEquals(geometryCollection, geometryCollectionObject);
    }


}
