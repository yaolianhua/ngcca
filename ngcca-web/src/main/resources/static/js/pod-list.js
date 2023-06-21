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

    // CodeMirror
    codemirror = CodeMirror.fromTextArea(document.getElementById("codemirror-log"), {
        mode: "text",
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
            codemirror.setValue(response.data)
            codemirror.refresh();
        })
        .catch(error => {
            fail(error);
        });
}