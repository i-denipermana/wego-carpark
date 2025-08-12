package com.wego.carpark.utils;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.proj4j.*;

@Slf4j
public class CoordinateConverter {
    private static final CRSFactory CRS_FACTORY = new CRSFactory();
    private static final CoordinateReferenceSystem SRC = CRS_FACTORY.createFromName("EPSG:3414"); // SVY21
    private static final CoordinateReferenceSystem DST = CRS_FACTORY.createFromName("EPSG:4326"); // WGS84
    private static final CoordinateTransform TRANSFORM =
            new CoordinateTransformFactory().createTransform(SRC, DST);

    public static LatLng svy21ToWgs84(double x, double y) {
        ProjCoordinate in = new ProjCoordinate(x, y);
        ProjCoordinate out = new ProjCoordinate();
        TRANSFORM.transform(in, out);
        // out.x = lon, out.y = lat (Proj4J ordering)
        return new LatLng(out.y, out.x);
    }

    public record LatLng(double latitude, double longitude) {}
}
