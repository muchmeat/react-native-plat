/**
 * 警情查询 查询条件页
 * @author:czq
 * @date:2020-11-30
 */
import React, {Component} from 'react';
import {
    View,
    Text,
    StyleSheet,
    PixelRatio,
    TouchableOpacity,
    FlatList
} from 'react-native';
import {connect} from 'react-redux'; // 引入connect函数
import {NavigationActions} from "react-navigation";

class example3 extends Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    static navigationOptions = ({navigation}) => {
        return {
            title: '示例',
            headerStyle: {
                borderBottomWidth: 0,
                alignItems: "center",
                backgroundColor: "#2E84E6",
                elevation: 0
            }
        }
    };

    _renderItem(props) {
        let _this = this;
        let item = props.item;
        let {navigation} = _this.props;
        return (
            <TouchableOpacity onPress={() => {
                navigation.dispatch(NavigationActions.navigate({
                    routeName: item.routeName
                }))
            }}>
                <View style={{flex: 1, paddingTop: 10, paddingHorizontal: 10}}>
                    <Text style={{color: "#191919"}}>
                        {item.title}
                    </Text>
                </View>
            </TouchableOpacity>
        )
    }

    render() {
        let _this = this;
        let pageList = [
            {title: "文字转语音", routeName: "tts"}
        ];
        return (
            <View style={styles.container}>
                <FlatList data={pageList}
                          renderItem={(item) => _this._renderItem(item)}
                          onEndReachedThreshold={0.2}
                />
            </View>
        )
    }


}

const styles = StyleSheet.create({
    container: {
        flex: 1
    },
    queryContainer: {backgroundColor: "#fff", margin: 12, borderRadius: 4},
    buttonContainer: {
        marginVertical: 12,
        justifyContent: "space-around",
        alignItems: "center",
        flexDirection: "row"
    },
    button: {
        height: 40,
        width: 100,
        borderRadius: 5,
        borderColor: "#158CFF",
        borderWidth: 1 / PixelRatio.get(),
        justifyContent: "center",
        alignItems: "center",
    },
    text: {color: "#158CFF", fontSize: 15}
});

export default connect(
    (state) => ({}),
    (dispatch) => ({})
)(example3)
