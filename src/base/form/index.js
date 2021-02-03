import React, {Component} from "react";
import {View} from "react-native";
import {ComponentFactory} from "./factory/ComponentFactory";

export default class Form extends Component {

    constructor(props) {
        super(props);
        this.state = {
            items: this.props.items
        };
    }

    /**
     * 获取表单值
     */
    getValue() {
        let value = {};
        this.state.items.map((item, index) => {
            let cpt = this[item.name];
            if (cpt) {
                value[item.name] = cpt.getValue()
            }
        });
        return value
    }

    /**
     * 验证表单值
     * @returns {boolean}
     */
    validate() {
        let hasError = false;
        this.state.items.map((item, index) => {
            let cpt = this[item.name];
            if (cpt) {
                let validateHasError = cpt.validate();
                if (validateHasError) {
                    hasError = true
                }
            }
        });
        return hasError
    }

    /**
     * 获取表单组件
     * @param key
     * @returns {*}
     */
    getComponent(key) {
        return this[key];
    }

    /**
     * 获取表单组件
     * @returns {*}
     * @private
     */
    _generateForms() {
        return this.state.items.map((item, index) => {
            let Cpt = ComponentFactory.getComponent(item);
            if (Cpt) {
                return (
                    <Cpt key={'form_key_' + item.name}
                         ref={(ref) => {
                             this[item.name] = ref;
                         }}
                         setValue={(text) => {
                             this.setState({[item.name]: text})
                         }}
                         {...item}
                    />
                )
            }
        });
    }


    render() {
        return <View style={{flex: 1}}>
            {this._generateForms()}
        </View>
    }
}
