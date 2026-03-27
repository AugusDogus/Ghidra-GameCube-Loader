package gamecubeloader.common;

import ghidra.util.Msg;
import ghidra.util.SystemUtilities;

public final class HeadlessSupport {
	private HeadlessSupport() {
	}

	public static boolean isInteractive() {
		return !SystemUtilities.isInHeadlessMode();
	}

	public static void logSkippedPrompt(Class<?> owner, String message) {
		Msg.info(owner, "Headless mode: " + message);
	}
}
