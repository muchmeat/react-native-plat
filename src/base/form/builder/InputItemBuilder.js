import React from "react";
import {BaseComponentBuilder} from "./BaseComponentBuilder";
import InputItem from "../InputItem";

/**
 *  输入框组件
 */
export class InputItemBuilder extends BaseComponentBuilder {

    constructor() {
        super("InputItem", (item, target) => {
            return <InputItem ref={(ref) => {
                target[item.key] = ref;
            }}

                              label={item.label}
                              isMaybe={false}
                              placeholder={item.placeholder}
                              setValue={(text) => {
                                  target.setState({[item.key]: text})
                              }}
                              key={'form_key_' + item.key}/>
        });
    }
}

