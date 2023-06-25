const POD_API = "/v1/kubernetes/pods";
let codemirror;
$(function () {
    //分页
    $('#pod-list').DataTable({
        "paging": true,
        "lengthChange": false,
        "searching": true,
        "ordering": true,
        "info": true,
        "autoWidth": true,
        "responsive": true,
    });

    // log CodeMirror
    logcodemirror = CodeMirror.fromTextArea(document.getElementById("codemirror-log"), {
        mode: "text",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });

    // yaml CodeMirror
    yamlcodemirror = CodeMirror.fromTextArea(document.getElementById("codemirror-yaml"), {
        mode: "yaml",
        theme: "monokai",
        lineNumbers: true,
        readOnly: true
    });
});


function logs(namespace, pod) {
    $('#modal-pod-log').modal('show');
    axios.get(POD_API + "/" + namespace + '/' + pod + "/log?tail=500")
        .then(response => {
            // Populate data into table
            logcodemirror.setValue(response.data)
            logcodemirror.refresh();
        })
        .catch(error => {
            fail(error);
        });
}

function yaml(namespace, pod) {
    $('#modal-pod-yaml').modal('show');
    axios.get(POD_API + "/" + namespace + '/' + pod + "/yaml")
        .then(response => {
            // Populate data into table
            yamlcodemirror.setValue(response.data)
            yamlcodemirror.refresh();
        })
        .catch(error => {
            fail(error);
        });
}