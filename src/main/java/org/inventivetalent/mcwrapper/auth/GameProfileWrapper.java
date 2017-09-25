package org.inventivetalent.mcwrapper.auth;

import com.google.gson.JsonObject;
import org.inventivetalent.mcwrapper.ConstructorPopulator;
import org.inventivetalent.mcwrapper.Wrapper;
import org.inventivetalent.mcwrapper.auth.properties.PropertyMapWrapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public class GameProfileWrapper extends Wrapper {

	public GameProfileWrapper(final UUID uuid, final String name) {
		super(CLASS_RESOLVER.resolveWrapper("net.minecraft.util.com.mojang.authlib.GameProfile", "com.mojang.authlib.GameProfile"), new ConstructorPopulator() {
			@Override
			public Class<?>[] types() {
				return new Class[] {
						UUID.class,
						String.class };
			}

			@Override
			public Object[] values() {
				return new Object[] {
						uuid,
						name };
			}
		});
	}

	public GameProfileWrapper(Object handle) {
		super(handle);
	}

	public GameProfileWrapper(JsonObject jsonObject) {
		this(parseUUID(jsonObject.get("id").getAsString()), jsonObject.get("name").getAsString());
		getProperties().clear();
		if (jsonObject.has("properties")) {
			getProperties().putAll(new PropertyMapWrapper(jsonObject.get("properties").getAsJsonArray()));
		}
	}

	@Deprecated
	public GameProfileWrapper(JSONObject jsonObject) {
		this(parseUUID((String) jsonObject.get("id")), (String) jsonObject.get("name"));
		getProperties().clear();
		if (jsonObject.containsKey("properties")) {
			getProperties().putAll(new PropertyMapWrapper((JSONArray) jsonObject.get("properties")));
		}
	}

	public UUID getId() {
		return getFieldValue("id");
	}

	public String getName() {
		return getFieldValue("name");
	}

	public void setName(String name) {
		setFieldValue(name, "name");
	}

	public Object getPropertiesHandle() {
		return getFieldValue("properties");
	}

	public PropertyMapWrapper getProperties() {
		return new PropertyMapWrapper(getPropertiesHandle());
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", getId() != null ? getId().toString() : "");
		jsonObject.addProperty("name", getName());
		if (getProperties() != null) {
			jsonObject.add("properties", getProperties().toJson());
		}
		return jsonObject;
	}

	static UUID parseUUID(String string) {
		if (string == null || string.isEmpty()) { return null; }
		return UUID.fromString(string);
	}

}
