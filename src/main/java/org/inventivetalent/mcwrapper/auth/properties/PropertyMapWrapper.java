package org.inventivetalent.mcwrapper.auth.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.inventivetalent.mcwrapper.Wrapper;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.ResolverQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PropertyMapWrapper extends Wrapper {

	static Class<?> Multimap           = CLASS_RESOLVER.resolveSilent("net.minecraft.util.com.google.common.collect.Multimap", "com.google.common.collect.Multimap");
	static Class<?> ForwardingMutlimap = CLASS_RESOLVER.resolveSilent("net.minecraft.util.com.google.common.collect.ForwardingMultimap", "com.google.common.collect.ForwardingMultimap");

	static MethodResolver MultimapMethodResolver           = new MethodResolver(Multimap);
	static MethodResolver ForwardingMultimapMethodResolver = new MethodResolver(ForwardingMutlimap);

	public PropertyMapWrapper() {
		super(Type.GENERAL, "net.minecraft.util.com.mojang.authlib.properties.PropertyMap", "com.mojang.authlib.properties.PropertyMap");
	}

	public PropertyMapWrapper(Object handle) {
		super(handle);
	}

	public PropertyMapWrapper(JsonArray jsonArray) {
		this();
		for (JsonElement next : jsonArray) {
			if (next instanceof JsonObject) {
				JsonObject jsonObject = next.getAsJsonObject();
				put(jsonObject.get("name").getAsString(), new PropertyWrapper(jsonObject.get("name").getAsString(), jsonObject.get("value").getAsString(), jsonObject.has("signature") ? jsonObject.get("signature").getAsString() : null));
			}
		}
	}

	@Deprecated
	public PropertyMapWrapper(JSONArray jsonArray) {
		this();
		for (Object next : jsonArray) {
			if (next instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) next;
				put((String) jsonObject.get("name"), new PropertyWrapper((String) jsonObject.get("name"), (String) jsonObject.get("value"), jsonObject.containsKey("signature") ? ((String) jsonObject.get("signature")) : null));
			}
		}
	}

	public void putAll(PropertyMapWrapper wrapper) {
		ForwardingMultimapMethodResolver.resolveWrapper(new ResolverQuery("putAll", Multimap)).invoke(getHandle(), wrapper.getHandle());
	}

	public void put(String key, PropertyWrapper wrapper) {
		ForwardingMultimapMethodResolver.resolveWrapper("put").invoke(getHandle(), key, wrapper.getHandle());
	}

	public Collection valuesHandle() {
		return (Collection) MultimapMethodResolver.resolveWrapper("values").invoke(getHandle());
	}

	public Collection<PropertyWrapper> values() {
		List<PropertyWrapper> wrappers = new ArrayList<>();
		for (Object handle : valuesHandle()) {
			wrappers.add(new PropertyWrapper(handle));
		}
		return wrappers;
	}

	public void clear() {
		MultimapMethodResolver.resolveWrapper("clear").invoke(getHandle());
	}

	public JsonArray toJson() {
		JsonArray jsonArray = new JsonArray();
		for (PropertyWrapper wrapper : values()) {
			jsonArray.add(wrapper.toJson());
		}
		return jsonArray;
	}

}
