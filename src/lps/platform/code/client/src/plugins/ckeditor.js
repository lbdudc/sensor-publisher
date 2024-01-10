/*% if (feature.GUI_SP_Management) { %*/
import Vue from "vue";

import CKEditor from "@ckeditor/ckeditor5-vue";
Vue.use(CKEditor);

import HeadingPlugin from "@ckeditor/ckeditor5-heading/src/heading";
import ClassicEditorBase from "@ckeditor/ckeditor5-editor-classic/src/classiceditor";
import EssentialsPlugin from "@ckeditor/ckeditor5-essentials/src/essentials";
import BoldPlugin from "@ckeditor/ckeditor5-basic-styles/src/bold";
import ItalicPlugin from "@ckeditor/ckeditor5-basic-styles/src/italic";
import UnderlinePlugin from '@ckeditor/ckeditor5-basic-styles/src/underline';
import StrikethroughPlugin from '@ckeditor/ckeditor5-basic-styles/src/strikethrough';
import BlockQuotePlugin from "@ckeditor/ckeditor5-block-quote/src/blockquote";
import CodePlugin from '@ckeditor/ckeditor5-basic-styles/src/code';
import SubscriptPlugin from '@ckeditor/ckeditor5-basic-styles/src/subscript';
import SuperscriptPlugin from '@ckeditor/ckeditor5-basic-styles/src/superscript';
import AlignmentPlugin from '@ckeditor/ckeditor5-alignment/src/alignment';
import ListPlugin from "@ckeditor/ckeditor5-list/src/list";
import LinkPlugin from "@ckeditor/ckeditor5-link/src/link";
import ParagraphPlugin from "@ckeditor/ckeditor5-paragraph/src/paragraph";
import Image from "@ckeditor/ckeditor5-image/src/image";
import ImageToolbar from "@ckeditor/ckeditor5-image/src/imagetoolbar";
import ImageCaption from "@ckeditor/ckeditor5-image/src/imagecaption";
import ImageStyle from "@ckeditor/ckeditor5-image/src/imagestyle";
import ImageResize from "@ckeditor/ckeditor5-image/src/imageresize";
import ImageUpload from "@ckeditor/ckeditor5-image/src/imageupload";
import Base64UploadAdapter from "@ckeditor/ckeditor5-upload/src/adapters/base64uploadadapter";

export default class ClassicEditor extends ClassicEditorBase {};

ClassicEditor.builtinPlugins = [
    HeadingPlugin,
    Base64UploadAdapter,
    EssentialsPlugin,
    BoldPlugin,
    ItalicPlugin,
    UnderlinePlugin,
    StrikethroughPlugin,
    BlockQuotePlugin,
    CodePlugin,
    SubscriptPlugin,
    SuperscriptPlugin,
    AlignmentPlugin,
    ListPlugin,
    LinkPlugin,
    ParagraphPlugin,
    Image,
    ImageCaption,
    ImageToolbar,
    ImageStyle,
    ImageResize,
    ImageUpload
];
/*% } %*/
