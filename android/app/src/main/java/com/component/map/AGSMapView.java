package com.component.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.geometry.PartCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.mapbox.geojson.Feature;
import com.plat.R;

import java.util.ArrayList;


public class AGSMapView extends LinearLayout implements LifecycleEventListener {

    Context context;
    View rootView;
    public MapView mapView;
    private ArcGISMap arcGISMap;
    private LocationDisplay locationDisplay;
    private Double minZoom = 1.0;
    private Double maxZoom = 10.0;

    //draw图层
    protected static GraphicsOverlay mDrawGraphicsOverlay;

    public AGSMapView(Context context) {
        super(context);
        context = context;
        //移除水印 Licensed For Developer Use Only
        //http://zhihu.geoscene.cn/article/3936
        //https://developers.arcgis.com/pricing/licensing/
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");
        rootView = inflate(context.getApplicationContext(), R.layout.activity_main_arcgis, this);
        mapView = rootView.findViewById(R.id.mapView);
        //移除下方logo Powered By Esri
        mapView.setAttributionTextVisible(false);
//        Basemap basemap = new Basemap(WebTiledLayerUtil.createLayer("http://192.168.0.117:8080/", null));
//        Basemap basemap = new Basemap(WebTiledLayerUtil.createLayer("http://t0.tianditu.com/", null));
//        WebTiledLayer webTiledLayer = TianDiTuTiledMapServiceLayerUtil.CreateTianDiTuTiledLayer(TianDiTuTiledMapServiceLayerUtil.LayerType.TIANDITU_IMAGE_2000);
//        WebTiledLayer webTiledLayer = TDTLayerUtil.CreateTianDiTuTiledLayer(TDTLayerUtil.LayerType.TIANDITU_VECTOR_MERCATOR);
        WebTiledLayer webTiledLayer = TDTLayerUtil.CreateTianDiTuTiledLayer(TDTLayerUtil.LayerType.TDT_IMAGE_MERCATOR);
//        webTiledLayer.loadAsync();
        Basemap basemap = new Basemap(webTiledLayer);
        basemap.loadAsync();
        arcGISMap = new ArcGISMap(basemap);
//        arcGISMap.setMinScale(1500);
//        arcGISMap.setMaxScale(50000000);
//        mapView.setMap(new ArcGISMap(Basemap.Type.STREETS_VECTOR, 31.3583, 118.4271, 17));
//        mapView.setMap(arcGISMap);
//        Envelope initialExtent = new Envelope(-12211308.778729, 4645116.003309, -12208257.879667, 4650542.535773,
//                SpatialReference.create(102100));
//        Viewpoint viewpoint = new Viewpoint(initialExtent);
//        mapView.setViewpoint(viewpoint);

//        ArcGISMap mMap = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

        // set the map to be displayed in this view
        mapView.setMap(arcGISMap);
//        mapView.setViewpoint(new Viewpoint(31.336, 118.386, 10000));

        locationDisplay = mapView.getLocationDisplay();
        Log.i("AGS", "getInitialViewpoint(): " + mapView.getMap().getInitialViewpoint().toJson());

        mDrawGraphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(mDrawGraphicsOverlay);


        SketchEditor sketchEditor = new SketchEditor();
        SketchStyle sketchStyle = new SketchStyle();
        sketchEditor.setSketchStyle(sketchStyle);
        mapView.setSketchEditor(sketchEditor);

        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(context, mapView) {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                Log.d("AGS", "onSingleTapConfirmed: " + motionEvent.toString());

                // get the point that was clicked and convert it to a point in map coordinates
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
                        Math.round(motionEvent.getY()));
                // create a map point from screen point
                Point mapPoint = mapView.screenToLocation(screenPoint);
                // convert to WGS84 for lat/lon format
                Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
                // create a textview for the callout
                TextView calloutContent = new TextView(getContext().getApplicationContext());
                calloutContent.setTextColor(Color.BLACK);
                calloutContent.setSingleLine();
                // format coordinates to 4 decimal places
                calloutContent.setText("Lat: " + String.format("%.4f", wgs84Point.getY()) + ", Lon: " + String.format("%.4f", wgs84Point.getX()));

                // get callout, set content and show
                Callout mCallout = mapView.getCallout();
                mCallout.setLocation(mapPoint);
                mCallout.setContent(calloutContent);
                mCallout.show();

                // center on tapped point
                mapView.setViewpointCenterAsync(mapPoint);

                return true;
            }
        });
