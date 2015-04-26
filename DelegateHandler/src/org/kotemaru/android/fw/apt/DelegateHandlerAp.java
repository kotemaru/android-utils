package org.kotemaru.android.fw.apt;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.apache.velocity.VelocityContext;
import org.kotemaru.android.fw.annotation.GenerateDelegateHandler;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("org.kotemaru.android.fw.annotation.GenerateDelegateHandler") // <-- Chenge it!!
public class DelegateHandlerAp extends ApBase
{
	private static String OUTPUT_PACKAGE_PATH = ".";
	private static String OUTPUT_CLASS_SUFFIX = "Handler";
	private static String TEMPLATE = DelegateHandlerAp.class.getCanonicalName().replace('.', '/')+".vm";

	public DelegateHandlerAp() {
	}

	@Override
	public boolean processClass(TypeElement classDecl) throws Exception {
		GenerateDelegateHandler anno = classDecl.getAnnotation(GenerateDelegateHandler.class);
		if (anno == null) return false;

		VelocityContext context = new VelocityContext();
		context.put("typeElement", classDecl);
		context.put("annotation", anno);
		context.put("classDecl", new ClassDecl(classDecl, environment));

		String pkgName = AptUtil.getPackageName(classDecl, OUTPUT_PACKAGE_PATH);
		String clsName = classDecl.getSimpleName() + OUTPUT_CLASS_SUFFIX;

		applyTemplate(context, pkgName, clsName, TEMPLATE);
		return true;
	}


}
