import InputItem from "../InputItem";
import InputWidget from "../InputWidget";

/**
 * 组件库
 */
export class ComponentFactory {

    static componentBuilderCache = {
        InputItem,
        InputWidget
    };

    static getComponent(item) {
        let component = ComponentFactory.componentBuilderCache[item.type];
        if (!component) {
            console.warn(`Form component ${item.type} is not exist`)
            return null;
        }
        return component;
    }

}


