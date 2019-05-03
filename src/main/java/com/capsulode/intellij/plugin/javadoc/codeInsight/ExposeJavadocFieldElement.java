package com.capsulode.intellij.plugin.javadoc.codeInsight;

import org.jetbrains.java.generate.element.FieldElement;

/**
 * ExposeJavadocFieldElement
 *
 * @author lipei
 */
public class ExposeJavadocFieldElement extends FieldElement {
    private String javadoc;
    private boolean isConstant;
    private boolean isModifierTransient;
    private boolean isModifierVolatile;
    public String getJavadoc() {
        return javadoc;
    }

    void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

    /**
     * Does the field have a transient modifier?
     */
    public boolean isModifierTransient() {
        return isModifierTransient;
    }

    /**
     * Does the field have a volatile modifier?
     */
    public boolean isModifierVolatile() {
        return isModifierVolatile;
    }

    /**
     * Is the field a constant type?
     */
    public boolean isConstant() {
        return isConstant;
    }

    void setConstant(boolean constant) {
        isConstant = constant;
    }

    void setModifierTransient(boolean modifierTransient) {
        isModifierTransient = modifierTransient;
    }

    void setModifierVolatile(boolean modifierVolatile) {
        this.isModifierVolatile = modifierVolatile;
    }


    void setNumeric(boolean numeric) {
        isNumeric = numeric;
    }

    void setObject(boolean object) {
        isObject = object;
    }

    void setDate(boolean date) {
        isDate = date;
    }

    void setArray(boolean array) {
        isArray = array;
    }

    void setCollection(boolean collection) {
        isCollection = collection;
    }

    void setMap(boolean map) {
        isMap = map;
    }

    void setPrimitive(boolean primitive) {
        isPrimitive = primitive;
    }

    void setString(boolean string) {
        isString = string;
    }

    void setPrimitiveArray(boolean primitiveArray) {
        isPrimitiveArray = primitiveArray;
    }

    void setObjectArray(boolean objectArray) {
        isObjectArray = objectArray;
    }

    void setSet(boolean set) {
        isSet = set;
    }

    void setList(boolean list) {
        isList = list;
    }

    void setStringArray(boolean stringArray) {
        isStringArray = stringArray;
    }

    void setCalendar(boolean calendar) {
        isCalendar = calendar;
    }

    void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    void setTypeQualifiedName(String typeQualifiedName) {
        this.typeQualifiedName = typeQualifiedName;
    }

    void setModifierPublic(boolean modifierPublic) {
        isModifierPublic = modifierPublic;
    }

    void setModifierProtected(boolean modifierProtected) {
        isModifierProtected = modifierProtected;
    }

    void setModifierPrivate(boolean modifierPrivate) {
        isModifierPrivate = modifierPrivate;
    }

    void setModifierPackageLocal(boolean modifierPackageLocal) {
        isModifierPackageLocal = modifierPackageLocal;
    }

    void setModifierFinal(boolean modifierFinal) {
        isModifierFinal = modifierFinal;
    }

    void setModifierStatic(boolean modifierStatic) {
        isModifierStatic = modifierStatic;
    }

    public String toString() {
        return super.toString() + " ::: ExposeJavadocFieldElement{" +
                "isConstant=" + isConstant +
                ", isEnum=" + isEnum() +
                ", isModifierTransient=" + isModifierTransient +
                ", isModifierVolatile=" + isModifierVolatile +
                "}";
    }
}
