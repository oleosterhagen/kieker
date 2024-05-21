package kieker.monitoring.probe.bytebuddy;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.List;

import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;
import kieker.monitoring.timer.ITimeSource;
import kieker.monitoring.util.KiekerPattern;
import kieker.monitoring.util.KiekerPatternUtil;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional.Valuable;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;

public class PremainClass {
	private static final AgentBuilder.Listener ONLY_ERROR_LOGGER = new AgentBuilder.Listener() {
		@Override
		public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
		}

		@Override
		public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
				boolean loaded, DynamicType dynamicType) {
		}

		@Override
		public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
				boolean loaded) {
		}

		@Override
		public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded,
				Throwable throwable) {
			throwable.printStackTrace();
		}

		@Override
		public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
			
		}
		
	};
	
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("Starting instrumentation...");
		
		String instrumentables = System.getenv("KIEKER_SIGNATURES");
		if (instrumentables != null) {
			final List<KiekerPattern> patternObjects = KiekerPatternUtil.getPatterns(instrumentables);
			new AgentBuilder.Default()
			.with(ONLY_ERROR_LOGGER)
	        .type(new ElementMatcher<TypeDescription>() {
				@Override
				public boolean matches(TypeDescription target) {
					if (target.isInterface()) {
						return false;
					}
					String typeName = target.getTypeName();
					return KiekerPatternUtil.classIsContained(patternObjects, typeName);
				}
	        })
	        .transform(new AgentBuilder.Transformer.ForAdvice()
	                .advice(
	                        new ElementMatcher<MethodDescription>() {

								public boolean matches(MethodDescription target) {
									if (target.isMethod()) {
										// TODO: Here, we would need the signature. This would require building it from target and target.getType
										// So for now, just instrument every method (and type is checked before already)
										// KiekerPatternUtil.classIsContained(patternObjects, target.getName())
										return true;
									} else {
										return true;
									}
								}
	                        	
	                        },
	                        OperationExecutionAdvice.class.getName()
	                ))
	        .transform(new AgentBuilder.Transformer() {

				@Override
				public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
						ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
					System.out.println("Instrumenting: " + typeDescription.getActualName());
					Valuable<?> definedField = builder.defineField("CTRLINST", IMonitoringController.class, Modifier.STATIC | Modifier.FINAL | Modifier.PRIVATE);
					return definedField;
				}
	        })
	        .transform(new AgentBuilder.Transformer() {

				@Override
				public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
						ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
					Valuable<?> definedField = builder.defineField("TIME", ITimeSource.class, Modifier.STATIC | Modifier.FINAL | Modifier.PRIVATE);
					return definedField;
				}
	        })
	        .transform(new AgentBuilder.Transformer() {

				@Override
				public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
						ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
					Valuable<?> definedField = builder.defineField("VMNAME", String.class, Modifier.STATIC | Modifier.FINAL | Modifier.PRIVATE);
					return definedField;
				}
	        })
	        .transform(new AgentBuilder.Transformer() {

				@Override
				public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
						ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
					Valuable<?> definedField = builder.defineField("CFREGISTRY", ControlFlowRegistry.class, Modifier.STATIC | Modifier.FINAL | Modifier.PRIVATE);
					return definedField;
				}
	        })
	        .transform(new AgentBuilder.Transformer() {

				@Override
				public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
						ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
					Valuable<?> definedField = builder.defineField("SESSIONREGISTRY", SessionRegistry.class, Modifier.STATIC | Modifier.FINAL | Modifier.PRIVATE);
					return definedField;
				}
	        })
	        .installOn(inst);
		} else {
			System.err.println("Environment variable KIEKER_SIGNATURES not defined - not instrumenting anything!");
		}
	}
}
