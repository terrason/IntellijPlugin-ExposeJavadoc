package com.capsulode.intellij.plugin.javadoc.codeInsight;

import com.intellij.application.options.CodeStyle;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.JavaCodeStyleSettings;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.containers.ContainerUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.generate.GenerateToStringWorker;
import org.jetbrains.java.generate.element.*;
import org.jetbrains.java.generate.exception.GenerateCodeException;
import org.jetbrains.java.generate.velocity.VelocityFactory;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ExposeJavadocGenerationUtil
 *
 * @author lipei
 */
class ExposeJavadocGenerationUtil {
    private static final Logger LOG = Logger.getInstance(ExposeJavadocGenerationUtil.class);

    /**
     * Generates the code using Velocity.
     * <p>
     * This is used to create the {@code toString} method body and it's javadoc.
     *
     * @param selectedMembers the selected members as both {@link PsiField} and {@link PsiMethod}.
     * @param params          additional parameters stored with key/value in the map.
     * @param templateMacro   the velocity macro template
     * @return code (usually Java). Returns null if templateMacro is null.
     * @throws GenerateCodeException is thrown when there is an error generating the code.
     */
    public static String velocityGenerateCode(PsiClass clazz,
                                              Collection<? extends PsiMember> selectedMembers,
                                              Map<String, String> params,
                                              String templateMacro,
                                              int sortElements,
                                              boolean useFullyQualifiedName)
            throws GenerateCodeException {
        return velocityGenerateCode(clazz, selectedMembers, Collections.emptyList(), params, Collections.emptyMap(), templateMacro, sortElements, useFullyQualifiedName, false);
    }


    /**
     * Generates the code using Velocity.
     * <p/>
     * This is used to create the {@code toString} method body and it's javadoc.
     *
     * @param selectedMembers the selected members as both {@link PsiField} and {@link PsiMethod}.
     * @param params          additional parameters stored with key/value in the map.
     * @param templateMacro   the velocity macro template
     * @param useAccessors    if true, accessor property for FieldElement bean would be assigned to field getter name append with ()
     * @return code (usually Java). Returns null if templateMacro is null.
     * @throws GenerateCodeException is thrown when there is an error generating the code.
     */
    public static String velocityGenerateCode(@Nullable PsiClass clazz,
                                              Collection<? extends PsiMember> selectedMembers,
                                              Collection<? extends PsiMember> selectedNotNullMembers,
                                              Map<String, String> params,
                                              Map<String, Object> contextMap,
                                              String templateMacro,
                                              int sortElements,
                                              boolean useFullyQualifiedName,
                                              boolean useAccessors)
            throws GenerateCodeException {
        if (templateMacro == null) {
            return null;
        }

        StringWriter sw = new StringWriter();
        try {
            VelocityContext vc = new VelocityContext();

            // field information
            LOG.debug("Velocity Context - adding fields");

            final List<FieldElement> fieldElements = ExposeJavadocElementUtils.getOnlyAsFieldElements(selectedMembers, selectedNotNullMembers, useAccessors);
            vc.put("fields", fieldElements);
            if (fieldElements.size() == 1) {
                vc.put("field", fieldElements.get(0));
            }

            PsiMember member = clazz != null ? clazz : ContainerUtil.getFirstItem(selectedMembers);

            // method information
            LOG.debug("Velocity Context - adding methods");
            vc.put("methods", ElementUtils.getOnlyAsMethodElements(selectedMembers));

            // element information (both fields and methods)
            LOG.debug("Velocity Context - adding members (fields and methods)");
            List<Element> elements = ElementUtils.getOnlyAsFieldAndMethodElements(selectedMembers, selectedNotNullMembers, useAccessors);
            // sort elements if enabled and not using chooser dialog
            if (sortElements != 0 && sortElements < 3) {
                elements.sort(new ElementComparator(sortElements));
            }
            vc.put("members", elements);

            // class information
            if (clazz != null) {
                ClassElement ce = ElementFactory.newClassElement(clazz);
                vc.put("class", ce);
                if (LOG.isDebugEnabled()) LOG.debug("Velocity Context - adding class: " + ce);

                // information to keep as it is to avoid breaking compatibility with prior releases
                vc.put("classname", useFullyQualifiedName ? ce.getQualifiedName() : ce.getName());
                vc.put("FQClassname", ce.getQualifiedName());
                vc.put("classSignature", ce.getName() + (clazz.hasTypeParameters() ? "<" + StringUtil.join(clazz.getTypeParameters(), NavigationItem::getName, ", ") + ">" : ""));
            }

            if (member != null) {
                vc.put("java_version", PsiUtil.getLanguageLevel(member).toJavaVersion().feature);
                final Project project = member.getProject();
                vc.put("settings", CodeStyle.getSettings(project).getCustomSettings(JavaCodeStyleSettings.class));
                vc.put("project", project);
            }

            vc.put("helper", GenerationHelper.class);
            vc.put("StringUtil", StringUtil.class);
            vc.put("NameUtil", NameUtil.class);

            for (String paramName : contextMap.keySet()) {
                vc.put(paramName, contextMap.get(paramName));
            }

            if (LOG.isDebugEnabled()) LOG.debug("Velocity Macro:\n" + templateMacro);

            // velocity
            VelocityEngine velocity = VelocityFactory.getVelocityEngine();
            LOG.debug("Executing velocity +++ START +++");
            velocity.evaluate(vc, sw, GenerateToStringWorker.class.getName(), templateMacro);
            LOG.debug("Executing velocity +++ END +++");

            // any additional packages to import returned from velocity?
            if (vc.get("autoImportPackages") != null) {
                params.put("autoImportPackages", (String) vc.get("autoImportPackages"));
            }
        } catch (ProcessCanceledException e) {
            throw e;
        } catch (Exception e) {
            throw new GenerateCodeException("Error in Velocity code generator", e);
        }

        return StringUtil.convertLineSeparators(sw.getBuffer().toString());
    }

}
