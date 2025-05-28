import React, { useEffect, useState, useRef } from 'react';
import { EditorState, convertToRaw, ContentState } from 'draft-js';
import draftToHtml from 'draftjs-to-html';
import htmlToDraft from 'html-to-draftjs';
import { Editor } from 'react-draft-wysiwyg';
import 'react-draft-wysiwyg/dist/react-draft-wysiwyg.css';

interface DraftEditorProps {
    value: string;
    disabled?: boolean;
    hidden?: boolean;
    onChange: (value: string) => void;
    textDirection?: 'ltr' | 'rtl';
}

const DraftEditor: React.FC<DraftEditorProps> = ({
                                                     value,
                                                     onChange,
                                                     disabled = false,
                                                     hidden = false,
                                                 }) => {
    const [editorState, setEditorState] = useState(EditorState.createEmpty());
    const lastHtml = useRef<string>(''); // cache to prevent unnecessary resets

    // Only update state if external value changes and is different
    useEffect(() => {
        if (value && value !== lastHtml.current) {
            try {
                const blocksFromHtml = htmlToDraft(value);
                const contentState = ContentState.createFromBlockArray(
                    blocksFromHtml.contentBlocks,
                    blocksFromHtml.entityMap
                );
                const newEditorState = EditorState.createWithContent(contentState);
                setEditorState(newEditorState);
                lastHtml.current = value;
            } catch (error) {
                console.error('Error parsing HTML:', error);
            }
        }
    }, [value]);

    const handleEditorChange = (state: EditorState) => {
        setEditorState(state);
        const html = draftToHtml(convertToRaw(state.getCurrentContent()));

        // Avoid re-calling if content hasn't changed
        if (html !== lastHtml.current) {
            lastHtml.current = html;
            onChange(html);
        }
    };

    if (hidden) return null;

    return (
        <div>
            <Editor
                editorState={editorState}
                wrapperClassName="editor-wrapper"
                editorClassName="afyaquik-editor"
                onEditorStateChange={handleEditorChange}
                readOnly={disabled}
                editorStyle={{
                    minHeight: '200px',
                    padding: '10px',
                }}
                wrapperStyle={{
                    border: '1px solid #ddd',
                    borderRadius: '4px'
                }}
            />
        </div>
    );
};

export default DraftEditor;
