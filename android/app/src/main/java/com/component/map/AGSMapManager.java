package com.component.map;


import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;


public class AGSMapManager extends SimpleViewManager<AGSMapView> {

    public static final String REACT_CLASS = "AGSMapManager";

    private static final int COMMAND_ZOOM_IN = 1;
    private static final int COMMAND_ZOOM_OUT = 2;
    private static final int COMMAND_ADD_MARKER = 3;
    private static final int COMMAND_ADD_POLYLINE = 4;
    private static final int COMMAND_ADD_POLYGON = 5;
    private static final int COMMAND_ADD_CIRCLE = 6;
    private static final int COMMAND_DISPOSE = 7;

    private AGSMapView mAGSMapView;


    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AGSMapView createViewInstance(ThemedReactContext reactContext) {
        mAGSMapView = new AGSMapView(reactContext);
        return mAGSMapView;
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
        map.put("dispose", COMMAND_DISPOSE);
        return map;
    }

    @Override
    public void receiveCommand(AGSMapView root, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_ZOOM_IN: {
            }
            break;
            case COMMAND_ZOOM_OUT: {
            }
            break;
            case COMMAND_ADD_MARKER: {
                ReadableType readableType = Objects.requireNonNull(args).getType(0);
                if (readableType.equals(ReadableType.Array)) {
//                    mAGSMapView.addMarkers(Objects.requireNonNull(args).getArray(0));
                } else {
//                    mAGSMapView.addMarker(Objects.requireNonNull(args).getMap(0));
                }
            }
            break;
            case COMMAND_ADD_POLYLINE: {
                ReadableType readableType = Objects.requireNonNull(args).getType(0);
                if (readableType.equals(ReadableType.Array)) {
//                    mAGSMapView.addPolyline(Objects.requireNonNull(args).getArray(0));
                }
            }
            case COMMAND_ADD_POLYGON: {
                ReadableType readableType = Objects.requireNonNull(args).getType(0);
                if (readableType.equals(ReadableType.Array)) {
//                    mAGSMapView.addPolygon(Objects.requireNonNull(args).getArray(0));
                }
            }
            case COMMAND_ADD_CIRCLE: {
                ReadableType readableType = Objects.requireNonNull(args).getType(0);
                if (readableType.equals(ReadableType.Map)) {
//                    mAGSMapView.addCircle(Objects.requireNonNull(args).getMap(0));
                }
            }
            break;
            case COMMAND_DISPOSE: {
//                mAGSMapView.dispose();
            }
            break;
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
                .build();
    }


}
