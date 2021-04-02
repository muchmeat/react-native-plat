package com.component.map;


import android.graphics.Color;
import android.view.MotionEvent;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;


public class AGSMapManager extends SimpleViewManager<AGSMapView> {

    ThemedReactContext context;
    public static final String REACT_CLASS = "AGSMapManager";

    private static final int COMMAND_ZOOM_IN = 1;
    private static final int COMMAND_ZOOM_OUT = 2;
    private static final int COMMAND_ADD_MARKER = 3;
    private static final int COMMAND_ADD_POLYLINE = 4;
    private static final int COMMAND_ADD_POLYGON = 5;
    private static final int COMMAND_ADD_CIRCLE = 6;
    private static final int COMMAND_ADD_GEOMETRY = 7;
    private static final int COMMAND_DISPOSE = 8;
    private static final int COMMAND_CENTER = 9;
    private static final int COMMAND_LOCATION = 10;
    private static final int COMMAND_BASE_MAP = 11;

//    private static final int COMMAND_DRAW_POINT = 10;
//    private static final int COMMAND_DRAW_MULTI_POINT = 11;
//    private static final int COMMAND_DRAW_FREEHAND_LINE = 12;
//    private static final int COMMAND_DRAW_POLYLINE = 13;
//    private static final int COMMAND_DRAW_FREEHAND_POLYGON = 14;
//    private static final int COMMAND_DRAW_POLYGON = 15;
//    private static final int COMMAND_DRAW_RECTANGLE = 16;
//    private static final int COMMAND_DRAW_STOP = 17;

    private AGSMapView mAGSMapView;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AGSMapView createViewInstance(ThemedReactContext reactContext) {
        context = reactContext;
        mAGSMapView = new AGSMapView(reactContext);
        return mAGSMapView;
    }

    @ReactProp(name = "initialMapCenter")
    public void setInitialMapCenter(AGSMapView agsMapView, @Nullable ReadableArray array) {
        agsMapView.setInitialMapCenter(array);
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("zoomIn", COMMAND_ZOOM_IN);
        map.put("zoomOut", COMMAND_ZOOM_OUT);
        map.put("addMarker", COMMAND_ADD_MARKER);
        map.put("addPolyline", COMMAND_ADD_POLYLINE);
        map.put("addPolygon", COMMAND_ADD_POLYGON);
        map.put("addCircle", COMMAND_ADD_CIRCLE);
        map.put("addGeometry", COMMAND_ADD_GEOMETRY);
        map.put("location", COMMAND_LOCATION);
        map.put("changeBaseMap", COMMAND_BASE_MAP);
        map.put("dispose", COMMAND_DISPOSE);
//        map.put("drawPoint", COMMAND_DRAW_POINT);
//        map.put("drawMultiPoint", COMMAND_DRAW_MULTI_POINT);
//        map.put("drawPolyline", COMMAND_DRAW_POLYLINE);
//        map.put("drawFreeHandLine", COMMAND_DRAW_POLYLINE);
//        map.put("drawPolygon", COMMAND_DRAW_POLYGON);
//        map.put("drawFreeHandPolygon", COMMAND_DRAW_FREEHAND_POLYGON);
//        map.put("drawRectangle", COMMAND_DRAW_RECTANGLE);
//        map.put("drawStop", COMMAND_DRAW_STOP);
        return map;
    }

