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
import {connect} from 'react-redux';// 引入connect函数
import Form from "../../../base/form";
import Button from "../../../base/Button";

class example extends Component {
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
        let items = [
            {
                type: "InputWidget",
                name: "text",
                label: "标题1",
                validations: (vv) => {
                    return {hasError: true, errorMsg: "errorMsg"}
                },
                placeholder: '请输入用户名text'
            },
            {
                type: "InputWidget",
                name: "text2",
                label: "标题2",
                value: "",
                placeholder: '请输入用户名text2'
            },
            {
                type: "InputWidget",
                name: "text3",
                label: "标题3",
                validations: ["isNotNull", "isInteger"],
                placeholder: '请输入用户名text3'
            }
        ];

        return (
            <View style={styles.container}>
                <Form ref={(ref) => {
                    this.form = ref;
                }}
                      items={items}
                />
                <Button text={"确定"}
                        onClick={() => {
                            let hasError = this.form.validate();
                            console.warn(hasError)
                            console.warn(this.form.getValue())
                        }}
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
)(example)