//        mapView.getMap().addDoneLoadingListener(() -> {
//            ArcGISRuntimeException e = mapView.getMap().getLoadError();
//            Boolean success = e != null;
//            String errorMessage = !success ? "" : e.getMessage();
//            WritableMap map = Arguments.createMap();
//            map.putBoolean("success",success);
//            map.putString("errorMessage",errorMessage);
//
//            emitEvent("onMapDidLoad",map);
//        });
    }

    @Override
    public void onHostResume() {
        mapView.resume();
    }

    @Override
    public void onHostPause() {
        mapView.pause();
    }

    @Override
    public void onHostDestroy() {
        mapView.dispose();
        if (getContext() instanceof ReactContext) {
            ((ReactContext) getContext()).removeLifecycleEventListener(this);
        }
    }

    public void emitEvent(String eventName, WritableMap args) {
        ((ReactContext) getContext()).getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                eventName,
                args
        );
    }

    public void drawRectangle() {
        mapView.getSketchEditor().start(SketchCreationMode.RECTANGLE);
    }


    public void drawMultiPoint() {
        mapView.getSketchEditor().start(SketchCreationMode.MULTIPOINT);
    }

    public void drawPoint() {
        mapView.getSketchEditor().start(SketchCreationMode.POINT);
    }

    public void drawFreeHandLine() {
        mapView.getSketchEditor().start(SketchCreationMode.FREEHAND_LINE);
    }

    public void drawPolyline() {
        mapView.getSketchEditor().start(SketchCreationMode.POLYLINE);
    }

    public void drawPolygon() {
        mapView.getSketchEditor().start(SketchCreationMode.POLYGON);
    }

    public void drawFreeHandPolygon() {
        mapView.getSketchEditor().start(SketchCreationMode.FREEHAND_POLYGON);
    }

    public void drawStop() {
        mapView.getSketchEditor().getGeometry();
        Log.i("AGS", "getSketchCreationMode: " + mapView.getSketchEditor().getSketchCreationMode());
        WritableMap map = Arguments.createMap();
//        map.putString("geometry", mapView.getSketchEditor().getGeometry().toJson());
        map.putString("geometry", "mapView.getSketchEditor().getGeometry().toJson()");
        emitEvent("drawCallback", map);
        mapView.getSketchEditor().stop();
    }

    public void drawStop(Callback callback) {
        Log.i("AGS", "getSketchCreationMode2: " + mapView.getSketchEditor().getSketchCreationMode());
        callback.invoke(mapView.getSketchEditor().getGeometry().toJson());
        mapView.getSketchEditor().stop();
    }

    public void setInitialMapCenter(ReadableArray initialCenter) {
        ArrayList<Point> points = new ArrayList<>();
        if (null != initialCenter) {
            for (int i = 0; i < initialCenter.size(); i++) {
                ReadableMap item = initialCenter.getMap(i);
                if (item == null) {
                    continue;
                }
                Double latitude = item.getDouble("latitude");
                Double longitude = item.getDouble("longitude");
                if (latitude == 0 || longitude == 0) {
                    continue;
                }
                Point point = new Point(longitude, latitude, SpatialReferences.getWgs84());
                points.add(point);
            }
        }
        Log.i("AGS", "points.size(): " + points.size());
        if (points.size() == 0) {
//            points.add(new Point(118.386, 31.336, SpatialReferences.getWgs84()));
            points.add(new Point(31.336, 118.386, SpatialReferences.getWgs84()));
        }
        if (points.size() == 1) {
            mapView.getMap().setInitialViewpoint(new Viewpoint(points.get(0), 10));
            Log.i("AGS", "getInitialViewpoint(): " + mapView.getMap().getInitialViewpoint().toJson());
//            mapView.setViewpoint(new Viewpoint(31.336, 118.386, 10000));
        } else {
            Polygon polygon = new Polygon(new PointCollection(points));
            Viewpoint viewpoint = viewpointFromPolygon(polygon);
            mapView.getMap().setInitialViewpoint(viewpoint);
        }
    }

    public void setMinZoom(Double value) {
        minZoom = value;
        mapView.getMap().setMinScale(minZoom);
    }

    public void setMaxZoom(Double value) {
        maxZoom = value;
        mapView.getMap().setMaxScale(maxZoom);
    }

    /**
     * 放大
     */
    public void zoomIn() {
        double scale = mapView.getMapScale();
        mapView.setViewpointScaleAsync(scale * 2);
    }

    /**
     * 缩小
     */
    public void zoomOut() {
        double scale = mapView.getMapScale();
        mapView.setViewpointScaleAsync(scale * 0.5);
    }

    /**
     * 定位
     */
    public void center() {
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        locationDisplay.setShowLocation(true);
        locationDisplay.setShowAccuracy(true);//隐藏符号的缓存区域
        locationDisplay.setShowPingAnimation(true);//隐藏位置更新的符号动画
        locationDisplay.startAsync();
    }

    public void changeBaseMap(@NonNull ReadableMap baseMap) {
        Viewpoint viewpoint = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);
        String layerType = baseMap.getString("layerType");
        WebTiledLayer webTiledLayer = TDTLayerUtil.CreateTianDiTuTiledLayer(TDTLayerUtil.LayerType.valueOf(layerType));
        Basemap basemap = new Basemap(webTiledLayer);
        arcGISMap = new ArcGISMap(basemap);
        mapView.setMap(arcGISMap);
        mapView.setViewpointAsync(viewpoint);
    }

    public void setCenter(@Nullable ReadableMap center) {
        Point centerPoint;
        if (center != null) {
            centerPoint = new Point(center.getDouble("latitude"), center.getDouble("longitude"), SpatialReferences.getWgs84());
        } else {
            centerPoint = mapView.getLocationDisplay().getLocation().getPosition();
        }
        Viewpoint vp = new Viewpoint(centerPoint, 10000);
        mapView.setViewpointAsync(vp);
    }

    /**
     * 将GeoJSON转为ArcGIS支持的Geometry
     *
     * @param geoJson
     * @return
     */
    public String parseGeoJsonToGeometry(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        String type = geometry.getString("type");
        ReadableArray coordinates = geometry.getArray("coordinates");
        if ("Point".equalsIgnoreCase(type)) {
            return "{\"x\" : " + coordinates.getDouble(0) + ", \"y\" : " + coordinates.getDouble(1) + ", \"spatialReference\": {\"wkid\" : 4326}}";
        }
        if ("MultiPoint".equalsIgnoreCase(type)) {
            return "{\"points\" : " + JSONObject.toJSONString(coordinates.toArrayList()) + ", \"spatialReference\": {\"wkid\" : 4326}}";
        }
        if ("Polygon".equalsIgnoreCase(type) || "MultiPolygon".equalsIgnoreCase(type)) {
            return "{\"rings\" : " + JSONObject.toJSONString(coordinates.toArrayList()) + ", \"spatialReference\": {\"wkid\" : 4326}}";
        }
        if ("LineString".equalsIgnoreCase(type) || "MultiLineString".equalsIgnoreCase(type)) {
            return "{\"paths\" : [" + JSONObject.toJSONString(coordinates.toArrayList()) + "], \"spatialReference\": {\"wkid\" : 4326}}";
        }
        return null;
    }

    /**
     * 获取geometryType对应的Symbol
     *
     * @param geometryType
     * @return
     */
    public Symbol getSymbol(GeometryType geometryType) {
        switch (geometryType) {
            case ENVELOPE:
                break;
            case POLYLINE: {
                SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f);
                return polylineSymbol;
            }
            case POLYGON: {
                SimpleLineSymbol outlineSymbol =
                        new SimpleLineSymbol(
                                SimpleLineSymbol.Style.SOLID,
                                Color.argb(255, 97, 181, 252), 1.0f);
                SimpleFillSymbol fillSymbol =
                        new SimpleFillSymbol(
                                SimpleFillSymbol.Style.SOLID,
                                Color.argb(Math.round(100), 1, 21, 244),
                                outlineSymbol);
                return fillSymbol;
            }
            case MULTIPOINT:
            case POINT: {
                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
                pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f));
                return pointSymbol;
            }
            case UNKNOWN: {
                return null;
            }
        }
        return null;
    }

    /**
     * ArcGIS的Geometry添加到图层上图
     *
     * @param geometryJson
     */
    public void addGeometry(ReadableMap geometryJson) {
        String geometry = geometryJson.getString("geometry");
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        Geometry g = Geometry.fromJson(geometry);
        Graphic graphic = new Graphic(g, getSymbol(g.getGeometryType()));
        graphicsOverlay.getGraphics().add(graphic);
        mapView.getGraphicsOverlays().add(graphicsOverlay);
    }

    /**
     * 点
     *
     * @param geoJson
     */
    public void addMarker(ReadableMap geoJson) {
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

//        coordinates = geometry.getArray("coordinates");
//        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
//        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f));
//        Point point = new Point(coordinates.getDouble(0), coordinates.getDouble(1), SpatialReferences.getWgs84());
//        String icon = properties.hasKey("icon") ? properties.getString("icon") : null;
//        if (StringUtils.isNotEmpty(icon) && getResId(icon) != 0) {
//            BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(context, getResId(icon));
//            final PictureMarkerSymbol pinStarBlueSymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
//            pinStarBlueSymbol.setHeight(25);
//            pinStarBlueSymbol.setWidth(25);
//            Graphic pointGraphic = new Graphic(point, pinStarBlueSymbol);
//            graphicsOverlay.getGraphics().add(pointGraphic);
//        } else {
//            pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
//            pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f));
//            Graphic pointGraphic = new Graphic(point, pointSymbol);
//            graphicsOverlay.getGraphics().add(pointGraphic);
//        }
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f));
        Point point = new Point(118.4335, 31.3550, SpatialReferences.getWgs84());
