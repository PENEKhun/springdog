package org.easypeelsecurity.springdog.domain.errortracing.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.ListProperty;
import org.apache.cayenne.exp.property.NumericProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;
import org.easypeelsecurity.springdog.domain.errortracing.model.ExceptionClass;

/**
 * Class _ExceptionType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ExceptionType extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final String ID_PK_COLUMN = "ID";

    public static final StringProperty<String> DESCRIPTION = PropertyFactory.createString("description", String.class);
    public static final NumericProperty<Long> ID = PropertyFactory.createNumeric("id", Long.class);
    public static final StringProperty<String> PACKAGE_TYPE = PropertyFactory.createString("packageType", String.class);
    public static final ListProperty<ExceptionClass> EXCEPTION_CLASSES = PropertyFactory.createList("exceptionClasses", ExceptionClass.class);

    protected String description;
    protected long id;
    protected String packageType;

    protected Object exceptionClasses;

    public void setDescription(String description) {
        beforePropertyWrite("description", this.description, description);
        this.description = description;
    }

    public String getDescription() {
        beforePropertyRead("description");
        return this.description;
    }

    public void setId(long id) {
        beforePropertyWrite("id", this.id, id);
        this.id = id;
    }

    public long getId() {
        beforePropertyRead("id");
        return this.id;
    }

    public void setPackageType(String packageType) {
        beforePropertyWrite("packageType", this.packageType, packageType);
        this.packageType = packageType;
    }

    public String getPackageType() {
        beforePropertyRead("packageType");
        return this.packageType;
    }

    public void addToExceptionClasses(ExceptionClass obj) {
        addToManyTarget("exceptionClasses", obj, true);
    }

    public void removeFromExceptionClasses(ExceptionClass obj) {
        removeToManyTarget("exceptionClasses", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<ExceptionClass> getExceptionClasses() {
        return (List<ExceptionClass>)readProperty("exceptionClasses");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "description":
                return this.description;
            case "id":
                return this.id;
            case "packageType":
                return this.packageType;
            case "exceptionClasses":
                return this.exceptionClasses;
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
            case "description":
                this.description = (String)val;
                break;
            case "id":
                this.id = val == null ? 0 : (long)val;
                break;
            case "packageType":
                this.packageType = (String)val;
                break;
            case "exceptionClasses":
                this.exceptionClasses = val;
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
        out.writeObject(this.description);
        out.writeLong(this.id);
        out.writeObject(this.packageType);
        out.writeObject(this.exceptionClasses);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.description = (String)in.readObject();
        this.id = in.readLong();
        this.packageType = (String)in.readObject();
        this.exceptionClasses = in.readObject();
    }

}
