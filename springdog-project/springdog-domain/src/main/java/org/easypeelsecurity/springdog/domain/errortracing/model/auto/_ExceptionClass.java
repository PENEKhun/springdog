package org.easypeelsecurity.springdog.domain.errortracing.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.BaseProperty;
import org.apache.cayenne.exp.property.EntityProperty;
import org.apache.cayenne.exp.property.NumericProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionType;

/**
 * Class _ExceptionClass was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ExceptionClass extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final String ID_PK_COLUMN = "ID";

    public static final StringProperty<String> EXCEPTION_CLASS_NAME = PropertyFactory.createString("exceptionClassName", String.class);
    public static final NumericProperty<Long> ID = PropertyFactory.createNumeric("id", Long.class);
    public static final BaseProperty<Boolean> MONITORING_ENABLED = PropertyFactory.createBase("monitoringEnabled", Boolean.class);
    public static final EntityProperty<ExceptionType> ROOT_EXCEPTION_PACKAGE_TYPE = PropertyFactory.createEntity("rootExceptionPackageType", ExceptionType.class);

    protected String exceptionClassName;
    protected long id;
    protected boolean monitoringEnabled;

    protected Object rootExceptionPackageType;

    public void setExceptionClassName(String exceptionClassName) {
        beforePropertyWrite("exceptionClassName", this.exceptionClassName, exceptionClassName);
        this.exceptionClassName = exceptionClassName;
    }

    public String getExceptionClassName() {
        beforePropertyRead("exceptionClassName");
        return this.exceptionClassName;
    }

    public void setId(long id) {
        beforePropertyWrite("id", this.id, id);
        this.id = id;
    }

    public long getId() {
        beforePropertyRead("id");
        return this.id;
    }

    public void setMonitoringEnabled(boolean monitoringEnabled) {
        beforePropertyWrite("monitoringEnabled", this.monitoringEnabled, monitoringEnabled);
        this.monitoringEnabled = monitoringEnabled;
    }

	public boolean isMonitoringEnabled() {
        beforePropertyRead("monitoringEnabled");
        return this.monitoringEnabled;
    }

    public void setRootExceptionPackageType(ExceptionType rootExceptionPackageType) {
        setToOneTarget("rootExceptionPackageType", rootExceptionPackageType, true);
    }

    public ExceptionType getRootExceptionPackageType() {
        return (ExceptionType)readProperty("rootExceptionPackageType");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "exceptionClassName":
                return this.exceptionClassName;
            case "id":
                return this.id;
            case "monitoringEnabled":
                return this.monitoringEnabled;
            case "rootExceptionPackageType":
                return this.rootExceptionPackageType;
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
            case "exceptionClassName":
                this.exceptionClassName = (String)val;
                break;
            case "id":
                this.id = val == null ? 0 : (long)val;
                break;
            case "monitoringEnabled":
                this.monitoringEnabled = val == null ? false : (boolean)val;
                break;
            case "rootExceptionPackageType":
                this.rootExceptionPackageType = val;
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
        out.writeObject(this.exceptionClassName);
        out.writeLong(this.id);
        out.writeBoolean(this.monitoringEnabled);
        out.writeObject(this.rootExceptionPackageType);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.exceptionClassName = (String)in.readObject();
        this.id = in.readLong();
        this.monitoringEnabled = in.readBoolean();
        this.rootExceptionPackageType = in.readObject();
    }

}
