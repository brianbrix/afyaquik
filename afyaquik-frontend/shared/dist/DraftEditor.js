"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importStar(require("react"));
const draft_js_1 = require("draft-js");
const draftjs_to_html_1 = __importDefault(require("draftjs-to-html"));
const html_to_draftjs_1 = __importDefault(require("html-to-draftjs"));
const react_draft_wysiwyg_1 = require("react-draft-wysiwyg");
require("react-draft-wysiwyg/dist/react-draft-wysiwyg.css");
const DraftEditor = ({ value, onChange, disabled = false, hidden = false, }) => {
    const [editorState, setEditorState] = (0, react_1.useState)(draft_js_1.EditorState.createEmpty());
    const lastHtml = (0, react_1.useRef)(''); // cache to prevent unnecessary resets
    // Only update state if external value changes and is different
    (0, react_1.useEffect)(() => {
        if (value && value !== lastHtml.current) {
            try {
                const blocksFromHtml = (0, html_to_draftjs_1.default)(value);
                const contentState = draft_js_1.ContentState.createFromBlockArray(blocksFromHtml.contentBlocks, blocksFromHtml.entityMap);
                const newEditorState = draft_js_1.EditorState.createWithContent(contentState);
                setEditorState(newEditorState);
                lastHtml.current = value;
            }
            catch (error) {
                console.error('Error parsing HTML:', error);
            }
        }
    }, [value]);
    const handleEditorChange = (state) => {
        setEditorState(state);
        const html = (0, draftjs_to_html_1.default)((0, draft_js_1.convertToRaw)(state.getCurrentContent()));
        // Avoid re-calling if content hasn't changed
        if (html !== lastHtml.current) {
            lastHtml.current = html;
            onChange(html);
        }
    };
    if (hidden)
        return null;
    return (react_1.default.createElement("div", null,
        react_1.default.createElement(react_draft_wysiwyg_1.Editor, { editorState: editorState, wrapperClassName: "editor-wrapper", editorClassName: "afyaquik-editor", onEditorStateChange: handleEditorChange, readOnly: disabled, editorStyle: {
                minHeight: '200px',
                padding: '10px',
            }, wrapperStyle: {
                border: '1px solid #ddd',
                borderRadius: '4px'
            } })));
};
exports.default = DraftEditor;
