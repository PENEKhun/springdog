package org.easypeelsecurity.springdog.shared.ratelimit.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.BaseProperty;
import org.apache.cayenne.exp.property.EntityProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;
import org.easypeelsecurity.springdog.shared.ratelimit.model.EndpointVersionControl;

/**
 * Class _EndpointChangelog was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _EndpointChangelog extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final String ID_PK_COLUMN = "ID";

    public static final StringProperty<String> CHANGE_TYPE = PropertyFactory.createString("changeType", String.class);
    public static final StringProperty<String> DETAIL_STRING = PropertyFactory.createString("detailString", String.class);
    public static final BaseProperty<Boolean> IS_RESOLVED = PropertyFactory.createBase("isResolved", Boolean.class);
    public static final StringProperty<String> TARGET_FQCN = PropertyFactory.createString("targetFqcn", String.class);
    public static final StringProperty<String> TARGET_METHOD = PropertyFactory.createString("targetMethod", String.class);
    public static final StringProperty<String> TARGET_PATH = PropertyFactory.createString("targetPath", String.class);
    public static final EntityProperty<EndpointVersionControl> ENDPOINTVERSIONCONTROL = PropertyFactory.createEntity("endpointversioncontrol", EndpointVersionControl.class);

    protected String changeType;
    protected String detailString;
    protected boolean isResolved;
    protected String targetFqcn;
    protected String targetMethod;
    protected String targetPath;

    protected Object endpointversioncontrol;

    public void setChangeType(String changeType) {
        beforePropertyWrite("changeType", this.changeType, changeType);
        this.changeType = changeType;
    }

    public String getChangeType() {
        beforePropertyRead("changeType");
        return this.changeType;
    }

    public void setDetailString(String detailString) {
        beforePropertyWrite("detailString", this.detailString, detailString);
        this.detailString = detailString;
    }

    public String getDetailString() {
        beforePropertyRead("detailString");
        return this.detailString;
    }

    public void setIsResolved(boolean isResolved) {
        beforePropertyWrite("isResolved", this.isResolved, isResolved);
        this.isResolved = isResolved;
    }

	public boolean isIsResolved() {
        beforePropertyRead("isResolved");
        return this.isResolved;
    }

    public void setTargetFqcn(String targetFqcn) {
        beforePropertyWrite("targetFqcn", this.targetFqcn, targetFqcn);
        this.targetFqcn = targetFqcn;
    }

    public String getTargetFqcn() {
        beforePropertyRead("targetFqcn");
        return this.targetFqcn;
    }

    public void setTargetMethod(String targetMethod) {
        beforePropertyWrite("targetMethod", this.targetMethod, targetMethod);
        this.targetMethod = targetMethod;
    }

    public String getTargetMethod() {
        beforePropertyRead("targetMethod");
        return this.targetMethod;
    }

    public void setTargetPath(String targetPath) {
        beforePropertyWrite("targetPath", this.targetPath, targetPath);
        this.targetPath = targetPath;
    }

    public String getTargetPath() {
        beforePropertyRead("targetPath");
        return this.targetPath;
    }

    public void setEndpointversioncontrol(EndpointVersionControl endpointversioncontrol) {
        setToOneTarget("endpointversioncontrol", endpointversioncontrol, true);
    }

    public EndpointVersionControl getEndpointversioncontrol() {
        return (EndpointVersionControl)readProperty("endpointversioncontrol");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "changeType":
                return this.changeType;
            case "detailString":
                return this.detailString;
            case "isResolved":
                return this.isResolved;
            case "targetFqcn":
                return this.targetFqcn;
            case "targetMethod":
                return this.targetMethod;
            case "targetPath":
                return this.targetPath;
            case "endpointversioncontrol":
                return this.endpointversioncontrol;
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
            case "changeType":
                this.changeType = (String)val;
                break;
            case "detailString":
                this.detailString = (String)val;
                break;
            case "isResolved":
                this.isResolved = val == null ? false : (boolean)val;
                break;
            case "targetFqcn":
                this.targetFqcn = (String)val;
                break;
            case "targetMethod":
                this.targetMethod = (String)val;
                break;
            case "targetPath":
                this.targetPath = (String)val;
                break;
            case "endpointversioncontrol":
                this.endpointversioncontrol = val;
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
        out.writeObject(this.changeType);
        out.writeObject(this.detailString);
        out.writeBoolean(this.isResolved);
        out.writeObject(this.targetFqcn);
        out.writeObject(this.targetMethod);
        out.writeObject(this.targetPath);
        out.writeObject(this.endpointversioncontrol);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.changeType = (String)in.readObject();
        this.detailString = (String)in.readObject();
        this.isResolved = in.readBoolean();
        this.targetFqcn = (String)in.readObject();
        this.targetMethod = (String)in.readObject();
        this.targetPath = (String)in.readObject();
        this.endpointversioncontrol = in.readObject();
    }

}
