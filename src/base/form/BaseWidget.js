import React, {Component} from "react";
import {isEmpty} from "./common";
import {ValidateUtil} from "./ValidateUtil";
import PropTypes from "prop-types";

export default class BaseWidget extends Component {

    constructor(props) {
        super(props);
        this.state = {
            value: props.value,
            hasError: false,
            errorMsg: null
        };
    }

    static propTypes = {
        validations: PropTypes.oneOfType([
            PropTypes.array,
            PropTypes.func
        ]),
        setValue: PropTypes.func.isRequired
    };

    /**
     * 私有方法 设置值
     * @param value
     * @private
     */
    _setValue(value) {
        let {setValue} = this.props;
        this.setState({value: value}, () => {
            setValue && setValue(value);
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
        let errorMsg = null, hasError = false;
        let {value} = this.state;
        if (typeof this.props.validations === "function") {
            let msg = this.props.validations(value);
            hasError = msg.hasError;
            errorMsg = msg.errorMsg;
        } else if (Object.prototype.toString.call(this.props.validations) === "[object Array]") {
            for (let validation of this.props.validations) {
                errorMsg = ValidateUtil[validation].call(null, value);
                if (!isEmpty(errorMsg)) {
                    hasError = true;
                    break;
                }
            }
        }
        this.setState({hasError: hasError, errorMsg: errorMsg});
        return hasError;
    }

}