//        Graphic pointGraphic = new Graphic(point, pointSymbol);
        Graphic pointGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), pointSymbol);
        graphicsOverlay.getGraphics().add(pointGraphic);
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        mapView.getMap().setInitialViewpoint(new Viewpoint(point, 10000));
    }

    public void addPolyline(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        ReadableArray coordinates = geometry.getArray("coordinates");
        int size = coordinates.size();
        if (size > 0) {
            PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
            for (int i = 0; i < size; i++) {
                ReadableArray polygonCoordinates = coordinates.getArray(i);
                polylinePoints.add(new Point(polygonCoordinates.getDouble(0), polygonCoordinates.getDouble(1)));
            }
            SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f);
            Graphic polylineGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), polylineSymbol);
            graphicsOverlay.getGraphics().add(polylineGraphic);
            mapView.getGraphicsOverlays().add(graphicsOverlay);
        }
    }

    public void addPolygon(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        ReadableArray coordinates = geometry.getArray("coordinates");
        int size = coordinates.size();
        Log.i("AGS", String.valueOf(size));
        if (size > 0) {
            SimpleLineSymbol outlineSymbol =
                    new SimpleLineSymbol(
                            SimpleLineSymbol.Style.SOLID,
                            Color.argb(255, 97, 181, 252), 1.5f);
            SimpleFillSymbol fillSymbol =
                    new SimpleFillSymbol(
                            SimpleFillSymbol.Style.SOLID,
                            Color.argb(Math.round(100), 1, 21, 244),
                            outlineSymbol);
            PartCollection parts = new PartCollection(SpatialReferences.getWgs84());
            for (int i = 0; i < size; i++) {
                ReadableArray polygonCoordinates = coordinates.getArray(i);
                PointCollection points = new PointCollection(SpatialReferences.getWgs84());
                for (int j = 0; j < polygonCoordinates.size(); j++) {
                    ReadableArray coordinate = polygonCoordinates.getArray(j);
                    points.add(coordinate.getDouble(0), coordinate.getDouble(1));
                }
                parts.add(new Part(points));
            }
            Polygon polygon = new Polygon(parts);
            Graphic graphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), fillSymbol);
