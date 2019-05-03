package com.capsulode.intellij.plugin.javadoc.codeInsight;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import org.jetbrains.java.generate.element.FieldElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ExposeJavadocElementUtils
 *
 * @author lipei
 */
public class ExposeJavadocElementUtils {

    /**
     * Gets the list of members to be put in the VelocityContext.
     *
     * @param members                a list of {@link PsiMember} objects.
     * @param selectedNotNullMembers
     * @param useAccessors
     * @return a filtered list of only the fields as {@link FieldElement} objects.
     */
    public static List<FieldElement> getOnlyAsFieldElements(Collection<? extends PsiMember> members,
                                                            Collection<? extends PsiMember> selectedNotNullMembers,
                                                            boolean useAccessors) {
        List<FieldElement> fieldElementList = new ArrayList<>();

        for (PsiMember member : members) {
            if (member instanceof PsiField) {
                PsiField field = (PsiField) member;
                FieldElement fe = ExposeJavadocElementFactory.newFieldElement(field, useAccessors);
                if (selectedNotNullMembers.contains(member)) {
                    fe.setNotNull(true);
                }
                fieldElementList.add(fe);
            }
        }

        return fieldElementList;
    }
}
