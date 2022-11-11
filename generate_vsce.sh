cd vscode
npm list | npm install
vsce package | npm install -g vsce && vsce package

code --install-extension improver-0.0.1.vsix
code -r
