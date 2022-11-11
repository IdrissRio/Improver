'use strict';
Object.defineProperty(exports, "__esModule", { value: true });
exports.activate = void 0;
const path = require("path");
const vscode_1 = require("vscode");
const vscode_languageclient_1 = require("vscode-languageclient");
function activate(context) {
    let script = 'java';
    let args = ['-jar', context.asAbsolutePath(path.join('improver.jar')), '-vscode'];
    let serverOptions = {
        run: { command: script, args: args },
        debug: { command: script, args: args }
    };
    let clientOptions = {
        documentSelector: [{ scheme: 'file', language: 'java' }],
        synchronize: {
            configurationSection: 'java',
            fileEvents: [vscode_1.workspace.createFileSystemWatcher('**/*.java')]
        }
    };
    let lc = new vscode_languageclient_1.LanguageClient('improver', 'improver', serverOptions, clientOptions);
    lc.start();
}
exports.activate = activate;
//# sourceMappingURL=extension.js.map