    @Override
    public void receiveCommand(AGSMapView root, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_ZOOM_IN: {
                root.zoomIn();
                return;
            }
            case COMMAND_ZOOM_OUT: {
                root.zoomOut();
                return;
            }
            case COMMAND_CENTER: {
                root.center();
                return;
            }
            case COMMAND_LOCATION: {
                root.location();
                return;
            }
//            case COMMAND_DRAW_POINT: {
////                drawPoint();
//                root.drawPoint();
//                return;
//            }
//            case COMMAND_DRAW_MULTI_POINT: {
//                root.drawMultiPoint();
//                return;
//            }
//            case COMMAND_DRAW_FREEHAND_LINE: {
////                drawPolyline();
//                root.drawFreeHandLine();
//                return;
//            }
//            case COMMAND_DRAW_POLYLINE: {
//                root.drawPolyline();
//                return;
//            }
//            case COMMAND_DRAW_FREEHAND_POLYGON: {
//                root.drawFreeHandPolygon();
//                return;
//            }
//            case COMMAND_DRAW_POLYGON: {
//                root.drawPolygon();
//                return;
//            }
//            case COMMAND_DRAW_RECTANGLE: {
//                root.drawRectangle();
//                return;
//            }
//            case COMMAND_DRAW_STOP: {
//                root.drawStop();
//                return;
//            }
            case COMMAND_BASE_MAP: {
                root.changeBaseMap(Objects.requireNonNull(args).getMap(0));
                return;
            }
            case COMMAND_ADD_GEOMETRY: {
                root.addGeometry(Objects.requireNonNull(args).getMap(0));
                return;
            }
            case COMMAND_ADD_MARKER: {
                root.addMarker(Objects.requireNonNull(args).getMap(0));
                return;
            }
            case COMMAND_ADD_POLYLINE: {
                root.addPolyline(Objects.requireNonNull(args).getMap(0));
                return;
            }
            case COMMAND_ADD_POLYGON: {
                root.addPolygon(Objects.requireNonNull(args).getMap(0));
                return;
            }
            case COMMAND_ADD_CIRCLE: {
                root.addCircle(Objects.requireNonNull(args).getMap(0));
                return;
            }
            case COMMAND_DISPOSE: {
                root.onHostDestroy();
                return;
            }
        }
    }

    @Nullable
    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "onMapReady",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onMapReady")))
                .put(
                        "drawCallback1",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "drawCallback1")))
                .build();
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("drawCallback",
                        MapBuilder.of("registrationName", "drawCallback"))
                .build();
    }

    @Deprecated
    public void drawPoint() {
        mAGSMapView.mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(context, mAGSMapView.mapView) {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //清除上一个点
                mAGSMapView.mDrawGraphicsOverlay.getGraphics().clear();

                Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 5);
                Graphic graphic = new Graphic(clickPoint, simpleMarkerSymbol);
                mAGSMapView.mDrawGraphicsOverlay.getGraphics().add(graphic);

                return super.onSingleTapConfirmed(e);
            }
        });
    }

    @Deprecated
    public void drawPolyline() {

        mAGSMapView.mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(context, mAGSMapView.mapView) {
            PointCollection mPointCollection = new PointCollection(SpatialReferences.getWebMercator());

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                mAGSMapView.mDrawGraphicsOverlay.getGraphics().clear();

                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                mPointCollection.add(point);

                Polyline polyline = new Polyline(mPointCollection);

                //点
                SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 5);
                Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
                mAGSMapView.mDrawGraphicsOverlay.getGraphics().add(pointGraphic);

                //线
                SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.parseColor("#FC8145"), 3);
                Graphic graphic = new Graphic(polyline, simpleLineSymbol);
                mAGSMapView.mDrawGraphicsOverlay.getGraphics().add(graphic);

                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                return true;
            }
        });
    }

    @Deprecated
    private void drawPolygon() {

        mAGSMapView.mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(context, mAGSMapView.mapView) {
            PointCollection mPointCollection = new PointCollection(SpatialReferences.getWebMercator());

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                mAGSMapView.mDrawGraphicsOverlay.getGraphics().clear();

                Point point = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                mPointCollection.add(point);

                Polygon polygon = new Polygon(mPointCollection);

                if (mPointCollection.size() == 1) {
                    SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 5);
                    Graphic pointGraphic = new Graphic(point, simpleMarkerSymbol);
                    mAGSMapView.mDrawGraphicsOverlay.getGraphics().add(pointGraphic);
                }

                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GREEN, 3.0f);
                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.parseColor("#33e97676"), lineSymbol);
                Graphic graphic = new Graphic(polygon, simpleFillSymbol);
                mAGSMapView.mDrawGraphicsOverlay.getGraphics().add(graphic);

                return super.onSingleTapConfirmed(e);
            }
        });
    }
}
