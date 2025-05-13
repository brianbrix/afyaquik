import React, { useEffect, useState } from 'react';
import { EditorState, convertToRaw, ContentState } from 'draft-js';
import draftToHtml from 'draftjs-to-html';
import htmlToDraft from 'html-to-draftjs';
import { Editor } from 'react-draft-wysiwyg';
import 'react-draft-wysiwyg/dist/react-draft-wysiwyg.css';

interface DraftEditorProps {
    value: string;
    onChange: (html: string) => void;
    disabled?: boolean;
    hidden?: boolean;
    textDirection?: 'ltr' | 'rtl';
}

const DraftEditor: React.FC<DraftEditorProps> = ({
                                                     value,
                                                     onChange,
                                                     disabled = false,
                                                     hidden = false,
                                                     textDirection = 'ltr'
                                                 }) => {
    const [editorState, setEditorState] = useState(EditorState.createEmpty());

    // Initialize editor content
    useEffect(() => {
        if (value) {
            try {
                const blocksFromHtml = htmlToDraft(value);
                const contentState = ContentState.createFromBlockArray(
                    blocksFromHtml.contentBlocks,
                    blocksFromHtml.entityMap
                );
                setEditorState(EditorState.createWithContent(contentState));
            } catch (error) {
                console.error('Error parsing HTML:', error);
                setEditorState(EditorState.createEmpty());
            }
        } else {
            setEditorState(EditorState.createEmpty());
        }
    }, [value]);

    const handleEditorChange = (state: EditorState) => {
        setEditorState(state);
        const html = draftToHtml(convertToRaw(state.getCurrentContent()));
        onChange(html);
    };

    if (hidden) return null;

    return (
        <div style={{ direction: textDirection }}>
            <Editor
                editorState={editorState}
                onEditorStateChange={handleEditorChange}
                toolbarHidden={disabled}
                readOnly={disabled}
                editorStyle={{
                    minHeight: '200px',
                    padding: '10px',
                    direction: textDirection
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
