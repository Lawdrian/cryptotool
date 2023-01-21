# Projektarbeit


### How to configure VM and ModulePath
To make this project work follow the below steps:

1. Adjust your vm arguments as follows:
   ``--module-path path\to\project\libs\javafx-sdk-19-{your-os}\lib --add-modules=javafx.controls`` (for runtime)

2. Set the main class of your run configuration to ``de.hswt.swa.gui.MainFrame``.

3. Add the files from ``path\to\project\libs\javafx-sdk-19-{your-os}\lib`` to your project dependencies. (for IDE)
