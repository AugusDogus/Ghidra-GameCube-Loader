package gamecubeloader.common;

import ghidra.app.util.Option;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LoaderOptionSupport {
	private LoaderOptionSupport() {
	}

	public static String getStringOptionValue(String name, List<Option> options, String defaultValue) {
		for (Option option : options) {
			if (!name.equals(option.getName())) {
				continue;
			}

			Object value = option.getValue();
			if (value == null) {
				return defaultValue;
			}

			String text = value.toString().trim();
			return text.isEmpty() ? defaultValue : text;
		}

		return defaultValue;
	}

	public static Map<String, String> parseAssignmentList(String input) {
		Map<String, String> result = new LinkedHashMap<>();
		if (input == null || input.isBlank()) {
			return result;
		}

		String[] entries = input.split("[;\\r\\n]+");
		for (String rawEntry : entries) {
			String entry = rawEntry.trim();
			if (entry.isEmpty()) {
				continue;
			}

			int separator = entry.indexOf('=');
			if (separator <= 0 || separator == entry.length() - 1) {
				throw new IllegalArgumentException("Expected KEY=VALUE entry, got: " + entry);
			}

			String key = normalizeKey(entry.substring(0, separator));
			String value = entry.substring(separator + 1).trim();
			if (key.isEmpty() || value.isEmpty()) {
				throw new IllegalArgumentException("Expected non-empty KEY=VALUE entry, got: " + entry);
			}
			result.put(key, value);
		}

		return result;
	}

	public static Map<String, Long> parseAddressOverrides(String input) {
		Map<String, Long> result = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : parseAssignmentList(input).entrySet()) {
			result.put(entry.getKey(), parseUnsignedLong(entry.getValue()));
		}
		return result;
	}

	public static long parseUnsignedLong(String value) {
		String text = value.trim();
		if (text.startsWith("0x") || text.startsWith("0X")) {
			text = text.substring(2);
		}
		return Long.parseUnsignedLong(text, 16);
	}

	public static String resolveModuleStringValue(Map<String, String> optionsByModule, String moduleName) {
		if (optionsByModule.isEmpty()) {
			return null;
		}

		String normalizedName = normalizeKey(moduleName);
		String value = optionsByModule.get(normalizedName);
		if (value != null) {
			return value;
		}

		return resolveMatchingEntry(optionsByModule, normalizedName);
	}

	public static Long resolveModuleLongValue(Map<String, Long> optionsByModule, String moduleName) {
		if (optionsByModule.isEmpty()) {
			return null;
		}

		String normalizedName = normalizeKey(moduleName);
		Long value = optionsByModule.get(normalizedName);
		if (value != null) {
			return value;
		}

		return resolveMatchingEntry(optionsByModule, normalizedName);
	}

	private static <T> T resolveMatchingEntry(Map<String, T> optionsByModule, String normalizedName) {
		String normalizedBaseName = stripExtension(normalizedName);
		if (!normalizedBaseName.equals(normalizedName)) {
			T value = optionsByModule.get(normalizedBaseName);
			if (value != null) {
				return value;
			}
		}

		for (Map.Entry<String, T> entry : optionsByModule.entrySet()) {
			String normalizedKey = normalizeKey(entry.getKey());
			String normalizedKeyBase = stripExtension(normalizedKey);
			if (normalizedKey.equals(normalizedName) || normalizedKey.equals(normalizedBaseName)
					|| normalizedKeyBase.equals(normalizedName) || normalizedKeyBase.equals(normalizedBaseName)) {
				return entry.getValue();
			}
		}

		return null;
	}

	private static String stripExtension(String key) {
		int extension = key.lastIndexOf('.');
		return extension > 0 ? key.substring(0, extension) : key;
	}

	private static String normalizeKey(String key) {
		return key.trim().toLowerCase();
	}
}
