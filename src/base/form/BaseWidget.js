import React, {Component} from "react";
import {Dimensions, PixelRatio, Text, TextInput, TouchableHighlight, View} from "react-native";
import PropTypes from "prop-types";
import Svg from "react-native-svg";
import IconLib from "../../resources/svg/IconLib";
import {isEmpty} from "./common";

export default class BaseWidget extends Component{

    constructor(props) {
        super(props);
        this.state = {
            value: props.value,
            hasError: false,
            error: null
        };
    }


    /**
     * 私有方法 设置值
     * @param value
     * @private
     */
    _setValue(value) {
        let {setValue} = this.props;
        this.setState({value: value}, () => {
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

    /**
     * 验证
     * @returns {boolean}
     */
    validate() {
        let error = null, hasError = false;
        this.setState({hasError: hasError, error: error});
        return hasError;
    }

}

