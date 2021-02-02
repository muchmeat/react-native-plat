import React, {Component} from 'react';
import PropTypes from "prop-types";
import {Text, TouchableOpacity} from 'react-native';

export default class Button extends Component {

    //定义暴露属性,进行类型检查
    static propTypes = {
        onClick: PropTypes.func,
        text: PropTypes.string.isRequired,
        textStyle: PropTypes.object,
        buttonStyle: PropTypes.object,
    };

    //默认属性
    static defaultProps = {
        buttonStyle: {
            height: 40,
            width: 100,
            borderRadius: 5,
            backgroundColor: "#158CFF",
            justifyContent: "center",
            alignItems: "center"
        },
        textStyle: {
            color: "#FFF",
            fontSize: 15
        }
    };

    render() {
        const {text, onClick, buttonStyle, textStyle} = this.props;
        return (
            <TouchableOpacity style={buttonStyle} onPress={onClick}>
                <Text style={textStyle}>{text}</Text>
            </TouchableOpacity>
        )
    }
}
