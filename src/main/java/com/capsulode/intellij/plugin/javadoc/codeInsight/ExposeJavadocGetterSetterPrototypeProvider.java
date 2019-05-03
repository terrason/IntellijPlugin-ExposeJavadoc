package com.capsulode.intellij.plugin.javadoc.codeInsight;

import com.intellij.codeInsight.generation.GetterSetterPrototypeProvider;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;

/**
 * ExposeJavadocGetterSetterPrototypeProvider
 *
 * @author lipei
 */
public class ExposeJavadocGetterSetterPrototypeProvider extends GetterSetterPrototypeProvider {

    @Override
    public boolean canGeneratePrototypeFor(PsiField field) {
        return true;
    }

    @Override
    public PsiMethod[] generateGetters(PsiField field) {
        return new PsiMethod[]{
                ExposeJavadocGenerateMemberUtil.generateGetterPrototype(field)
        };
    }

    @Override
    public PsiMethod[] generateSetters(PsiField field) {
        return new PsiMethod[]{
                ExposeJavadocGenerateMemberUtil.generateSetterPrototype(field)
        };
    }

    @Override
    public boolean isReadOnly(PsiField field) {
        return field.hasModifierProperty(PsiModifier.FINAL);
    }
}
