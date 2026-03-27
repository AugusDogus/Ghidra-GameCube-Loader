package gamecubeloader.ramdump;

import docking.widgets.OptionDialog;
import docking.widgets.filechooser.GhidraFileChooser;
import gamecubeloader.common.HeadlessSupport;
import gamecubeloader.common.SymbolLoader;
import gamecubeloader.common.SystemMemorySections;
import ghidra.app.util.MemoryBlockUtils;
import ghidra.app.util.opinion.LoadException;
import ghidra.app.util.opinion.Loader;
import ghidra.program.model.listing.Program;
import ghidra.util.Msg;
import ghidra.util.filechooser.ExtensionFileFilter;

import java.io.File;

public final class RAMDumpProgramBuilder {
	public static void load(Program program, Loader.ImporterSettings settings, boolean createSystemMemSections, String manualMapPath)
		throws LoadException {
		var baseAddress = 0x80000000L;
		var addressSpace = program.getAddressFactory().getDefaultAddressSpace();
		var provider = settings.provider();

		try {
			program.setImageBase(addressSpace.getAddress(baseAddress), true);

			// Create full RAM section.
			MemoryBlockUtils.createInitializedBlock(program, false, "RAM", addressSpace.getAddress(baseAddress), provider.getInputStream(0),
				provider.length(), "", null, true, true, true, null, settings.monitor());
		} catch (Exception e) {
			throw new LoadException(e);
		}

		/* Optionally load symbol map */
		if (manualMapPath != null && !manualMapPath.isBlank()) {
			SymbolLoader.TryLoadMapFile(new File(manualMapPath), program, settings.monitor(), baseAddress, 0, -1, "RAM Dump", false);
		} else if (!HeadlessSupport.isInteractive()) {
			HeadlessSupport.logSkippedPrompt(RAMDumpProgramBuilder.class,
				"manual RAM dump symbol map selection was skipped.");
		} else if (OptionDialog.showOptionNoCancelDialog(null, "Load Symbols?", "Would you like to load a symbol map for this RAM dump?", "Yes", "No", null) == 1) {
			var fileChooser = new GhidraFileChooser(null);
			fileChooser.setCurrentDirectory(provider.getFile().getParentFile());
			fileChooser.addFileFilter(new ExtensionFileFilter("map", "Symbol Map Files"));
			var selectedFile = fileChooser.getSelectedFile(true);

			if (selectedFile != null) {
				var loaderResult = SymbolLoader.TryLoadMapFile(selectedFile, program, settings.monitor(), baseAddress, 0, -1, "RAM Dump", false);
				if (!loaderResult.loaded) {
					Msg.error(RAMDumpProgramBuilder.class, "Failed to open the symbol map file: " + selectedFile.getAbsolutePath());
				}
			}
		}

		if (createSystemMemSections) {
			SystemMemorySections.Create(provider, program, settings.monitor(), settings.log());
		}
	}
}
