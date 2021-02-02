import {ComponentFactory} from "../factory/ComponentFactory";

export class BaseComponentBuilder {

    constructor(type, buildComponent) {
        this.type = type;
        this.buildComponent = buildComponent;
        // ComponentFactory.addComponent(this);
    }

}
