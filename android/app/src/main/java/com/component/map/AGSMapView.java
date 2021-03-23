package com.component.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Part;
import com.esri.arcgisruntime.geometry.PartCollection;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.plat.R;

public class AGSMapView extends LinearLayout {

    Context context;
    View rootView;
    public MapView mapView;
    private ArcGISMap arcGISMap;
    private Double minZoom = 1.0;
    private Double maxZoom = 10.0;

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
        WebTiledLayer webTiledLayer = TDTLayerUtil.CreateTianDiTuTiledLayer(TDTLayerUtil.LayerType.TIANDITU_VECTOR_MERCATOR);
//        webTiledLayer.loadAsync();
        Basemap basemap = new Basemap(webTiledLayer);
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
        mapView.setViewpoint(new Viewpoint(31.336, 118.386, 10000));

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

    public void setCenter(@Nullable ReadableMap center) {
        Point centerPoint;
        if (center != null) {
            centerPoint = new Point(center.getDouble("latitude"), center.getDouble("longitude"), SpatialReferences.getWgs84());
        } else {
            centerPoint = mapView.getLocationDisplay().getLocation().getPosition();
        }
        Viewpoint vp = new Viewpoint(centerPoint, 10000);
        mapView.getMap().setInitialViewpoint(vp);
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
        Graphic pointGraphic = new Graphic(point, pointSymbol);
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
            Polyline polyline = new Polyline(polylinePoints);
            SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 3.0f);
            Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
            graphicsOverlay.getGraphics().add(polylineGraphic);
            mapView.getGraphicsOverlays().add(graphicsOverlay);
        }
    }

    public void addPolygon(ReadableMap geoJson) {
        ReadableMap geometry = geoJson.getMap("geometry");
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        ReadableArray coordinates = geometry.getArray("coordinates");
        int size = coordinates.size();
        Log.d("AGS", String.valueOf(size));
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
            Graphic graphic = new Graphic(polygon, fillSymbol);
            graphicsOverlay.getGraphics().add(graphic);
            mapView.getGraphicsOverlays().add(graphicsOverlay);
//            for (int i = 0; i < size; i++) {
//                SimpleFillSymbol fillSymbol =
//                        new SimpleFillSymbol(
//                                SimpleFillSymbol.Style.SOLID,
//                                Color.argb(Math.round(100), 1, 21, 244),
//                                outlineSymbol);
//                ReadableArray polygonCoordinates = coordinates.getArray(i);
//                PointCollection points = new PointCollection(SpatialReferences.getWgs84());
//                for (int j = 0; j < polygonCoordinates.size(); j++) {
//                    ReadableArray coordinate = polygonCoordinates.getArray(j);
//                    points.add(coordinate.getDouble(0), coordinate.getDouble(1));
//                }
//                Polygon polygon = new Polygon(points);
//                Graphic graphic = new Graphic(polygon, fillSymbol);
//                graphicsOverlay.getGraphics().add(graphic);
//                mapView.getGraphicsOverlays().add(graphicsOverlay);
//            }
        }
    }

    public int getResId(String name) {
        Resources r = context.getResources();
        int id = r.getIdentifier(name, "drawable", context.getPackageName());
        return id;
    }
}