//            Graphic graphic = new Graphic(polygon, fillSymbol);
            graphicsOverlay.getGraphics().add(graphic);
            mapView.getGraphicsOverlays().add(graphicsOverlay);
        }
    }

    public void addCircle(ReadableMap circleParams) {
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        double radius = circleParams.getDouble("radius");
        ReadableArray center = circleParams.getArray("center");
        Point point = new Point(center.getDouble(0), center.getDouble(1), SpatialReferences.getWgs84());
        SimpleLineSymbol planarOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.argb(15, 1, 21, 244), 2);
        SimpleFillSymbol planarBufferFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.argb(15, 1, 21, 244),
                planarOutlineSymbol);
        //平面测量，椭圆
//        double transferRadius = radius / (111000 * Math.cos(point.getY()));
//        Geometry bufferGeometryPlanar = GeometryEngine.buffer(point, transferRadius);
        //大地测量
        Geometry bufferGeometryPlanar = GeometryEngine.bufferGeodetic(point, radius, new LinearUnit(LinearUnitId.METERS), 1, GeodeticCurveType.NORMAL_SECTION);
        Graphic planarBufferGraphic = new Graphic(bufferGeometryPlanar);
        planarBufferGraphic.setSymbol(planarBufferFillSymbol);
        graphicsOverlay.getGraphics().add(planarBufferGraphic);
        mapView.getGraphicsOverlays().add(graphicsOverlay);
    }

    public Viewpoint viewpointFromPolygon(Polygon polygon) {
        Envelope envelope = polygon.getExtent();
        Double paddingWidth = envelope.getWidth() * 0.5;
        Double paddingHeight = envelope.getHeight() * 0.5;
        return new Viewpoint(new Envelope(
                envelope.getXMin() - paddingWidth, envelope.getYMax() + paddingHeight,
                envelope.getXMax() + paddingWidth, envelope.getYMin() - paddingHeight,
                SpatialReferences.getWgs84()), 0);
    }

    public int getResId(String name) {
        Resources r = context.getResources();
        int id = r.getIdentifier(name, "drawable", context.getPackageName());
        return id;
    }
}
