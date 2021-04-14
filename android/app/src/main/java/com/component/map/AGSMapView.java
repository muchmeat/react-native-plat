package com.component.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
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
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
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
import com.plat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.component.map.TDTLayerUtil.SCALES;


public class AGSMapView extends LinearLayout implements LifecycleEventListener {

    Context context;
    View rootView;
    public MapView mapView;
    private ArcGISMap arcGISMap;
    private LocationDisplay locationDisplay;
    private Callout callout;
    private int minZoom = 1;
    private int maxZoom = 18;
    private static List<String> symbolProperties = Arrays.asList("marker-color", "marker-size", "marker-symbol", "marker-opacity", "line-opacity", "line-width", "line-color", "line-symbol", "fill-symbol", "fill-color", "fill-opacity");

    //draw图层
    protected static GraphicsOverlay mDrawGraphicsOverlay = new GraphicsOverlay();
    //add图层
    protected static GraphicsOverlay mAddGraphicsOverlay = new GraphicsOverlay();

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
        webTiledLayer.loadAsync();
        Basemap basemap = new Basemap(webTiledLayer);
        basemap.loadAsync();
        arcGISMap = new ArcGISMap(basemap);

        setUpCallout();
        setUpSketchEditor();

        locationDisplay = mapView.getLocationDisplay();

        mapView.getGraphicsOverlays().add(mDrawGraphicsOverlay);
        mapView.getGraphicsOverlays().add(mAddGraphicsOverlay);

        mapView.setOnTouchListener(new OnSingleTapConfirmedListener(context, mapView));

//        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(context, mapView) {
//
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
//                Log.d("AGS", "onSingleTapConfirmed: " + motionEvent.toString());
//
//                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
//                        Math.round(motionEvent.getY()));
//                // create a map point from screen point
//                Point mapPoint = mapView.screenToLocation(screenPoint);
//                //将屏幕坐标 传入 identifyGraphicsOverlaysAsync （屏幕坐标，范围，包括图形和弹出窗口时为false，最大检索数）
//                final ListenableFuture<List<IdentifyGraphicsOverlayResult>> listListenableFuture = mapView.identifyGraphicsOverlaysAsync(screenPoint, 12, false, 5);
//                //添加点击事件
//                listListenableFuture.addDoneListener(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            //获取点击的范围图层
//                            List<IdentifyGraphicsOverlayResult> identifyLayerResults = listListenableFuture.get();
//                            if (identifyLayerResults.size() != 0) {
//                                //循环图层获取Graphic
//                                List<GeoElement> graphics3 = new ArrayList<>();
//                                for (IdentifyGraphicsOverlayResult identifyLayerResult : identifyLayerResults) {
//                                    graphics3.addAll(identifyLayerResult.getGraphics());
//                                }
//                                Collections.sort(graphics3, (o1, o2) -> {
//                                    Integer sort = getGeometryTypeSort(o1.getGeometry().getGeometryType());
//                                    Integer sort2 = getGeometryTypeSort(o2.getGeometry().getGeometryType());
//                                    return sort.compareTo(sort2);
//                                });
//                                showCallout(mapPoint, graphics3.get(0));
//
//                            } else {
//                                callout.dismiss();
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                return true;
//            }
//        });
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

