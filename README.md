# Projektarbeit


### How to configure VM and ModulePath
To make this project work follow the below steps:

1. Adjust your vm arguments as follows (not on server):
   ``--module-path {project-path}\libs\javafx-sdk-19-{your-os}\lib --add-modules=javafx.controls`` (for runtime)

2. Set the main class of your run configuration to ``de.hswt.swa.gui.MainFrame``.

3. Add the files from ``{project-path}\libs\{foldername}\lib`` to your project dependencies. (for IDE)
