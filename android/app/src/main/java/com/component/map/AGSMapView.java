package com.component.map;

import android.annotation.SuppressLint;
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
import com.esri.arcgisruntime.geometry.SpatialReference;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        webTiledLayer.loadAsync();
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
//        mapView.setMap(arcGISMap);
//        mapView.setViewpoint(new Viewpoint(31.336, 118.386, 10000));

        initCallout();

        locationDisplay = mapView.getLocationDisplay();

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

                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(motionEvent.getX()),
                        Math.round(motionEvent.getY()));
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
                                //循环图层
                                for (IdentifyGraphicsOverlayResult identifyLayerResult : identifyLayerResults) {

                                    for (final Graphic graphic : identifyLayerResult.getGraphics()) {
                                        Map<String, Object> attributes = graphic.getAttributes();
                                        Log.d("AGS", "当前点击的sss" + graphic.getAttributes());
                                    }

                                    //循环所点击要素
                                    for (final GeoElement geoElement : identifyLayerResult.getGraphics()) {
                                        Log.d("AGS", "当前点击的" + geoElement.getAttributes());

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

                                        Map<String, Object> attributes = geoElement.getAttributes();
//                                        Iterator iterator = attributes.keySet().iterator();
//                                        while (iterator.hasNext()) {
//                                            Object objKey = iterator.next();
//                                            Object objValue = attributes.get(objKey);
//                                            TextView textView = new TextView(getContext().getApplicationContext());
//                                            textView.setTextColor(Color.BLACK);
//                                            textView.setSingleLine();
//                                            textView.setText(objKey + ": " + objValue);
//                                            linearLayout.addView(textView);
//                                        }
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
                                }
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

                // get the point that was clicked and convert it to a point in map coordinates


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

    private void initCallout() {
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
     * 定位
     */
    public void center() {
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        locationDisplay.setShowLocation(true);
        locationDisplay.setShowAccuracy(true);//隐藏符号的缓存区域
        locationDisplay.setShowPingAnimation(true);//隐藏位置更新的符号动画
        locationDisplay.startAsync();
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
    public Symbol getSymbol(GeometryType geometryType, ReadableMap properties) {
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
                SimpleMarkerSymbol.Style symbolStyle = SimpleMarkerSymbol.Style.CIRCLE;
                int color = Color.rgb(226, 119, 40);
                float size = 10.0f;
                if (null != properties) {
                    if (!TextUtils.isEmpty(properties.getString("marker-symbol"))) {
                        try {
                            symbolStyle = SimpleMarkerSymbol.Style.valueOf(properties.getString("marker-symbol").toUpperCase());
                        } catch (IllegalArgumentException e) {
                        }
                    }
                    if (!TextUtils.isEmpty(properties.getString("marker-color"))) {
                        try {
                            color = Color.parseColor(properties.getString("marker-color"));
                        } catch (IllegalArgumentException e) {
                        }
                    }
                    boolean flag = false;
                    if (!Double.isNaN(properties.getDouble("marker-size"))) {
                        size = (float) properties.getDouble("marker-size");
                        flag = true;
                    }
                    if (!flag && !TextUtils.isEmpty(properties.getString("marker-size"))) {
                        size = Float.parseFloat(properties.getString("marker-size"));
                    }
                }

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

    /**
     * ArcGIS的Geometry添加到图层上图
     *
     * @param geometryJson
     */
    public void addGeometry(ReadableMap geometryJson) {
        String geometry = geometryJson.getString("geometry");
        ReadableMap properties = geometryJson.getMap("properties");
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        Geometry g = Geometry.fromJson(geometry);
        Graphic graphic = new Graphic(g, getSymbol(g.getGeometryType(), properties));
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

        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f));
//        Graphic pointGraphic = new Graphic(point, pointSymbol);
//        Graphic pointGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), properties.toHashMap(), pointSymbol);
        Map<String, Object> attributesMap = getAttributesMap(properties);
        Graphic pointGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), attributesMap, getSymbol(GeometryType.POINT, properties));
        graphicsOverlay.getGraphics().add(pointGraphic);
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        Point point = new Point(coordinates.getDouble(0), coordinates.getDouble(1), SpatialReferences.getWgs84());
        Viewpoint viewpoint = new Viewpoint(point, 10000);
        mapView.setViewpointAsync(viewpoint);
    }

    public Map<String, Object> getAttributesMap(ReadableMap properties) {
        if(null == properties){
            return null;
        }
        Map<String, Object> map = properties.toHashMap();
        for(Map.Entry<String, Object> entry : map.entrySet()){
            if(entry.getKey().equals("marker-color")){
                map.remove(entry);
            }
        }


        return map;
    }

    public void addPolyline(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        ReadableMap properties = geoJson.getMap("properties");
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        ReadableArray coordinates = geometry.getArray("coordinates");
        int size = coordinates.size();

        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("name", "芜湖市公安局-线");
        attributes.put("address", "赤铸山路-线");

        if (size > 0) {
            PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
            for (int i = 0; i < size; i++) {
                ReadableArray polygonCoordinates = coordinates.getArray(i);
                polylinePoints.add(new Point(polygonCoordinates.getDouble(0), polygonCoordinates.getDouble(1)));
            }
            SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 1.0f);
            Graphic polylineGraphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), attributes, polylineSymbol);
            graphicsOverlay.getGraphics().add(polylineGraphic);
            mapView.getGraphicsOverlays().add(graphicsOverlay);

            Viewpoint viewpoint = viewpointFromGeometry(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)));
            mapView.setViewpointAsync(viewpoint);
        }
    }

    public void addPolygon(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        ReadableMap properties = geoJson.getMap("properties");
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        ReadableArray coordinates = geometry.getArray("coordinates");
        int size = coordinates.size();
        Log.i("AGS", String.valueOf(size));

        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("name", "芜湖市公安局-面");
        attributes.put("address", "赤铸山路-面赤铸山路-面赤铸山路-面");
        attributes.put("address2", "赤铸山路-面赤铸山路-面赤铸山路-面");
        attributes.put("address3", "赤铸山路-面赤铸山路-面赤铸山路-面");
        attributes.put("address4", "赤铸山路-面赤铸山路-面赤铸山路-面");

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
            Graphic graphic = new Graphic(Geometry.fromJson(parseGeoJsonToGeometry(geoJson)), attributes, fillSymbol);
//            Graphic graphic = new Graphic(polygon, fillSymbol);
            graphicsOverlay.getGraphics().add(graphic);
            mapView.getGraphicsOverlays().add(graphicsOverlay);

            Viewpoint viewpoint = viewpointFromPolygon(polygon);
//            Viewpoint viewpoint = new Viewpoint(centerPoint, 10000);
            mapView.setViewpointAsync(viewpoint);

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
