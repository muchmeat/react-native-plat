import React, {Component} from 'react';
import {
    View,
    Text,
    StyleSheet,
    TextInput,
    PixelRatio
} from 'react-native';
import {connect} from 'react-redux'; // 引入connect函数
import {TTS} from '../../../base/nativemodule';
import {TouchableOpacity} from "react-native-gesture-handler";
import {isEmpty} from "../../../base/form/common";

class tts extends Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    static navigationOptions = ({navigation}) => {
        return {
            title: '文字转语音',
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
                <TextInput style={{color: "#888888"}}
                           underlineColorAndroid="transparent"
                           onChangeText={(text) => {
                               this.setState({text: text})
                           }}
                           placeholder={"请输入文本"}/>
                <TouchableOpacity onPress={() => {
                    let {text} = this.state;
                    console.warn(text)
                    TTS.speak({text: text}, () => {
                    })
                }}>
                    <Text>文字转语音</Text>
                </TouchableOpacity>
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
)(tts)
