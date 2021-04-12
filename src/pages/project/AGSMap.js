/**
 * 地图
 * @author：czq
 */
import React, {Component} from 'react';
import {
    StyleSheet,
    View,
    TouchableOpacity,
    Text
} from 'react-native';
import {connect} from 'react-redux';// 引入connect函数
import Svg from 'react-native-svg';
import AGSMapView from "../../base/map/AGSMapView";
import IconLib from "../../resources/svg/IconLib";

class AGSMap extends Component {
    constructor(props) {
        super(props);
    }

    static navigationOptions = ({navigation}) => {
        return {
            title: 'ArcGIS地图',
            headerStyle: {
                borderBottomWidth: 0,
                backgroundColor: "#2E84E6",
                elevation: 0,
            }
        }
    };

    componentDidMount() {
    }

    render() {
        return <View style={{flex: 1}}>
            <AGSMapView style={{flex: 1}}
                        ref={(ref) => this.map = ref}

                        initialMapCenter={[
                            [
                                118.36850166320801,
                                31.3734800138603
                            ],
                            [
                                118.36855530738829,
                                31.372344143074773
                            ],
                            [
                                118.37855530738829,
                                31.382344143074773
                            ]
                        ]}
                        onGraphicClick={(res)=>{
                            console.warn(JSON.stringify(res))
                        }}
            />
            <TouchableOpacity onPress={() => {
                this.map.zoomIn();
            }} style={{
                backgroundColor: "#70e9ff",
                position: "absolute",
                width: 20,
                height: 20,
                top: 10,
                right: 10
            }}>
                <Text>放大</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => {
                this.map.zoomOut();
            }} style={{
                backgroundColor: "#bcff80",
                position: "absolute",
                width: 20,
                height: 20,
                top: 35,
                right: 10
            }}>
                <Text>缩小</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
                this.map.addMarker({
                    "type": "Feature",
                    "properties": {
                        "类型": "点",
                        "marker-color": "#bb5eff",
                        "marker-size": 30,
                        "marker-symbol": "DIAMOND",
                        "marker-opacity": "0.5"
                    },
                    "geometry": {
                        "type": "Point",
                        "coordinates": [
                            118.42643737792969,
                            31.34923901780546
                        ]
                    }
                });
            }} style={{
                backgroundColor: "#ff3844",
                position: "absolute",
                width: 20,
                height: 20,
                top: 100,
                right: 10
            }}><Text>点</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
                let geoJson =
                    {
                        "type": "Feature",
                        "properties": {
                            "name": "莲塘新村",
                            "line-symbol": "DASH_DOT",
                            "line-color": "#575bc0",
                            "line-width": 5.3,
                            "line-opacity": 0.5
                        },
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [
                                [
                                    118.36850166320801,
                                    31.3734800138603
                                ],
                                [
                                    118.36855530738829,
                                    31.372344143074773
                                ],
                                [
                                    118.37855530738829,
                                    31.382344143074773
                                ]
                            ]
                        }
                    };
                this.map.addPolyline(geoJson);
            }} style={{
                backgroundColor: "#ff74df",
                position: "absolute",
                width: 20,
                height: 20,
                top: 130,
                right: 10
            }}>
                <Text>线</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
                let geoJson =
                    {
                        "type": "Feature",
                        "properties": {
                            name: "莲塘新村"
                        },
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [118.36850166320801, 31.3734800138603],
                                    [118.36855530738829, 31.372344143074773],
                                    [118.36999297142029, 31.37238994458178],
                                    [118.3700680732727, 31.371061691814667],
                                    [118.37320089340211, 31.371217418972723],
                                    [118.37295413017272, 31.374359388264196],
                                    [118.37042212486269, 31.374194506190342],
                                    [118.37037920951842, 31.37368153788809],
                                    [118.36850166320801, 31.3734800138603]
                                ]
                            ]
                        }
                    };
                //一个有孔单面。
                // 这个数组的第一个元素表示的是外部环。其他后续的元素表示的内部环（或者孔）
                let geoJson2 =
                    {
                        "type": "Feature",
                        "properties": {
                            "name": "莲塘新村",
                            "line-symbol": "DASH_DOT",
                            "line-color": "#575bc0",
                            "line-width": 5.3,
                            "fill-color": "#c04597",
                        },
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [118.36850166320801, 31.3734800138603],
                                    [118.36855530738829, 31.372344143074773],
                                    [118.36999297142029, 31.37238994458178],
                                    [118.3700680732727, 31.371061691814667],
                                    [118.37320089340211, 31.371217418972723],
                                    [118.37295413017272, 31.374359388264196],
                                    [118.37042212486269, 31.374194506190342],
                                    [118.37037920951842, 31.37368153788809],
                                    [118.36850166320801, 31.3734800138603]
                                ],
                                [
                                    [118.36997151374815, 31.37250902839554],
                                    [118.3700144290924, 31.371602155550885],
                                    [118.37158083915709, 31.37172124036301],
                                    [118.37137699127196, 31.37260063122653],
                                    [118.36997151374815, 31.37250902839554]
                                ],
                                [
                                    [118.37227821350098, 31.37293956092466],
                                    [118.37240695953369, 31.372298341545427],
                                    [118.37317943572998, 31.372325822465733],
                                    [118.37287902832033, 31.372957881414035],
                                    [118.37227821350098, 31.37293956092466]
                                ],
                                [
                                    [118.37058305740358, 31.36990747071634],
                                    [118.3708620071411, 31.36867995018374],
                                    [118.37390899658203, 31.3691929457893],
                                    [118.37358713150024, 31.370292212657603],
                                    [118.37058305740358, 31.36990747071634]
                                ]
                            ]
                        }
                    };
                // this.map.addPolygon(geoJson);
                this.map.addPolygon(geoJson2);
            }} style={{
                backgroundColor: "#8bffa9",
                position: "absolute",
                width: 20,
                height: 20,
                top: 160,
                right: 10
            }}>
                <Text>面</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
                let circleParams = {
                    "center": [118.43643737792969, 31.35923901780546],
                    "radius": 5000,
                    "properties": {
                        "fill-color": "#c04597",
                        "fill-opacity": 0.5,
                        "地点":"区域中心"
                    }
                };
                this.map.addCircle(circleParams);
            }} style={{
                backgroundColor: "#9fbbff",
                position: "absolute",
                width: 20,
                height: 20,
                top: 190,
                right: 10
            }}>
                <Text>圆</Text>
            </TouchableOpacity>

            {/*左*/}
            <TouchableOpacity onPress={() => {
                this.map.drawPoint();
            }} style={{
                backgroundColor: "#70e9ff",
                position: "absolute",
                alignItems: "center",
                justifyContent: "center",
                width: 30,
                height: 30,
                borderRadius: 15,
                top: 10,
                left: 10
            }}>
                <Text>点</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
                this.map.drawPolyline();
            }} style={{
                backgroundColor: "#ff9d66",
                position: "absolute",
                alignItems: "center",
                justifyContent: "center",
                width: 30,
                height: 30,
                borderRadius: 15,
                top: 50,
                left: 10
            }}>
                <Text>线</Text>
            </TouchableOpacity>

            <TouchableOpacity onPress={() => {
                this.map.drawPolygon();
            }} style={{
                backgroundColor: "#bb5eff",
                position: "absolute",
                alignItems: "center",
                justifyContent: "center",
                width: 30,
                height: 30,
                borderRadius: 15,
                top: 90,
                left: 10
            }}>
                <Text>面</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => {
                this.map.drawStop((res) => {
                    console.warn(res)
                    this.map.addGeometry({geometry: res})
                });
            }} style={{
                backgroundColor: "#a2ff77",
                position: "absolute",
                alignItems: "center",
                justifyContent: "center",
                width: 30,
                height: 30,
                borderRadius: 15,
                top: 130,
                left: 10
            }}>
                <Text>stop</Text>
            </TouchableOpacity>

            {/*下*/}
            <TouchableOpacity onPress={() => {
                this.map.location();
            }} style={{
                backgroundColor: "#FFF",
                position: "absolute",
                width: 40,
                height: 40,
                borderRadius: 20,
                alignItems: "center",
                justifyContent: "center",
                bottom: 10,
                right: 10
            }}>
                <Svg height={20} width={20} viewBox="0 0 1024 1024">
                    {IconLib.IC_CURRENT_POSITION}
                </Svg>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => {
                this.map.changeBaseMap({layerType: "TDT_IMAGE_MERCATOR"});
            }} style={{
                backgroundColor: "#FFF",
                position: "absolute",
                width: 40,
                height: 40,
                borderRadius: 20,
                alignItems: "center",
                justifyContent: "center",
                bottom: 10,
                right: 60
            }}>
                <Svg height={20} width={20} viewBox="0 0 1024 1024">
                    {IconLib.IC_SATELLITE}
                </Svg>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => {
                this.map.changeBaseMap({layerType: "TDT_VECTOR_MERCATOR"});
            }} style={{
                backgroundColor: "#FFF",
                position: "absolute",
                width: 40,
                height: 40,
                borderRadius: 20,
                alignItems: "center",
                justifyContent: "center",
                bottom: 10,
                right: 110
            }}>
                <Svg height={20} width={20} viewBox="0 0 1024 1024">
                    {IconLib.IC_STANDARD_MAP}
                </Svg>
            </TouchableOpacity>
        </View>;
    }
}


export default connect(
    (state) => ({
        user: state.accountReducer.user,
        dictionary: state.accountReducer.dictionary
    }),
    (dispatch) => ({})
)(AGSMap)
