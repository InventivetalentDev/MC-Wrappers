package org.inventivetalent.mcwrapper;

import org.inventivetalent.reflection.accessor.FieldAccessor;
import org.inventivetalent.reflection.resolver.ClassResolver;
import org.inventivetalent.reflection.resolver.ConstructorResolver;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.inventivetalent.reflection.resolver.minecraft.OBCClassResolver;
import org.inventivetalent.reflection.resolver.wrapper.ClassWrapper;
import org.inventivetalent.reflection.resolver.wrapper.ConstructorWrapper;

import java.util.Arrays;

public abstract class Wrapper {

	protected static final ClassResolver    CLASS_RESOLVER     = new ClassResolver();
	protected static final NMSClassResolver NMS_CLASS_RESOLVER = new NMSClassResolver();
	protected static final OBCClassResolver OBC_CLASS_RESOLVER = new OBCClassResolver();

	private final   Object        handle;
	private final   ClassWrapper  classWrapper;
	private final   Type          type;
	protected final FieldResolver fieldResolver;

	public Wrapper(Object handle) {
		this.handle = handle;
		this.classWrapper = new ClassWrapper<>(handle.getClass());
		this.type = Type.fromPackage(handle.getClass().getName());
		this.fieldResolver = new FieldResolver(handle.getClass());
	}

	public Wrapper(ClassWrapper classWrapper) {
		this.handle = classWrapper.newInstance();
		this.classWrapper = classWrapper;
		this.type = Type.fromPackage(classWrapper.getName());
		this.fieldResolver = new FieldResolver(classWrapper.getClazz());
	}

	public Wrapper(Class<?> clazz) {
		this(new ClassWrapper<>(clazz));
	}

	public Wrapper(ClassWrapper classWrapper, ConstructorPopulator... populators) {
		this.classWrapper = classWrapper;
		this.type = Type.fromPackage(classWrapper.getName());
		this.fieldResolver = new FieldResolver(classWrapper.getClazz());

		Class<?>[][] classArray = new Class[populators.length][0];
		for (int i = 0; i < populators.length; i++) {
			classArray[i] = populators[i].types();
		}
		ConstructorWrapper<?> constructorWrapper = new ConstructorResolver(classWrapper.getClazz()).resolveWrapper(classArray);
		int i = 0;
		for (Class<?>[] array : classArray) {
			if (Arrays.equals(array, constructorWrapper.getParameterTypes())) {
				this.handle = constructorWrapper.newInstance(populators[i].values());
				return;
			}
			i++;
		}
		throw new IllegalArgumentException("no matching constructor found");
	}

	public Wrapper(Class<?> clazz, ConstructorPopulator... populators) {
		this(new ClassWrapper<>(clazz), populators);
	}

	public Wrapper(String... classNames) {
		this(CLASS_RESOLVER.resolveWrapper(classNames));
	}

	public Wrapper(Type type, String... classNames) {
		this(type.getClassResolver().resolveWrapper(classNames));
	}

	public <T> T getFieldValue(String... names) {
		FieldAccessor accessor = getFieldResolver().resolveAccessor(names);
		return accessor != null ? accessor.get(getHandle()) : null;
	}

	public <T> void setFieldValue(T value, String... names) {
		FieldAccessor accessor = getFieldResolver().resolveAccessor(names);
		if (accessor != null) { accessor.set(getHandle(), value); }
	}

	public Type getType() {
		return type;
	}

	public FieldResolver getFieldResolver() {
		return fieldResolver;
	}

	public Object getHandle() {
		return handle;
	}

	public enum Type {
		GENERAL,
		NMS,
		OBC;

		public static Type fromPackage(String className) {
			if (className.startsWith("net.minecraft.server")) { return NMS; }
			if (className.startsWith("org.bukkit.craftbukkit")) { return OBC; }
			return GENERAL;
		}

		public ClassResolver getClassResolver() {
			switch (this) {
				case GENERAL:
					return CLASS_RESOLVER;
				case NMS:
					return NMS_CLASS_RESOLVER;
				case OBC:
					return OBC_CLASS_RESOLVER;
				default:
					throw new IllegalStateException();
			}

		}
	}
}
