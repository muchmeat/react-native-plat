import React, {Component} from "react";
import {Dimensions, PixelRatio, Text, TextInput, TouchableHighlight, View} from "react-native";
import PropTypes from "prop-types";
import Svg from "react-native-svg";
import IconLib from "../../resources/svg/IconLib";
import {isEmpty} from "./common";

export default class InputItem extends Component{

    constructor(props) {
        super(props)
        this.state = {
            value: props.value,
            hasError: false,
            error: null
        };
    }

    static propTypes = {
        placeholder: PropTypes.string,
        label: PropTypes.string.isRequired,
        validators: PropTypes.array,
        value: PropTypes.string,
        isMaybe: PropTypes.boolean,
        hasBorder: PropTypes.boolean,
        setValue: PropTypes.func.isRequired,
        validate: PropTypes.func,
        keyboardType: PropTypes.oneOf(['default', 'number-pad', 'decimal-pad', 'numeric', 'email-address', 'phone-pad'])
    };

    // 属性默认值
    static defaultProps = {
        value: null,
        isMaybe: true,//默认非必填
        hasBorder: true,//默认存在底边线
        keyboardType: 'default'
    };

    /**
     * 私有方法 设置值
     * @param value
     * @private
     */
    _setValue(value) {
        let {setValue} = this.props;
        this.setState({value: value}, () => {
            console.warn(value)
            setValue(value);
        })
    }

    /**
     * 对外接口
     * 获取当前组件值
     * @returns {*}
     */
    getValue() {
        return this.state.value;
    }

    validate() {
        let {value} = this.state;
        let {isMaybe, validate, label} = this.props;
        let error = null, hasError = false;
        if (!isEmpty(value)) {
            if (validate) {
                let r = validate(value);
                console.warn(r)
                if (!r) {
                    hasError = true;
                    error = "请输入正确的" + label;
                }
            }
        } else {
            if (!isMaybe) {
                hasError = true;
                error = "不能为空";
            }
        }
        this.setState({hasError: hasError, error: error});
        // this.setState({hasError: true, error: "不能为空"});
        // hasError = true;
        return hasError;
    }

    render() {
        let {label, placeholder, keyboardType, hasBorder} = this.props;
        let {value, hasError, error} = this.state;
        return <View style={styles.paddingContainer}>
            <View style={[styles.rowContainer, hasBorder ? styles.borderBottom : null]}>
                <View style={styles.leftContainer}>
                    <Text style={styles.label}>{label}</Text>
                </View>
                <View style={styles.rightContainer}>
                    <View style={styles.rightRowContainer}>
                        <TextInput style={[styles.label, isEmpty(value) ? {color: "#888888"} : {color: "#222222"}]}
                                   value={value}
                                   keyboardType={keyboardType}
                                   underlineColorAndroid="transparent"
                                   onChangeText={(text) => {
                                       this._setValue(text)
                                   }}
                                   placeholder={placeholder}/>
                        {
                            value ?
                                <TouchableHighlight activeOpacity={0.8} underlayColor='transparent'
                                                    onPress={() => {
                                                        this._setValue(null)
                                                    }}>
                                    <View style={styles.clear.svgContainer}>
                                        <Svg height={styles.clear.svg.height} width={styles.clear.svg.width}
                                             viewBox="0 0 1024 1024">{IconLib.IC_CLEAR}</Svg>
                                    </View>
                                </TouchableHighlight>
                                : null
                        }
                    </View>
                    {
                        hasError ?
                            <View style={styles.error.container}>
                                <Svg height={styles.error.svg.height} width={styles.error.svg.width}
                                     viewBox="0 0 1024 1024">
                                    {IconLib.FORM_ERROR}
                                </Svg>
                                <View style={styles.error.textContainer}>
                                    <Text accessibilityLiveRegion="polite"
                                          style={styles.error.text}>
                                        {error}
                                    </Text>
                                </View>
                            </View> : null
                    }

                </View>
            </View>
        </View>
    }
}
const styles = Object.freeze({
    screen: {
        height: Dimensions.get("window").height,
        width: Dimensions.get("window").width
    },
    paddingContainer: {paddingHorizontal: 14},
    rowContainer: {
        flexDirection: "row",
        // backgroundColor: "#f72fff"
    },
    leftContainer: {
        width: 100,
        height: 47,
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "center",
        // backgroundColor: "#5de4ff"
    },
    label: {flex: 1, fontSize: 16, color: "#444444"},
    rightContainer: {
        flex: 1,
        // backgroundColor: "#fc5eff"
    },
    rightRowContainer: {
        height: 47,
        flexDirection: "row",
        alignItems: "center",
        // backgroundColor: "#443bff"
    },
    borderBottom: {
        borderBottomColor: "#DBDFE2",
        borderBottomWidth: 1 / PixelRatio.get(),
    },
    clear: {
        svg: {
            height: 16, width: 16
        },
        svgContainer: {
            height: 47, width: 20, justifyContent: "center", alignItems: "center"
        }
    },
    error: {
        container: {
            flexDirection: "row",
            justifyContent: "flex-start",
            alignItems: "center",
            paddingVertical: 2,
            paddingLeft: 5,
            // backgroundColor:"#5dfffa"
        },
        svg: {
            height: 14, width: 14
        },
        textContainer: {paddingLeft: 5},
        text: {marginRight: 5, fontSize: 14, color: "#a94442"}
    }
});

