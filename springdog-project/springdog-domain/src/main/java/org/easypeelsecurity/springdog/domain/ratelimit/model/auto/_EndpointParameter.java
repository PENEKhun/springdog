package org.easypeelsecurity.springdog.domain.ratelimit.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.BaseProperty;
import org.apache.cayenne.exp.property.EntityProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;
import org.easypeelsecurity.springdog.domain.ratelimit.model.Endpoint;
import org.easypeelsecurity.springdog.shared.enums.EndpointParameterType;

/**
 * Class _EndpointParameter was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _EndpointParameter extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final String ID_PK_COLUMN = "ID";

    public static final BaseProperty<Boolean> ENABLED = PropertyFactory.createBase("enabled", Boolean.class);
    public static final StringProperty<String> NAME = PropertyFactory.createString("name", String.class);
    public static final BaseProperty<EndpointParameterType> TYPE = PropertyFactory.createBase("type", EndpointParameterType.class);
    public static final EntityProperty<Endpoint> ENDPOINT = PropertyFactory.createEntity("endpoint", Endpoint.class);

    protected boolean enabled;
    protected String name;
    protected EndpointParameterType type;

    protected Object endpoint;

    public void setEnabled(boolean enabled) {
        beforePropertyWrite("enabled", this.enabled, enabled);
        this.enabled = enabled;
    }

	public boolean isEnabled() {
        beforePropertyRead("enabled");
        return this.enabled;
    }

    public void setName(String name) {
        beforePropertyWrite("name", this.name, name);
        this.name = name;
    }

    public String getName() {
        beforePropertyRead("name");
        return this.name;
    }

    public void setType(EndpointParameterType type) {
        beforePropertyWrite("type", this.type, type);
        this.type = type;
    }

    public EndpointParameterType getType() {
        beforePropertyRead("type");
        return this.type;
    }

    public void setEndpoint(Endpoint endpoint) {
        setToOneTarget("endpoint", endpoint, true);
    }

    public Endpoint getEndpoint() {
        return (Endpoint)readProperty("endpoint");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "enabled":
                return this.enabled;
            case "name":
                return this.name;
            case "type":
                return this.type;
            case "endpoint":
                return this.endpoint;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "enabled":
                this.enabled = val == null ? false : (boolean)val;
                break;
            case "name":
                this.name = (String)val;
                break;
            case "type":
                this.type = (EndpointParameterType)val;
                break;
            case "endpoint":
                this.endpoint = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeBoolean(this.enabled);
        out.writeObject(this.name);
        out.writeObject(this.type);
        out.writeObject(this.endpoint);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.enabled = in.readBoolean();
        this.name = (String)in.readObject();
        this.type = (EndpointParameterType)in.readObject();
        this.endpoint = in.readObject();
    }

}
