import React from 'react';
import 'react-draft-wysiwyg/dist/react-draft-wysiwyg.css';
interface DraftEditorProps {
    value: string;
    disabled?: boolean;
    hidden?: boolean;
    onChange: (value: string) => void;
    textDirection?: 'ltr' | 'rtl';
}
declare const DraftEditor: React.FC<DraftEditorProps>;
export default DraftEditor;
