package com.capsulode.intellij.plugin.javadoc.codeInsight;

import com.intellij.codeInsight.NullableNotNullManager;
import com.intellij.codeInsight.generation.GetterTemplatesManager;
import com.intellij.codeInsight.generation.SetterTemplatesManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.generate.exception.GenerateCodeException;
import org.jetbrains.java.generate.template.TemplatesManager;

import java.util.Collections;
import java.util.HashMap;

import static com.intellij.codeInsight.generation.GenerateMembersUtil.annotateOnOverrideImplement;
import static com.intellij.codeInsight.generation.GenerateMembersUtil.setVisibility;

/**
 * ExposeJavadocGenerateMemberUtil
 *
 * @author lipei
 */
class ExposeJavadocGenerateMemberUtil {

    static PsiMethod generateGetterPrototype(@NotNull PsiField field) {
        return generatePrototype(field, field.getContainingClass(), GetterTemplatesManager.getInstance());
    }

    static PsiMethod generateSetterPrototype(@NotNull PsiField field) {
        return generatePrototype(field, field.getContainingClass(), SetterTemplatesManager.getInstance());
    }

    private static PsiMethod generatePrototype(@NotNull PsiField field,
                                               PsiClass psiClass,
                                               TemplatesManager templatesManager) {
        Project project = field.getProject();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        String template = templatesManager.getDefaultTemplate().getTemplate();
        Function<String, String> calculateTemplateText =
                currentTemplate -> ExposeJavadocGenerationUtil.velocityGenerateCode(psiClass, Collections.singletonList(field), new HashMap<>(), currentTemplate, 0, false);
        String methodText = calculateTemplateText.fun(template);
        boolean isGetter = templatesManager instanceof GetterTemplatesManager;
        PsiMethod result;
        try {
            result = factory.createMethodFromText(methodText, psiClass);
        } catch (IncorrectOperationException e) {
            throw new GenerateCodeException(e);
        }
        result = (PsiMethod) CodeStyleManager.getInstance(project).reformat(result);

        PsiModifierListOwner annotationTarget;
        if (isGetter) {
            annotationTarget = result;
        } else {
            final PsiParameter[] parameters = result.getParameterList().getParameters();
            annotationTarget = parameters.length == 1 ? parameters[0] : null;
        }
        if (annotationTarget != null) {
            NullableNotNullManager.getInstance(project).copyNullableOrNotNullAnnotation(field, annotationTarget);
        }

        return generatePrototype(field, result);
    }

    @NotNull
    private static PsiMethod generatePrototype(@NotNull PsiField field, @NotNull PsiMethod result) {
        return setVisibility(field, annotateOnOverrideImplement(field.getContainingClass(), result));
    }

}