    public class OnSingleTapConfirmedListener extends DefaultMapViewOnTouchListener {
        public OnSingleTapConfirmedListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()),
                    Math.round(e.getY()));
            // create a map point from screen point
            Point mapPoint = mapView.screenToLocation(screenPoint);
            //将屏幕坐标 传入 identifyGraphicsOverlaysAsync （屏幕坐标，范围，包括图形和弹出窗口时为false，最大检索数）
            final ListenableFuture<List<IdentifyGraphicsOverlayResult>> listListenableFuture = mapView.identifyGraphicsOverlaysAsync(screenPoint, 12, false, 5);
            //添加点击事件
            listListenableFuture.addDoneListener(new Runnable() {

                @Override
                public void run() {
                    try {
                        //获取点击的范围图层
                        List<IdentifyGraphicsOverlayResult> identifyLayerResults = listListenableFuture.get();
                        if (identifyLayerResults.size() != 0) {
                            //循环图层获取Graphic
                            List<GeoElement> graphics = new ArrayList<>();
                            for (IdentifyGraphicsOverlayResult identifyLayerResult : identifyLayerResults) {
                                graphics.addAll(identifyLayerResult.getGraphics());
                            }
                            Collections.sort(graphics, (o1, o2) -> {
                                Integer sort = getGeometryTypeSort(o1.getGeometry().getGeometryType());
                                Integer sort2 = getGeometryTypeSort(o2.getGeometry().getGeometryType());
                                return sort.compareTo(sort2);
                            });
                            showCallout(mapPoint, graphics.get(0));
                        } else {
                            callout.dismiss();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (null != mapView.getSketchEditor().getSketchCreationMode()) {
                mapView.getSketchEditor().stop();
                return true;
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (null != mapView.getSketchEditor().getSketchCreationMode()) {
                mapView.getSketchEditor().stop();
                return true;
            }
            return super.onDoubleTapEvent(e);
        }
    }

    private void showCallout(Point mapPoint, GeoElement graphic) {
        //实例化一个LinearLayout
        ScrollView scrollView = new ScrollView(getContext().getApplicationContext());
        LinearLayout linearLayout = new LinearLayout(getContext().getApplicationContext());
        //为垂直方向布局
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //设置LinearLayout属性(宽和高)
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //设置边距
//                                        layoutParams.setMargins(54, 0, 84, 0);
        //将以上的属性赋给LinearLayout
        linearLayout.setLayoutParams(layoutParams);

        // convert to WGS84 for lat/lon format
        Point wgs84Point = (Point) GeometryEngine.project(mapPoint, SpatialReferences.getWgs84());
        Map<String, Object> attributes = graphic.getAttributes();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            LinearLayout horizontalLinearLayout = new LinearLayout(getContext().getApplicationContext());
            TextView keyTextView = new TextView(getContext().getApplicationContext());
            TextView valueTextView = new TextView(getContext().getApplicationContext());
            keyTextView.setTextColor(Color.BLACK);
//                                            keyTextView.setSingleLine();
            keyTextView.setWidth(40);
            keyTextView.setText(entry.getKey());
            valueTextView.setTextColor(Color.BLACK);
//                                            valueTextView.setSingleLine();
            valueTextView.setText(entry.getValue().toString());
            horizontalLinearLayout.addView(keyTextView);
            horizontalLinearLayout.addView(valueTextView);
            linearLayout.addView(horizontalLinearLayout);

        }
        if (0 == linearLayout.getChildCount()) {
            TextView emptyContent = new TextView(getContext().getApplicationContext());
            emptyContent.setTextColor(Color.BLACK);
            emptyContent.setSingleLine();
            emptyContent.setText("没有信息");
            linearLayout.addView(emptyContent);
        }

        // create a textview for the callout
        TextView calloutContent = new TextView(getContext().getApplicationContext());
        calloutContent.setTextColor(Color.BLACK);
        calloutContent.setSingleLine();
        // format coordinates to 4 decimal places
        calloutContent.setText("Lat: " + String.format("%.4f", wgs84Point.getY()) + ", Lon: " + String.format("%.4f", wgs84Point.getX()));
        linearLayout.addView(calloutContent);

        scrollView.addView(linearLayout);
        callout.setLocation(mapPoint);
        callout.setContent(scrollView);
        callout.show();

        // center on tapped point
        mapView.setViewpointCenterAsync(mapPoint);
    }

    private void setUpCallout() {
        callout = mapView.getCallout();
        Callout.ShowOptions showOptions = new Callout.ShowOptions();
        showOptions.setAnimateCallout(true);
        showOptions.setAnimateRecenter(true);
        showOptions.setRecenterMap(false);
        callout.getStyle().setMaxHeight(300);
        callout.getStyle().setMaxWidth(230);
        callout.getStyle().setMinHeight(30);
        callout.getStyle().setMinWidth(110);
        callout.setShowOptions(showOptions);
        callout.setPassTouchEventsToMapView(false);
    }

    private void setUpSketchEditor() {
        SketchEditor sketchEditor = new SketchEditor();
        SketchStyle sketchStyle = new SketchStyle();
        sketchEditor.setSketchStyle(sketchStyle);
        mapView.setSketchEditor(sketchEditor);
    }

    public Integer getGeometryTypeSort(GeometryType geometryType) {
        Integer i = 6;
        switch (geometryType) {
            case POINT: {
                i = 1;
                break;
            }
            case MULTIPOINT: {
                i = 2;
                break;
            }
            case POLYLINE: {
                i = 3;
                break;
            }
            case POLYGON: {
                i = 4;
                break;
            }
            case ENVELOPE: {
                i = 5;
                break;
            }
            case UNKNOWN: {
                break;
            }
        }
        return i;
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        post(measureAndLayout);
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

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
        Log.i("AGS", "getId(): " + getId());

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
        if (null != mapView.getSketchEditor().getSketchCreationMode()) {
            callback.invoke(mapView.getSketchEditor().getGeometry().toJson());
            mapView.getSketchEditor().stop();
        }
    }

    public void setInitialMapCenter(ReadableArray initialCenter) {

//        WebTiledLayer webTiledLayer = TDTLayerUtil.CreateTianDiTuTiledLayer(TDTLayerUtil.LayerType.TDT_IMAGE_MERCATOR);
//        Basemap basemap = new Basemap(webTiledLayer);
//        arcGISMap = new ArcGISMap(basemap);

        ArrayList<Point> points = new ArrayList<>();
        if (null != initialCenter) {
            for (int i = 0; i < initialCenter.size(); i++) {
                ReadableArray item = initialCenter.getArray(i);
                if (item == null) {
                    continue;
                }
                Double latitude = item.getDouble(0);
                Double longitude = item.getDouble(1);
                if (latitude == 0 || longitude == 0) {
                    continue;
                }
                Point point = new Point(longitude, latitude, SpatialReferences.getWgs84());
                points.add(point);
            }
        }
        Log.i("AGS", "points.size(): " + points.size());
        if (points.size() == 0) {
            points.add(new Point(31.3557, 118.4276, SpatialReferences.getWgs84()));
        }

        if (points.size() == 1) {
            arcGISMap.setInitialViewpoint(new Viewpoint(points.get(0).getX(), points.get(0).getY(), 10000));
        } else {
            Polygon polygon = new Polygon(new PointCollection(points));
            Viewpoint viewpoint = centerViewpointFromPolygon(polygon);
            arcGISMap.setInitialViewpoint(viewpoint);
        }
        mapView.setMap(arcGISMap);
    }

    public void setMinZoom(int value) {
        minZoom = value;
    }

    public void setMaxZoom(int value) {
        maxZoom = value;
    }

    /**
     * 放大
     */
    public void zoomIn() {
        double scale = mapView.getMapScale();
        double scale2 = scale * 2;
        if (SCALES[minZoom] >= scale2) {
            mapView.setViewpointScaleAsync(scale2);
        } else {
            mapView.setViewpointScaleAsync(SCALES[minZoom]);
        }
    }

    /**
     * 缩小
     */
    public void zoomOut() {
        double scale = mapView.getMapScale();
        double scale2 = scale * 0.5;
        if (SCALES[maxZoom] <= scale2) {
            mapView.setViewpointScaleAsync(scale2);
        }
//        else {
//            mapView.setViewpointScaleAsync(SCALES[maxZoom]);
//        }
    }

    /**
     * 定位当前位置
     */
    public void location() {
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

    /**
     * 设置中心点
     *
     * @param center
     */
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
     * @param properties
     * @return
     */
    public Symbol getSymbol(GeometryType geometryType, ReadableMap properties) {
        switch (geometryType) {
            case ENVELOPE:
                break;
            case POLYLINE: {
                SimpleLineSymbol.Style symbolStyle = getSimpleLineSymbolStyle(properties);
                int color = getColor(properties, "line-color", Color.BLUE, "line-opacity");
                float width = getSize(properties, "line-width", 1.0f);

                return new SimpleLineSymbol(symbolStyle, color, width);
            }
            case POLYGON: {
                SimpleLineSymbol.Style symbolStyle = getSimpleLineSymbolStyle(properties);
                int color = getColor(properties, "line-color", Color.argb(255, 97, 181, 252), "line-opacity");
                float width = getSize(properties, "line-width", 1.0f);
                SimpleLineSymbol outlineSymbol = new SimpleLineSymbol(symbolStyle, color, width);

                SimpleFillSymbol.Style fillSymbolStyle = getSimpleFillSymbolStyle(properties);
                int fillColor = getColor(properties, "fill-color", Color.argb(Math.round(100), 1, 21, 244), "fill-opacity");
                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(fillSymbolStyle, fillColor, outlineSymbol);
                return fillSymbol;
            }
            case MULTIPOINT:
            case POINT: {
                SimpleMarkerSymbol.Style symbolStyle = getSimpleMarkerSymbolStyle(properties);
                int color = getColor(properties, "marker-color", Color.rgb(226, 119, 40), "marker-opacity");
                float size = getSize(properties, "marker-size", 10.0f);

                SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(symbolStyle, color, size);
                pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f));
                return pointSymbol;
            }
            case UNKNOWN: {
                return null;
            }
        }
        return null;
    }

    public float getSize(ReadableMap properties, String sizeName, Float defaultSize) {
        float size = defaultSize == null ? 10.0f : defaultSize;
        if (null != properties) {
            try {
                boolean flag = false;
                if (!Double.isNaN(properties.getDouble(sizeName))) {
                    size = (float) properties.getDouble(sizeName);
                    flag = true;
                }
                if (!flag && !TextUtils.isEmpty(properties.getString(sizeName))) {
                    size = Float.parseFloat(properties.getString(sizeName));
                }
            } catch (IllegalArgumentException e) {
            }
        }
        return size;
    }

    public int getColor(ReadableMap properties, String colorName, Integer defaultColor, String opacityName) {
        int color = defaultColor == null ? Color.rgb(226, 119, 40) : defaultColor;
        if (null != properties && !TextUtils.isEmpty(properties.getString(colorName))) {
            try {
                color = Color.parseColor(properties.getString(colorName));
                if (null != opacityName) {
                    Map map = properties.toHashMap();
                    Object opacity = map.get(opacityName);
                    if (null != opacity) {
                        int red = Color.red(color);
                        int green = Color.green(color);
                        int blue = Color.blue(color);
                        int alpha = (int) (Double.parseDouble(opacity.toString()) * 255);
                        color = Color.argb(alpha, red, green, blue);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return color;
    }

    public SimpleMarkerSymbol.Style getSimpleMarkerSymbolStyle(ReadableMap properties) {
        SimpleMarkerSymbol.Style symbolStyle = SimpleMarkerSymbol.Style.CIRCLE;
        if (null != properties && !TextUtils.isEmpty(properties.getString("marker-symbol"))) {
            try {
                return SimpleMarkerSymbol.Style.valueOf(properties.getString("marker-symbol").toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        return symbolStyle;
    }

    public SimpleFillSymbol.Style getSimpleFillSymbolStyle(ReadableMap properties) {
        SimpleFillSymbol.Style symbolStyle = SimpleFillSymbol.Style.SOLID;
        if (null != properties && !TextUtils.isEmpty(properties.getString("fill-symbol"))) {
            try {
                return SimpleFillSymbol.Style.valueOf(properties.getString("fill-symbol").toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        return symbolStyle;
    }

    public SimpleLineSymbol.Style getSimpleLineSymbolStyle(ReadableMap properties) {
        SimpleLineSymbol.Style symbolStyle = SimpleLineSymbol.Style.SOLID;
        if (null != properties && !TextUtils.isEmpty(properties.getString("line-symbol"))) {
            try {
                return SimpleLineSymbol.Style.valueOf(properties.getString("line-symbol").toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        return symbolStyle;
    }

    /**
     * ArcGIS的Geometry添加到图层上图
     *
     * @param geometryJson
     */
    public void addGeometry(ReadableMap geometryJson) {
        String geometry = geometryJson.getString("geometry");
        ReadableMap properties = geometryJson.getMap("properties");
        Geometry g = Geometry.fromJson(geometry);
        Graphic graphic = new Graphic(g, getSymbol(g.getGeometryType(), properties));
        mAddGraphicsOverlay.getGraphics().add(graphic);
//        graphicsOverlay.getGraphics().add(graphic);
//        mapView.getGraphicsOverlays().add(graphicsOverlay);
    }

    /**
     * 点
     *
     * @param geoJson
     */
    public void addMarker(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        ReadableMap properties = geoJson.getMap("properties");
        ReadableArray coordinates = geometry.getArray("coordinates");
//        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.valueOf("circle"), Color.rgb(226, 119, 40), 10.0f);
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
//        Graphic pointGraphic = new Graphic(point, pointSymbol);
//        Graphic pointGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), properties.toHashMap(), pointSymbol);
        Map<String, Object> attributesMap = getAttributesMap(properties);
        Graphic pointGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), attributesMap, getSymbol(GeometryType.POINT, properties));
        mAddGraphicsOverlay.getGraphics().add(pointGraphic);
//        graphicsOverlay.getGraphics().add(pointGraphic);
//        mapView.getGraphicsOverlays().add(graphicsOverlay);

        Point point = new Point(coordinates.getDouble(0), coordinates.getDouble(1), SpatialReferences.getWgs84());
        Viewpoint viewpoint = new Viewpoint(point, 10000);
        mapView.setViewpointAsync(viewpoint);
    }

    /**
     * 对properties内的symbolProperties进行筛选移除
     *
     * @param properties
     * @return
     */
    public Map<String, Object> getAttributesMap(ReadableMap properties) {
        if (null == properties) {
            return new HashMap<>();
        }
        Map<String, Object> map = properties.toHashMap();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            if (symbolProperties.contains(iterator.next())) {
                iterator.remove();
            }
        }
        return map;
    }

    public void addPolyline(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        ReadableMap properties = geoJson.getMap("properties");
        ReadableArray coordinates = geometry.getArray("coordinates");
        int size = coordinates.size();

        if (size > 0) {
            PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
            for (int i = 0; i < size; i++) {
                ReadableArray polygonCoordinates = coordinates.getArray(i);
                polylinePoints.add(new Point(polygonCoordinates.getDouble(0), polygonCoordinates.getDouble(1)));
            }
            Map<String, Object> attributesMap = getAttributesMap(properties);
            Graphic polylineGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), attributesMap, getSymbol(GeometryType.POLYLINE, properties));
            mAddGraphicsOverlay.getGraphics().add(polylineGraphic);

            Viewpoint viewpoint = viewpointFromGeometry(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)));
            mapView.setViewpointAsync(viewpoint);
        }
    }

    public void addPolygon(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        ReadableMap properties = geoJson.getMap("properties");
        ReadableArray coordinates = geometry.getArray("coordinates");
        int size = coordinates.size();
        Log.i("AGS", String.valueOf(size));

        if (size > 0) {
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
            Map<String, Object> attributesMap = getAttributesMap(properties);
            Graphic graphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), attributesMap, getSymbol(GeometryType.POLYGON, properties));
            mAddGraphicsOverlay.getGraphics().add(graphic);

            Viewpoint viewpoint = viewpointFromPolygon(polygon);
            mapView.setViewpointAsync(viewpoint);
        }
    }

    public void addCircle(ReadableMap circleParams) {
        double radius = circleParams.getDouble("radius");
        ReadableArray center = circleParams.getArray("center");
        ReadableMap properties = circleParams.getMap("properties");
        int fillColor = getColor(properties, "fill-color", Color.argb(Math.round(100), 1, 21, 244), "fill-opacity");
        Point point = new Point(center.getDouble(0), center.getDouble(1), SpatialReferences.getWgs84());
        SimpleLineSymbol planarOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, fillColor, 2);
        SimpleFillSymbol planarBufferFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, fillColor, planarOutlineSymbol);
        //fixme todo 平面测量，椭圆
//        double transferRadius = radius / (111000 * Math.cos(point.getY()));
//        Geometry bufferGeometryPlanar = GeometryEngine.buffer(point, transferRadius);
        //大地测量
        Geometry bufferGeometryPlanar = GeometryEngine.bufferGeodetic(point, radius, new LinearUnit(LinearUnitId.METERS), 1, GeodeticCurveType.NORMAL_SECTION);

        Map<String, Object> attributesMap = getAttributesMap(properties);
        Graphic planarBufferGraphic = new Graphic(bufferGeometryPlanar, attributesMap, planarBufferFillSymbol);
        mAddGraphicsOverlay.getGraphics().add(planarBufferGraphic);

        Viewpoint viewpoint = viewpointFromGeometry(bufferGeometryPlanar);
        mapView.setViewpointAsync(viewpoint);
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

    public Viewpoint centerViewpointFromPolygon(Polygon polygon) {
        Envelope envelope = polygon.getExtent();
        double x = (envelope.getXMin() + envelope.getXMax()) / 2;
        double y = (envelope.getYMin() + envelope.getYMax()) / 2;
        return new Viewpoint(x, y, 10000);
    }

    public Viewpoint viewpointFromGeometry(Geometry geometry) {
        Envelope envelope = geometry.getExtent();
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
