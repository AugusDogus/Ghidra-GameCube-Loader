# Ghidra GameCube Loader
This is a slopfork.

> [!IMPORTANT]
> This fork exists to support deterministic `analyzeHeadless` workflows.
> It keeps the normal interactive GUI flow, but adds headless-safe behavior and explicit loader options for cases that previously depended on GUI prompts.

A  Nintendo GameCube binary loader for [Ghidra](https://github.com/NationalSecurityAgency/ghidra).

Includes optional symbol map importing, automatic namespace creation, and demangling support.

## Fork-specific Changes
* Headless-safe DOL, REL, and RAM dump loading when a fallback workflow would otherwise open a GUI prompt.
* ``-loader-manualMapPaths`` for explicit manual symbol map paths in headless runs.
* ``-loader-relBaseAddrs`` for explicit REL base address overrides in headless runs.
* ``-loader-relBssAddrs`` for explicit REL BSS address overrides in headless runs.

## Supported Formats
* DOL Executables (.dol)
* Relocatable Modules (.rel)
* Apploaders
* RAM Dumps

## Building
- Ensure you have ``JAVA_HOME`` set to the path of your JDK 21 installation.
- Set ``GHIDRA_INSTALL_DIR`` to your Ghidra install directory. This can be done in one of the following ways:
    - **Windows**: Running ``set GHIDRA_INSTALL_DIR=<Absolute path to Ghidra without quotations>``
    - **macos/Linux**: Running ``export GHIDRA_INSTALL_DIR=<Absolute path to Ghidra>``
    - Using ``-PGHIDRA_INSTALL_DIR=<Absolute path to Ghidra>`` when running ``./gradlew``
    - Adding ``GHIDRA_INSTALL_DIR`` to your Windows environment variables.
- Run ``./gradlew``
- You'll find the output zip file inside `/dist`

## Installation
- Copy the zip file to ``<Ghidra install directory>/Extensions/Ghidra``.
- Start Ghidra and use the "Install Extensions" dialog to finish the installation. (``File -> Install Extensions...``).

## Headless Options
- ``-loader-manualMapPaths=<path>`` for DOLs and RAM dumps.
- ``-loader-manualMapPaths=main=/abs/path/main.map;module1=/abs/path/module1.map`` for REL workflows.
- ``-loader-relBaseAddrs=module1=0x80500000;module2=0x80600000`` to replace interactive REL base address prompts.
- ``-loader-relBssAddrs=module1=0x80700000;module2=0x80800000`` to replace interactive REL BSS address prompts.

Module keys are matched case-insensitively against the module file basename, with or without extension.
