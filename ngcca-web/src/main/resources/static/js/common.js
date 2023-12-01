//codemirror
let codemirror_text;
let codemirror_yaml;
$(function () {

    // text CodeMirror
    codemirror_text = CodeMirror.fromTextArea(document.getElementById("codemirror-textarea"), {
        mode: "text",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    // yaml CodeMirror
    codemirror_yaml = CodeMirror.fromTextArea(document.getElementById("codemirror-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    //Initialize Select2 Elements
    $('.select2').select2();
    //Initialize Select2 Elements
    $('.select2bs4').select2({
        theme: 'bootstrap4'
    });

});
