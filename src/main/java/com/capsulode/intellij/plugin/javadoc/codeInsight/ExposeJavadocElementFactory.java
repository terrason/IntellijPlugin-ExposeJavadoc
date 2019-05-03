package com.capsulode.intellij.plugin.javadoc.codeInsight;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PropertyUtilBase;
import org.jetbrains.java.generate.element.FieldElement;
import org.jetbrains.java.generate.psi.PsiAdapter;

/**
 * ExposeJavadocElementFactory
 *
 * @author lipei
 */
public class ExposeJavadocElementFactory {

    /**
     * Create a new {@link FieldElement} object.
     *
     * @param field the {@link PsiField} to get the information from.
     * @return a new {@link FieldElement} object.
     */
    public static FieldElement newFieldElement(PsiField field, boolean useAccessor) {
        ExposeJavadocFieldElement fe = new ExposeJavadocFieldElement();
        fe.setName(field.getName());
        PsiDocComment docComment = field.getDocComment();
        if (docComment != null) fe.setJavadoc(docComment.getText());
        final PsiMethod getterForField = useAccessor ? PropertyUtilBase.findGetterForField(field) : null;
        fe.setAccessor(getterForField != null ? getterForField.getName() + "()" : field.getName());

        if (PsiAdapter.isConstantField(field)) fe.setConstant(true);
        if (PsiAdapter.isEnumField(field)) fe.setEnum(true);
        PsiModifierList modifiers = field.getModifierList();
        if (modifiers != null) {
            if (modifiers.hasModifierProperty(PsiModifier.TRANSIENT)) fe.setModifierTransient(true);
            if (modifiers.hasModifierProperty(PsiModifier.VOLATILE)) fe.setModifierVolatile(true);
        }

        PsiElementFactory factory = JavaPsiFacade.getInstance(field.getProject()).getElementFactory();
        PsiType type = field.getType();
        setElementInfo(fe, factory, type, modifiers);

        return fe;
    }


    /**
     * Sets the basic element information from the given type.
     *
     * @param element   the element to set information from the type
     * @param factory
     * @param type      the type
     * @param modifiers modifier list
     * @since 2.15
     */
    private static void setElementInfo(ExposeJavadocFieldElement element,
                                       PsiElementFactory factory,
                                       PsiType type,
                                       PsiModifierList modifiers) {

        // type names
        element.setTypeName(PsiAdapter.getTypeClassName(type));
        element.setTypeQualifiedName(PsiAdapter.getTypeQualifiedClassName(type));
        element.setType(type.getCanonicalText());

        // arrays, collections and maps types
        if (PsiAdapter.isObjectArrayType(type)) {
            element.setObjectArray(true);
            element.setArray(true);

            // additional specify if the element is a string array
            if (PsiAdapter.isStringArrayType(type)) element.setStringArray(true);

        } else if (PsiAdapter.isPrimitiveArrayType(type)) {
            element.setPrimitiveArray(true);
            element.setArray(true);
        }
        if (PsiAdapter.isCollectionType(factory, type)) element.setCollection(true);
        if (PsiAdapter.isListType(factory, type)) element.setList(true);
        if (PsiAdapter.isSetType(factory, type)) element.setSet(true);
        if (PsiAdapter.isMapType(factory, type)) element.setMap(true);

        // other types
        if (PsiAdapter.isPrimitiveType(type)) element.setPrimitive(true);
        if (PsiAdapter.isObjectType(factory, type)) element.setObject(true);
        if (PsiAdapter.isStringType(factory, type)) element.setString(true);
        if (PsiAdapter.isNumericType(factory, type)) element.setNumeric(true);
        if (PsiAdapter.isDateType(factory, type)) element.setDate(true);
        if (PsiAdapter.isCalendarType(factory, type)) element.setCalendar(true);
        if (PsiAdapter.isBooleanType(factory, type)) element.setBoolean(true);
        if (PsiType.VOID.equals(type)) element.setVoid(true);
        if (PsiType.LONG.equals(type)) element.setLong(true);
        if (PsiType.FLOAT.equals(type)) element.setFloat(true);
        if (PsiType.DOUBLE.equals(type)) element.setDouble(true);
        if (PsiType.BYTE.equals(type)) element.setByte(true);
        if (PsiType.CHAR.equals(type)) element.setChar(true);
        if (PsiType.SHORT.equals(type)) element.setShort(true);
        element.setNestedArray(PsiAdapter.isNestedArray(type));
        // modifiers
        if (modifiers != null) {
            if (modifiers.hasModifierProperty(PsiModifier.STATIC)) element.setModifierStatic(true);
            if (modifiers.hasModifierProperty(PsiModifier.FINAL)) element.setModifierFinal(true);
            if (modifiers.hasModifierProperty(PsiModifier.PUBLIC)) {
                element.setModifierPublic(true);
            } else if (modifiers.hasModifierProperty(PsiModifier.PROTECTED)) {
                element.setModifierProtected(true);
            } else if (modifiers.hasModifierProperty(PsiModifier.PACKAGE_LOCAL)) {
                element.setModifierPackageLocal(true);
            } else if (modifiers.hasModifierProperty(PsiModifier.PRIVATE)) element.setModifierPrivate(true);
        }
    }
}
