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
    PixelRatio
} from 'react-native';
import {connect} from 'react-redux'; // 引入connect函数

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

    render() {
        let _this = this;
        return (
            <View style={styles.container}>
                <Text>example.js example.js example.js example.js</Text>
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
